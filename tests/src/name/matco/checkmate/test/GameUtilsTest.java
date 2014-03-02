package name.matco.checkmate.test;

import name.matco.checkmate.game.GameUtils;
import name.matco.checkmate.game.exception.InvalidAlgebraic;
import android.test.InstrumentationTestCase;

public class GameUtilsTest extends InstrumentationTestCase {
	
	public void testCoordinate() {
		try {
			assertEquals(0, GameUtils.indexFromAlgebraic("a1") % GameUtils.CHESSBOARD_SIZE);
			assertEquals(0, GameUtils.indexFromAlgebraic("a1") / GameUtils.CHESSBOARD_SIZE);
			
			assertEquals(7, GameUtils.indexFromAlgebraic("h1") % GameUtils.CHESSBOARD_SIZE);
			assertEquals(0, GameUtils.indexFromAlgebraic("h1") / GameUtils.CHESSBOARD_SIZE);
			
			assertEquals(0, GameUtils.indexFromAlgebraic("a8") % GameUtils.CHESSBOARD_SIZE);
			assertEquals(7, GameUtils.indexFromAlgebraic("a8") / GameUtils.CHESSBOARD_SIZE);
			
			assertEquals(7, GameUtils.indexFromAlgebraic("h8") % GameUtils.CHESSBOARD_SIZE);
			assertEquals(7, GameUtils.indexFromAlgebraic("h8") / GameUtils.CHESSBOARD_SIZE);
			
			assertEquals(3, GameUtils.indexFromAlgebraic("d1") % GameUtils.CHESSBOARD_SIZE);
			assertEquals(0, GameUtils.indexFromAlgebraic("d1") / GameUtils.CHESSBOARD_SIZE);
			
			assertEquals(5, GameUtils.indexFromAlgebraic("f6") % GameUtils.CHESSBOARD_SIZE);
			assertEquals(5, GameUtils.indexFromAlgebraic("f6") / GameUtils.CHESSBOARD_SIZE);
			
		} catch (final InvalidAlgebraic e) {
			fail(e.getLocalizedMessage());
		}
		
		try {
			assertEquals(5, GameUtils.indexFromAlgebraic("e9"));
			fail("e9 should not be a valid algebraic notation");
		} catch (final InvalidAlgebraic e) {
			// it works!
		}
		
		try {
			assertEquals(5, GameUtils.indexFromAlgebraic("i1"));
			fail("i1 should not be a valid algebraic notation");
		} catch (final InvalidAlgebraic e) {
			// it works!
		}
	}
}
