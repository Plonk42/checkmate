package name.matco.checkmate.game.exception;

public class InvalidAlgebraic extends Exception {

	private static final long serialVersionUID = -7758139330464943570L;

	public InvalidAlgebraic(final CharSequence algebraic, final Throwable cause) {
		super(String.format("Invalid algebraic: %s", algebraic), cause);
	}

	public InvalidAlgebraic(final CharSequence algebraic) {
		this(algebraic, null);
	}

}
