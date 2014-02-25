package name.matco.checkmate.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import name.matco.checkmate.game.Movement.Direction;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import name.matco.checkmate.ui.listeners.CaptureListener;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Board implements Parcelable {
	
	public static final String PARCELABLE_KEY = Board.class.getName();
	
	public static final Parcelable.Creator<Board> CREATOR = new Parcelable.Creator<Board>() {
		@Override
		public Board createFromParcel(final Parcel in) {
			return new Board(in);
		}
		
		@Override
		public Board[] newArray(final int size) {
			return new Board[size];
		}
	};
	
	private final Square[] squares = new Square[64];
	
	private final List<Piece> pieces = Collections.synchronizedList(new ArrayList<Piece>());
	private final List<Piece> capturedPieces = Collections.synchronizedList(new ArrayList<Piece>());
	
	private final Piece whiteKing;
	private final Piece blackKing;
	
	private final List<Move> moves = Collections.synchronizedList(new ArrayList<Move>());
	
	// listeners
	private final Set<CaptureListener> captureListeners = new HashSet<CaptureListener>();
	
	public Board(final Parcel in) {
		in.readList(this.pieces, null);
		in.readList(this.capturedPieces, null);
		in.readList(this.moves, null);
		
		whiteKing = getPieces(Player.WHITE, PieceType.KING).get(0);
		blackKing = getPieces(Player.BLACK, PieceType.KING).get(0);
	}
	
	public Board() {
		capturedPieces.clear();
		moves.clear();
		
		// init squares
		for (int i = 0; i < 64; i++) {
			try {
				squares[i] = new Square(this, i);
			} catch (final OutOfBoardCoordinateException e) {
				// cannot be raised here
			}
		}
		
		// white player
		addPiece(0, new Piece(this, 0, PieceType.ROOK, Player.WHITE));
		addPiece(1, new Piece(this, 1, PieceType.KNIGHT, Player.WHITE));
		addPiece(2, new Piece(this, 2, PieceType.BISHOP, Player.WHITE));
		addPiece(3, new Piece(this, 3, PieceType.QUEEN, Player.WHITE));
		whiteKing = new Piece(this, 4, PieceType.KING, Player.WHITE);
		addPiece(4, whiteKing);
		addPiece(5, new Piece(this, 5, PieceType.BISHOP, Player.WHITE));
		addPiece(6, new Piece(this, 6, PieceType.KNIGHT, Player.WHITE));
		addPiece(7, new Piece(this, 7, PieceType.ROOK, Player.WHITE));
		
		for (int i = 8; i < 16; i++) {
			addPiece(i, new Piece(this, i, PieceType.PAWN, Player.WHITE));
		}
		
		// black player
		addPiece(63 - 0, new Piece(this, 63 - 0, PieceType.ROOK, Player.BLACK));
		addPiece(63 - 1, new Piece(this, 63 - 1, PieceType.KNIGHT, Player.BLACK));
		addPiece(63 - 2, new Piece(this, 63 - 2, PieceType.BISHOP, Player.BLACK));
		blackKing = new Piece(this, 63 - 3, PieceType.KING, Player.BLACK);
		addPiece(63 - 3, blackKing);
		addPiece(63 - 4, new Piece(this, 63 - 4, PieceType.QUEEN, Player.BLACK));
		addPiece(63 - 5, new Piece(this, 63 - 5, PieceType.BISHOP, Player.BLACK));
		addPiece(63 - 6, new Piece(this, 63 - 6, PieceType.KNIGHT, Player.BLACK));
		addPiece(63 - 7, new Piece(this, 63 - 7, PieceType.ROOK, Player.BLACK));
		
		for (int i = 8; i < 16; i++) {
			addPiece(63 - i, new Piece(this, 63 - i, PieceType.PAWN, Player.BLACK));
		}
	}
	
	public Board(final Board board) {
		this();
		
		for (final Piece piece : board.getCapturedPieces()) {
			final Piece p = getPiece(piece.getId());
			p.getSquare().setPiece(null);
			p.setSquare(null);
			pieces.remove(piece);
			capturedPieces.add(piece);
		}
		
		for (final Piece piece : board.getPieces()) {
			final Piece p = getPiece(piece.getId());
			p.setHasMoved(piece.hasMoved());
			// remove piece from initial square
			p.getSquare().setPiece(null);
			// put piece on the right square
			final Square square = getSquareAt(piece.getSquare().getIndex());
			square.setPiece(p);
			p.setSquare(square);
		}
		
		moves.addAll(board.getMoves());
	}
	
	public Square[] getSquares() {
		return squares;
	}
	
	public List<Piece> getPieces() {
		return pieces;
	}
	
	public List<Move> getMoves() {
		return moves;
	}
	
	public Move getLastMove() {
		return moves.isEmpty() ? null : moves.get(moves.size() - 1);
	}
	
	public final Piece getWhiteKing() {
		return whiteKing;
	}
	
	public final Piece getBlackKing() {
		return blackKing;
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
	
	// TODO : check index bounds?
	// public Square getSquareAt(final int index) throws OutOfBoardCoordinateException {
	// return squares[GameUtils.checkIndex(index)];
	// }
	public Square getSquareAt(final int index) {
		return squares[index];
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
	
	public Piece getPiece(final int id) {
		for (final Piece piece : this.pieces) {
			if (piece.getId() == id) {
				return piece;
			}
		}
		throw new UnsupportedOperationException();
	}
	
	private void addPiece(final int index, final Piece piece) {
		final Square square = squares[index];
		square.setPiece(piece);
		piece.setSquare(square);
		pieces.add(piece);
	}
	
	public void movePiece(final Piece p, final int index) {
		p.getSquare().setPiece(null);
		getSquareAt(index).setPiece(p);
		p.setSquare(getSquareAt(index));
	}
	
	// improve this by describing which piece can do what kind of movement
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
					// outside the squares; stop going in this direction
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
					// outside the squares; stop going in this direction
					break;
				}
			}
		}
		// check paws
		try {
			final Square backEastSquare = king.getSquare().apply(player.getForward().getMovement().withAdd(Direction.EAST.getMovement()));
			if (backEastSquare.getPiece() != null && backEastSquare.getPiece().is(PieceType.PAWN, player.getOpponent())) {
				return true;
			}
		} catch (final OutOfBoardCoordinateException e1) {
			// outside the squares
		}
		try {
			final Square backWestSquare = king.getSquare().apply(player.getForward().getMovement().withAdd(Direction.WEST.getMovement()));
			if (backWestSquare.getPiece() != null && backWestSquare.getPiece().is(PieceType.PAWN, player.getOpponent())) {
				return true;
			}
		} catch (final OutOfBoardCoordinateException e1) {
			// outside the squares
		}
		
		// check knights
		for (final List<Movement> directions : Movement.KNIGHT_MOVEMENTS) {
			for (final Movement m : directions) {
				try {
					final Square s = king.getSquare().apply(m);
					if (s.getPiece() != null && s.getPiece().is(PieceType.KNIGHT, player.getOpponent())) {
						return true;
					}
				} catch (final OutOfBoardCoordinateException e) {
					// outside the squares; stop going in this direction
				}
			}
		}
		
		// check kings
		for (final List<Movement> directions : Movement.KING_MOVEMENTS) {
			for (final Movement m : directions) {
				try {
					final Square s = king.getSquare().apply(m);
					if (s.getPiece() != null && s.getPiece().is(PieceType.KING, player.getOpponent())) {
						return true;
					}
				} catch (final OutOfBoardCoordinateException e) {
					// outside the squares; stop going in this direction
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
					movePiece(piece, square.getIndex());
					
					final boolean isCheck = isCheck(player);
					
					// revert back to original position
					movePiece(piece, originalPieceSquare.getIndex());
					if (capturedPiece != null) {
						movePiece(capturedPiece, square.getIndex());
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
	
	public void addCaptureListener(final CaptureListener cl) {
		captureListeners.add(cl);
	}
	
	public void removeCaptureListener(final CaptureListener cl) {
		captureListeners.remove(cl);
	}
	
	// parcellable
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeList(pieces);
		dest.writeList(capturedPieces);
		dest.writeList(moves);
	}
}
