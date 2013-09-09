package name.matco.rookoid.game;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement.Direction;

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
		
		@Override
		public Direction getForward() {
			return Direction.NORTH;
		}
		
		@Override
		public int getBaseline() {
			return 0;
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
		
		@Override
		public Direction getForward() {
			return Direction.SOUTH;
		}
		
		@Override
		public int getBaseline() {
			return 7;
		}
	};
	
	abstract public Player getOpponent();
	
	abstract public int getShortname();
	
	abstract public Direction getForward();
	
	abstract public int getBaseline();
	
}