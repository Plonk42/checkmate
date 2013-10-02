package name.matco.rookoid.ui.listeners;

import name.matco.rookoid.game.piece.Piece;

public interface CaptureListener {
	
	void onCapture(Piece piece);
	
	void onRelease(Piece piece);
	
}
