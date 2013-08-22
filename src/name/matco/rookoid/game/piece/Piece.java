package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.game.Coordinate;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;

public abstract class Piece {
	
	protected final Player player;
	
	public Piece(Player player) {
		this.player = player;
	}
	
	public abstract String getName();
	
	public abstract int getResource();
	
	public List<Coordinate> getAllowedPositions(Coordinate from) {
		ArrayList<Coordinate> allowed = new ArrayList<Coordinate>();
		for (Movement m : getAllowedMovements()) {
			allowed.add(from.apply(m));
		}
		return allowed;
	}
	
	public abstract List<Movement> getAllowedMovements();
	
	public String getDescription() {
		return player + " " + getName();
	}
	
}
