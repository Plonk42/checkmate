package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Knight extends Piece {
	
	public static List<Movement> MOVEMENTS = new ArrayList<Movement>();
	static {
		MOVEMENTS.add(new Movement(2, 1));
		MOVEMENTS.add(new Movement(2, -1));
		MOVEMENTS.add(new Movement(1, 2));
		MOVEMENTS.add(new Movement(1, -2));
		MOVEMENTS.add(new Movement(-2, 1));
		MOVEMENTS.add(new Movement(-2, -1));
		MOVEMENTS.add(new Movement(-1, 2));
		MOVEMENTS.add(new Movement(-1, -2));
	}
	
	public Knight(Player player) {
		super(player);
	}
	
	@Override
	public String getName() {
		return "Knight";
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_knight : R.drawable.white_knight;
	}
	
	@Override
	public List<Movement> getAllowedMovements() {
		return MOVEMENTS;
	}
	
}
