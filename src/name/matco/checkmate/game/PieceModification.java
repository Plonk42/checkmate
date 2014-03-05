package name.matco.checkmate.game;

import name.matco.checkmate.game.piece.PieceType;
import android.os.Parcel;
import android.os.Parcelable;

public class PieceModification implements Parcelable {
	
	// TODO : choose a better key?
	public static final String PARCELABLE_KEY = PieceModification.class.getName();
	
	public static final Parcelable.Creator<PieceModification> CREATOR = new Parcelable.Creator<PieceModification>() {
		@Override
		public PieceModification createFromParcel(final Parcel in) {
			return new PieceModification(in);
		}
		
		@Override
		public PieceModification[] newArray(final int size) {
			return new PieceModification[size];
		}
	};
	
	private final int pieceId;
	private final int from;
	private final Integer to;
	private final PieceType newType;
	
	public PieceModification(final int pieceId, final int from, final Integer to, final PieceType newType) {
		this.pieceId = pieceId;
		this.from = from;
		this.to = to;
		this.newType = newType;
	}
	
	public PieceModification(final Parcel in) {
		pieceId = in.readInt();
		from = in.readInt();
		to = in.readInt();
		newType = PieceType.valueOf(in.readString());
	}
	
	public int getPieceId() {
		return pieceId;
	}
	
	public int getFrom() {
		return from;
	}
	
	public Integer getTo() {
		return to;
	}
	
	public PieceType getNewType() {
		return newType;
	}
	
	public Movement getMovement() {
		return GameUtils.getMovement(from, to);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(pieceId);
		dest.writeInt(from);
		dest.writeInt(to);
		dest.writeString(newType.name());
	}
	
}
