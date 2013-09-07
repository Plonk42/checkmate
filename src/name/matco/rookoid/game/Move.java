package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Piece;

public class Move {
	
	protected final Piece piece;
	protected Piece capturedPiece;
	protected boolean pieceFirstMove;
	
	protected final Square from;
	protected final Square to;
	protected final Movement movement;
	
	public Move(final Piece piece, final Square to) {
		this.piece = piece;
		this.pieceFirstMove = !piece.hasMoved();
		this.from = piece.getSquare();
		this.to = to;
		this.movement = from.getMovementTo(to);
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
		if (to.getPiece() != null) {
			capturedPiece = to.getPiece();
			game.getCapturedPieces().add(capturedPiece);
			to.setPiece(null);
		}
		game.movePiece(piece, to);
		piece.setHasMoved(true);
	}
	
	// FIXME : when needed, this should call piece.setHasMoved(false);
	public Move getRevertMove() {
		final Move parent = this;
		return new Move(piece, to) {
			@Override
			public void doMove(final Game game) {
				game.movePiece(parent.getPiece(), parent.getFrom());
				to.setPiece(parent.getCapturedPiece());
				game.getCapturedPieces().remove(parent.getCapturedPiece());
				if (parent.isPieceFirstMove()) {
					parent.getPiece().setHasMoved(false);
				}
			}
		};
	}
	
	@Override
	public String toString() {
		return capturedPiece == null ? String.format("Piece %s moves %s", piece, movement) : String.format("Piece %s moves %s, capture %s", piece, movement, capturedPiece);
	}
	
}
