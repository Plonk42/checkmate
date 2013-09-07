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
	
	public abstract int getResource();
	
	public boolean is(final Player player) {
		return is(this, player);
	}
	
	public boolean is(final PieceType type) {
		return is(this, type);
	}
	
	public boolean is(final PieceType type, final Player player) {
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
						if (p.is(getPlayer()) || p.is(PieceType.KING)) {
							break;
						}
					}
					// check if moving this piece to this square does not set the player in check
					final Square originalSquare = getSquare();
					getSquare().getGame().movePiece(this, s);
					if (!getSquare().getGame().isCheck(getPlayer())) {
						allowed.add(s);
					}
					
					// revert back to original position
					getSquare().getGame().movePiece(this, originalSquare);
					if (p != null) {
						getSquare().getGame().movePiece(p, s);
					}
				} catch (final OutOfBoardCoordinateException e) {
					// outside the board; stop going in this direction
					break;
				}
			}
		}
		return allowed;
	}
	
	public abstract List<List<Movement>> getAllowedMovements();
	
	public String getDescription() {
		return player + " " + getType();
	}
	
	public boolean hasMoved() {
		return hasMoved;
	}
	
	public void setHasMoved(final boolean hasMoved) {
		this.hasMoved = hasMoved;
	}
	
	public Square getSquare() {
		return square;
	}
	
	public void setSquare(final Square place) {
		this.square = place;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public String toString() {
		return getDescription();
	}
	
}
