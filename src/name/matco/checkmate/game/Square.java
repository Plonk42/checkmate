package name.matco.checkmate.game;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;

public class Square implements Comparable<Square> {
	
	private final Board board;
	private final int index;
	private final int x;
	private final int y;
	private Piece piece;
	
	public Square(final Board board, final int x, final int y) throws OutOfBoardCoordinateException {
		this.board = board;
		this.index = GameUtils.coordinateToIndex(x, y);
		this.x = x;
		this.y = y;
	}
	
	public final int getIndex() {
		return index;
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
		return board.getSquareAt(GameUtils.apply(index, m));
	}
	
	public Movement getMovementTo(final Square to) {
		return new Movement(to.x - x, to.y - y);
	}
	
	@Override
	public String toString() {
		return x + "," + y + " = " + getAlgebraic();
	}
	
	public String getAlgebraic() {
		return String.format("%s%s", getFile(), getRank());
	}
	
	public char getFile() {
		return (char) ('a' + x);
	}
	
	public char getRank() {
		return (char) ('1' + y);
	}
	
	public boolean isEmpty() {
		return getPiece() == null;
	}
	
	public boolean isQueenSide() {
		return x <= 3;
	}
	
	public boolean isKingSide() {
		return x >= 4;
	}
	
	public boolean isPromotionDestination(final Player player) {
		return y == player.getOpponent().getBaseline();
	}
	
	public boolean isCastlingDestination(final Player player) {
		return y == player.getBaseline() && (x == 2 || x == 6);
	}
	
	@Override
	public int compareTo(final Square otherSquare) {
		return getIndex() - otherSquare.getIndex();
	}
}
