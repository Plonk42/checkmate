package name.matco.checkmate.game.piece;

import java.util.List;

import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;

public class Rook extends Piece {
	
	public Rook(final int id, final Player player) {
		super(id, player);
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
