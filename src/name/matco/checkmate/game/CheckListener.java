package name.matco.checkmate.game;

import name.matco.checkmate.game.piece.Piece;

public interface CheckListener {
	
	void onCheck(Piece p, Square from, Square to);
	
	void onCheckmate(Piece p, Square from, Square to);
	
}
