package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;

public abstract class Piece {
	
	protected final Player player;
	
	protected Square place;
	
	public Piece(Player player) {
		this.player = player;
	}
	
	public abstract PieceType getType();
	
	public abstract int getResource();
	
	public List<Square> getAllowedPositions(Game game) {
		ArrayList<Square> allowed = new ArrayList<Square>();
		for (List<Movement> directions : getAllowedMovements()) {
			for (Movement m : directions) {
				try {
					Square c = place.apply(m);
					Piece p = c.getPiece();
					if (p != null) {
						// player can not capture his own pieces or capture the opponent's king
						if (!getPlayer().equals(p.getPlayer()) && !PieceType.KING.equals(p.getType())) {
							allowed.add(c);
						}
						break;
					} else {
						// nothing on this case
						allowed.add(c);
					}
				} catch (OutOfBoardCoordinateException e) {
					// outside the board; stop going in this direction
					break;
				}
			}
		}
		return allowed;
	}
	
	public abstract List<List<Movement>> getAllowedMovements();
	
	public String getDescription() {
		return player + " " + getType();
	}
	
	public Square getPlace() {
		return place;
	}
	
	public void setPlace(Square place) {
		this.place = place;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
