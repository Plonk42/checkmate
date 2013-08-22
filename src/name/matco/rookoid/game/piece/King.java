package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class King extends Piece {
	
	public static List<Movement> MOVEMENTS = new ArrayList<Movement>();
	static {
		MOVEMENTS.add(new Movement(0, 1));
		MOVEMENTS.add(new Movement(0, -1));
		MOVEMENTS.add(new Movement(1, 0));
		MOVEMENTS.add(new Movement(-1, 0));
		MOVEMENTS.add(new Movement(1, 1));
		MOVEMENTS.add(new Movement(1, -1));
		MOVEMENTS.add(new Movement(-1, 1));
		MOVEMENTS.add(new Movement(-1, -1));
	}
	
	public King(Player player) {
		super(player);
	}
	
	@Override
	public String getName() {
		return "King";
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_king : R.drawable.white_king;
	}
	
	@Override
	public List<Movement> getAllowedMovements() {
		return MOVEMENTS;
	}
	
}
