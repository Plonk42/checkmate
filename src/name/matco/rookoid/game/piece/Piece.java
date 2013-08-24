package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

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
	
	public List<Square> getAllowedPositions() {
		ArrayList<Square> allowed = new ArrayList<Square>();
		for (Movement m : getAllowedMovements()) {
			try {
				Square c = place.apply(m);
				
				boolean valid = true;
				
				//only knight can jump over other pieces
				//other pieces need a clear field
				if(!PieceType.KNIGHT.equals(getType())) {
					Square initialCase = getPlace();
					if(initialCase.getCoordinate().x == c.getCoordinate().x) {
						for(int n = Math.min(initialCase.getCoordinate().y + 1, c.getCoordinate().y); n <= Math.max(initialCase.getCoordinate().y, c.getCoordinate().y); n++) {
							if(getPlace().getGame().getCaseAt(initialCase.getCoordinate().x, n).getPiece() != null) {
								valid = false;
								break;
							}
						}
					}
					else if(initialCase.getCoordinate().y == c.getCoordinate().y) {
						for(int n = Math.min(initialCase.getCoordinate().x + 1, c.getCoordinate().x); n <= Math.max(initialCase.getCoordinate().x, c.getCoordinate().x); n++) {
							if(getPlace().getGame().getCaseAt(n, initialCase.getCoordinate().y).getPiece() != null) {
								valid = false;
								break;
							}
						}
					}
					else {
						int xDirection = initialCase.getCoordinate().x > c.getCoordinate().x ? -1 : 1;
						int yDirection = initialCase.getCoordinate().y > c.getCoordinate().y ? -1 : 1;
						for(int n = Math.min(initialCase.getCoordinate().x, c.getCoordinate().x); n <= Math.max(initialCase.getCoordinate().x, c.getCoordinate().x); n++) {
							Piece mv = getPlace().getGame().getCaseAt(initialCase.getCoordinate().x + xDirection * n, initialCase.getCoordinate().y + yDirection * n).getPiece();
							if(mv != null && !mv.equals(this)) {
								valid = false;
								break;
							}
 						}
					}
				}
				
				if(valid) {
					Piece p = c.getPiece();
					//nothing on this case
					if(p == null) { 
						allowed.add(c);
					}
					//player can not capture his own pieces or capture the opponent's king 
					else if(!p.getPlayer().equals(getPlayer()) && !PieceType.KING.equals(p.getType())) {
						allowed.add(c);
					}
				}
			} catch (OutOfBoardCoordinateException e) {
				// outside the board; that's ok.
			}
		}
		return allowed;
	}
	
	public abstract List<Movement> getAllowedMovements();
	
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
