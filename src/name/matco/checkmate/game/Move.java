package name.matco.checkmate.game;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import name.matco.checkmate.game.exception.InvalidAlgebraic;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;

public class Move {
	
	protected final Player player;
	protected final Piece piece;
	protected Piece capturedPiece;
	protected boolean pieceFirstMove;
	
	protected Square from;
	protected final Square to;
	protected final Movement movement;
	
	public static Move fromAlgebraic(final Board board, final Player player, final String a) throws InvalidAlgebraic {
		// TODO manage castling, en passant and promotion
		final String algebraic = a.replaceAll("x", "");
		int index = 0;
		final String algebraicPiece = algebraic.length() == 3 ? Character.toString(algebraic.charAt(index++)) : "";
		final Coordinate coordinate = Coordinate.fromAlgebraic(algebraic.subSequence(index, index + 2));
		// retrieve destination square
		final Square to = board.getSquareAt(coordinate);
		// retrieve piece
		final PieceType type = PieceType.fromAlgebraic(algebraicPiece);
		Piece piece = null;
		final List<Piece> potentialPieces = board.getPieces(player, type);
		if (potentialPieces.size() == 1) {
			piece = potentialPieces.get(0);
		}
		else {
			for (final Piece p : potentialPieces) {
				if (p.getAllowedPositions().contains(to)) {
					piece = p;
				}
			}
		}
		
		return new Move(player, piece, to);
	}
	
	public Move(final Player player, final Piece piece, final Square to) {
		this.player = player;
		this.piece = piece;
		this.pieceFirstMove = !piece.hasMoved();
		this.from = piece.getSquare();
		this.to = to;
		this.movement = from.getMovementTo(to);
		this.capturedPiece = to.getPiece();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	public Movement getMovement() {
		return movement;
	}
	
	public Set<Piece> getRelatedPieces() {
		return Collections.singleton(getPiece());
	}
	
	public boolean isPieceFirstMove() {
		return pieceFirstMove;
	}
	
	public Piece getCapturedPiece() {
		return capturedPiece;
	}
	
	public Square getFrom() {
		return from;
	}
	
	public Square getTo() {
		return to;
	}
	
	public void doMove(final Game game) {
		if (capturedPiece != null) {
			game.getBoard().getPieces().remove(capturedPiece);
			game.getBoard().addCapturedPiece(capturedPiece);
			capturedPiece.getSquare().setPiece(null);
			capturedPiece.setSquare(null);
		}
		game.getBoard().movePiece(piece, to);
		piece.setHasMoved(true);
	}
	
	public void revertMove(final Game game) {
		game.getBoard().movePiece(piece, from);
		if (capturedPiece != null) {
			game.getBoard().removeCapturedPiece(capturedPiece);
			game.getBoard().getPieces().add(capturedPiece);
			to.setPiece(capturedPiece);
			capturedPiece.setSquare(to);
		}
		if (pieceFirstMove) {
			piece.setHasMoved(false);
		}
	}
	
	// TODO : Disambiguating moves
	// TODO : check, checkmate
	public String getAlgebraic() {
		if (capturedPiece != null) {
			if (piece.is(PieceType.PAWN)) {
				return String.format("%sx%s", piece.getSquare().getFile(), to.getAlgebraic());
			}
			return String.format("%sx%s", piece.getType().getAlgebraic(), to.getAlgebraic());
		}
		
		return String.format("%s%s", piece.getType().getAlgebraic(), to.getAlgebraic());
	}
	
	@Override
	public String toString() {
		return capturedPiece == null ? String.format("%s moves %s from %s to %s", piece, movement, from, to) : String.format("%s moves %s  from %s to %s and captures %s", piece, movement, from, to, capturedPiece);
	}
	
}
