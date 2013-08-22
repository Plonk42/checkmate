package name.matco.rookoid.game.piece;

import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Pawn extends Piece {
	
	public Pawn(Player player) {
		super(player);
	}
	
	@Override
	public String getName() {
		return "Pawn";
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_pawn : R.drawable.white_pawn;
	}
	
	@Override
	public List<Movement> getAllowedMovements() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
