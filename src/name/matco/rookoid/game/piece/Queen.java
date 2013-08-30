package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Queen extends Piece {
	
	public Queen(final Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.QUEEN;
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_queen : R.drawable.white_queen;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		final List<List<Movement>> movements = new ArrayList<List<Movement>>(Movement.LINE_MOVEMENTS);
		movements.addAll(Movement.DIAGONALE_MOVEMENTS);
		return movements;
	}
}
