package name.matco.rookoid.game;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Coordinate implements Parcelable {
	
	public final int x;
	public final int y;
	
	public Coordinate(int x, int y) throws OutOfBoardCoordinateException {
		if (x < 0 || x > GameUtils.CHESSBOARD_SIZE - 1 || y < 0 || y > GameUtils.CHESSBOARD_SIZE - 1) {
			throw new OutOfBoardCoordinateException(x, y);
		}
		this.x = x;
		this.y = y;
	}
	
	public final Coordinate apply(final Movement m) throws OutOfBoardCoordinateException {
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
			try {
				return new Coordinate(in.readInt(), in.readInt());
			} catch (OutOfBoardCoordinateException e) {
				Log.e(getClass().getName(), e.getMessage(), e);
				return null;
			}
		}
		
		@Override
		public Coordinate[] newArray(int size) {
			return new Coordinate[size];
		}
	};
	
}
