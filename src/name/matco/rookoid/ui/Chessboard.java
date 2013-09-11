package name.matco.rookoid.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.GameUtils;
import name.matco.rookoid.game.Move;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.MovementListener;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Promotion;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.piece.Piece;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Chessboard extends SurfaceView implements SurfaceHolder.Callback, MovementListener {
	
	private static final int BOARD_MARGIN = 10;
	private static final int PIECE_MARGIN = 5;
	
	private static final int MOVE_DURATION = 200; // ms
	
	private Rookoid container;
	
	private static Paint blackPainter;
	private static Paint whitePainter;
	
	private static Paint hightlightPainter;
	private Timer paintTimer;
	private final Object drawLock = new Object();
	
	private Move animatedMove;
	private boolean animatedMoveWay;
	private long startMovingMillis = 0;
	
	private final Hashtable<Integer, Drawable> drawableCache = new Hashtable<Integer, Drawable>();
	
	private int x0;
	private int y0;
	private int boardSize;
	private int squareSize;
	
	private final Game game;
	
	private Piece selectedPiece;
	private final List<Square> highlightedSquares = new ArrayList<Square>();
	
	float isometricScaleFactor = 1;
	
	static {
		blackPainter = new Paint();
		blackPainter.setAntiAlias(true);
		blackPainter.setStyle(Style.FILL);
		blackPainter.setARGB(255, 209, 139, 71);
		
		whitePainter = new Paint();
		whitePainter.setAntiAlias(true);
		whitePainter.setStyle(Style.FILL);
		whitePainter.setARGB(255, 255, 206, 158);
		
		hightlightPainter = new Paint();
		hightlightPainter.setAntiAlias(true);
		hightlightPainter.setStyle(Style.FILL);
		hightlightPainter.setARGB(96, 255, 255, 255);
	}
	
	public Chessboard(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		Log.i(getClass().getName(), "Chessboard instantiated [context = " + context + ", attrs = " + attrs);
		getHolder().addCallback(this);
		
		game = Game.getInstance();
		game.addMovementListener(this);
		buildDrawableCache();
	}
	
	public void setContainer(final Rookoid container) {
		this.container = container;
	}
	
	private void buildDrawableCache() {
		for (final Square c : game.getBoard()) {
			final Piece p = c.getPiece();
			if (p != null) {
				drawableCache.put(p.getResource(), getContext().getResources().getDrawable(p.getResource()));
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		Log.i(getClass().getName(), String.format("Touch Event [x = %.1f, y = %.1f, action = %d]", event.getX(), event.getY(), event.getAction()));
		if (event.getAction() != MotionEvent.ACTION_DOWN || animatedMove != null) {
			return false;
		}
		
		highlightedSquares.clear();
		
		final Square s = getSquareAt(event.getX(), event.getY());
		
		// synchronized (drawLock) {
		if (s != null) {
			final Piece p = s.getPiece();
			
			// move selected piece
			if (selectedPiece != null) {
				if (selectedPiece.getAllowedPositions().contains(s)) {
					if (p == null || !p.is(selectedPiece.getPlayer())) {
						final Move m = game.getMove(selectedPiece, s);
						Log.i(getClass().getName(), "Move is " + m);
						if (m instanceof Promotion) {
							final PromotionDialog dialog = new PromotionDialog();
							dialog.setMove((Promotion) m).setPlayer(selectedPiece.getPlayer());
							dialog.show(container.getFragmentManager(), "promotion");
						} else {
							game.playMove(m);
						}
					}
				}
				selectedPiece = null;
			}
			// select of target piece
			else {
				if (p != null && game.getActivePlayer().equals(p.getPlayer())) {
					final List<Square> allowedPositions = p.getAllowedPositions();
					if (!allowedPositions.isEmpty()) {
						selectedPiece = p;
						highlightedSquares.addAll(selectedPiece.getAllowedPositions());
					}
					
					final String str = selectedPiece != null ? selectedPiece.getDescription() : "[none]";
					Log.i(getClass().getName(), String.format("Selected piece : %s", str));
				}
			}
		}
		else {
			selectedPiece = null;
		}
		// }
		doDraw();
		return true;
	}
	
	private Square getSquareAt(final float x, final float y) {
		if (x < x0 || x > x0 + squareSize * GameUtils.CHESSBOARD_SIZE || y < y0 || y > y0 + squareSize * GameUtils.CHESSBOARD_SIZE) {
			return null;
		}
		
		final int squareX = (int) ((x - x0) / squareSize);
		final int squareY = (int) ((y0 + boardSize - y) / squareSize);
		return game.getBoard()[squareY * GameUtils.CHESSBOARD_SIZE + squareX];
	}
	
	@Override
	public void draw(final Canvas canvas) {
		super.draw(canvas);
		
		final int width = (canvas.getWidth() - BOARD_MARGIN * 2);
		final int height = (canvas.getHeight() - BOARD_MARGIN * 2);
		boardSize = Math.min(width, height);
		squareSize = boardSize / GameUtils.CHESSBOARD_SIZE;
		
		Log.v(getClass().getName(), String.format("Draw canvas [dimension = %dx%d, size = %d]", width, height, squareSize));
		
		x0 = (canvas.getWidth() - boardSize) / 2;
		y0 = (canvas.getHeight() - boardSize) / 2;
		
		canvas.translate(x0, y0);
		
		final long now = System.currentTimeMillis();
		
		// synchronized (drawLock) {
		
		// draw squares
		for (final Square s : game.getBoard()) {
			
			final int x = s.getCoordinate().x;
			final int y = s.getCoordinate().y;
			final int left = x * squareSize;
			final int right = left + squareSize;
			final int top = boardSize - (y + 1) * squareSize;
			final int bottom = top + squareSize;
			
			canvas.drawRect(left, top, right, bottom, ((x + y) % 2 == 0) ? whitePainter : blackPainter);
			if (highlightedSquares.contains(s)) {
				canvas.drawRect(left, top, right, bottom, hightlightPainter);
			}
		}
		
		// draw pieces
		for (final Square s : game.getBoard()) {
			
			final Piece p = s.getPiece();
			// draw piece if it's not moving piece
			if (p != null && (animatedMove == null || !p.equals(animatedMove.getPiece()))) {
				
				final Drawable drawable = drawableCache.get(p.getResource());
				
				final int x = s.getCoordinate().x;
				final int y = s.getCoordinate().y;
				final int left = x * squareSize + PIECE_MARGIN;
				final int right = left + squareSize - 2 * PIECE_MARGIN;
				final int top = boardSize - (y + 1) * squareSize + PIECE_MARGIN;
				final int bottom = top + squareSize - 2 * PIECE_MARGIN;
				
				// FIXME : reset offset to 0 when unselected
				final int offset = p.equals(selectedPiece) ? (int) (8.0 * Math.cos(now / 200.0) + 8.0) : 0;
				drawable.setBounds(
						(int) (isometricScaleFactor * left),
						(int) (isometricScaleFactor * top - offset),
						(int) (isometricScaleFactor * right),
						(int) (isometricScaleFactor * bottom - offset));
				drawable.draw(canvas);
			}
		}
		
		// draw moving piece
		// moving piece must be drawn after all other pieces to appear over all other pieces (knight jump over other pieces)
		if (animatedMove != null) {
			
			final Drawable drawable = drawableCache.get(animatedMove.getPiece().getResource());
			
			final Square s = animatedMoveWay ? animatedMove.getTo() : animatedMove.getFrom();
			final Movement m = animatedMoveWay ? animatedMove.getMovement() : animatedMove.getMovement().withInversion();
			
			// calculate current position offset
			final float coeff;
			if (now >= startMovingMillis + MOVE_DURATION) {
				animatedMove = null;
				coeff = 1;
				Log.i(getClass().getName(), "Move finished at " + now);
			} else {
				coeff = (float) (now - startMovingMillis) / MOVE_DURATION;
			}
			
			final int dx = (int) ((coeff - 1.0) * m.dx * squareSize);
			final int dy = (int) ((1.0 - coeff) * m.dy * squareSize);
			final int factor = (int) (Math.sin(coeff * Math.PI) * squareSize / 3);
			
			final int x = s.getCoordinate().x;
			final int y = s.getCoordinate().y;
			final int left = x * squareSize + PIECE_MARGIN;
			final int right = left + squareSize - 2 * PIECE_MARGIN;
			final int top = boardSize - (y + 1) * squareSize + PIECE_MARGIN;
			final int bottom = top + squareSize - 2 * PIECE_MARGIN;
			
			Log.v(getClass().getName(), "Painting movement : coeff = " + coeff + ", dx = " + dx + ", dy = " + dy + ", factor " + factor);
			drawable.setBounds(
					(int) (isometricScaleFactor * (left + dx - factor)),
					(int) (isometricScaleFactor * (top + dy - factor)),
					(int) (isometricScaleFactor * (right + dx + factor)),
					(int) (isometricScaleFactor * (bottom + dy + factor)));
			drawable.draw(canvas);
		}
		
		drawCapturedPieces(canvas);
		
		// }
	}
	
	private void drawCapturedPieces(final Canvas canvas) {
		final int capturedBlackY = -squareSize / 2;
		int offsetBlack = 0;
		
		final int capturedWhiteY = boardSize;
		int offsetWhite = 0;
		synchronized (game.getCapturedPieces()) {
			for (final Piece p : game.getCapturedPieces()) {
				final Drawable drawable = drawableCache.get(p.getResource());
				if (p.is(Player.BLACK)) {
					drawable.setBounds((int) (isometricScaleFactor * offsetBlack), (int) (isometricScaleFactor * capturedBlackY), (int) (isometricScaleFactor * (offsetBlack + squareSize / 2)),
							(int) (isometricScaleFactor * (capturedBlackY + squareSize / 2)));
					offsetBlack += squareSize / 2;
				} else {
					drawable.setBounds((int) (isometricScaleFactor * offsetWhite), (int) (isometricScaleFactor * capturedWhiteY), (int) (isometricScaleFactor * (offsetWhite + squareSize / 2)),
							(int) (isometricScaleFactor * (capturedWhiteY + squareSize / 2)));
					offsetWhite += squareSize / 2;
				}
				drawable.draw(canvas);
			}
		}
	}
	
	private void doDraw() {
		final SurfaceHolder holder = getHolder();
		final Canvas theCanvas = holder.lockCanvas();
		if (theCanvas != null) {
			try {
				draw(theCanvas);
			} finally {
				holder.unlockCanvasAndPost(theCanvas);
			}
		}
	}
	
	public void refresh() {
		doDraw();
	}
	
	public void reset() {
		// synchronized (drawLock) {
		animatedMove = null;
		selectedPiece = null;
		highlightedSquares.clear();
		// }
		
		refresh();
	}
	
	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface created");
		
		doDraw();
		
		paintTimer = new Timer();
		paintTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (selectedPiece != null || animatedMove != null) {
					doDraw();
				}
			}
		}, 0, 25);
	}
	
	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		Log.i(getClass().getName(), "Surface changed");
	}
	
	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface destroyed");
		
		paintTimer.cancel();
		paintTimer = null;
	}
	
	@Override
	public void onMovement(final Move m, final boolean way) {
		Log.i(getClass().getName(), String.format("Prepare to draw movement %s", m));
		animatedMove = m;
		animatedMoveWay = way;
		startMovingMillis = System.currentTimeMillis();
		Log.i(getClass().getName(), "Move started at " + startMovingMillis);
	}
	
}
