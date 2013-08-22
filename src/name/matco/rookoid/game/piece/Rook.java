package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.GameUtils;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Rook extends Piece {
	
	public Rook(Player player) {
		super(player);
	}
	
	@Override
	public String getName() {
		return "Rook";
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_rook : R.drawable.white_rook;
	}
	
	@Override
	public List<Movement> getAllowedMovements() {
		List<Movement> movements = new ArrayList<Movement>();
		movements.addAll(GameUtils.LINE_MOVEMENTS);
		return movements;
	}
	
}
