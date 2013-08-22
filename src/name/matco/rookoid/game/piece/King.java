package name.matco.rookoid.game.piece;

import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class King extends Piece {
	
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
		// TODO Auto-generated method stub
		return null;
	}
	
}
