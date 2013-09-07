package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Piece;

public interface CheckListener {
	
	void onCheck(Piece p, Square from, Square to);
	
	void onCheckmate(Piece p, Square from, Square to);
	
}
