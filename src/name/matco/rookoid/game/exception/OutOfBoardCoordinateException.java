package name.matco.rookoid.game.exception;

@SuppressWarnings("serial")
public class OutOfBoardCoordinateException extends Exception {
	
	public OutOfBoardCoordinateException(int x, int y) {
		super("Out-of-board coordinate: " + x + ", " + y);
	}
	
}
