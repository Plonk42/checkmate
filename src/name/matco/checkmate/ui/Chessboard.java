package name.matco.checkmate.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import name.matco.checkmate.R;
import name.matco.checkmate.game.Castling;
import name.matco.checkmate.game.Game;
import name.matco.checkmate.game.GameUtils;
import name.matco.checkmate.game.Move;
import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Promotion;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import name.matco.checkmate.ui.listeners.GameStateListener;
import name.matco.checkmate.ui.listeners.MovementListener;
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

public class Chessboard extends SurfaceView implements SurfaceHolder.Callback2, GameStateListener, MovementListener {
	
	private static final int PIECE_MARGIN = 5;
	
	private static final int SELECTION_ANIMATION_DURATION = 200; // ms
	private static final int MOVE_DURATION = 200; // ms
	
	private Checkmate container;
	private Game game;
	
	private ChessboardDrawer drawer;
	// private final Object drawLock = new Object();
	
	private static Drawable blackSquareDrawable;
	private static Drawable whiteSquareDrawable;
	private static Paint hightlightPainter;
	
	private Move animatedMove;
	private boolean animatedMoveWay;
	private long startMovingMillis = 0;
	private long selectionMillis = 0;
	
	private final Hashtable<Integer, Drawable> drawableCache = new Hashtable<Integer, Drawable>();
	
	private int x0;
	private int y0;
	int boardSize;
	int squareSize;
	float isometricScaleFactor = 1;
	
	private Piece selectedPiece;
	private final List<Square> highlightedSquares = new ArrayList<Square>();
	
	static {
		hightlightPainter = new Paint();
		hightlightPainter.setAntiAlias(true);
		hightlightPainter.setStyle(Style.FILL);
		hightlightPainter.setARGB(96, 255, 255, 255);
	}
	
	public Chessboard(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		Log.i(getClass().getName(), "Chessboard instantiated [context = " + context + ", attrs = " + attrs);
		getHolder().addCallback(this);
		buildDrawableCache();
		blackSquareDrawable = getContext().getResources().getDrawable(R.drawable.black_square);
		whiteSquareDrawable = getContext().getResources().getDrawable(R.drawable.white_square);
	}
	
	public void setGame(final Game game) {
		this.game = game;
		this.game.addMovementListener(this);
		this.game.addGameStateListeners(this);
	}
	
	public void setContainer(final Checkmate container) {
		this.container = container;
	}
	
	private void buildDrawableCache() {
		for (final Player player : Player.values()) {
			for (final PieceType pieceType : PieceType.values()) {
				final int resourceId = pieceType.getImageResource(player);
				drawableCache.put(resourceId, getContext().getResources().getDrawable(resourceId));
			}
		}
	}
	
	public Drawable getPieceImage(final Piece piece) {
		return drawableCache.get(piece.getImageResource());
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		Log.v(getClass().getName(), String.format("Touch Event [x = %.1f, y = %.1f, action = %d]", event.getX(), event.getY(), event.getAction()));
		if (event.getAction() != MotionEvent.ACTION_DOWN || animatedMove != null) {
			return false;
		}
		
		highlightedSquares.clear();
		
		final Square s = getSquareAt(event.getX(), event.getY());
		
		// synchronized (drawLock) {
		final Piece p = s.getPiece();
		
		// move selected piece
		if (selectedPiece != null) {
			// let time to selection animation to finish
			drawer.drawFor(SELECTION_ANIMATION_DURATION);
			if (selectedPiece.getAllowedPositions().contains(s)) {
				if (p == null || !p.is(selectedPiece.getPlayer())) {
					final Move m = game.getMove(selectedPiece, s);
					Log.i(getClass().getName(), "Move is " + m);
					if (m instanceof Promotion) {
						final PromotionDialog dialog = new PromotionDialog();
						dialog.setGame(game).setMove((Promotion) m).setPlayer(selectedPiece.getPlayer());
						dialog.show(container.getFragmentManager(), "promotion");
					} else {
						game.playMove(m);
					}
				}
			}
			// unselect piece anyway
			selectedPiece = null;
		}
		// select target piece
		else {
			if (p != null && game.getActivePlayer().equals(p.getPlayer())) {
				final List<Square> allowedPositions = p.getAllowedPositions();
				if (!allowedPositions.isEmpty()) {
					selectedPiece = p;
					selectionMillis = System.currentTimeMillis();
					highlightedSquares.addAll(allowedPositions);
					drawer.drawContinuous();
				}
				
				final String str = selectedPiece != null ? selectedPiece.getDescription() : "[none]";
				Log.i(getClass().getName(), String.format("Selected piece : %s", str));
			}
		}
		// }
		
		return true;
	}
	
