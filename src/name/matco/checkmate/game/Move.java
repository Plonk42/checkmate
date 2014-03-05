package name.matco.checkmate.game;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import name.matco.checkmate.game.exception.InvalidAlgebraic;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Move implements Parcelable {
	
	// TODO : choose a better key?
	public static final String PARCELABLE_KEY = Move.class.getName();
	
	public static final Parcelable.Creator<Move> CREATOR = new Parcelable.Creator<Move>() {
		@Override
		public Move createFromParcel(final Parcel in) {
			return new Move(in);
		}
		
		@Override
		public Move[] newArray(final int size) {
			return new Move[size];
		}
	};
	
	protected Board board;
	protected final Player player;
	protected final Piece piece;
	protected Piece capturedPiece;
	
	protected int from;
	protected final int to;
	protected final Movement movement;
	
	private Date date;
	
	public static Move fromAlgebraic(final Board board, final Player player, final String a) throws InvalidAlgebraic {
		// TODO manage castling, en passant and promotion
		final String algebraic = a.replaceAll("x", "");
		int index = 0;
		final String algebraicPiece = algebraic.length() == 3 ? Character.toString(algebraic.charAt(index++)) : "";
		// retrieve destination square
		final int i = GameUtils.indexFromAlgebraic(algebraic.subSequence(index, index + 2));
		final Square to = board.getSquareAt(i);
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
		
		return new Move(board, player, piece, to);
	}
	
	public Move(final Board board, final Player player, final Piece piece, final Square to) {
		this.board = board;
		this.player = player;
		this.piece = piece;
		this.from = piece.getSquare().getIndex();
		this.to = to.getIndex();
		movement = GameUtils.getMovement(from, this.to);
		capturedPiece = to.getPiece();
	}
	
	public Move(final Parcel in) {
		player = Player.valueOf(in.readString());
		piece = in.readParcelable(null);
		capturedPiece = in.readParcelable(null);
		from = in.readParcelable(null);
		to = in.readParcelable(null);
		movement = GameUtils.getMovement(from, to);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Piece getPiece() {
		return piece;
	}
	
	@JsonIgnore
	public Set<Piece> getRelatedPieces() {
		return Collections.singleton(getPiece());
	}
	
	@JsonIgnore
	public Piece getCapturedPiece() {
		return capturedPiece;
	}
	
	public int getFrom() {
		return from;
	}
	
	public int getTo() {
		return to;
	}
	
	@JsonIgnore
	public Square getSquareFrom() {
		return board.getSquareAt(from);
	}
	
	@JsonIgnore
	public Square getSquareTo() {
		return board.getSquareAt(to);
	}
	
	public void doMove() {
		if (capturedPiece != null) {
			board.capturePiece(capturedPiece);
		}
		board.movePiece(piece, to);
		date = new Date();
	}
	
	public void revertMove() {
		board.movePiece(piece, from);
		if (capturedPiece != null) {
			board.releasePiece(capturedPiece, to);
		}
	}
	
	// TODO : Disambiguating moves
	// TODO : check, checkmate
	public String getAlgebraic() {
		if (capturedPiece != null) {
			if (piece.is(PieceType.PAWN)) {
				return String.format("%sx%s", piece.getSquare().getFile(), getSquareTo().getAlgebraic());
			}
			return String.format("%sx%s", piece.getType().getAlgebraic(), getSquareTo().getAlgebraic());
		}
		
		return String.format("%s%s", piece.getType().getAlgebraic(), getSquareTo().getAlgebraic());
	}
	
	@Override
	public String toString() {
		final String algebraicFrom = GameUtils.algebraicFromIndex(from);
		final String algebraicTo = GameUtils.algebraicFromIndex(to);
		if (capturedPiece != null) {
			return String.format("%s moves %s  from %s to %s and captures %s", piece, movement, algebraicFrom, algebraicTo, capturedPiece);
		}
		return String.format("%s moves %s from %s to %s", piece, movement, algebraicFrom, algebraicTo);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(player.name());
		dest.writeParcelable(piece, 0);
		dest.writeParcelable(capturedPiece, 0);
		dest.writeInt(from);
		dest.writeInt(to);
	}
	
}
