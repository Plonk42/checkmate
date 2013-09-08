package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Pawn;
import name.matco.rookoid.game.piece.PieceType;
import android.util.Log;

public class Promotion extends Move {
	
	private final PieceType chosenType;
	
	public Promotion(final Pawn pawn, final Square to, final PieceType chosenType) {
		super(pawn, to);
		this.chosenType = chosenType;
	}
	
	@Override
	public void doMove(final Game game) {
		// move the pawn
		super.doMove(game);
		
		// create new piece
		try {
			to.setPiece(this.chosenType.getPieceClass().getConstructor(Player.class).newInstance(getPiece().getPlayer()));
			Log.d(getClass().getName(), String.format("Promote %s to %s", getPiece(), to.getPiece()));
		} catch (final Exception e) {
			// just no way to come here
		}
		
		// remove pawn from pieces list
		game.getPieces().remove(getPiece());
		// add promoted piece in pieces list
		game.getPieces().add(to.getPiece());
	}
	
	@Override
	public Move getRevertMove() {
		return new Move(piece, to) {
			@Override
			public void doMove(final Game game) {
				// remove promoted piece from pieces list
				game.getPieces().remove(to.getPiece());
				// re add pawn in pieces list
				game.getPieces().add(Promotion.this.getPiece());
				
				// use revert move
				super.getRevertMove().doMove(game);
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
}
