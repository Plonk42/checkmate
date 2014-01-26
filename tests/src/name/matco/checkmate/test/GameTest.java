package name.matco.checkmate.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.matco.checkmate.game.CheckListener;
import name.matco.checkmate.game.Coordinate;
import name.matco.checkmate.game.Game;
import name.matco.checkmate.game.Move;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.exception.InvalidAlgebraic;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import name.matco.checkmate.ui.PieceMovement;
import android.test.InstrumentationTestCase;
import android.util.Log;

public class GameTest extends InstrumentationTestCase {
	
	public void testCoordinate() {
		try {
			assertEquals(0, Coordinate.fromAlgebraic("a1").x);
			assertEquals(0, Coordinate.fromAlgebraic("a1").y);
			
			assertEquals(7, Coordinate.fromAlgebraic("h1").x);
			assertEquals(0, Coordinate.fromAlgebraic("h1").y);
			
			assertEquals(0, Coordinate.fromAlgebraic("a8").x);
			assertEquals(7, Coordinate.fromAlgebraic("a8").y);
			
			assertEquals(7, Coordinate.fromAlgebraic("h8").x);
			assertEquals(7, Coordinate.fromAlgebraic("h8").y);
			
			assertEquals(3, Coordinate.fromAlgebraic("d1").x);
			assertEquals(0, Coordinate.fromAlgebraic("d1").y);
			
			assertEquals(5, Coordinate.fromAlgebraic("f6").x);
			assertEquals(5, Coordinate.fromAlgebraic("f6").y);
			
		} catch (final InvalidAlgebraic e) {
			fail(e.getLocalizedMessage());
		}
		
		try {
			assertEquals(5, Coordinate.fromAlgebraic("e9").x);
			fail("e9 should not be a valid algebraic notation");
		} catch (final InvalidAlgebraic e) {
			// it works!
		}
		
		try {
			assertEquals(5, Coordinate.fromAlgebraic("i1").y);
			fail("i1 should not be a valid algebraic notation");
		} catch (final InvalidAlgebraic e) {
			// it works!
		}
	}
	
