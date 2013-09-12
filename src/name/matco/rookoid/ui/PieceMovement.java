package name.matco.rookoid.ui;

import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.piece.Piece;

public class PieceMovement {
	
	private final Piece piece;
	private final Square from;
	private final Square to;
	
	public PieceMovement(final Piece piece, final Square from, final Square to) {
		this.piece = piece;
		this.from = from;
		this.to = to;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public Square getFrom() {
		return from;
	}
	
	public Square getTo() {
		return to;
	}
	
}
