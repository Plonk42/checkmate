package name.matco.checkmate.game.piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import android.util.Log;

public class King extends Piece {
	
	public King(final int id, final Player player) {
		super(id, player);
	}
	
	@Override
	public PieceType getType() {
		return PieceType.KING;
	}
	
	@Override
	public List<List<Movement>> getAllowedMovements() {
		// king has already been moved
		if (hasMoved) {
			return Movement.KING_MOVEMENTS;
		}
		// king is in check
		if (getSquare().getBoard().isCheck(getPlayer())) {
			return Movement.KING_MOVEMENTS;
		}
		// castling
		boolean isKingSideCastlingValid = true;
		boolean isQueenSideCastlingValid = true;
		
		try {
			// rook must be at its original square
			final Square kingCorner = getSquare().getBoard().getSquareAt(getPlayer().getKingCorner());
			if (kingCorner.getPiece() == null || kingCorner.getPiece().hasMoved() || !kingCorner.getPiece().is(PieceType.ROOK, getPlayer())) {
				isKingSideCastlingValid = false;
			}
			final Square queenCorner = getSquare().getBoard().getSquareAt(getPlayer().getQueenCorner());
			if (queenCorner.getPiece() == null || queenCorner.getPiece().hasMoved() || !queenCorner.getPiece().is(PieceType.ROOK, getPlayer())) {
				isQueenSideCastlingValid = false;
			}
			Log.i(getClass().getName(), String.format("Kingside castling seems to be possible? " + isKingSideCastlingValid));
			Log.i(getClass().getName(), String.format("Queenside castling seems to be possible? " + isQueenSideCastlingValid));
			
			// kingside castling
			// all squares between king and rook must be empty and king must no be in check in all squares
			if (isKingSideCastlingValid) {
				for (int i = kingCorner.getCoordinate().x + 1; i <= getSquare().getCoordinate().x - 1; i++) {
					final Square s = getSquare().getBoard().getSquareAt(i, getPlayer().getBaseline());
					if (!s.isEmpty()) {
						isKingSideCastlingValid = false;
						break;
					}
					getSquare().getBoard().movePiece(this, s);
					// FIXME replace king at its previous location
					if (getSquare().getBoard().isCheck(getPlayer())) {
						isKingSideCastlingValid = false;
						break;
					}
				}
			}
			Log.i(getClass().getName(), String.format("Kingside castling : all squares between king and rook are empty and king is not in check in all squares for kingside castling = " + isKingSideCastlingValid));
			
			// queenside castling
			// all squares between king and rook must be empty and king must no be in check in all squares
			if (isQueenSideCastlingValid) {
				for (int i = getSquare().getCoordinate().x + 1; i <= queenCorner.getCoordinate().x - 1; i++) {
					final Square s = getSquare().getBoard().getSquareAt(i, getPlayer().getBaseline());
					if (!s.isEmpty()) {
						isQueenSideCastlingValid = false;
						break;
					}
					getSquare().getBoard().movePiece(this, s);
					// FIXME replace king at its previous location
					if (getSquare().getBoard().isCheck(getPlayer())) {
						isQueenSideCastlingValid = false;
						break;
					}
				}
			}
			Log.i(getClass().getName(), String.format("Queenside castling : all squares between king and rook are empty and king is not in check in all squares for queenside castling = " + isQueenSideCastlingValid));
		} catch (final OutOfBoardCoordinateException e) {
			// no way to come here, all retrieved square are obviously inside the board
		}
		
		final List<List<Movement>> movements = new ArrayList<List<Movement>>();
		movements.addAll(Movement.KING_MOVEMENTS);
		if (isKingSideCastlingValid) {
			movements.add(Collections.singletonList(new Movement(2, 0)));
		}
		if (isQueenSideCastlingValid) {
			movements.add(Collections.singletonList(new Movement(-2, 0)));
		}
		return movements;
	}
}
