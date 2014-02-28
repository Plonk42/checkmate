package name.matco.checkmate.game;

import name.matco.checkmate.game.exception.InvalidAlgebraic;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;

public class GameUtils {
	
	public final static int CHESSBOARD_SIZE = 8;
	
	public final static int checkIndex(final int index) throws OutOfBoardCoordinateException {
		if (index < 0 || index >= CHESSBOARD_SIZE * CHESSBOARD_SIZE) {
			throw new OutOfBoardCoordinateException(index);
		}
		return index;
	}
	
	public final static int coordinateToIndex(final int x, final int y) throws OutOfBoardCoordinateException {
		if (x < 0 || x >= CHESSBOARD_SIZE || y < 0 || y >= CHESSBOARD_SIZE) {
			throw new OutOfBoardCoordinateException(x, y);
		}
		return y * CHESSBOARD_SIZE + x;
	}
	
	public final static int apply(final int index, final Movement m) throws OutOfBoardCoordinateException {
		return checkIndex(index + m.dy * CHESSBOARD_SIZE + m.dx);
	}
	
	public final static Movement getMovement(final int from, final int to) {
		final int fromX = indexToX(from);
		final int fromY = indexToY(from);
		final int toX = indexToX(to);
		final int toY = indexToY(to);
		return new Movement(toX - fromX, toY - fromY);
	}
	
	public final static int indexFromAlgebraic(final CharSequence algebraic) throws InvalidAlgebraic {
		final char x = algebraic.charAt(0);
		final char y = algebraic.charAt(1);
		try {
			return coordinateToIndex(x - 'a', y - '1');
		} catch (final OutOfBoardCoordinateException e) {
			throw new InvalidAlgebraic(algebraic, e);
		}
	}
	
	public final static String algebraicFromIndex(final int index) {
		return String.format("%s%s", 'a' + indexToX(index), '1' + indexToY(index));
	}
	
	public final static int indexToX(final int index) {
		return index % 8;
	}
	
	public final static int indexToY(final int index) {
		return index / 8;
	}
	
	public static boolean onSameFile(final Square s1, final Square s2) {
		return indexToX(s1.getIndex()) == indexToX(s2.getIndex());
	}
	
	public static boolean onSameRank(final Square s1, final Square s2) {
		return indexToY(s1.getIndex()) == indexToY(s2.getIndex());
	}
	
}
