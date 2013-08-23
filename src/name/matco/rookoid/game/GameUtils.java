package name.matco.rookoid.game;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;

public class GameUtils {
	
	public static final int CHESSBOARD_SIZE = 8;
	
	public static List<Movement> LINE_MOVEMENTS = new ArrayList<Movement>();
	static {
		for (int i = 0; i < CHESSBOARD_SIZE; i++) {
			LINE_MOVEMENTS.add(new Movement(-i, 0));
			LINE_MOVEMENTS.add(new Movement(i, 0));
			LINE_MOVEMENTS.add(new Movement(0, -i));
			LINE_MOVEMENTS.add(new Movement(0, i));
		}
	}
	
	public static List<Movement> DIAGONALE_MOVEMENTS = new ArrayList<Movement>();
	static {
		for (int i = 0; i < CHESSBOARD_SIZE; i++) {
			DIAGONALE_MOVEMENTS.add(new Movement(i, i));
			DIAGONALE_MOVEMENTS.add(new Movement(-i, i));
			DIAGONALE_MOVEMENTS.add(new Movement(i, -i));
			DIAGONALE_MOVEMENTS.add(new Movement(-i, -i));
		}
	}
	
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
