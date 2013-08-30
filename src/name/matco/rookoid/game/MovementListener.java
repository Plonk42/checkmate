package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Piece;

public interface MovementListener {
	
	void onMovement(Piece p, Square from, Square to);
}
