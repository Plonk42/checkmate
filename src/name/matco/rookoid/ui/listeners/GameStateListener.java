package name.matco.rookoid.ui.listeners;

import name.matco.rookoid.game.Player;

public interface GameStateListener {
	
	void onGameInit();
	
	// void onGameReset();
	
	void onPlayerChange(Player player);
	
}
