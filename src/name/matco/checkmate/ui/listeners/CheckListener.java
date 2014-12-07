package name.matco.checkmate.ui.listeners;

import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.piece.Piece;

public interface CheckListener {
	
	void onCheck(Piece p, Square from, Square to);
	
	void onCheckmate(Piece p, Square from, Square to);
	
}
