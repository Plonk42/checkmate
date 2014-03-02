package name.matco.checkmate.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.checkmate.game.Board;
import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Piece implements Parcelable {
	
	public static boolean is(final Piece piece, final Player player) {
		return piece.getPlayer().equals(player);
	}
	
	public static boolean is(final Piece piece, final PieceType type) {
		return piece.getType().equals(type);
	}
	
	public static boolean is(final Piece piece, final PieceType type, final Player player) {
		return is(piece, player) && is(piece, type);
	}
	
	public static final Parcelable.Creator<Piece> CREATOR = new Parcelable.Creator<Piece>() {
		@Override
		@SuppressWarnings("unchecked")
		public Piece createFromParcel(final Parcel in) {
			final Class<? extends Piece> _class = (Class<? extends Piece>) in.readSerializable();
			try {
				return _class.getConstructor(Parcel.class).newInstance(in);
			} catch (final Exception e) {
				Log.e(getClass().getName(), "Unable to load Piece from Parcel", e);
				return null;
			}
		}
		
		@Override
		public Piece[] newArray(final int size) {
			return new Piece[size];
		}
	};
	
	private Board board;
	private final int id;
	private PieceType type;
	private final Player player;
	private boolean hasMoved;
	
	public Piece(final Board board, final int id, final PieceType type, final Player player) {
		this.board = board;
		this.id = id;
		this.type = type;
		this.player = player;
	}
	
	public Piece(final Parcel parcel) {
		id = parcel.readInt();
		player = (Player) parcel.readSerializable();
		type = (PieceType) parcel.readSerializable();
		hasMoved = (parcel.readByte() == 1);
	}
	
	public Board getBoard() {
		return board;
	}
	
	public void setBoard(final Board board) {
		this.board = board;
	}
	
	public final Player getPlayer() {
		return player;
	}
	
	public final int getId() {
		return id;
	}
	
	public void setType(final PieceType type) {
		this.type = type;
	}
	
	public PieceType getType() {
		return type;
	}
	
	public final boolean hasMoved() {
		return hasMoved;
	}
	
	public final void setHasMoved(final boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	public final Square getSquare() {
		return board.getSquare(this);
	}
	
	public final int getImageResource() {
		return getType().getImageResource(getPlayer());
	}
	
	public final boolean is(final Player player) {
		return is(this, player);
	}
	
	public final boolean is(final PieceType type) {
		return is(this, type);
	}
	
	public final boolean is(final PieceType type, final Player player) {
		return is(this, type, player);
	}
	
	public List<Square> getAllowedPositions() {
		Log.i(getClass().getName(), String.format("Check allowed positions for piece %s", this));
		
		final Board clonedBoard = new Board(getBoard());
		
		final ArrayList<Square> allowed = new ArrayList<Square>();
		for (final List<Movement> directions : getType().getAllowedMovements(this)) {
			for (final Movement m : directions) {
				try {
					final Square candidate = getSquare().apply(m);
					final Piece p = candidate.getPiece();
					// square is empty
					if (p == null) {
						checkCheckAndAdd(clonedBoard, this, candidate, allowed);
					}
					// there is a piece on square
					else {
						// if piece is capturable, movement is possible only if it does not set player in check
						if (!p.is(getPlayer()) && !p.is(PieceType.KING)) {
							checkCheckAndAdd(clonedBoard, this, candidate, allowed);
						}
						// once a piece has been encountered, direction is blocked
						break;
					}
				} catch (final OutOfBoardCoordinateException e) {
					// outside the board; stop going in this direction
					break;
				}
			}
		}
		
		// retrieve squares in good board
		final ArrayList<Square> realAllowed = new ArrayList<Square>();
		for (final Square s : allowed) {
			realAllowed.add(getSquare().getBoard().getSquareAt(s.getIndex()));
		}
		return realAllowed;
	}
	
	// TODO this must not be in piece
	// ensure that moving this piece does not set the player in check
	private static void checkCheckAndAdd(final Board board, final Piece piece, final Square candidate, final List<Square> to) {
		// capture piece on candidate if any
		final Piece capturedPiece = candidate.getPiece();
		if (capturedPiece != null) {
			board.capturePiece(capturedPiece);
		}
		// keep a handle on current piece position
		final Square previousSquare = piece.getSquare();
		
		// move piece to candidate square
		board.movePiece(piece, candidate.getIndex());
		
		// check if this state is viable
		if (!board.isCheck(piece.getPlayer())) {
			to.add(candidate);
		}
		
		// restore state
		board.movePiece(piece, previousSquare.getIndex());
		if (capturedPiece != null) {
			board.movePiece(capturedPiece, candidate.getIndex());
		}
	}
	
	public String getDescription() {
		return player + " " + getType();
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(id);
		dest.writeSerializable(player);
		dest.writeByte((byte) (hasMoved ? 1 : 0));
	}
	
}
