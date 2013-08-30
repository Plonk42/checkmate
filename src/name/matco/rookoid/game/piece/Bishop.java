package name.matco.rookoid.game.piece;

import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Bishop extends Piece {
	
	public Bishop(final Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.BISHOP;
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_bishop : R.drawable.white_bishop;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		return Movement.DIAGONAL_MOVEMENTS;
	}
}
