package name.matco.checkmate.ui;

import java.util.ArrayList;
import java.util.List;

import name.matco.checkmate.game.Game;
import name.matco.checkmate.game.GameUtils;
import name.matco.checkmate.game.Move;
import name.matco.checkmate.game.PieceModification;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Promotion;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.ui.listeners.GameStateListener;
import name.matco.checkmate.ui.listeners.MovementListener;
import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Chessboard extends SurfaceView implements SurfaceHolder.Callback2, GameStateListener, MovementListener {
	
	private static final int SELECTION_ANIMATION_DURATION = 200; // ms
	private static final int MOVE_DURATION = 200; // ms
	
	private Checkmate container;
	private Game game;
	
	private ChessboardDrawer drawer;
	private final DrawFactory drawFactory;
	
	private Move animatedMove;
	private boolean animatedMoveWay;
	private long startMovingMillis = 0;
	private long selectionMillis = 0;
	
	private Piece selectedPiece;
	private final List<Square> highlightedSquares = new ArrayList<Square>();
	
	public Chessboard(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		Log.i(getClass().getName(), "Chessboard instantiated [context = " + context + ", attrs = " + attrs);
		drawFactory = new DrawFactory(context);
		getHolder().addCallback(this);
	}
	
	public void setGame(final Game game) {
		this.game = game;
		this.game.addMovementListener(this);
		this.game.addGameStateListeners(this);
	}
	
	public void setContainer(final Checkmate container) {
		this.container = container;
	}
	
	public DrawFactory getDrawFactory() {
		return drawFactory;
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		Log.v(getClass().getName(), String.format("Touch Event [x = %.1f, y = %.1f, action = %d]", event.getX(), event.getY(), event.getAction()));
		if (event.getAction() != MotionEvent.ACTION_DOWN || animatedMove != null) {
			return false;
		}
		
		highlightedSquares.clear();
		
		final Square s = getSquareAt(event.getX(), event.getY());
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
					selectionMillis = SystemClock.uptimeMillis();
					highlightedSquares.addAll(allowedPositions);
					drawer.drawContinuous();
				}
				final String str = selectedPiece != null ? selectedPiece.getDescription() : "[none]";
				Log.i(getClass().getName(), String.format("Selected piece : %s", str));
			}
		}
		
		return true;
	}
	
	private Square getSquareAt(final float x, final float y) {
		final int squareSize = drawFactory.getSquareSize();
		final int boardSize = drawFactory.getBoardSize();
		
		final int squareX = (int) x / squareSize;
		final int squareY = (int) ((boardSize - y) / squareSize);
		
		try {
			return game.getBoard().getSquareAt(GameUtils.coordinateToIndex(squareX, squareY));
		} catch (final OutOfBoardCoordinateException e) {
			return null;
		}
	}
	
	@Override
	public void draw(final Canvas canvas) {
		super.draw(canvas);
		
		// TODO improve this
		if (game == null) {
			return;
		}
		
		Log.v(getClass().getName(), String.format("Draw canvas [board size = %dx%d, square size = %d]", canvas.getWidth(), canvas.getHeight(), drawFactory.getSquareSize()));
		
		final long now = SystemClock.uptimeMillis();
		
		// draw squares
		for (final Square s : game.getBoard().getSquares()) {
			drawFactory.draw(canvas, s, highlightedSquares.contains(s));
		}
		
		// draw pieces
		for (final Piece p : game.getBoard().getOnboardPieces()) {
			// draw piece if it's not moving piece(s)
			if (animatedMove == null || !animatedMove.getMovingPieces().contains(p.getId())) {
				final boolean flipped = container.getTwoPlayerMode() && p.is(Player.BLACK);
				
				if (p.equals(selectedPiece)) {
					final int offset = (int) (8.0 * Math.sin((now - selectionMillis) / (float) SELECTION_ANIMATION_DURATION) + 8.0);
					canvas.save();
					canvas.translate(0, flipped ? offset : -offset);
				}
				
				drawFactory.draw(canvas, p, flipped);
				
				if (p.equals(selectedPiece)) {
					canvas.restore();
				}
			}
		}
		
		// draw moving piece
		// moving piece must be drawn after all other pieces to appear over all other pieces (knight jump over other pieces)
		if (animatedMove != null) {
			// detect end of move
			final boolean endOfMove = now >= startMovingMillis + MOVE_DURATION;
			// calculate current position offset
			final float coeff;
			if (endOfMove) {
				coeff = 1;
				Log.i(getClass().getName(), "Move finished at " + now);
			} else {
				coeff = (float) (now - startMovingMillis) / MOVE_DURATION;
			}
			
			for (final PieceModification modification : animatedMove.getModifications()) {
				if (modification.isMovement()) {
					final Piece p = game.getBoard().getPieceFromId(modification.getPieceId());
					final Square from = game.getBoard().getSquareAt(modification.getFrom());
					final Square to = game.getBoard().getSquareAt(modification.getTo());
					final PieceMovement move = new PieceMovement(p, from, to);
					drawFactory.drawMovement(canvas, move, animatedMoveWay, coeff, container.getTwoPlayerMode() && p.is(Player.BLACK));
				}
			}
			
			if (endOfMove) {
				animatedMove = null;
			}
		}
	}
	
	public void redraw() {
		drawer.drawNow();
	}
	
	public void reset() {
		animatedMove = null;
		selectedPiece = null;
		highlightedSquares.clear();
		
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
		final int size = Math.min(width, height);
		drawFactory.setBoardSize(size);
		holder.setFixedSize(size, size);
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
		startMovingMillis = SystemClock.uptimeMillis();
		drawer.drawFor(MOVE_DURATION);
	}
	
}
