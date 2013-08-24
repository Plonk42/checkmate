package name.matco.rookoid.game;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;
import name.matco.rookoid.game.piece.Piece;

public class Case {
	
	private final Game game;
	private final Coordinate coordinate;
	private Piece piece;
	
	public Case(Game game, final int x, final int y) throws OutOfBoardCoordinateException {
		this(game, new Coordinate(x, y));
	}
	
	public Case(Game game, final Coordinate c) {
		this.game = game;
		this.coordinate = c;
	}
	
	public final Coordinate getCoordinate() {
		return coordinate;
	}
	
	public final void setPiece(final Piece piece) {
		this.piece = piece;
	}
	
	public final Piece getPiece() {
		return piece;
	}
	
	public Case apply(Movement m) throws OutOfBoardCoordinateException {
		return game.getCaseAt(new Coordinate(coordinate.x + m.dx, coordinate.y + m.dy));
	}

	public Game getGame() {
		return game;
	}
	
	public String toString() {
		return getCoordinate().toString();
	}
}
