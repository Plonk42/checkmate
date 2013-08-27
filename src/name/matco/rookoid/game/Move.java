package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Piece;

public class Move {
	
	private final Piece piece;
	private final Movement movement;
	private final Piece capturedPiece;
	
	public Move(Piece piece, Movement movement, Piece capturedPiece) {
		this.piece = piece;
		this.movement = movement;
		this.capturedPiece = capturedPiece;
	}
	
	public Movement getMovement() {
		return movement;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public Piece getCapturedPiece() {
		return capturedPiece;
	}
	
	@Override
	public String toString() {
		return capturedPiece == null ? String.format("Piece %s moves %s", piece, movement) : String.format("Piece %s moves %s, capture %s", piece, movement, capturedPiece);
	}
	
}
