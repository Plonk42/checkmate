package name.matco.rookoid.game;

import android.os.Parcel;
import android.os.Parcelable;

public class Coordinate implements Parcelable {
	
	public final int x;
	public final int y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public final Coordinate apply(final Movement m) {
		return new Coordinate(x + m.dx, y + m.dy);
	}
	
	@Override
	public String toString() {
		return String.format("(%d,%d)", x, y);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(x);
		dest.writeInt(y);
	}
	
	public static final Parcelable.Creator<Coordinate> CREATOR = new Parcelable.Creator<Coordinate>() {
		@Override
		public Coordinate createFromParcel(Parcel in) {
			return new Coordinate(in.readInt(), in.readInt());
		}
		
		@Override
		public Coordinate[] newArray(int size) {
			return new Coordinate[size];
		}
	};
	
}
