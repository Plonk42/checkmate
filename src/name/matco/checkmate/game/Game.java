package name.matco.checkmate.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.King;
import name.matco.checkmate.game.piece.Pawn;
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
	
	private List<Move> moves = new ArrayList<Move>();
	private int progression = 0;
	
	private Player activePlayer;
	
	// no-arg constructor
	public Game() {
		Log.i(getClass().getName(), "Instantiating new Game()");
		init();
	}
	
	public Game(final Parcel in) {
		Log.i(getClass().getName(), "Restoring Game() from Parcel");
		board = new Board(in);
		activePlayer = (Player) in.readSerializable();
		progression = in.readInt();
		// TODO : parcel moves;
		// in.readList(this.moves, null);
		for (int i = 0; i < progression; i++) {
			moves.add(new Move(null, null, null));
		}
		
		// for (final Piece p :this.pieces) {
		// getSquareAt(p.getSquare().getCoordinate());
		// this.pieces.add(p);
		// }
		
		// TODO : whiteKing && BlackKing
	}
	
	private void init() {
		board = new Board();
		moves.clear();
		progression = 0;
		
		setPlayer(Player.WHITE);
		
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
	
	public boolean isFirstMove() {
		return progression == 0;
	}
	
	public boolean isLastMove() {
		return progression == moves.size();
	}
	
	public List<Move> getMoves() {
		return moves;
	}
	
	public Move getLastMove() {
		return moves.size() > 0 ? moves.get(progression - 1) : null;
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
			m = new Castling(getActivePlayer(), (King) p, to);
		} else if (p.is(PieceType.PAWN) && to.isEmpty() && (p.getSquare().getCoordinate().x != to.getCoordinate().x)) {
			try {
				m = new EnPassant(getBoard(), getActivePlayer(), (Pawn) p, to);
			} catch (final OutOfBoardCoordinateException e) {
				// no move could have been done outside board
				Log.e(getClass().getName(), "Move is outside board", e);
				return null;
			}
		} else if (p.is(PieceType.PAWN) && to.isPromotionDestination(getActivePlayer())) {
			m = new Promotion(getActivePlayer(), (Pawn) p, to);
		} else {
			m = new Move(getActivePlayer(), p, to);
		}
		return m;
	}
	
	public void playMove(final Move m) {
		Log.i(getClass().getName(), String.format("Logging : %s = %s", m.getAlgebraic(), m));
		
		// log movement
		if (progression < moves.size()) {
			moves = moves.subList(0, progression);
		}
		moves.add(m);
		progression = moves.size();
		
		playMoveWithoutLog(m);
		
		Log.i(getClass().getName(), String.format("Check check for player %s", getActivePlayer()));
		if (board.isCheck(getActivePlayer())) {
			Log.i(getClass().getName(), String.format("Check"));
			if (board.isCheckmate(getActivePlayer())) {
				Log.i(getClass().getName(), String.format("Checkmate"));
				for (final CheckListener cv : checkListeners) {
					cv.onCheckmate(m.getPiece(), m.getFrom(), m.getTo());
				}
			}
			else {
				for (final CheckListener cv : checkListeners) {
					cv.onCheck(m.getPiece(), m.getFrom(), m.getTo());
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
			m.doMove(this);
		}
		else {
			m.revertMove(this);
		}
		board.setLastMove(moves.get(progression - 1));
		
		// manage listeners
		for (final MovementListener mv : movementListeners) {
			mv.onMovement(m, way);
		}
		
		// change active player
		nextPlayer();
	}
	
	private void playMoveWithoutLog(final Move m) {
		playMoveWithoutLog(m, true);
	}
	
	public Player getActivePlayer() {
		return activePlayer;
	}
	
	private void nextPlayer() {
		setPlayer(activePlayer.getOpponent());
	}
	
	private void setPlayer(final Player player) {
		activePlayer = player;
		for (final GameStateListener gl : gameStateListeners) {
			gl.onPlayerChange(activePlayer);
		}
	}
	
	public boolean goPrevious() {
		if (progression > 0) {
			playMoveWithoutLog(moves.get(--progression), false);
			return true;
		}
		return false;
	}
	
	public boolean goNext() {
		if (progression < moves.size()) {
			playMoveWithoutLog(moves.get(progression++));
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
		dest.writeSerializable(activePlayer);
		dest.writeInt(progression);
		// TODO : parcel moves
		// dest.writeList(moves);
	}
	
}
