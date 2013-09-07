package name.matco.rookoid.game;

import java.util.ArrayList;
import java.util.Collections;
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
	
	public final static List<List<Movement>> LINE_MOVEMENTS;
	static {
		final List<List<Movement>> lines = new ArrayList<List<Movement>>();
		for (final Direction dir : new Direction[] { Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST }) {
			final List<Movement> moves = new ArrayList<Movement>();
			Movement prev = dir.getMovement();
			moves.add(prev);
			for (int i = 0; i < GameUtils.CHESSBOARD_SIZE - 1; i++) {
				prev = prev.withAdd(dir.getMovement());
				moves.add(prev);
			}
			lines.add(Collections.unmodifiableList(moves));
		}
		LINE_MOVEMENTS = Collections.unmodifiableList(lines);
	}
	
	public final static List<List<Movement>> DIAGONAL_MOVEMENTS;
	static {
		final List<List<Movement>> diagonals = new ArrayList<List<Movement>>();
		for (final Direction dir : new Direction[] { Direction.NORTH_EAST, Direction.SOUTH_EAST, Direction.SOUTH_WEST, Direction.NORTH_WEST }) {
			final List<Movement> moves = new ArrayList<Movement>();
			Movement prev = dir.getMovement();
			moves.add(prev);
			for (int i = 0; i < GameUtils.CHESSBOARD_SIZE - 1; i++) {
				prev = prev.withAdd(dir.getMovement());
				moves.add(prev);
			}
			diagonals.add(Collections.unmodifiableList(moves));
		}
		DIAGONAL_MOVEMENTS = Collections.unmodifiableList(diagonals);
	}
	
	public final static List<List<Movement>> KNIGHT_MOVEMENTS;
	static {
		final List<List<Movement>> knightMovements = new ArrayList<List<Movement>>();
		
		knightMovements.add(Collections.singletonList(new Movement(2, 1)));
		knightMovements.add(Collections.singletonList(new Movement(2, -1)));
		knightMovements.add(Collections.singletonList(new Movement(1, 2)));
		knightMovements.add(Collections.singletonList(new Movement(1, -2)));
		knightMovements.add(Collections.singletonList(new Movement(-2, 1)));
		knightMovements.add(Collections.singletonList(new Movement(-2, -1)));
		knightMovements.add(Collections.singletonList(new Movement(-1, 2)));
		knightMovements.add(Collections.singletonList(new Movement(-1, -2)));
		
		KNIGHT_MOVEMENTS = Collections.unmodifiableList(knightMovements);
	}
	
	public final static List<List<Movement>> KING_MOVEMENTS;
	static {
		final List<List<Movement>> kingMovements = new ArrayList<List<Movement>>();
		
		for (final Direction dir : Direction.values()) {
			kingMovements.add(Collections.singletonList(dir.getMovement()));
		}
		KING_MOVEMENTS = Collections.unmodifiableList(kingMovements);
	}
	
	public final int dx;
	public final int dy;
	
	public Movement(final int dx, final int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	public Movement withAdd(final Movement m) {
		return new Movement(dx + m.dx, dy + m.dy);
	}
	
	public Movement withInversion() {
		return new Movement(-dx, -dy);
	}
	
	@Override
	public String toString() {
		return String.format("(%d,%d)", dx, dy);
	}
	
}
