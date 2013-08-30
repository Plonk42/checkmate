package name.matco.rookoid.game;

public enum Player {
	
	BLACK {
		@Override
		public Player getOpponent() {
			return WHITE;
		}
	},
	WHITE {
		@Override
		public Player getOpponent() {
			return BLACK;
		}
	};
	
	abstract public Player getOpponent();
	
}