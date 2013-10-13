package name.matco.checkmate.ui.listeners;

import name.matco.checkmate.game.Move;

public interface MovementListener {
	
	void onMovement(final Move m, final boolean way);
	
}