	public void testGame() {
		final Game game = new Game();
		
		assertEquals(game.getBoard().getSquares().length, 64);
		
		try {
			// check some squares
			assertNotNull(game.getBoard().getSquares()[0]);
			assertNotNull(game.getBoard().getSquares()[3]);
			assertNotNull(game.getBoard().getSquares()[63]);
			
			// check square retrieving
			assertEquals(game.getBoard().getSquares()[0], game.getBoard().getSquareAt(0, 0));
			assertEquals(game.getBoard().getSquares()[3], game.getBoard().getSquareAt(3, 0));
			assertEquals(game.getBoard().getSquares()[63], game.getBoard().getSquareAt(7, 7));
			
		} catch (final OutOfBoardCoordinateException e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	public void testInitialGame() {
		final Game game = new Game();
		
		// check piece positions
		assertTrue(game.getBoard().getSquares()[0].getPiece().is(PieceType.ROOK, Player.WHITE));
		assertTrue(game.getBoard().getSquares()[1].getPiece().is(PieceType.KNIGHT, Player.WHITE));
		assertTrue(game.getBoard().getSquares()[2].getPiece().is(PieceType.BISHOP, Player.WHITE));
		assertTrue(game.getBoard().getSquares()[3].getPiece().is(PieceType.QUEEN, Player.WHITE));
		assertTrue(game.getBoard().getSquares()[4].getPiece().is(PieceType.KING, Player.WHITE));
		assertTrue(game.getBoard().getSquares()[5].getPiece().is(PieceType.BISHOP, Player.WHITE));
		assertTrue(game.getBoard().getSquares()[6].getPiece().is(PieceType.KNIGHT, Player.WHITE));
		assertTrue(game.getBoard().getSquares()[7].getPiece().is(PieceType.ROOK, Player.WHITE));
		for (int i = 8; i < 8 + 8; i++) {
			assertTrue(game.getBoard().getSquares()[i].getPiece().is(PieceType.PAWN, Player.WHITE));
		}
		for (int i = 48; i < 56; i++) {
			assertTrue(game.getBoard().getSquares()[i].getPiece().is(PieceType.PAWN, Player.BLACK));
		}
		assertTrue(game.getBoard().getSquares()[56].getPiece().is(PieceType.ROOK, Player.BLACK));
		assertTrue(game.getBoard().getSquares()[57].getPiece().is(PieceType.KNIGHT, Player.BLACK));
		assertTrue(game.getBoard().getSquares()[58].getPiece().is(PieceType.BISHOP, Player.BLACK));
		assertTrue(game.getBoard().getSquares()[59].getPiece().is(PieceType.QUEEN, Player.BLACK));
		assertTrue(game.getBoard().getSquares()[60].getPiece().is(PieceType.KING, Player.BLACK));
		assertTrue(game.getBoard().getSquares()[61].getPiece().is(PieceType.BISHOP, Player.BLACK));
		assertTrue(game.getBoard().getSquares()[62].getPiece().is(PieceType.KNIGHT, Player.BLACK));
		assertTrue(game.getBoard().getSquares()[63].getPiece().is(PieceType.ROOK, Player.BLACK));
		
	}
	
	public void testInitialAllowedPositions() {
		final Game game = new Game();
		
		final Piece whiteQueen = game.getBoard().getSquares()[3].getPiece();
		final Piece whiteKing = game.getBoard().getSquares()[4].getPiece();
		final Piece whiteQueenPawn = game.getBoard().getSquares()[11].getPiece();
		
		// check some piece allowed positions
		assertTrue(whiteQueen.getAllowedPositions().isEmpty());
		assertTrue(whiteKing.getAllowedPositions().isEmpty());
		
		final List<Square> whiteQueenPawnAllowedPosition = whiteQueenPawn.getAllowedPositions();
		
		try {
			assertEquals(2, whiteQueenPawnAllowedPosition.size());
			assertTrue(whiteQueenPawnAllowedPosition.contains(game.getBoard().getSquareAt(3, 2)));
			assertTrue(whiteQueenPawnAllowedPosition.contains(game.getBoard().getSquareAt(3, 3)));
		} catch (final OutOfBoardCoordinateException e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	private void checkBoardConsistency(final Game game) {
		for (final Square s : game.getBoard().getSquares()) {
			if (s.getPiece() != null && !s.getPiece().getSquare().equals(s)) {
				fail(String.format("Square %s contains piece %s which is linked to square %s", s, s.getPiece(), s.getPiece().getSquare()));
			}
		}
	}
	
	private final Map<Piece, Square> piecePositions = new HashMap<Piece, Square>();
	
	private void saveBoardState(final Game game) {
		piecePositions.clear();
		for (final Piece p : game.getBoard().getPieces()) {
			piecePositions.put(p, p.getSquare());
		}
	}
	
	public List<PieceMovement> compareAgainsLastSave(final Game game) {
		final List<PieceMovement> movements = new ArrayList<PieceMovement>();
		for (final Piece p : game.getBoard().getPieces()) {
			if (!p.getSquare().equals(piecePositions.get(p))) {
				movements.add(new PieceMovement(p, piecePositions.get(p), p.getSquare()));
			}
		}
		return movements;
	}
	
	public void testMove() {
		final Game game = new Game();
		
		final Piece whiteQueenPawn = game.getBoard().getSquares()[11].getPiece();
		final List<Square> whiteQueenPawnAllowedPosition = whiteQueenPawn.getAllowedPositions();
		
		saveBoardState(game);
		final Move move = game.getMove(whiteQueenPawn, whiteQueenPawnAllowedPosition.get(0));
		game.playMove(move);
		final List<PieceMovement> movements = compareAgainsLastSave(game);
		
		try {
			checkBoardConsistency(game);
			assertEquals(1, movements.size());
			assertEquals(whiteQueenPawn, movements.get(0).getPiece());
			assertEquals(movements.get(0).getFrom(), game.getBoard().getSquareAt(3, 1));
			assertEquals(movements.get(0).getTo(), game.getBoard().getSquareAt(3, 2));
		} catch (final OutOfBoardCoordinateException e) {
			fail(e.getLocalizedMessage());
		}
	}
	
	public void testScholarsMate() {
		final Game game = new Game();
		
		final List<Piece> checks = new ArrayList<Piece>();
		final List<Piece> checkMates = new ArrayList<Piece>();
		
		game.addCheckListener(new CheckListener() {
			
			@Override
			public void onCheckmate(final Piece p, final Square from, final Square to) {
				checkMates.add(p);
			}
			
			@Override
			public void onCheck(final Piece p, final Square from, final Square to) {
				checks.add(p);
			}
		});
		
		try {
			game.playMove(Move.fromAlgebraic(game.getBoard(), Player.WHITE, "e4"));
			game.playMove(Move.fromAlgebraic(game.getBoard(), Player.BLACK, "e5"));
			game.playMove(Move.fromAlgebraic(game.getBoard(), Player.WHITE, "Bc4"));
			game.playMove(Move.fromAlgebraic(game.getBoard(), Player.BLACK, "Bc5"));
			game.playMove(Move.fromAlgebraic(game.getBoard(), Player.WHITE, "Qh5"));
			game.playMove(Move.fromAlgebraic(game.getBoard(), Player.BLACK, "Nc6"));
			
			try {
				assertNotNull(game.getBoard().getSquareAt(5, 6).getPiece());
				assertTrue(game.getBoard().getSquareAt(5, 6).getPiece().is(PieceType.PAWN, Player.BLACK));
				
				assertNotNull(game.getBoard().getSquareAt(4, 7).getPiece());
				assertTrue(game.getBoard().getSquareAt(4, 7).getPiece().is(PieceType.KING, Player.BLACK));
				
				assertNotNull(game.getBoard().getSquareAt(3, 7).getPiece());
				assertTrue(game.getBoard().getSquareAt(3, 7).getPiece().is(PieceType.QUEEN, Player.BLACK));
				
				assertNotNull(game.getBoard().getSquareAt(2, 3).getPiece());
				Log.i(getClass().getName(), game.getBoard().getSquareAt(2, 3).getPiece().toString());
				assertTrue(game.getBoard().getSquareAt(2, 3).getPiece().is(PieceType.BISHOP, Player.WHITE));
			} catch (final OutOfBoardCoordinateException e) {
				fail(e.getLocalizedMessage());
			}
			
			assertEquals(0, checks.size());
			assertEquals(0, checkMates.size());
			
			game.playMove(Move.fromAlgebraic(game.getBoard(), Player.WHITE, "Qxf7"));
			
			try {
				assertNotNull(game.getBoard().getSquareAt(5, 6).getPiece());
				Log.i(getClass().getName(), game.getBoard().getSquareAt(5, 6).getPiece().toString());
				assertTrue(game.getBoard().getSquareAt(5, 6).getPiece().is(PieceType.QUEEN, Player.WHITE));
			} catch (final OutOfBoardCoordinateException e) {
				fail(e.getLocalizedMessage());
			}
			
			assertEquals(0, checks.size());
			assertEquals(1, checkMates.size());
			
		} catch (final InvalidAlgebraic e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		
	}
}
