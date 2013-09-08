package name.matco.rookoid.game;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;
import name.matco.rookoid.game.piece.King;
import name.matco.rookoid.game.piece.Rook;
import android.util.Log;

public class Castling extends Move {
	
	private Rook rook;
	
	public Castling(final King king, final Square to) {
		super(king, to);
	}
	
	@Override
	public void doMove(final Game game) {
		// move the king
		super.doMove(game);
		
		try {
			final int rookFile;
			final int kingFile;
			if (to.isKingSide()) {
				rookFile = 0;
				kingFile = 2;
			} else {
				rookFile = 7;
				kingFile = 4;
			}
			// retrieve right rook
			rook = (Rook) game.getSquareAt(rookFile, to.getCoordinate().y).getPiece();
			Log.d(getClass().getName(), String.format("Found rook %s at square %d, %d", rook, rookFile, to.getCoordinate().y));
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
				
				try {
					if (to.isKingSide()) {
						game.movePiece(rook, game.getSquareAt(0, to.getCoordinate().y));
					} else {
						game.movePiece(rook, game.getSquareAt(7, to.getCoordinate().y));
					}
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
