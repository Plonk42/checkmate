package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.game.Case;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public abstract class Piece {
	
	protected final Player player;
	
	private Case place;
	
	public Piece(Player player) {
		this.player = player;
	}
	
	public abstract String getName();
	
	public abstract int getResource();
	
	public List<Case> getAllowedPositions() {
		ArrayList<Case> allowed = new ArrayList<Case>();
		for (Movement m : getAllowedMovements()) {
			try {
				allowed.add(place.apply(m));
			} catch(ArrayIndexOutOfBoundsException e) {
				//outside the board
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
