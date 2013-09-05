package name.matco.rookoid.game.piece;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;

public abstract class Piece {
	
	protected final Player player;
	
	protected Square square;
	
	protected boolean hasMoved;
	
	public Piece(final Player player) {
		this.player = player;
	}
	
	public abstract PieceType getType();
	
	public abstract int getResource();
	
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
						if (getPlayer().equals(p.getPlayer()) || PieceType.KING.equals(p.getType())) {
							break;
						}
					}
					// check if moving this piece to this square does not set the player in check
					final Square originalSquare = getSquare();
					getSquare().getGame().movePieceToInternal(this, s);
					if (!getSquare().getGame().isCheck(getPlayer())) {
						allowed.add(s);
					}
					
					// revert back to original position
					getSquare().getGame().movePieceToInternal(this, originalSquare);
					if (p != null) {
						getSquare().getGame().movePieceToInternal(p, s);
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
	
	public boolean getHasMoved() {
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
