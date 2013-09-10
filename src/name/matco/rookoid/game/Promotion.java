package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Pawn;
import name.matco.rookoid.game.piece.Piece;
import name.matco.rookoid.game.piece.PieceType;
import android.util.Log;

public class Promotion extends Move {
	
	private PieceType chosenType;
	
	public Promotion(final Pawn pawn, final Square to) {
		super(pawn, to);
	}
	
	public void setChosenType(final PieceType chosenType) {
		this.chosenType = chosenType;
	}
	
	@Override
	public void doMove(final Game game) {
		// move the pawn
		super.doMove(game);
		
		// create new piece
		try {
			final Piece promotedPiece = this.chosenType.getPieceClass().getConstructor(Player.class).newInstance(getPiece().getPlayer());
			to.setPiece(promotedPiece);
			promotedPiece.setSquare(to);
			Log.d(getClass().getName(), String.format("Promote %s to %s", getPiece(), promotedPiece));
			
			// remove pawn from pieces list
			game.getPieces().remove(getPiece());
			// add promoted piece in pieces list
			game.getPieces().add(promotedPiece);
		} catch (final Exception e) {
			// just no way to come here
		}
	}
	
	@Override
	public Move getRevertMove() {
		return new Move(piece, from) {
			@Override
			public void doMove(final Game game) {
				capturedPiece = null;
				from = Promotion.this.from;
				
				// remove promoted piece from pieces list
				game.getPieces().remove(Promotion.this.getPiece());
				// re add pawn in pieces list
				game.getPieces().add(Promotion.this.getPiece());
				
				// use revert move
				getRevertMove().doMove(game);
			}
		};
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
