package name.matco.rookoid.ui;

import name.matco.rookoid.game.Player;

public interface GameStateListener {
	
	void onGameInit();
	
	// void onGameReset();
	
	void onPlayerChange(Player player);
	
}
