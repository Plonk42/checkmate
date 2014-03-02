package name.matco.checkmate.game;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import name.matco.checkmate.ui.listeners.GameListener;
import name.matco.checkmate.ui.listeners.GameStateListener;
import name.matco.checkmate.ui.listeners.MovementListener;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Game implements Parcelable {
	
	// TODO : choose a better key?
	public static final String PARCELABLE_KEY = Game.class.getName();
	
	public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
		@Override
		public Game createFromParcel(final Parcel in) {
			return new Game(in);
		}
		
		@Override
		public Game[] newArray(final int size) {
			return new Game[size];
		}
	};
	
	private final Set<GameStateListener> gameStateListeners = new HashSet<GameStateListener>();
	private final Set<MovementListener> movementListeners = new HashSet<MovementListener>();
	private final Set<CheckListener> checkListeners = new HashSet<CheckListener>();
	
	private Board board;
	
	private Date _startDate;
	private Date _endDate;
	
	private int progression = 0;
	
	// no-arg constructor
	public Game() {
		Log.i(getClass().getName(), "Instantiating new Game()");
		init();
	}
	
	public Game(final Parcel in) {
		Log.i(getClass().getName(), "Restoring Game() from Parcel");
		board = new Board(in);
		progression = in.readInt();
	}
	
	private void init() {
		board = new Board();
		setProgression(0);
		
		for (final GameStateListener gl : gameStateListeners) {
			gl.onGameInit();
		}
	}
	
	public Date getStartDate() {
		return _startDate;
	}
	
	public Date getEndDate() {
		return _endDate;
	}
	
	public int getProgression() {
		return progression;
	}
	
	public void setProgression(final int progression) {
		this.progression = progression;
		for (final GameStateListener gl : gameStateListeners) {
			gl.onPlayerChange(getActivePlayer());
		}
	}
	
	public Player getActivePlayer() {
		return progression % 2 == 0 ? Player.WHITE : Player.BLACK;
	}
	
	public boolean isFirstMove() {
		return progression == 0;
	}
	
	public boolean isLastMove() {
		return progression == board.getMoves().size();
	}
	
	public Move getLastMove() {
		return board.getMoves().size() > 0 ? board.getMoves().get(progression - 1) : null;
	}
	
	public void reset() {
		init();
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Move getMove(final Piece p, final Square to) {
		Log.d(getClass().getName(), String.format("Retrieve move for %s - has moved: %s, to is castling destination: %s, to is empty: %s", p, p.hasMoved(), to.isCastlingDestination(getActivePlayer()), to.isEmpty()));
		final Move m;
		if (p.is(PieceType.KING) && !p.hasMoved() && to.isCastlingDestination(getActivePlayer())) {
			m = new Castling(getBoard(), getActivePlayer(), p, to);
		} else if (p.is(PieceType.PAWN) && to.isEmpty() && !GameUtils.onSameFile(p.getSquare(), to)) {
			try {
				m = new EnPassant(getBoard(), getActivePlayer(), p, to);
			} catch (final OutOfBoardCoordinateException e) {
				// no move could have been done outside board
				Log.e(getClass().getName(), "Move is outside board", e);
				return null;
			}
		} else if (p.is(PieceType.PAWN) && to.isPromotionDestination(getActivePlayer())) {
			m = new Promotion(getBoard(), getActivePlayer(), p, to);
		} else {
			m = new Move(getBoard(), getActivePlayer(), p, to);
		}
		return m;
	}
	
	public void playMove(final Move m) {
		Log.i(getClass().getName(), String.format("Logging : %s = %s", m.getAlgebraic(), m));
		
		// log movement
		if (progression < board.getMoves().size()) {
			board.getMoves().subList(progression, board.getMoves().size()).clear();
		}
		board.getMoves().add(m);
		
		playMoveWithoutLog(m, true);
		
		Log.i(getClass().getName(), String.format("Check check for player %s", getActivePlayer()));
		if (board.isCheck(getActivePlayer())) {
			Log.i(getClass().getName(), String.format("Check"));
			if (board.isCheckmate(getActivePlayer())) {
				Log.i(getClass().getName(), String.format("Checkmate"));
				for (final CheckListener cv : checkListeners) {
					cv.onCheckmate(m.getPiece(), m.getSquareFrom(), m.getSquareTo());
				}
			}
			else {
				for (final CheckListener cv : checkListeners) {
					cv.onCheck(m.getPiece(), m.getSquareFrom(), m.getSquareTo());
				}
			}
		}
	}
	
	/**
	 * @param m the move
	 */
	private void playMoveWithoutLog(final Move m, final boolean way) {
		Log.i(getClass().getName(), String.format("Playing : %s = %s", m.getAlgebraic(), m));
		
		if (way) {
			m.doMove();
		}
		else {
			m.revertMove();
		}
		
		// manage progression
		setProgression(progression + (way ? 1 : -1));
		
		// manage listeners
		for (final MovementListener mv : movementListeners) {
			mv.onMovement(m, way);
		}
	}
	
	public boolean goPrevious() {
		Log.i(getClass().getName(), String.format("Go previous, current progression is %d, moves number is %d", progression, board.getMoves().size()));
		if (progression > 0) {
			playMoveWithoutLog(board.getMoves().get(progression - 1), false);
			return true;
		}
		return false;
	}
	
	public boolean goNext() {
		Log.i(getClass().getName(), String.format("Go next, current progression is %d, moves number is %d", progression, board.getMoves().size()));
		if (progression < board.getMoves().size()) {
			playMoveWithoutLog(board.getMoves().get(progression), true);
			return true;
		}
		return false;
	}
	
	/*
	 * Listeners
	 */
	public void addGameListener(final GameListener gui) {
		addGameStateListeners(gui);
		addMovementListener(gui);
		addCheckListener(gui);
	}
	
	public void removeGameListener(final GameListener gui) {
		removeGameStateListeners(gui);
		removeMovementListener(gui);
		removeCheckListener(gui);
	}
	
	public void addGameStateListeners(final GameStateListener gsl) {
		gameStateListeners.add(gsl);
	}
	
	public void removeGameStateListeners(final GameStateListener gsl) {
		gameStateListeners.remove(gsl);
	}
	
	public void addMovementListener(final MovementListener ml) {
		movementListeners.add(ml);
	}
	
	public void removeMovementListener(final MovementListener ml) {
		movementListeners.remove(ml);
	}
	
	public void addCheckListener(final CheckListener cl) {
		checkListeners.add(cl);
	}
	
	public void removeCheckListener(final CheckListener cl) {
		checkListeners.remove(cl);
	}
	
	// parcelable implementation
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeParcelable(board, 0);
		dest.writeInt(progression);
	}
	
}
