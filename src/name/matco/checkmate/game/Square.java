package name.matco.checkmate.game;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;

public class Square implements Comparable<Square> {
	
	private final Board board;
	// TODO integrate coordinate to square
	private final Coordinate coordinate;
	private Piece piece;
	
	public Square(final Board board, final int x, final int y) throws OutOfBoardCoordinateException {
		this(board, new Coordinate(x, y));
	}
	
	public Square(final Board board, final Coordinate c) {
		this.board = board;
		this.coordinate = c;
	}
	
	public final Coordinate getCoordinate() {
		return coordinate;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public final void setPiece(final Piece piece) {
		this.piece = piece;
	}
	
	public final Piece getPiece() {
		return piece;
	}
	
	public Square apply(final Movement m) throws OutOfBoardCoordinateException {
		return board.getSquareAt(new Coordinate(coordinate.x + m.dx, coordinate.y + m.dy));
	}
	
	public Movement getMovementTo(final Square s) {
		return new Movement(s.getCoordinate().x - getCoordinate().x, s.getCoordinate().y - getCoordinate().y);
	}
	
	@Override
	public String toString() {
		return getCoordinate().toString() + " = " + getAlgebraic();
	}
	
	public String getAlgebraic() {
		return String.format("%s%s", getFile(), getRank());
	}
	
	public char getFile() {
		return (char) ('a' + coordinate.x);
	}
	
	public char getRank() {
		return (char) ('1' + coordinate.y);
	}
	
	public boolean isEmpty() {
		return getPiece() == null;
	}
	
	public boolean isQueenSide() {
		return getCoordinate().x <= 3;
	}
	
	public boolean isKingSide() {
		return getCoordinate().x >= 4;
	}
	
	public boolean isPromotionDestination(final Player player) {
		return getCoordinate().y == player.getOpponent().getBaseline();
	}
	
	public boolean isCastlingDestination(final Player player) {
		final int side = player.getBaseline();
		return getCoordinate().y == side && (getCoordinate().x == 2 || getCoordinate().x == 6);
	}
	
	@Override
	public int compareTo(final Square otherSquare) {
		return GameUtils.coordinateToIndex(getCoordinate()) - GameUtils.coordinateToIndex(otherSquare.getCoordinate());
	}
}
