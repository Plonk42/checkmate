package name.matco.checkmate.game;

import java.util.ArrayList;
import java.util.Collections;
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
	
	private Move lastMove;
	
	// listeners
	private final Set<CaptureListener> captureListeners = new HashSet<CaptureListener>();
	
	public Board(final Parcel in) {
		in.readList(this.pieces, null);
		in.readList(this.capturedPieces, null);
		
		whiteKing = getPieces(Player.WHITE, PieceType.KING).get(0);
		blackKing = getPieces(Player.BLACK, PieceType.KING).get(0);
	}
	
	public Board() {
		capturedPieces.clear();
		
		// init squares
		for (int i = 0; i < 64; i++) {
			try {
				squares[i] = new Square(this, i % 8, i / 8);
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
	
	public Board(final Board board) {
		this();
		
		for (final Piece piece : board.getCapturedPieces()) {
			piece.getSquare().setPiece(null);
			piece.setSquare(null);
			pieces.remove(piece);
			capturedPieces.add(piece);
		}
		
		// improve this (give ids to pieces)
		final List<Piece> managedPieces = new ArrayList<Piece>();
		
		for (final Piece piece : board.getPieces()) {
			for (final Piece p : getPieces(piece.getPlayer(), piece.getType())) {
				if (!managedPieces.contains(p)) {
					p.getSquare().setPiece(null);
					final Square square = getSquareAt(piece.getSquare().getCoordinate());
					square.setPiece(p);
					p.setSquare(square);
					managedPieces.add(p);
				}
			}
		}
	}
	
	public Square[] getSquares() {
		return squares;
	}
	
	public List<Piece> getPieces() {
		return pieces;
	}
	
	public Move getLastMove() {
		return lastMove;
	}
	
	public void setLastMove(final Move lastMove) {
		this.lastMove = lastMove;
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
	
	public Square getSquareAt(final Coordinate coordinate) {
		return squares[GameUtils.coordinateToIndex(coordinate)];
	}
	
	public Square getSquareAt(final int x, final int y) throws OutOfBoardCoordinateException {
		return getSquareAt(new Coordinate(x, y));
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
	
	private void addPiece(final int index, final Piece piece) {
		final Square square = squares[index];
		square.setPiece(piece);
		piece.setSquare(square);
		pieces.add(piece);
	}
	
	public void movePiece(final Piece p, final Square to) {
		p.getSquare().setPiece(null);
		to.setPiece(p);
		p.setSquare(to);
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
			final Square backEastSquare = king.getSquare().apply(player.getOpponent().getForward().getMovement().withAdd(Direction.EAST.getMovement()));
			if (backEastSquare.getPiece() != null && backEastSquare.getPiece().is(PieceType.PAWN, player.getOpponent())) {
				return true;
			}
		} catch (final OutOfBoardCoordinateException e1) {
			// outside the squares
		}
		try {
			final Square backWestSquare = king.getSquare().apply(player.getOpponent().getForward().getMovement().withAdd(Direction.WEST.getMovement()));
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
					if (s.getPiece() != null && s.getPiece().is(player.getOpponent()) && s.getPiece().is(PieceType.KNIGHT)) {
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
	}
}
