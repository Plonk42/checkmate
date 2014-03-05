package name.matco.checkmate.game;

import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;

public class Promotion extends Move {
	
	private PieceType chosenType;
	
	public Promotion(final Board board, final Player player, final Piece pawn, final Square to) {
		super(board, player, pawn, to);
	}
	
	public void setChosenType(final PieceType chosenType) {
		this.chosenType = chosenType;
	}
	
	public PieceType getChosenType() {
		return chosenType;
	}
	
	@Override
	public String getAlgebraic() {
		final StringBuilder algebraic = new StringBuilder();
		algebraic.append(getSquareTo().getAlgebraic());
		algebraic.append("=");
		algebraic.append(chosenType.getAlgebraic());
		return algebraic.toString();
	}
	
	@Override
	public String toString() {
		return String.format("%s is promoted by move %s", piece, movement);
	}
	
}