	private Square getSquareAt(final float x, final float y) {
		if (x < x0 || x > x0 + squareSize * GameUtils.CHESSBOARD_SIZE || y < y0 || y > y0 + squareSize * GameUtils.CHESSBOARD_SIZE) {
			return null;
		}
		
		final int squareX = (int) ((x - x0) / squareSize);
		final int squareY = (int) ((y0 + boardSize - y) / squareSize);
		return game.getBoard().getSquares()[squareY * GameUtils.CHESSBOARD_SIZE + squareX];
	}
	
	@Override
	public void draw(final Canvas canvas) {
		super.draw(canvas);
		
		// TODO improve this
		if (game == null) {
			return;
		}
		
		Log.v(getClass().getName(), String.format("Draw canvas [board size = %dx%d, square size = %d]", canvas.getWidth(), canvas.getHeight(), squareSize));
		
		final long now = System.currentTimeMillis();
		
		// synchronized (drawLock) {
		
		// draw squares
		for (final Square s : game.getBoard().getSquares()) {
			final int index = s.getIndex();
			final int x = index % GameUtils.CHESSBOARD_SIZE;
			final int y = index / GameUtils.CHESSBOARD_SIZE;
			final int left = x * squareSize;
			final int right = left + squareSize;
			final int top = boardSize - (y + 1) * squareSize;
			final int bottom = top + squareSize;
			
			final Drawable squareDrawable = ((x + y) % 2 == 0) ? whiteSquareDrawable : blackSquareDrawable;
			squareDrawable.setBounds(left + 1, top + 1, right - 1, bottom - 1);
			squareDrawable.draw(canvas);
			
			if (highlightedSquares.contains(s)) {
				canvas.drawRect(left, top, right, bottom, hightlightPainter);
			}
		}
		
		// draw pieces
		for (final Piece p : game.getBoard().getOnboardPieces()) {
			
			// draw piece if it's not moving piece(s)
			if (animatedMove == null || !animatedMove.getRelatedPieces().contains(p)) {
				final int index = p.getSquare().getIndex();
				final int x = index % GameUtils.CHESSBOARD_SIZE;
				final int y = index / GameUtils.CHESSBOARD_SIZE;
				final int left = x * squareSize + PIECE_MARGIN;
				final int right = left + squareSize - 2 * PIECE_MARGIN;
				final int top = boardSize - (y + 1) * squareSize + PIECE_MARGIN;
				final int bottom = top + squareSize - 2 * PIECE_MARGIN;
				
				final int offset = p.equals(selectedPiece) ? (int) (8.0 * Math.sin((now - selectionMillis) / (float) SELECTION_ANIMATION_DURATION) + 8.0) : 0;
				
				final Drawable drawable = getPieceImage(p);
				drawable.setBounds(
						(int) (isometricScaleFactor * left),
						(int) (isometricScaleFactor * top - offset),
						(int) (isometricScaleFactor * right),
						(int) (isometricScaleFactor * bottom - offset));
				
				if (container.getTwoPlayerMode() && p.is(Player.BLACK)) {
					canvas.save();
					canvas.rotate(180, left - PIECE_MARGIN, top - PIECE_MARGIN);
					canvas.translate(-squareSize, -squareSize);
				}
				drawable.draw(canvas);
				if (container.getTwoPlayerMode() && p.is(Player.BLACK)) {
					canvas.restore();
				}
			}
		}
		
		// draw moving piece
		// moving piece must be drawn after all other pieces to appear over all other pieces (knight jump over other pieces)
		if (animatedMove != null) {
			
			// detect end of move
			final boolean endOfMove = now >= startMovingMillis + MOVE_DURATION;
			
			// piece(s) to animate
			Set<PieceMovement> pieceMovements;
			
			// some moves have a special animation
			if (animatedMove instanceof Promotion) {
				final Promotion promotion = (Promotion) animatedMove;
				PieceMovement pieceMovement;
				if (endOfMove && animatedMoveWay) {
					pieceMovement = new PieceMovement(promotion.getPromotedPiece(), promotion.getSquareTo(), promotion.getSquareTo());
				}
				else {
					pieceMovement = new PieceMovement(promotion.getPiece(), promotion.getSquareFrom(), promotion.getSquareTo());
				}
				pieceMovements = Collections.singleton(pieceMovement);
			}
			else if (animatedMove instanceof Castling) {
				final Castling castling = (Castling) animatedMove;
				pieceMovements = new HashSet<PieceMovement>();
				pieceMovements.add(new PieceMovement(castling.getPiece(), castling.getSquareFrom(), castling.getSquareTo()));
				pieceMovements.add(new PieceMovement(castling.getRook(), game.getBoard().getSquareAt(castling.getCorner()), game.getBoard().getSquareAt(castling.getRookDestination())));
			}
			else {
				pieceMovements = Collections.singleton(new PieceMovement(animatedMove.getPiece(), animatedMove.getSquareFrom(), animatedMove.getSquareTo()));
			}
			
			for (final PieceMovement pa : pieceMovements) {
				final Drawable drawable = getPieceImage(pa.getPiece());
				
				final Square s = animatedMoveWay ? pa.getTo() : pa.getFrom();
				final Movement pam = pa.getFrom().getMovementTo(pa.getTo());
				final Movement m = animatedMoveWay ? pam : pam.withInversion();
				
				// calculate current position offset
				final float coeff;
				if (endOfMove) {
					coeff = 1;
					Log.i(getClass().getName(), "Move finished at " + now);
				} else {
					coeff = (float) (now - startMovingMillis) / MOVE_DURATION;
				}
				
				final int dx = (int) ((coeff - 1.0) * m.dx * squareSize);
				final int dy = (int) ((1.0 - coeff) * m.dy * squareSize);
				final int factor = (int) (Math.sin(coeff * Math.PI) * squareSize / 3);
				
				final int index = s.getIndex();
				final int x = index % GameUtils.CHESSBOARD_SIZE;
				final int y = index / GameUtils.CHESSBOARD_SIZE;
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
				
				if (container.getTwoPlayerMode() && pa.getPiece().is(Player.BLACK)) {
					canvas.save();
					canvas.rotate(180, left - PIECE_MARGIN + dx, top - PIECE_MARGIN + dy);
					canvas.translate(-squareSize, -squareSize);
				}
				drawable.draw(canvas);
				if (container.getTwoPlayerMode() && pa.getPiece().is(Player.BLACK)) {
					canvas.restore();
				}
			}
			
			if (endOfMove) {
				animatedMove = null;
			}
		}
		
		// }
	}
	
