package name.matco.rookoid.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.GameUtils;
import name.matco.rookoid.game.Player;
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

public class Chessboard extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final int BOARD_MARGIN = 10;
	private static final int PIECE_MARGIN = 5;
	
	private static Paint blackPainter;
	private static Paint whitePainter;
	
	private static Paint hightlightPainter;
	private Timer paintTimer;
	
	private Hashtable<Integer, Drawable> drawableCache = new Hashtable<Integer, Drawable>();
	
	private int x0;
	private int y0;
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
	
	public Chessboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(getClass().getName(), "Chessboard instantiated [context = " + context + ", attrs = " + attrs);
		getHolder().addCallback(this);
		
		game = Game.getInstance();
		buildDrawableCache();
		// setBackgroundColor(getResources().getColor(R.color.darker_gray));
	}
	
	private void buildDrawableCache() {
		for (Square c : game.getBoard()) {
			Piece p = c.getPiece();
			if (p != null) {
				drawableCache.put(p.getResource(), getContext().getResources().getDrawable(p.getResource()));
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(getClass().getName(), String.format("Touch Event [x = %.1f, y = %.1f, action = %d]", event.getX(), event.getY(), event.getAction()));
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}
		
		highlightedSquares.clear();
		
		Square c = getSquareAt(event.getX(), event.getY());
		
		if (c != null) {
			Piece p = c.getPiece();
			
			if (selectedPiece != null) {
				if (selectedPiece.getAllowedPositions(game).contains(c)) {
					if (p != null) {
						if (!p.getPlayer().equals(selectedPiece.getPlayer())) {
							moveSelectedPieceTo(c);
						}
					}
					else {
						moveSelectedPieceTo(c);
					}
				}
				selectedPiece = null;
			}
			else {
				if (p != null && game.getActivePlayer().equals(p.getPlayer())) {
					selectedPiece = p;
					if (selectedPiece != null) {
						highlightedSquares.addAll(selectedPiece.getAllowedPositions(game));
					}
					
					String str = selectedPiece != null ? selectedPiece.getDescription() : "[none]";
					Log.i(getClass().getName(), String.format("Selected piece : %s", str));
				}
			}
			doDraw();
			return true;
		}
		return false;
	}
	
	// TODO delete this
	private void moveSelectedPieceTo(Square c) {
		game.movePieceTo(selectedPiece, c);
	}
	
	private Square getSquareAt(float x, float y) {
		if (x < x0 || x > x0 + squareSize * GameUtils.CHESSBOARD_SIZE || y < y0 || y > y0 + squareSize * GameUtils.CHESSBOARD_SIZE) {
			return null;
		}
		
		int squareX = (int) ((x - x0) / squareSize);
		int squareY = (int) ((y - y0) / squareSize);
		return game.getBoard()[squareY * 8 + squareX];
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		int width = (canvas.getWidth() - BOARD_MARGIN * 2) / 8;
		int height = (canvas.getHeight() - BOARD_MARGIN * 2) / 8;
		squareSize = Math.min(width, height);
		
		Log.d(getClass().getName(), String.format("Draw canvas [dimension = %dx%d, size = %d]", width, height, squareSize));
		
		x0 = (canvas.getWidth() - squareSize * 8) / 2;
		y0 = (canvas.getHeight() - squareSize * 8) / 2;
		
		canvas.translate(x0, y0);
		for (Square c : game.getBoard()) {
			int x = c.getCoordinate().x;
			int y = c.getCoordinate().y;
			int left = x * squareSize;
			int right = left + squareSize;
			int top = y * squareSize;
			int bottom = top + squareSize;
			
			canvas.drawRect(left, top, right, bottom, ((x + y) % 2 == 0) ? whitePainter : blackPainter);
			if (highlightedSquares.contains(c)) {
				canvas.drawRect(left, top, right, bottom, hightlightPainter);
			}
		}
		
		for (int i = 0; i < game.getBoard().length; i++) {
			Piece p = game.getBoard()[i].getPiece();
			if (p == null) {
				continue;
			}
			
			int x = i % 8;
			int y = i / 8;
			int left = x * squareSize + PIECE_MARGIN;
			int right = left + squareSize - 2 * PIECE_MARGIN;
			int top = y * squareSize + PIECE_MARGIN;
			int bottom = top + squareSize - 2 * PIECE_MARGIN;
			
			long millis = System.currentTimeMillis();
			// FIXME : reset offset to 0 when unselected
			int offset = p.equals(selectedPiece) ? (int) (8.0 * Math.cos(millis / 200.0) + 8.0) : 0;
			
			Drawable drawable = drawableCache.get(p.getResource());
			drawable.setBounds((int) (isometricScaleFactor * left), (int) (isometricScaleFactor * top - offset), (int) (isometricScaleFactor * right), (int) (isometricScaleFactor * bottom - offset));
			drawable.draw(canvas);
			
			drawCapturedPieces(canvas);
		}
	}
	
	private void drawCapturedPieces(Canvas canvas) {
		int capturedWhiteY = -squareSize / 2;
		int offsetWhite = 0;
		
		int capturedBlackY = GameUtils.CHESSBOARD_SIZE * squareSize;
		int offsetBlack = 0;
		synchronized (game.getCapturedPieces()) {
			for (Piece p : game.getCapturedPieces()) {
				Drawable drawable = drawableCache.get(p.getResource());
				if (Player.WHITE.equals(p.getPlayer())) {
					drawable.setBounds((int) (isometricScaleFactor * offsetWhite), (int) (isometricScaleFactor * capturedWhiteY), (int) (isometricScaleFactor * (offsetWhite + squareSize / 2)),
							(int) (isometricScaleFactor * (capturedWhiteY + squareSize / 2)));
					offsetWhite += squareSize / 2;
				}
				else {
					drawable.setBounds((int) (isometricScaleFactor * offsetBlack), (int) (isometricScaleFactor * capturedBlackY), (int) (isometricScaleFactor * (offsetBlack + squareSize / 2)),
							(int) (isometricScaleFactor * (capturedBlackY + squareSize / 2)));
					offsetBlack += squareSize / 2;
				}
				drawable.draw(canvas);
			}
		}
	}
	
	private void doDraw() {
		SurfaceHolder holder = getHolder();
		Canvas theCanvas = holder.lockCanvas();
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
		selectedPiece = null;
		highlightedSquares.clear();
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
				if (selectedPiece != null) {
					doDraw();
				}
			}
		}, 0, 50);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(getClass().getName(), "Surface changed");
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface destroyed");
		
		paintTimer.cancel();
		paintTimer = null;
	}
	
}
