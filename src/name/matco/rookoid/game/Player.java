package name.matco.rookoid.game;

public enum Player {
	
	BLACK {
		@Override
		public Player next() {
			return WHITE;
		}
	},
	WHITE {
		@Override
		public Player next() {
			return BLACK;
		}
	};
	
	abstract public Player next();
	
}
