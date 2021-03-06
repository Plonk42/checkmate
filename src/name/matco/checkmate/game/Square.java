package name.matco.checkmate.game;

import java.util.ArrayList;
import java.util.List;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;

public class Square implements Comparable<Square> {
	
	private final Board board;
	private final int index;
	
	public Square(final Board board, final int index) throws OutOfBoardCoordinateException {
		this.board = board;
		this.index = GameUtils.checkIndex(index);
	}
	
	public int getX() {
		return GameUtils.indexToX(index);
	}
	
	public int getY() {
		return GameUtils.indexToY(index);
	}

	public final int getIndex() {
		return index;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public final Piece getPiece() {
		return board.getPiece(index);
	}
	
	public Square apply(final Movement m) throws OutOfBoardCoordinateException {
		return board.getSquareAt(GameUtils.apply(index, m));
	}
	
	public Movement getMovementTo(final Square to) {
		return new Movement(to.getX() - getX(), to.getY() - getY());
	}
	
	@Override
	public String toString() {
		return getX() + "," + getY() + " = " + getAlgebraic();
	}
	
	public String getAlgebraic() {
		return String.format("%s%s", getFile(), getRank());
	}
	
	public char getFile() {
		return (char) ('a' + getX());
	}
	
	public char getRank() {
		return (char) ('1' + getY());
	}
	
	public boolean isEmpty() {
		return getPiece() == null;
	}

	public boolean onSameFile(final Square otherSquare) {
		return getX() == otherSquare.getX();
	}

	public boolean onSameRank(final Square otherSquare) {
		return getY() == otherSquare.getY();
	}

	public boolean onSameDiagonale(final Square otherSquare) {
		return Math.abs(getX() - otherSquare.getX()) == Math.abs(getY() - otherSquare.getY());
	}

	public List<Square> getSquaresInBetween(final Square otherSquare) {
		final List<Square> squares = new ArrayList<Square>();
		try {
			if (onSameFile(otherSquare)) {
				for (int i = Math.min(getY(), otherSquare.getY()) + 1; i < Math.max(getY(), otherSquare.getY()); i++) {
					squares.add(board.getSquareAt(GameUtils.coordinateToIndex(getX(), i)));
				}
			}
			if (onSameRank(otherSquare)) {
				for (int i = Math.min(getX(), otherSquare.getX()) + 1; i < Math.max(getX(), otherSquare.getX()); i++) {
					squares.add(board.getSquareAt(GameUtils.coordinateToIndex(i, getY())));
				}
			}
			if (onSameDiagonale(otherSquare)) {
				// find what needs to be written here
				throw new UnsupportedOperationException("This need to be implemented");
			}
		} catch (final OutOfBoardCoordinateException e) {
			// no way to come here
		}
		return squares;
	}
	
	public boolean isQueenSide() {
		return getX() <= 3;
	}
	
	public boolean isKingSide() {
		return getX() >= 4;
	}
	
	public boolean isPromotionDestination(final Player player) {
		return getY() == player.getOpponent().getBaseline();
	}
	
	public boolean isCastlingDestination(final Player player) {
		return getY() == player.getBaseline() && (getX() == 2 || getX() == 6);
	}
	
	@Override
	public int compareTo(final Square otherSquare) {
		return getIndex() - otherSquare.getIndex();
	}
}
