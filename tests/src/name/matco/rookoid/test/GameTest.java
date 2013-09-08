package name.matco.rookoid.test;

import junit.framework.TestCase;
import name.matco.rookoid.game.Game;

public class GameTest extends TestCase {

	public void testGame() {
		Game game = Game.getInstance();
		assertEquals(game.getBoard().length, 64);
	}
}
