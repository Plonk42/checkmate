package name.matco.checkmate.game;

import name.matco.checkmate.R;
import name.matco.checkmate.game.Movement.Direction;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;

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
			return Direction.SOUTH;
		}
		
		@Override
		public int getBaseline() {
			return 7;
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
			return Direction.NORTH;
		}
		
		@Override
		public int getBaseline() {
			return 0;
		}
	};
	
	abstract public Player getOpponent();
	
	abstract public int getShortname();
	
	abstract public Direction getForward();
	
	abstract public int getBaseline();
	
	public Coordinate getKingCorner() {
		try {
			return new Coordinate(7, getBaseline());
		} catch (final OutOfBoardCoordinateException e) {
			// hard-coded coordinate
			throw new RuntimeException(e);
		}
	}
	
	public Coordinate getQueenCorner() {
		try {
			return new Coordinate(0, getBaseline());
		} catch (final OutOfBoardCoordinateException e) {
			// hard-coded coordinate
			throw new RuntimeException(e);
		}
	}
	
}