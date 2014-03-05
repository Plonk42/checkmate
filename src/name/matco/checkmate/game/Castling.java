package name.matco.checkmate.game;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;
import android.util.Log;

public class Castling extends Move {
	
	private Piece rook;
	
	public Castling(final Board board, final Player player, final Piece king, final Square to) {
		super(board, player, king, to);
		try {
			sideModification = new PieceModification(board.getPiece(getCorner()).getId(), getCorner(), to.apply(new Movement(to.isKingSide() ? -1 : 1, 0)).getIndex(), null);
		} catch (final OutOfBoardCoordinateException e) {
			// no move could have been done outside board
			Log.e(getClass().getName(), "Move is outside board", e);
		}
	}
	
	public Piece getRook() {
		return rook;
	}
	
	public int getCorner() {
		return getSquareTo().isKingSide() ? player.getKingCorner() : player.getQueenCorner();
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
