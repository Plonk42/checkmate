package name.matco.rookoid.game;

import java.util.ArrayList;
import java.util.List;

public class Movement {
	
	public enum Direction {
		NORTH {
			@Override
			public Movement getMovement() {
				return new Movement(0, -1);
			}
		},
		NORTH_EAST {
			@Override
			public Movement getMovement() {
				return new Movement(1, -1);
			}
		},
		EAST {
			@Override
			public Movement getMovement() {
				return new Movement(1, 0);
			}
		},
		SOUTH_EAST {
			@Override
			public Movement getMovement() {
				return new Movement(1, 1);
			}
		},
		SOUTH {
			@Override
			public Movement getMovement() {
				return new Movement(0, 1);
			}
		},
		SOUTH_WEST {
			@Override
			public Movement getMovement() {
				return new Movement(-1, 1);
			}
		},
		WEST {
			@Override
			public Movement getMovement() {
				return new Movement(-1, 0);
			}
		},
		NORTH_WEST {
			@Override
			public Movement getMovement() {
				return new Movement(-1, -1);
			}
		};
		
		public abstract Movement getMovement();
	}
	
	private final static List<List<Movement>> LINE_MOVEMENTS = new ArrayList<List<Movement>>();
	static {
		for (Direction dir : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
			List<Movement> moves = new ArrayList<Movement>();
			Movement prev = dir.getMovement();
			moves.add(prev);
			for (int i = 0; i < GameUtils.CHESSBOARD_SIZE - 1; i++) {
				prev = prev.add(dir.getMovement());
				moves.add(prev);
			}
			LINE_MOVEMENTS.add(moves);
		}
	}
	
	private final static List<List<Movement>> DIAGONALE_MOVEMENTS = new ArrayList<List<Movement>>();
	static {
		for (Direction dir : new Direction[] { Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST }) {
			List<Movement> moves = new ArrayList<Movement>();
			Movement prev = dir.getMovement();
			moves.add(prev);
			for (int i = 0; i < GameUtils.CHESSBOARD_SIZE - 1; i++) {
				prev = prev.add(dir.getMovement());
				moves.add(prev);
			}
			DIAGONALE_MOVEMENTS.add(moves);
		}
	}
	
	private final static List<List<Movement>> KNIGHT_MOVEMENTS = new ArrayList<List<Movement>>();
	static {
		{
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(new Movement(2, 1));
			KNIGHT_MOVEMENTS.add(moves);
		}
		{
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(new Movement(2, -1));
			KNIGHT_MOVEMENTS.add(moves);
		}
		{
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(new Movement(1, 2));
			KNIGHT_MOVEMENTS.add(moves);
		}
		{
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(new Movement(1, -2));
			KNIGHT_MOVEMENTS.add(moves);
		}
		{
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(new Movement(-2, 1));
			KNIGHT_MOVEMENTS.add(moves);
		}
		{
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(new Movement(-2, -1));
			KNIGHT_MOVEMENTS.add(moves);
		}
		{
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(new Movement(-1, 2));
			KNIGHT_MOVEMENTS.add(moves);
		}
		{
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(new Movement(-1, -2));
			KNIGHT_MOVEMENTS.add(moves);
		}
	}
	
	private final static List<List<Movement>> KING_MOVEMENTS = new ArrayList<List<Movement>>();
	static {
		for (Direction dir : Direction.values()) {
			List<Movement> moves = new ArrayList<Movement>();
			moves.add(dir.getMovement());
			KING_MOVEMENTS.add(moves);
		}
	}
	
	public static List<List<Movement>> getLineMovements() {
		List<List<Movement>> ret = new ArrayList<List<Movement>>();
		for (List<Movement> moves : LINE_MOVEMENTS) {
			ret.add(new ArrayList<Movement>(moves));
		}
		return ret;
	}
	
	public static List<List<Movement>> getDiagonaleMovements() {
		List<List<Movement>> ret = new ArrayList<List<Movement>>();
		for (List<Movement> moves : DIAGONALE_MOVEMENTS) {
			ret.add(new ArrayList<Movement>(moves));
		}
		return ret;
	}
	
	public static List<List<Movement>> getKnightMovements() {
		List<List<Movement>> ret = new ArrayList<List<Movement>>();
		for (List<Movement> moves : KNIGHT_MOVEMENTS) {
			ret.add(new ArrayList<Movement>(moves));
		}
		return ret;
	}
	
	public static List<List<Movement>> getKingMovements() {
		List<List<Movement>> ret = new ArrayList<List<Movement>>();
		for (List<Movement> moves : KING_MOVEMENTS) {
			ret.add(new ArrayList<Movement>(moves));
		}
		return ret;
	}
	
	public final int dx;
	public final int dy;
	
	public Movement(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public Movement add(Movement m) {
		return new Movement(dx + m.dx, dy + m.dy);
	}
	
}
