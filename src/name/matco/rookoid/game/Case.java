package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Piece;

public class Case {
	
	private final Coordinate coordinate;
	private Piece piece;
	
	public Case(final int x, final int y) {
		this(new Coordinate(x, y));
	}
	
	public Case(final Coordinate c) {
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
	
}
