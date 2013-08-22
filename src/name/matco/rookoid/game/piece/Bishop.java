package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.GameUtils;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Bishop extends Piece {
	
	public Bishop(Player player) {
		super(player);
	}
	
	@Override
	public String getName() {
		return "Bishop";
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_bishop : R.drawable.white_bishop;
	}
	
	@Override
	public List<Movement> getAllowedMovements() {
		List<Movement> movements = new ArrayList<Movement>();
		movements.addAll(GameUtils.DIAGONALE_MOVEMENTS);
		return movements;
	}
}
