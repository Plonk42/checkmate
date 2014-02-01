package name.matco.checkmate.game.piece;

import name.matco.checkmate.game.Player;

public class Pawn extends Piece {
	
	public Pawn(final int id, final Player player) {
		super(id, player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.PAWN;
	}
}
