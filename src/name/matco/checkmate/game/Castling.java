package name.matco.checkmate.game;

import java.util.HashSet;
import java.util.Set;

import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import name.matco.checkmate.game.piece.King;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.Rook;
import android.util.Log;

public class Castling extends Move {
	
	private final int rookFile;
	private Rook rook;
	
	public Castling(final Game game, final Player player, final King king, final Square to) {
		super(game, player, king, to);
		rookFile = to.getCoordinate().x + (to.isKingSide() ? -1 : 1);
	}
	
	public Rook getRook() {
		return rook;
	}
	
	public Coordinate getCorner() {
		return to.isKingSide() ? player.getKingCorner() : player.getQueenCorner();
	}
	
	public Coordinate getRookDestination() {
		try {
			return new Coordinate(rookFile, to.getCoordinate().y);
		} catch (final OutOfBoardCoordinateException e) {
			// no move could have been done outside board
			Log.e(getClass().getName(), "Move is outside board", e);
			return null;
		}
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
		rook = (Rook) game.getBoard().getSquareAt(getCorner()).getPiece();
		Log.d(getClass().getName(), String.format("Found %s at square %s, %d", rook, rook.getSquare(), to.getCoordinate().y));
		game.getBoard().movePiece(rook, game.getBoard().getSquareAt(getRookDestination()));
		rook.setHasMoved(true);
	}
	
	@Override
	public void revertMove(final Game game) {
		// revert king move
		game.getBoard().movePiece(piece, from);
		piece.setHasMoved(false);
		
		// revert rook move
		game.getBoard().movePiece(rook, game.getBoard().getSquareAt(to.isKingSide() ? player.getKingCorner() : player.getQueenCorner()));
		rook.setHasMoved(false);
	}
	
	@Override
	public String getAlgebraic() {
		return to.isKingSide() ? "0-0" : "0-0-0";
	}
	
	@Override
	public String toString() {
		return String.format("Castling %s with move %s", piece, movement);
	}
	
}
