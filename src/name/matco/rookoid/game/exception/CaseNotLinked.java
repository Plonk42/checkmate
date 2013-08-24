package name.matco.rookoid.game.exception;

import name.matco.rookoid.game.Square;

public class CaseNotLinked extends Exception {

	private static final long serialVersionUID = -6490437732200399263L;

	public CaseNotLinked(Square c1, Square c2) {
		super(String.format("Square %s is not linked to case %s", c1, c2));
	}
	
}
