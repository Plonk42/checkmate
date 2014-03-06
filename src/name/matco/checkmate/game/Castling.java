package name.matco.checkmate.game;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;
import android.util.Log;

public class Castling extends Move {
	
	public Castling(final Board board, final Player player, final Piece king, final Square to) {
		super(board, player, king, to);
		try {
			final int corner = getSquareTo().isKingSide() ? player.getKingCorner() : player.getQueenCorner();
			sideModification = new PieceModification(board.getPiece(corner).getId(), corner, to.apply(new Movement(to.isKingSide() ? -1 : 1, 0)).getIndex());
		} catch (final OutOfBoardCoordinateException e) {
			// no move could have been done outside board
			Log.e(getClass().getName(), "Move is outside board", e);
		}
	}
	
	@Override
	public String getAlgebraic() {
		return getSquareTo().isKingSide() ? "0-0" : "0-0-0";
	}
	
	@Override
	public String toString() {
		return String.format("Castling %s with move %s", getPiece(), mainModification.getMovement());
	}
	
}
