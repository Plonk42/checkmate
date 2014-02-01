package name.matco.checkmate.game.piece;

import name.matco.checkmate.game.Player;

public class Bishop extends Piece {
	
	public Bishop(final int id, final Player player) {
		super(id, player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.BISHOP;
	}
	
}
