package name.matco.checkmate.game.piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.matco.checkmate.game.Move;
import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Movement.Direction;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;

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
		final List<List<Movement>> movements = new ArrayList<List<Movement>>();
		final Direction forward = getPlayer().getForward();
		
		try {
			final List<Movement> forwardMovements = new ArrayList<Movement>();
			// pawn can not capture on line movements
			if (getSquare().apply(forward.getMovement()).getPiece() == null) {
				forwardMovements.add(forward.getMovement());
				// not been played yet
				if (!hasMoved()) {
					final Movement twoSquares = forward.getMovement().withAdd(forward.getMovement());
					if (getSquare().apply(twoSquares).getPiece() == null) {
						forwardMovements.add(twoSquares);
					}
				}
			}
			movements.add(forwardMovements);
			
			// pawn can move on diagonal if there is a piece to capture or "en passant"capture
			final Move lastMove = getSquare().getBoard().getLastMove();
			final Movement withEast = forward.getMovement().withAdd(Direction.EAST.getMovement());
			if (getSquare().apply(withEast).getPiece() != null) {
				movements.add(Collections.singletonList(withEast));
			} else {
				// "en passant"
				if (lastMove != null && lastMove.getPiece().is(PieceType.PAWN, getPlayer().getOpponent()) && Math.abs(lastMove.getFrom().getCoordinate().y - lastMove.getTo().getCoordinate().y) == 2) {
					if (lastMove.getPiece().equals(getSquare().apply(Direction.EAST.getMovement()).getPiece())) {
						movements.add(Collections.singletonList(withEast));
					}
				}
			}
			// pawn can move on diagonal if there is a piece to capture
			final Movement withWest = forward.getMovement().withAdd(Direction.WEST.getMovement());
			if (getSquare().apply(withWest).getPiece() != null) {
				movements.add(Collections.singletonList(withWest));
			} else {
				// "en passant"
				if (lastMove != null && lastMove.getPiece().is(PieceType.PAWN, getPlayer().getOpponent()) && Math.abs(lastMove.getFrom().getCoordinate().y - lastMove.getTo().getCoordinate().y) == 2) {
					if (lastMove.getPiece().equals(getSquare().apply(Direction.WEST.getMovement()).getPiece())) {
						movements.add(Collections.singletonList(withWest));
					}
				}
			}
		} catch (final OutOfBoardCoordinateException e) {
			// out of board moves not allowed
		}
		
		return movements;
	}
}
