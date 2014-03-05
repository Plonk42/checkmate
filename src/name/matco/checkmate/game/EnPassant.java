package name.matco.checkmate.game;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;

public class EnPassant extends Move {
	
	public EnPassant(final Board board, final Player player, final Piece pawn, final Square to) throws OutOfBoardCoordinateException {
		super(board, player, pawn, to);
		final Square opponentPawnTo = to.apply(player.getForward().getMovement().withInversion());
		sideModification = new PieceModification(opponentPawnTo.getPiece().getId(), opponentPawnTo.getIndex(), null, null);
	}
	
	@Override
	public String getAlgebraic() {
		return String.format("%sx%se.p.", getPiece().getSquare().getFile(), getSquareTo().getAlgebraic());
	}
	
	@Override
	public String toString() {
		return String.format("Pawn %s moves %s and captures \"en passant\" %s", getPiece(), mainModification.getMovement(), getCapturedPiece());
	}
	
}
