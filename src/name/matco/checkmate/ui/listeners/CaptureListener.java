package name.matco.checkmate.ui.listeners;

import name.matco.checkmate.game.piece.Piece;

public interface CaptureListener {
	
	void onCapture(Piece piece);
	
	void onRelease(Piece piece);
	
}
