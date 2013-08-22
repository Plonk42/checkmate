package name.matco.rookoid.game.piece;

import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Knight extends Piece {
	
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
		// TODO Auto-generated method stub
		return null;
	}
	
}
