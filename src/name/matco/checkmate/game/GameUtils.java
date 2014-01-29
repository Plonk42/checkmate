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
	
	public final static int indexFromAlgebraic(final CharSequence algebraic) throws InvalidAlgebraic {
		final char x = algebraic.charAt(0);
		final char y = algebraic.charAt(1);
		try {
			return coordinateToIndex(x - 'a', y - '1');
		} catch (final OutOfBoardCoordinateException e) {
			throw new InvalidAlgebraic(algebraic, e);
		}
	}
	
}
