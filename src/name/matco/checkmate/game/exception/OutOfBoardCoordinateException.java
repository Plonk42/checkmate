package name.matco.checkmate.game.exception;

public class OutOfBoardCoordinateException extends Exception {
	
	private static final long serialVersionUID = 5499607205307089921L;
	
	public OutOfBoardCoordinateException(final int index) {
		super("Out-of-board index: " + index);
	}
	
	public OutOfBoardCoordinateException(final int x, final int y) {
		super("Out-of-board coordinate: " + x + ", " + y);
	}
	
}
