package name.matco.rookoid.game;

import name.matco.rookoid.R;

public enum Player {
	
	BLACK {
		@Override
		public Player getOpponent() {
			return WHITE;
		}
		
		@Override
		public int getShortname() {
			return R.string.black_short;
		}
	},
	WHITE {
		@Override
		public Player getOpponent() {
			return BLACK;
		}
		
		@Override
		public int getShortname() {
			return R.string.white_short;
		}
	};
	
	abstract public Player getOpponent();
	
	abstract public int getShortname();
	
}