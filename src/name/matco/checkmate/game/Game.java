package name.matco.checkmate.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import name.matco.checkmate.game.Movement.Direction;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Bishop;
import name.matco.checkmate.game.piece.King;
import name.matco.checkmate.game.piece.Knight;
import name.matco.checkmate.game.piece.Pawn;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import name.matco.checkmate.game.piece.Queen;
import name.matco.checkmate.game.piece.Rook;
import name.matco.checkmate.ui.listeners.CaptureListener;
import name.matco.checkmate.ui.listeners.GameListener;
import name.matco.checkmate.ui.listeners.GameStateListener;
import name.matco.checkmate.ui.listeners.MovementListener;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Game implements Parcelable {
	
	// TODO : choose a better key?
	public static final String PARCELABLE_KEY = Game.class.getName();
	
	private final Set<GameStateListener> gameStateListeners = new HashSet<GameStateListener>();
	private final Set<MovementListener> movementListeners = new HashSet<MovementListener>();
	private final Set<CheckListener> checkListeners = new HashSet<CheckListener>();
	private final Set<CaptureListener> captureListeners = new HashSet<CaptureListener>();
	
	private Date _startDate;
	private Date _endDate;
	
	private final Square[] board = new Square[64];
	private final List<Piece> pieces = Collections.synchronizedList(new ArrayList<Piece>());
	private final List<Piece> capturedPieces = Collections.synchronizedList(new ArrayList<Piece>());
	private List<Move> moves = new ArrayList<Move>();
	private int progression = 0;
	
	private Player activePlayer;
	
	private Piece whiteKing;
	private Piece blackKing;
	
	// no-arg constructor
	public Game() {
		Log.i(getClass().getName(), "Instantiating new Game()");
		init();
	}
	
	public Game(final Parcel in) {
		Log.i(getClass().getName(), "Restoring Game() from Parcel");
		in.readList(this.pieces, null);
		in.readList(this.capturedPieces, null);
		this.activePlayer = (Player) in.readSerializable();
		this.progression = in.readInt();
		// TODO : parcel moves;
		// in.readList(this.moves, null);
		for (int i = 0; i < progression; i++) {
			moves.add(new Move(this, null, null, null));
		}
		
		// for (final Piece p :this.pieces) {
		// getSquareAt(p.getSquare().getCoordinate());
		// this.pieces.add(p);
		// }
		
		// TODO : whiteKing && BlackKing
	}
	
	private void init() {
		moves.clear();
		capturedPieces.clear();
		progression = 0;
		
		for (int i = 0; i < 64; i++) {
			try {
				board[i] = new Square(this, i % 8, i / 8);
			} catch (final OutOfBoardCoordinateException e) {
				// cannot be raised here
			}
		}
		
		initPieces();
	}
	
	private void initPieces() {
		// white player
		whiteKing = new King(Player.WHITE);
		addPiece(0, new Rook(Player.WHITE));
		addPiece(1, new Knight(Player.WHITE));
		addPiece(2, new Bishop(Player.WHITE));
		addPiece(3, new Queen(Player.WHITE));
		addPiece(4, whiteKing);
		addPiece(5, new Bishop(Player.WHITE));
		addPiece(6, new Knight(Player.WHITE));
		addPiece(7, new Rook(Player.WHITE));
		
		for (int i = 8; i < 16; i++) {
			addPiece(i, new Pawn(Player.WHITE));
		}
		
		// black player
		blackKing = new King(Player.BLACK);
		addPiece(63 - 0, new Rook(Player.BLACK));
		addPiece(63 - 1, new Knight(Player.BLACK));
		addPiece(63 - 2, new Bishop(Player.BLACK));
		addPiece(63 - 3, blackKing);
		addPiece(63 - 4, new Queen(Player.BLACK));
		addPiece(63 - 5, new Bishop(Player.BLACK));
		addPiece(63 - 6, new Knight(Player.BLACK));
		addPiece(63 - 7, new Rook(Player.BLACK));
		
		for (int i = 8; i < 16; i++) {
			addPiece(63 - i, new Pawn(Player.BLACK));
		}
		
		_startDate = new Date();
		
		for (final GameStateListener gl : gameStateListeners) {
			gl.onGameInit();
		}
		
		setPlayer(Player.WHITE);
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
	
	public Square getSquareAt(final Coordinate coordinate) {
		return board[GameUtils.coordinateToIndex(coordinate)];
	}
	
	public Square getSquareAt(final int x, final int y) throws OutOfBoardCoordinateException {
		return getSquareAt(new Coordinate(x, y));
	}
	
	public Square[] getBoard() {
		return board;
	}
	
	private void addPiece(final int index, final Piece piece) {
		final Square place = board[index];
		place.setPiece(piece);
		piece.setSquare(place);
		pieces.add(piece);
	}
	
	public List<Piece> getPieces() {
		return pieces;
	}
	
	public List<Piece> getPieces(final Player player, final PieceType type) {
		final List<Piece> pieces = new ArrayList<Piece>();
		for (final Piece piece : this.pieces) {
			if (piece.is(type, player)) {
				pieces.add(piece);
			}
		}
		return pieces;
	}
	
	public List<Piece> getCapturedPieces() {
		return Collections.unmodifiableList(capturedPieces);
	}
	
	public void addCapturedPiece(final Piece piece) {
		capturedPieces.add(piece);
		for (final CaptureListener cl : captureListeners) {
			cl.onCapture(piece);
		}
	}
	
	public void removeCapturedPiece(final Piece piece) {
		capturedPieces.remove(piece);
		for (final CaptureListener cl : captureListeners) {
			cl.onRelease(piece);
		}
	}
	
	public Move getMove(final Piece p, final Square to) {
		Log.d(getClass().getName(), String.format("Retrieve move for %s - has moved: %s, to is castling destination: %s, to is empty: %s", p, p.hasMoved(), to.isCastlingDestination(getActivePlayer()), to.isEmpty()));
		final Move m;
		if (p.is(PieceType.KING) && !p.hasMoved() && to.isCastlingDestination(getActivePlayer())) {
			m = new Castling(this, getActivePlayer(), (King) p, to);
		} else if (p.is(PieceType.PAWN) && to.isEmpty() && (p.getSquare().getCoordinate().x != to.getCoordinate().x)) {
			try {
				m = new EnPassant(this, getActivePlayer(), (Pawn) p, to);
			} catch (final OutOfBoardCoordinateException e) {
				// no move could have been done outside board
				Log.e(getClass().getName(), "Move is outside board", e);
				return null;
			}
		} else if (p.is(PieceType.PAWN) && to.isPromotionDestination(getActivePlayer())) {
			m = new Promotion(this, getActivePlayer(), (Pawn) p, to);
		} else {
			m = new Move(this, getActivePlayer(), p, to);
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
		if (isCheck(getActivePlayer())) {
			Log.i(getClass().getName(), String.format("Check"));
			if (isCheckmate(getActivePlayer())) {
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
		for (final MovementListener mv : movementListeners) {
			mv.onMovement(m, way);
		}
		
		// change active player
		nextPlayer();
	}
	
	private void playMoveWithoutLog(final Move m) {
		playMoveWithoutLog(m, true);
	}
	
	public void movePiece(final Piece p, final Square to) {
		p.getSquare().setPiece(null); // TODO : keep this here?
		to.setPiece(p);
		p.setSquare(to);
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
	
	// improve this by describing which piece can do a kind of movement
	public boolean isCheck(final Player player) {
		Log.i(getClass().getName(), String.format("Check if player %s is in check", player));
		final Piece king = Player.WHITE.equals(player) ? whiteKing : blackKing;
		// check lines
		for (final List<Movement> directions : Movement.LINE_MOVEMENTS) {
			for (final Movement m : directions) {
				try {
					final Square s = king.getSquare().apply(m);
					if (s.getPiece() != null) {
						if (s.getPiece().is(player.getOpponent())) {
							if (s.getPiece().is(PieceType.ROOK) || s.getPiece().is(PieceType.QUEEN)) {
								return true;
							}
						}
						break;
					}
				} catch (final OutOfBoardCoordinateException e) {
					// outside the board; stop going in this direction
					break;
				}
			}
		}
		// check diagonals
		for (final List<Movement> directions : Movement.DIAGONAL_MOVEMENTS) {
			for (final Movement m : directions) {
				try {
					final Square s = king.getSquare().apply(m);
					if (s.getPiece() != null) {
						if (s.getPiece().is(player.getOpponent())) {
							if (s.getPiece().is(PieceType.BISHOP) || s.getPiece().is(PieceType.QUEEN)) {
								return true;
							}
						}
						break;
					}
				} catch (final OutOfBoardCoordinateException e) {
					// outside the board; stop going in this direction
					break;
				}
			}
		}
		// check paws
		try {
			final Square backEastSquare = king.getSquare().apply(player.getOpponent().getForward().getMovement().withAdd(Direction.EAST.getMovement()));
			if (backEastSquare.getPiece() != null && backEastSquare.getPiece().is(PieceType.PAWN, player.getOpponent())) {
				return true;
			}
		} catch (final OutOfBoardCoordinateException e1) {
			// outside the board
		}
		try {
			final Square backWestSquare = king.getSquare().apply(player.getOpponent().getForward().getMovement().withAdd(Direction.WEST.getMovement()));
			if (backWestSquare.getPiece() != null && backWestSquare.getPiece().is(PieceType.PAWN, player.getOpponent())) {
				return true;
			}
		} catch (final OutOfBoardCoordinateException e1) {
			// outside the board
		}
		
		// check knights
		for (final List<Movement> directions : Movement.KNIGHT_MOVEMENTS) {
			for (final Movement m : directions) {
				try {
					final Square s = king.getSquare().apply(m);
					if (s.getPiece() != null && s.getPiece().is(player.getOpponent()) && s.getPiece().is(PieceType.KNIGHT)) {
						return true;
					}
				} catch (final OutOfBoardCoordinateException e) {
					// outside the board; stop going in this direction
				}
			}
		}
		return false;
	}
	
	public boolean isCheckmate(final Player player) {
		if (!isCheck(player)) {
			return false;
		}
		// check if any movement of any piece can save player
		for (final Piece piece : pieces) {
			if (player.equals(piece.getPlayer()) && !capturedPieces.contains(piece)) {
				final Square originalPieceSquare = piece.getSquare();
				for (final Square square : piece.getAllowedPositions()) {
					// apply movement
					// FIXME : that's dangerous and that's ugly
					final Piece capturedPiece = square.getPiece();
					movePiece(piece, square);
					
					final boolean isCheck = isCheck(player);
					
					// revert back to original position
					movePiece(piece, originalPieceSquare);
					if (capturedPiece != null) {
						movePiece(capturedPiece, square);
					}
					
					if (!isCheck) {
						Log.i(getClass().getName(), String.format("Can move piece %s to %s to escape check", piece, square));
						return false;
					}
				}
			}
		}
		return true;
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
	
	public void addCaptureListener(final CaptureListener cl) {
		captureListeners.add(cl);
	}
	
	public void removeCaptureListener(final CaptureListener cl) {
		captureListeners.remove(cl);
	}
	
	// parcelable implementation
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeList(pieces);
		dest.writeList(capturedPieces);
		dest.writeSerializable(activePlayer);
		dest.writeInt(progression);
		// TODO : parcel moves
		// dest.writeList(moves);
	}
	
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
	
}
