package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Piece;
import name.matco.rookoid.game.piece.PieceType;

public class Move {
	
	protected final Piece piece;
	protected Piece capturedPiece;
	protected boolean pieceFirstMove;
	
	protected Square from;
	protected final Square to;
	protected final Movement movement;
	
	public Move(final Piece piece, final Square to) {
		this.piece = piece;
		this.pieceFirstMove = !piece.hasMoved();
		this.from = piece.getSquare();
		this.to = to;
		this.movement = from.getMovementTo(to);
		this.capturedPiece = to.getPiece();
	}
	
	public Movement getMovement() {
		return movement;
	}
	
	public Piece getPiece() {
		return piece;
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
			game.getCapturedPieces().add(capturedPiece);
			capturedPiece.getSquare().setPiece(null);
		}
		game.movePiece(piece, to);
		piece.setHasMoved(true);
	}
	
	public Move getRevertMove() {
		final Move self = this;
		return new Move(piece, to) {
			@Override
			public void doMove(final Game game) {
				game.movePiece(self.getPiece(), self.getFrom());
				if (self.getCapturedPiece() != null) {
					self.getCapturedPiece().getSquare().setPiece(self.getCapturedPiece());
					game.getCapturedPieces().remove(self.getCapturedPiece());
				}
				if (self.isPieceFirstMove()) {
					self.getPiece().setHasMoved(false);
				}
			}
		};
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
		return capturedPiece == null ? String.format("%s moves %s", piece, movement) : String.format("%s moves %s and captures %s", piece, movement, capturedPiece);
	}
	
}
