package name.matco.checkmate.ui.listeners;

import name.matco.checkmate.game.Player;

public interface GameStateListener {
	
	void onGameInit();
	
	// void onGameReset();
	
	void onPlayerChange(Player player);
	
}
