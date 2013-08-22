package name.matco.rookoid.game.piece;

import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Queen extends Piece {
	
	public Queen(Player player) {
		super(player);
	}
	
	@Override
	public String getName() {
		return "Queen";
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_queen : R.drawable.white_queen;
	}
	
	@Override
	public List<Movement> getAllowedMovements() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
