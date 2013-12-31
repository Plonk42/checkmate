package name.matco.checkmate.game;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Pawn;

public class EnPassant extends Move {
	
	public EnPassant(final Game game, final Player player, final Pawn pawn, final Square to) throws OutOfBoardCoordinateException {
		super(game, player, pawn, to);
		this.capturedPiece = game.getBoard().getSquareAt(to.getCoordinate().x, pawn.getSquare().getCoordinate().y).getPiece();
	}
	
	@Override
	public String getAlgebraic() {
		return String.format("%sx%se.p.", piece.getSquare().getFile(), to.getAlgebraic());
	}
	
	@Override
	public String toString() {
		return String.format("Pawn %s moves %s and captures \"en passant\" %s", piece, movement, capturedPiece);
	}
	
}
