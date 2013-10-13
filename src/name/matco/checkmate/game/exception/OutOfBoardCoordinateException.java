package name.matco.checkmate.game.exception;

public class OutOfBoardCoordinateException extends Exception {
	
	private static final long serialVersionUID = 5499607205307089921L;

	public OutOfBoardCoordinateException(int x, int y) {
		super("Out-of-board coordinate: " + x + ", " + y);
	}
	
}
