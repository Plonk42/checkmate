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
	
	private final Square[] board = new Square[64];
	private final List<Piece> pieces = Collections.synchronizedList(new ArrayList<Piece>());
	private final List<Piece> capturedPieces = Collections.synchronizedList(new ArrayList<Piece>());
	private List<Move> moves = new ArrayList<Move>();
	private int progression = 0;
	
	private Player activePlayer = Player.WHITE;
	
	private final Piece whiteKing = new King(Player.WHITE);
	private final Piece blackKing = new King(Player.BLACK);
	
	private final Map<Player, Integer> timers = new TreeMap<Player, Integer>();
	
	// singleton
	private static Game instance;
	
	private Game() {
		init();
	}
	
	public static synchronized Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}
	
	private void init() {
		activePlayer = Player.WHITE;
		moves.clear();
		capturedPieces.clear();
		progression = 0;
		
		for (final Player player : Player.values()) {
			timers.put(player, 0);
		}
		
		for (int i = 0; i < 64; i++) {
			try {
				board[i] = new Square(this, i % 8, i / 8);
			} catch (final OutOfBoardCoordinateException e) {
				// cannot be raised here
			}
		}
		
		// white player
		addPiece(0, new Rook(Player.WHITE));
		addPiece(1, new Knight(Player.WHITE));
		addPiece(2, new Bishop(Player.WHITE));
		addPiece(3, whiteKing);
		addPiece(4, new Queen(Player.WHITE));
		addPiece(5, new Bishop(Player.WHITE));
		addPiece(6, new Knight(Player.WHITE));
		addPiece(7, new Rook(Player.WHITE));
		
		for (int i = 8; i < 16; i++) {
			addPiece(i, new Pawn(Player.WHITE));
		}
		
		// black player
		addPiece(63 - 0, new Rook(Player.BLACK));
		addPiece(63 - 1, new Knight(Player.BLACK));
		addPiece(63 - 2, new Bishop(Player.BLACK));
		addPiece(63 - 3, new Queen(Player.BLACK));
		addPiece(63 - 4, blackKing);
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
	
	public Move getLastMove() {
		return moves.size() > 0 ? moves.get(moves.size() - 1) : null;
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
	
	public List<Piece> getCapturedPieces() {
		return capturedPieces;
	}
	
	public void movePieceTo(final Piece p, final Square s) {
		// keep log of movement
		final Move m = new Move(p, p.getSquare().getMovementTo(s), s.getPiece());
		Log.i(getClass().getName(), String.format("Add move %s", m));
		if (progression < moves.size()) {
			moves = moves.subList(0, progression);
			progression = moves.size();
		}
		moves.add(m);
		progression++;
		
		movePieceToWithoutLog(p, s);
	}
	
	/**
	 * @param p piece to move
	 * @param s square where the square will be
	 */
	private void movePieceToWithoutLog(final Piece p, final Square s) {
		// change active player
		activePlayer = activePlayer.getOpponent();
		
		if (s.getPiece() != null) {
			capturedPieces.add(s.getPiece());
		}
		
		final Square from = p.getSquare();
		
		movePieceToInternal(p, s);
		
		for (final MovementListener mv : movementListeners) {
			mv.onMovement(p, from, s);
		}
	}
	
	public void movePieceToInternal(final Piece p, final Square s) {
		// move piece
		p.getSquare().setPiece(null);
		s.setPiece(p);
		p.setSquare(s);
	}
	
	public Player getActivePlayer() {
		return activePlayer;
	}
	
	public boolean goPrevious() {
		if (progression > 0) {
			final Move m = moves.get(--progression);
			try {
				Log.i(getClass().getName(), String.format("Moving piece %s back using movement %s", m.getPiece(), m.getMovement().withInversion()));
				final Square from = m.getPiece().getSquare();
				movePieceToWithoutLog(m.getPiece(), from.apply(m.getMovement().withInversion()));
				from.setPiece(m.getCapturedPiece());
				capturedPieces.remove(m.getCapturedPiece());
			} catch (final OutOfBoardCoordinateException e) {
				// no move could have been done outside board
			}
			return true;
		}
		return false;
	}
	
	public boolean goNext() {
		if (progression < moves.size()) {
			final Move m = moves.get(progression++);
			try {
				movePieceToWithoutLog(m.getPiece(), m.getPiece().getSquare().apply(m.getMovement()));
			} catch (final OutOfBoardCoordinateException e) {
				// no move could have been done outside board
			}
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
						if (s.getPiece().getPlayer().equals(player.getOpponent()) && (s.getPiece().getType().equals(PieceType.ROOK) || s.getPiece().getType().equals(PieceType.QUEEN))) {
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
		// check diagnoales
		for (final List<Movement> directions : Movement.DIAGONAL_MOVEMENTS) {
			boolean first = true;
			for (final Movement m : directions) {
				try {
					final Square s = king.getSquare().apply(m);
					if (s.getPiece() != null) {
						if (s.getPiece().getPlayer().equals(player.getOpponent())) {
							// wrong: a pawn only captures in front of itself (only 2 directions among 4)
							if (first && s.getPiece().getType().equals(PieceType.PAWN) || s.getPiece().getType().equals(PieceType.BISHOP) || s.getPiece().getType().equals(PieceType.QUEEN)) {
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
					if (s.getPiece() != null && s.getPiece().getPlayer().equals(player.getOpponent()) && s.getPiece().getType().equals(PieceType.KNIGHT)) {
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
					// oups that's dangerous and that's ugly
					final Piece capturedPiece = square.getPiece();
					movePieceToInternal(piece, square);
					
					final boolean isCheck = isCheck(player);
					
					// revert back to original position
					movePieceToInternal(piece, originalPieceSquare);
					if (capturedPiece != null) {
						movePieceToInternal(capturedPiece, square);
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
	
}
