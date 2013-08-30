package name.matco.rookoid.game.piece;

import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public class King extends Piece {
	
	public King(final Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.KING;
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_king : R.drawable.white_king;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		return Movement.KING_MOVEMENTS;
	}
	
}
