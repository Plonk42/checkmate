package name.matco.rookoid.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;
import name.matco.rookoid.game.piece.Bishop;
import name.matco.rookoid.game.piece.King;
import name.matco.rookoid.game.piece.Knight;
import name.matco.rookoid.game.piece.Pawn;
import name.matco.rookoid.game.piece.Piece;
import name.matco.rookoid.game.piece.PieceType;
import name.matco.rookoid.game.piece.Queen;
import name.matco.rookoid.game.piece.Rook;
import android.util.Log;

public class Game {
	
	private final Set<MovementListener> movementListeners = new HashSet<MovementListener>();
	private final Set<CheckListener> checkListeners = new HashSet<CheckListener>();
	
	private final Square[] board = new Square[64];
	private final List<Piece> pieces = Collections.synchronizedList(new ArrayList<Piece>());
	private final List<Piece> capturedPieces = Collections.synchronizedList(new ArrayList<Piece>());
	private List<Move> moves = new ArrayList<Move>();
	private int progression;
	
	private Player activePlayer;
	
	private Piece whiteKing;
	private Piece blackKing;
	
	private final Map<Player, Integer> timers = new TreeMap<Player, Integer>();
	private long lastMoveTime;
	
	public Game() {
		init();
	}
	
	private void init() {
		activePlayer = Player.WHITE;
		moves.clear();
		capturedPieces.clear();
		progression = 0;
		
		// manage timers
		for (final Player player : Player.values()) {
			timers.put(player, 0);
		}
		lastMoveTime = System.currentTimeMillis();
		
		for (int i = 0; i < 64; i++) {
			try {
				board[i] = new Square(this, i % 8, i / 8);
			} catch (final OutOfBoardCoordinateException e) {
				// cannot be raised here
			}
		}
		
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
	}
	
	public int getProgression() {
		return progression;
	}
	
	public List<Move> getMoves() {
		return moves;
	}
	
	public long getLastMoveTime() {
		return lastMoveTime;
	}
	
	public Map<Player, Integer> getTimers() {
		return timers;
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
	
	public void addPiece(final int index, final Piece piece) {
		final Square place = board[index];
		place.setPiece(piece);
		piece.setSquare(place);
		pieces.add(piece);
	}
	
	public List<Piece> getPieces() {
		return pieces;
	}
	
	public List<Piece> getCapturedPieces() {
		return capturedPieces;
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
			// FIXME : choose piece type
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
		
		// manage timer
		final long now = System.currentTimeMillis();
		timers.put(activePlayer, (int) (timers.get(activePlayer) + now - lastMoveTime));
		lastMoveTime = now;
		
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
		activePlayer = activePlayer.getOpponent();
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
		final Piece king = Player.WHITE.equals(player) ? whiteKing : blackKing;
		// check lines
		for (final List<Movement> directions : Movement.LINE_MOVEMENTS) {
			for (final Movement m : directions) {
				try {
					final Square s = king.getSquare().apply(m);
					if (s.getPiece() != null) {
						if (s.getPiece().is(player.getOpponent()) && (s.getPiece().is(PieceType.ROOK) || s.getPiece().is(PieceType.QUEEN))) {
							return true;
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
			boolean first = true;
			for (final Movement m : directions) {
				try {
					final Square s = king.getSquare().apply(m);
					if (s.getPiece() != null) {
						if (s.getPiece().is(player.getOpponent())) {
							// FIXME : wrong: a pawn only captures in front of itself (only 2 directions among 4)
							if (first && s.getPiece().is(PieceType.PAWN) || s.getPiece().is(PieceType.BISHOP) || s.getPiece().is(PieceType.QUEEN)) {
								return true;
							}
						}
						break;
					}
				} catch (final OutOfBoardCoordinateException e) {
					// outside the board; stop going in this direction
					break;
				}
				first = false;
			}
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
	
}
