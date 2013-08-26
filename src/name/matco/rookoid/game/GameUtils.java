package name.matco.rookoid.game;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;

public class GameUtils {
	
	public static final int CHESSBOARD_SIZE = 8;
	
	public static int coordinateToIndex(Coordinate coordinate) {
		return coordinate.y * CHESSBOARD_SIZE + coordinate.x;
	}
	
	public static int coordinateToIndex(int x, int y) {
		return y * CHESSBOARD_SIZE + x;
	}
	
	public static Coordinate indexTocoordinate(int index) throws OutOfBoardCoordinateException {
		return new Coordinate(index % CHESSBOARD_SIZE, index / CHESSBOARD_SIZE);
	}
}