	public void redraw() {
		drawer.drawNow();
	}
	
	public void reset() {
		// synchronized (drawLock) {
		animatedMove = null;
		selectedPiece = null;
		highlightedSquares.clear();
		// }
		
		redraw();
	}
	
	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface created");
		// create new drawer each time surface is created
		drawer = new ChessboardDrawer(this);
	}
	
	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		Log.i(getClass().getName(), String.format("Surface size changed to %d, %d", width, height));
		// board and square sizes must be recalculated when surface size changes
		boardSize = Math.min(width, height);
		squareSize = boardSize / GameUtils.CHESSBOARD_SIZE;
		holder.setFixedSize(boardSize, boardSize);
	}
	
	@Override
	public void surfaceRedrawNeeded(final SurfaceHolder holder) {
		redraw();
	}
	
	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface destroyed");
		// drawer is shutdown to stop scheduler
		// FIXME : this makes the next draw fail (once the surface has been re created)
		// drawer.shutdown();
	}
	
	@Override
	public void onGameInit() {
		reset();
	}
	
	@Override
	public void onPlayerChange(final Player player) {
		// no-op
	}
	
	@Override
	public void onMovement(final Move m, final boolean way) {
		Log.i(getClass().getName(), String.format("Draw movement %s", m));
		// clean ui
		selectedPiece = null;
		highlightedSquares.clear();
		// do move
		animatedMove = m;
		animatedMoveWay = way;
		startMovingMillis = System.currentTimeMillis();
		drawer.drawFor(MOVE_DURATION);
	}
	
}
