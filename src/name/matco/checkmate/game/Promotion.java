package name.matco.checkmate.game;

import java.util.HashSet;
import java.util.Set;

import name.matco.checkmate.game.piece.Pawn;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import android.util.Log;

public class Promotion extends Move {
	
	private PieceType chosenType;
	private Piece promotedPiece;
	
	public Promotion(final Game game, final Player player, final Pawn pawn, final Square to) {
		super(game, player, pawn, to);
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
	public void doMove(final Game game) {
		// move the pawn
		super.doMove(game);
		
		// create new piece
		try {
			promotedPiece = this.chosenType.getPieceClass().getConstructor(Player.class).newInstance(getPiece().getPlayer());
			to.setPiece(promotedPiece);
			promotedPiece.setSquare(to);
			Log.d(getClass().getName(), String.format("Promote %s to %s", getPiece(), promotedPiece));
			
			// remove pawn from pieces list
			game.getBoard().getPieces().remove(getPiece());
			// add promoted piece in pieces list
			game.getBoard().getPieces().add(promotedPiece);
		} catch (final Exception e) {
			// just no way to come here
		}
	}
	
	@Override
	public void revertMove(final Game game) {
		// remove promoted piece from pieces list
		game.getBoard().getPieces().remove(promotedPiece);
		// re add pawn in pieces list
		game.getBoard().getPieces().add(piece);
		
		super.revertMove(game);
	}
	
	@Override
	public String getAlgebraic() {
		final StringBuilder algebraic = new StringBuilder();
		algebraic.append(to.getAlgebraic());
		algebraic.append("=");
		algebraic.append(chosenType.getAlgebraic());
		return algebraic.toString();
	}
	
	@Override
	public String toString() {
		return String.format("%s is promoted by move %s", piece, movement);
	}
	
}
