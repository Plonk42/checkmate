package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.matco.rookoid.game.Move;
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
	public List<List<Movement>> getAllowedMovements() {
		final List<Movement> movements = new ArrayList<Movement>();
		final Direction forward = getPlayer().getForward();
		
		try {
			// pawn can not capture on line movements
			if (getSquare().apply(forward.getMovement()).getPiece() == null) {
				movements.add(forward.getMovement());
				// not been played yet
				if (!hasMoved()) {
					final Movement twoSquares = forward.getMovement().withAdd(forward.getMovement());
					if (getSquare().apply(twoSquares).getPiece() == null) {
						movements.add(twoSquares);
					}
				}
			}
			
			// pawn can move on diagonal if there is a piece to capture or "en passant"capture
			final Move lastMove = getSquare().getGame().getLastMove();
			final Movement withEast = forward.getMovement().withAdd(Direction.EAST.getMovement());
			if (getSquare().apply(withEast).getPiece() != null) {
				movements.add(withEast);
			} else {
				// "en passant"
				if (lastMove != null && lastMove.getPiece().is(PieceType.PAWN, getPlayer().getOpponent()) && Math.abs(lastMove.getFrom().getCoordinate().y - lastMove.getTo().getCoordinate().y) == 2) {
					if (lastMove.getPiece().equals(getSquare().apply(Direction.EAST.getMovement()).getPiece())) {
						movements.add(withEast);
					}
				}
			}
			// pawn can move on diagonal if there is a piece to capture
			final Movement withWest = forward.getMovement().withAdd(Direction.WEST.getMovement());
			if (getSquare().apply(withWest).getPiece() != null) {
				movements.add(withWest);
			} else {
				// "en passant"
				if (lastMove != null && lastMove.getPiece().is(PieceType.PAWN, getPlayer().getOpponent()) && Math.abs(lastMove.getFrom().getCoordinate().y - lastMove.getTo().getCoordinate().y) == 2) {
					if (lastMove.getPiece().equals(getSquare().apply(Direction.WEST.getMovement()).getPiece())) {
						movements.add(withWest);
					}
				}
			}
		} catch (final OutOfBoardCoordinateException e) {
			// out of board moves not allowed
		}
		
		return Collections.singletonList(movements);
	}
}
