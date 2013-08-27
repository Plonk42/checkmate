package name.matco.rookoid.game;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;
import name.matco.rookoid.game.piece.Piece;

public class Square {
	
	private final Game game;
	private final Coordinate coordinate;
	private Piece piece;
	
	public Square(Game game, final int x, final int y) throws OutOfBoardCoordinateException {
		this(game, new Coordinate(x, y));
	}
	
	public Square(Game game, final Coordinate c) {
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
	
	public Square apply(Movement m) throws OutOfBoardCoordinateException {
		return game.getSquareAt(new Coordinate(coordinate.x + m.dx, coordinate.y + m.dy));
	}
	
	public Game getGame() {
		return game;
	}
	
	public Movement getMovementTo(Square s) {
		return new Movement(s.getCoordinate().x - getCoordinate().x, s.getCoordinate().y - getCoordinate().y);
	}
	
	@Override
	public String toString() {
		return getCoordinate().toString();
	}
	
}
