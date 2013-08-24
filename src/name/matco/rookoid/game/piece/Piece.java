package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.game.Case;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;

public abstract class Piece {
	
	protected final Player player;
	
	protected Case place;
	
	public Piece(Player player) {
		this.player = player;
	}
	
	public abstract String getName();
	
	public abstract int getResource();
	
	public List<Case> getAllowedPositions() {
		ArrayList<Case> allowed = new ArrayList<Case>();
		for (Movement m : getAllowedMovements()) {
			try {
				Case c = place.apply(m);
				
				boolean valid = true;
				
				//only knight can jump over other pieces
				//other pieces need a clear field
				if(!"Knight".equals(getName())) {
					Case initialCase = getPlace();
					for(int i = Math.min(c.getCoordinate().x, initialCase.getCoordinate().x + 1); i < Math.max(c.getCoordinate().x, initialCase.getCoordinate().x); i++) {
						for(int j = Math.min(c.getCoordinate().y, initialCase.getCoordinate().y + 1); j < Math.max(c.getCoordinate().y, initialCase.getCoordinate().y); j++) {
							if(getPlace().getGame().getCaseAt(i, j).getPiece() != null) {
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
					else if(!p.getPlayer().equals(getPlayer()) && !"King".equals(p.getName())) {
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
		return player + " " + getName();
	}
	
	public Case getPlace() {
		return place;
	}
	
	public void setPlace(Case place) {
		this.place = place;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
