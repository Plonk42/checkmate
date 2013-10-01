package name.matco.rookoid.ui.listeners;

import name.matco.rookoid.game.Move;

public interface MovementListener {
	
	void onMovement(final Move m, final boolean way);
	
}
