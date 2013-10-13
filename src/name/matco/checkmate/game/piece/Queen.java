package name.matco.checkmate.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;

public class Queen extends Piece {
	
	public Queen(final Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.QUEEN;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		final List<List<Movement>> movements = new ArrayList<List<Movement>>(Movement.LINE_MOVEMENTS);
		movements.addAll(Movement.DIAGONAL_MOVEMENTS);
		return movements;
	}
}
