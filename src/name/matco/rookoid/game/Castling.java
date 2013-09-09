package name.matco.rookoid.game;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;
import name.matco.rookoid.game.piece.King;
import name.matco.rookoid.game.piece.Rook;
import android.util.Log;

public class Castling extends Move {
	
	private final int kingFile;
	private Rook rook;
	
	public Castling(final King king, final Square to) {
		super(king, to);
		kingFile = getPiece().getSquare().getCoordinate().x + (to.isKingSide() ? 2 : -2);
	}
	
	@Override
	public void doMove(final Game game) {
		// move the king
		super.doMove(game);
		
		try {
			rook = (Rook) game.getSquareAt(to.isKingSide() ? game.getActivePlayer().getKingCorner() : game.getActivePlayer().getQueenCorner()).getPiece();
			Log.d(getClass().getName(), String.format("Found rook %s at square %d, %d", rook, rook.getSquare(), to.getCoordinate().y));
			game.movePiece(rook, game.getSquareAt(kingFile, to.getCoordinate().y));
			
			rook.setHasMoved(true);
		} catch (final OutOfBoardCoordinateException e) {
			// no move could have been done outside board
			Log.e(getClass().getName(), "Move is outside board", e);
		}
	}
	
	@Override
	public Move getRevertMove() {
		return new Move(piece, to) {
			@Override
			public void doMove(final Game game) {
				// revert king move
				game.movePiece(piece, Castling.this.getFrom());
				piece.setHasMoved(false);
				
				// revert rook move
				try {
					game.movePiece(rook, game.getSquareAt(kingFile, to.getCoordinate().y));
				} catch (final OutOfBoardCoordinateException e) {
					// no move could have been done outside board
					Log.e(getClass().getName(), "Move is outside board", e);
				}
				rook.setHasMoved(false);
			}
		};
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
