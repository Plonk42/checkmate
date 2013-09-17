package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;

public abstract class Piece {
	
	public static boolean is(final Piece piece, final Player player) {
		return piece.getPlayer().equals(player);
	}
	
	public static boolean is(final Piece piece, final PieceType type) {
		return piece.getType().equals(type);
	}
	
	public static boolean is(final Piece piece, final PieceType type, final Player player) {
		return is(piece, player) && is(piece, type);
	}
	
	protected final Player player;
	
	protected Square square;
	
	protected boolean hasMoved;
	
	public Piece(final Player player) {
		this.player = player;
	}
	
	public abstract PieceType getType();
	
	public final int getImageResource() {
		return getType().getImageResource(getPlayer());
	}
	
	public final boolean is(final Player player) {
		return is(this, player);
	}
	
	public final boolean is(final PieceType type) {
		return is(this, type);
	}
	
	public final boolean is(final PieceType type, final Player player) {
		return is(this, type, player);
	}
	
	public List<Square> getAllowedPositions() {
		final ArrayList<Square> allowed = new ArrayList<Square>();
		for (final List<Movement> directions : getAllowedMovements()) {
			for (final Movement m : directions) {
				try {
					final Square s = square.apply(m);
					final Piece p = s.getPiece();
					// there is a piece on square
					if (p != null) {
						// player can not capture his own pieces or capture the opponent's king
						if (!p.is(getPlayer()) && !p.is(PieceType.KING)) {
							checkCheckAndAdd(s, allowed);
						}
						break;
					}
					// square is empty
					checkCheckAndAdd(s, allowed);
				} catch (final OutOfBoardCoordinateException e) {
					// outside the board; stop going in this direction
					break;
				}
			}
		}
		
		return allowed;
	}
	
	private void checkCheckAndAdd(final Square candidate, final List<Square> to) {
		// ensure that moving this piece does not set the player in check
		final Piece previous = candidate.getPiece();
		getSquare().setPiece(null);
		candidate.setPiece(this);
		if (!getSquare().getGame().isCheck(getPlayer())) {
			to.add(candidate);
		}
		// restore state
		getSquare().setPiece(this);
		candidate.setPiece(previous);
	}
	
	public abstract List<List<Movement>> getAllowedMovements();
	
	public String getDescription() {
		return player + " " + getType();
	}
	
	public final boolean hasMoved() {
		return hasMoved;
	}
	
	public final void setHasMoved(final boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	public final Square getSquare() {
		return square;
	}
	
	public final void setSquare(final Square place) {
		this.square = place;
	}
	
	public final Player getPlayer() {
		return player;
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
}
