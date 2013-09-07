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
			// retrieve right rook
			if (to.getCoordinate().x == 1) {
				rook = (Rook) game.getSquareAt(0, to.getCoordinate().y).getPiece();
				Log.d(getClass().getName(), String.format("Found rook %s at square 0, %d", rook, to.getCoordinate().y));
				game.movePiece(rook, game.getSquareAt(2, to.getCoordinate().y));
			} else {
				rook = (Rook) game.getSquareAt(7, to.getCoordinate().y).getPiece();
				Log.d(getClass().getName(), String.format("Found rook %s at square 7, %d", rook, to.getCoordinate().y));
				game.movePiece(rook, game.getSquareAt(4, to.getCoordinate().y));
			}
			rook.setHasMoved(true);
		} catch (final OutOfBoardCoordinateException e) {
			// no move could have been done outside board
			Log.e(getClass().getName(), "Move is outside board", e);
		}
	}
	
	@Override
	public Move getRevertMove() {
		final Move parent = this;
		return new Move(piece, to) {
			@Override
			public void doMove(final Game game) {
				// revert king move
				game.movePiece(parent.getPiece(), parent.getFrom());
				parent.getPiece().setHasMoved(false);
				
				try {
					if (to.getCoordinate().x == 1) {
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
		// FIXME : update this when WHITE will move to the bottom
		return to.getFile() == 'b' ? "0-0" : "0-0-0";
	}
	
	@Override
	public String toString() {
		return String.format("Castling %s with move %s", piece, movement);
	}
	
}
