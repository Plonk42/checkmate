package name.matco.checkmate.game.piece;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import name.matco.checkmate.R;
import name.matco.checkmate.game.GameUtils;
import name.matco.checkmate.game.Move;
import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Movement.Direction;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.exception.InvalidAlgebraic;
import name.matco.checkmate.game.exception.OutOfBoardCoordinateException;
import android.util.Log;

public enum PieceType {
	
	PAWN {
		@Override
		public String toString() {
			return "Pawn";
		}
		
		@Override
		public String getAlgebraic() {
			return ""; // not letter for Pawn, or (P)
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_pawn : R.drawable.white_pawn;
		}
		
		@Override
		public int getNameResource() {
			return R.string.pawn;
		}
		
		@Override
		public List<List<Movement>> getAllowedMovements(final Piece piece) {
			final List<List<Movement>> movements = new ArrayList<List<Movement>>();
			final Direction forward = piece.getPlayer().getForward();
			
			try {
				final List<Movement> forwardMovements = new ArrayList<Movement>();
				// pawn can not capture on line movements
				if (piece.getSquare().apply(forward.getMovement()).getPiece() == null) {
					forwardMovements.add(forward.getMovement());
					// not been played yet
					if (!piece.hasMoved()) {
						final Movement twoSquares = forward.getMovement().withAdd(forward.getMovement());
						if (piece.getSquare().apply(twoSquares).getPiece() == null) {
							forwardMovements.add(twoSquares);
						}
					}
				}
				movements.add(forwardMovements);
				
				// pawn can move on diagonal if there is a piece to capture or "en passant"capture
				final Move lastMove = piece.getBoard().getLastMove();
				final Movement withEast = forward.getMovement().withAdd(Direction.EAST.getMovement());
				if (piece.getSquare().apply(withEast).getPiece() != null) {
					movements.add(Collections.singletonList(withEast));
				} else {
					// "en passant"
					if (lastMove != null && lastMove.getPiece().is(PieceType.PAWN, piece.getPlayer().getOpponent()) && Math.abs(lastMove.getFrom() - lastMove.getTo()) == 2 * GameUtils.CHESSBOARD_SIZE) {
						if (lastMove.getPiece().equals(piece.getSquare().apply(Direction.EAST.getMovement()).getPiece())) {
							movements.add(Collections.singletonList(withEast));
						}
					}
				}
				// pawn can move on diagonal if there is a piece to capture
				final Movement withWest = forward.getMovement().withAdd(Direction.WEST.getMovement());
				if (piece.getSquare().apply(withWest).getPiece() != null) {
					movements.add(Collections.singletonList(withWest));
				} else {
					// "en passant"
					if (lastMove != null && lastMove.getPiece().is(PieceType.PAWN, piece.getPlayer().getOpponent()) && Math.abs(lastMove.getFrom() - lastMove.getTo()) == 2 * GameUtils.CHESSBOARD_SIZE) {
						if (lastMove.getPiece().equals(piece.getSquare().apply(Direction.WEST.getMovement()).getPiece())) {
							movements.add(Collections.singletonList(withWest));
						}
					}
				}
			} catch (final OutOfBoardCoordinateException e) {
				// out of board moves not allowed
			}
			
			return movements;
		}
	},
	ROOK {
		@Override
		public String toString() {
			return "Rook";
		}
		
		@Override
		public String getAlgebraic() {
			return "R";
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_rook : R.drawable.white_rook;
		}
		
		@Override
		public int getNameResource() {
			return R.string.rook;
		}
		
		@Override
		public List<List<Movement>> getAllowedMovements(final Piece piece) {
			return Movement.LINE_MOVEMENTS;
		}
	},
	KNIGHT {
		@Override
		public String toString() {
			return "Knight";
		}
		
		@Override
		public String getAlgebraic() {
			return "N";
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_knight : R.drawable.white_knight;
		}
		
		@Override
		public int getNameResource() {
			return R.string.knight;
		}
		
		@Override
		public List<List<Movement>> getAllowedMovements(final Piece piece) {
			return Movement.KNIGHT_MOVEMENTS;
		}
	},
	BISHOP {
		@Override
		public String toString() {
			return "Bishop";
		}
		
		@Override
		public String getAlgebraic() {
			return "B";
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_bishop : R.drawable.white_bishop;
		}
		
		@Override
		public int getNameResource() {
			return R.string.bishop;
		}
		
		@Override
		public List<List<Movement>> getAllowedMovements(final Piece piece) {
			return Movement.DIAGONAL_MOVEMENTS;
		}
	},
	QUEEN {
		@Override
		public String toString() {
			return "Queen";
		}
		
		@Override
		public String getAlgebraic() {
			return "Q";
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_queen : R.drawable.white_queen;
		}
		
		@Override
		public int getNameResource() {
			return R.string.queen;
		}
		
		@Override
		public List<List<Movement>> getAllowedMovements(final Piece piece) {
			final List<List<Movement>> movements = new ArrayList<List<Movement>>(Movement.LINE_MOVEMENTS);
			movements.addAll(Movement.DIAGONAL_MOVEMENTS);
			return movements;
		}
	},
	KING {
		@Override
		public String toString() {
			return "King";
		}
		
		@Override
		public String getAlgebraic() {
			return "K";
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_king : R.drawable.white_king;
		}
		
		@Override
		public int getNameResource() {
			return R.string.king;
		}
		
		@Override
		public List<List<Movement>> getAllowedMovements(final Piece piece) {
			// king has already been moved
			if (piece.hasMoved()) {
				return Movement.KING_MOVEMENTS;
			}
			// king is in check
			if (piece.getBoard().isCheck(piece.getPlayer())) {
				return Movement.KING_MOVEMENTS;
			}
			// castling
			boolean isKingSideCastlingValid = true;
			boolean isQueenSideCastlingValid = true;
			
			// rook must be at its original square
			final Square kingCorner = piece.getBoard().getSquareAt(piece.getPlayer().getKingCorner());
			if (kingCorner.getPiece() == null || kingCorner.getPiece().hasMoved() || !kingCorner.getPiece().is(PieceType.ROOK, piece.getPlayer())) {
				isKingSideCastlingValid = false;
			}
			final Square queenCorner = piece.getBoard().getSquareAt(piece.getPlayer().getQueenCorner());
			if (queenCorner.getPiece() == null || queenCorner.getPiece().hasMoved() || !queenCorner.getPiece().is(PieceType.ROOK, piece.getPlayer())) {
				isQueenSideCastlingValid = false;
			}
			Log.i(getClass().getName(), String.format("Kingside castling seems to be possible? " + isKingSideCastlingValid));
			Log.i(getClass().getName(), String.format("Queenside castling seems to be possible? " + isQueenSideCastlingValid));
			
			// kingside castling
			// all squares between king and rook must be empty and king must no be in check in all squares
			if (isKingSideCastlingValid) {
				for (int i = kingCorner.getIndex(); i <= piece.getSquare().getIndex() - 1; i++) {
					final Square s = piece.getBoard().getSquareAt(i);
					if (!s.isEmpty()) {
						isKingSideCastlingValid = false;
						break;
					}
					piece.getBoard().movePiece(piece, s.getIndex());
					// FIXME replace king at its previous location
					if (piece.getBoard().isCheck(piece.getPlayer())) {
						isKingSideCastlingValid = false;
						break;
					}
				}
			}
			Log.i(getClass().getName(), String.format("Kingside castling : all squares between king and rook are empty and king is not in check in all squares for kingside castling = " + isKingSideCastlingValid));
			
			// queenside castling
			// all squares between king and rook must be empty and king must no be in check in all squares
			if (isQueenSideCastlingValid) {
				for (int i = piece.getSquare().getIndex() + 1; i <= queenCorner.getIndex() - 1; i++) {
					final Square s = piece.getBoard().getSquareAt(i);
					if (!s.isEmpty()) {
						isQueenSideCastlingValid = false;
						break;
					}
					piece.getBoard().movePiece(piece, s.getIndex());
					// FIXME replace king at its previous location
					if (piece.getBoard().isCheck(piece.getPlayer())) {
						isQueenSideCastlingValid = false;
						break;
					}
				}
			}
			Log.i(getClass().getName(), String.format("Queenside castling : all squares between king and rook are empty and king is not in check in all squares for queenside castling = " + isQueenSideCastlingValid));
			
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
	};
	
	abstract public String getAlgebraic();
	
	abstract public List<List<Movement>> getAllowedMovements(Piece piece);
	
	abstract public int getImageResource(Player player);
	
	abstract public int getNameResource();
	
	public static PieceType fromAlgebraic(final String algebraic) throws InvalidAlgebraic {
		if (algebraic.trim().isEmpty()) {
			return PieceType.PAWN;
		}
		for (final PieceType pt : PieceType.values()) {
			if (pt.getAlgebraic().equals(algebraic)) {
				return pt;
			}
		}
		throw new InvalidAlgebraic(algebraic);
	}
}
