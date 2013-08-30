package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Movement.Direction;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;

public class Pawn extends Piece {
	
	public Pawn(final Player player) {
		super(player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.PAWN;
	}
	
	@Override
	public int getResource() {
		return player.equals(Player.BLACK) ? R.drawable.black_pawn : R.drawable.white_pawn;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		final List<Movement> movements = new ArrayList<Movement>();
		if (Player.WHITE.equals(getPlayer())) {
			try {
				// pawn can not capture on line movements
				if (getSquare().apply(Direction.SOUTH.getMovement()).getPiece() == null) {
					movements.add(Direction.SOUTH.getMovement());
					// not been played yet
					if (square.getCoordinate().y == 1) {
						final Movement twoSquares = new Movement(0, 2);
						if (getSquare().apply(twoSquares).getPiece() == null) {
							movements.add(twoSquares); // TODO : use SOUTH?
						}
					}
				}
				// pawn can move on diagonal if there is a piece to capture
				if (getSquare().apply(Direction.SOUTH_EAST.getMovement()).getPiece() != null) {
					movements.add(Direction.SOUTH_EAST.getMovement());
				}
				// pawn can move on diagonal if there is a piece to capture
				if (getSquare().apply(Direction.SOUTH_WEST.getMovement()).getPiece() != null) {
					movements.add(Direction.SOUTH_WEST.getMovement());
				}
			} catch (final OutOfBoardCoordinateException e) {
				// out of board moves not allowed
			}
		}
		else {
			try {
				// pawn can not capture on line movements
				if (getSquare().apply(Direction.NORTH.getMovement()).getPiece() == null) {
					movements.add(Direction.NORTH.getMovement());
					// not been played yet
					if (square.getCoordinate().y == 6) {
						final Movement twoSquares = new Movement(0, -2);
						if (getSquare().apply(twoSquares).getPiece() == null) {
							movements.add(twoSquares); // TODO : use NORTH?
						}
					}
				}
				// pawn can move on diagonal if there is a piece to capture
				if (getSquare().apply(Direction.NORTH_EAST.getMovement()).getPiece() != null) {
					movements.add(Direction.NORTH_EAST.getMovement());
				}
				// pawn can move on diagonal if there is a piece to capture
				if (getSquare().apply(Direction.NORTH_WEST.getMovement()).getPiece() != null) {
					movements.add(Direction.NORTH_WEST.getMovement());
				}
			} catch (final OutOfBoardCoordinateException e) {
				// out of board moves not allowed
			}
		}
		final List<List<Movement>> ret = new ArrayList<List<Movement>>();
		ret.add(movements);
		return ret;
	}
}
