package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Movement.Direction;
import name.matco.rookoid.game.Player;

public class Pawn extends Piece {
	
	public Pawn(Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.PAWN;
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_pawn : R.drawable.white_pawn;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		List<Movement> movements = new ArrayList<Movement>();
		if (Player.WHITE.equals(getPlayer())) {
			movements.add(Direction.SOUTH.getMovement());
			// not been played yet
			if (place.getCoordinate().y == 1) {
				movements.add(new Movement(0, 2)); // TODO : use SOUTH?
			}
		}
		else {
			movements.add(Direction.NORTH.getMovement());
			// not been played yet
			if (place.getCoordinate().y == 6) {
				movements.add(new Movement(0, -2)); // TODO : use NORTH?
			}
		}
		List<List<Movement>> ret = new ArrayList<List<Movement>>();
		ret.add(movements);
		return ret;
	}
}
