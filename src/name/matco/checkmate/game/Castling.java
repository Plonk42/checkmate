package name.matco.checkmate.game;

import java.util.HashSet;
import java.util.Set;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.Piece;
import android.util.Log;

public class Castling extends Move {
	
	private int rookDestinationIndex;
	private Piece rook;
	
	public Castling(final Board board, final Player player, final Piece king, final Square to) {
		super(board, player, king, to);
		try {
			this.rookDestinationIndex = to.apply(new Movement(to.isKingSide() ? -1 : 1, 0)).getIndex();
		} catch (final OutOfBoardCoordinateException e) {
			// no move could have been done outside board
			Log.e(getClass().getName(), "Move is outside board", e);
			this.rookDestinationIndex = -1;
		}
	}
	
	public Piece getRook() {
		return rook;
	}
	
	public int getCorner() {
		return getSquareTo().isKingSide() ? player.getKingCorner() : player.getQueenCorner();
	}
	
	public int getRookDestination() {
		return rookDestinationIndex;
	}
	
	@Override
	public Set<Piece> getRelatedPieces() {
		final Set<Piece> relatedPieces = new HashSet<Piece>();
		relatedPieces.add(piece);
		relatedPieces.add(rook);
		return relatedPieces;
	}
	
	@Override
	public void doMove(final Game game) {
		// move the king
		super.doMove(game);
		
		// move rook
		rook = game.getBoard().getSquareAt(getCorner()).getPiece();
		Log.d(getClass().getName(), String.format("Found %s at square %s", rook, rook.getSquare()));
		game.getBoard().movePiece(rook, getRookDestination());
		rook.setHasMoved(true);
	}
	
	@Override
	public void revertMove(final Game game) {
		// revert king move
		game.getBoard().movePiece(piece, from);
		piece.setHasMoved(false);
		
		// revert rook move
		game.getBoard().movePiece(rook, getCorner());
		rook.setHasMoved(false);
	}
	
	@Override
	public String getAlgebraic() {
		return getSquareTo().isKingSide() ? "0-0" : "0-0-0";
	}
	
	@Override
	public String toString() {
		return String.format("Castling %s with move %s", piece, movement);
	}
	
}
