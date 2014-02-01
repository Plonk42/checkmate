package name.matco.checkmate.game.piece;

import name.matco.checkmate.game.Player;

public class Queen extends Piece {
	
	public Queen(final int id, final Player player) {
		super(id, player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.QUEEN;
	}
}
