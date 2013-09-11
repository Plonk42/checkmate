package name.matco.rookoid.game.piece;

import java.util.List;

import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Rook extends Piece {
	
	public Rook(final Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.ROOK;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		return Movement.LINE_MOVEMENTS;
	}
	
}
