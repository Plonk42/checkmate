package name.matco.checkmate.game.piece;

import java.util.List;

import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;

public class Bishop extends Piece {
	
	public Bishop(final Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.BISHOP;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		return Movement.DIAGONAL_MOVEMENTS;
	}
	
}
