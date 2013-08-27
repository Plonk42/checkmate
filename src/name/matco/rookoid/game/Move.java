package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Piece;

public class Move {
	
	private final Piece piece;
	private final Movement movement;
	
	public Move(Piece piece, Movement movement) {
		this.piece = piece;
		this.movement = movement;
	}

	public Movement getMovement() {
		return movement;
	}
	public Piece getPiece() {
		return piece;
	}
	
	@Override
	public String toString() {
		return String.format("Piece %s go to %s", piece, movement);
	}
	
}
