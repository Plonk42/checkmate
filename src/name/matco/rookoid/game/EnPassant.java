package name.matco.rookoid.game;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;
import name.matco.rookoid.game.piece.Pawn;
import name.matco.rookoid.game.piece.Piece;
import android.util.Log;

public class EnPassant extends Move {
	
	public EnPassant(final Pawn pawn, final Square to) {
		super(pawn, to);
		Piece capturedPawn = null;
		try {
			capturedPawn = Game.getInstance().getSquareAt(to.getCoordinate().x, pawn.getSquare().getCoordinate().y).getPiece();
		} catch (final OutOfBoardCoordinateException e) {
			// no move could have been done outside board
			Log.e(getClass().getName(), "Move is outside board", e);
		}
		this.capturedPiece = capturedPawn;
	}
	
	@Override
	public String toString() {
		return String.format("Pawn %s moves %s and captures \"en passant\" %s", piece, movement, capturedPiece);
	}
	
}
