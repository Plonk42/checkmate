package name.matco.checkmate.game.piece;

import java.util.List;

import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;

public class Knight extends Piece {
	
	public Knight(final int id, final Player player) {
		super(id, player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.KNIGHT;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		return Movement.KNIGHT_MOVEMENTS;
	}
	
}
