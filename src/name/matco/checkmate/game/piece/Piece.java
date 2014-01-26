package name.matco.checkmate.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public abstract class Piece implements Parcelable {
	
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
	
	protected final int id;
	protected final Player player;
	protected Square square;
	protected boolean hasMoved;
	
	public Piece(final int id, final Player player) {
		this.id = id;
		this.player = player;
	}
	
	public Piece(final Parcel parcel) {
		id = parcel.readInt();
		player = (Player) parcel.readSerializable();
		hasMoved = (parcel.readByte() == 1);
	}
	
	public abstract PieceType getType();
	
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
		final ArrayList<Square> allowed = new ArrayList<Square>();
		for (final List<Movement> directions : getAllowedMovements()) {
			for (final Movement m : directions) {
				try {
					final Square candidate = square.apply(m);
					final Piece p = candidate.getPiece();
					// square is empty
					if (p == null) {
						checkCheckAndAdd(candidate, allowed);
					}
					// there is a piece on square
					else {
						// if piece is capturable, movement is possible only if it does not set player in check
						if (!p.is(getPlayer()) && !p.is(PieceType.KING)) {
							checkCheckAndAdd(candidate, allowed);
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
		
		return allowed;
	}
	
	private void checkCheckAndAdd(final Square candidate, final List<Square> to) {
		// ensure that moving this piece does not set the player in check
		final Square previousSquare = getSquare();
		final Piece previous = candidate.getPiece();
		getSquare().setPiece(null);
		// put piece on candidate
		candidate.setPiece(this);
		setSquare(candidate);
		if (!getSquare().getBoard().isCheck(getPlayer())) {
			to.add(candidate);
		}
		// restore state
		setSquare(previousSquare);
		previousSquare.setPiece(this);
		candidate.setPiece(previous);
	}
	
	public abstract List<List<Movement>> getAllowedMovements();
	
	public String getDescription() {
		return player + " " + getType();
	}
	
	public final boolean hasMoved() {
		return hasMoved;
	}
	
	public final void setHasMoved(final boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	public final Square getSquare() {
		return square;
	}
	
	public final void setSquare(final Square place) {
		this.square = place;
	}
	
	public final int getId() {
		return id;
	}
	
	public final Player getPlayer() {
		return player;
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
