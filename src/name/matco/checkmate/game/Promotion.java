package name.matco.checkmate.game;

import java.util.HashSet;
import java.util.Set;

import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import android.util.Log;

public class Promotion extends Move {
	
	private PieceType chosenType;
	private Piece promotedPiece;
	
	public Promotion(final Board board, final Player player, final Piece pawn, final Square to) {
		super(board, player, pawn, to);
	}
	
	public void setChosenType(final PieceType chosenType) {
		this.chosenType = chosenType;
	}
	
	public Piece getPromotedPiece() {
		return promotedPiece;
	}
	
	@Override
	public Set<Piece> getRelatedPieces() {
		final Set<Piece> relatedPieces = new HashSet<Piece>();
		relatedPieces.add(piece);
		relatedPieces.add(promotedPiece);
		return relatedPieces;
	}
	
	@Override
	public void doMove() {
		// move the pawn
		super.doMove();
		// transform pawn
		getPiece().setType(this.chosenType);
		Log.d(getClass().getName(), String.format("Promote %s to %s", getPiece(), promotedPiece));
	}
	
	@Override
	public void revertMove() {
		// untransform piece
		getPiece().setType(PieceType.PAWN);
		// undo move
		super.revertMove();
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
