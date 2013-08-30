package name.matco.rookoid.game.piece;

import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class Knight extends Piece {
	
	public Knight(final Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.KNIGHT;
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_knight : R.drawable.white_knight;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		return Movement.KNIGHT_MOVEMENTS;
	}
	
}
