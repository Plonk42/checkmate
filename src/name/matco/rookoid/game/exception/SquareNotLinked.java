package name.matco.rookoid.game.exception;

import name.matco.rookoid.game.Square;

public class SquareNotLinked extends Exception {

	private static final long serialVersionUID = -6490437732200399263L;

	public SquareNotLinked(Square c1, Square c2) {
		super(String.format("Square %s is not linked to square %s", c1, c2));
	}
	
}
