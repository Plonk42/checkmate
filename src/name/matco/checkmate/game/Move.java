package name.matco.checkmate.game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
	protected final PieceModification mainModification;
	protected PieceModification sideModification;
	
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
		mainModification = new PieceModification(piece.getId(), piece.getSquare().getIndex(), to.getIndex(), null);
		if (to.getPiece() != null) {
			sideModification = new PieceModification(to.getPiece().getId(), to.getIndex(), null, null);
		}
		else {
			sideModification = null;
		}
	}
	
	public Move(final Parcel in) {
		player = Player.valueOf(in.readString());
		mainModification = in.readParcelable(null);
		sideModification = in.readParcelable(null);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public List<PieceModification> getModifications() {
		final List<PieceModification> modifications = new ArrayList<PieceModification>();
		modifications.add(mainModification);
		if (sideModification != null) {
			modifications.add(sideModification);
		}
		return modifications;
	}
	
	public List<Integer> getRelatedPiece() {
		final List<Integer> pieces = new ArrayList<Integer>();
		pieces.add(mainModification.getPieceId());
		if (sideModification != null) {
			pieces.add(sideModification.getPieceId());
		}
		return pieces;
	}
	
	public boolean isCapture() {
		return sideModification != null && sideModification.getTo() == null;
	}
	
	public Piece getPiece() {
		return board.getPieceFromId(mainModification.getPieceId());
	}
	
	@JsonIgnore
	public Piece getCapturedPiece() {
		return board.getPieceFromId(sideModification.getPieceId());
	}
	
	public int getFrom() {
		return mainModification.getFrom();
	}
	
	public int getTo() {
		return mainModification.getTo();
	}
	
	@JsonIgnore
	public Square getSquareFrom() {
		return board.getSquareAt(mainModification.getFrom());
	}
	
	@JsonIgnore
	public Square getSquareTo() {
		return board.getSquareAt(mainModification.getTo());
	}
	
	public void doMove() {
		if (isCapture()) {
			board.capturePiece(getCapturedPiece());
		}
		board.movePiece(getPiece(), mainModification.getTo());
		date = new Date();
	}
	
	public void revertMove() {
		board.movePiece(getPiece(), mainModification.getFrom());
		if (isCapture()) {
			board.releasePiece(getCapturedPiece(), sideModification.getFrom());
		}
	}
	
	// TODO : Disambiguating moves
	// TODO : check, checkmate
	public String getAlgebraic() {
		final Piece piece = getPiece();
		if (isCapture()) {
			if (piece.is(PieceType.PAWN)) {
				return String.format("%sx%s", piece.getSquare().getFile(), getSquareTo().getAlgebraic());
			}
			return String.format("%sx%s", piece.getType().getAlgebraic(), getSquareTo().getAlgebraic());
		}
		
		return String.format("%s%s", piece.getType().getAlgebraic(), getSquareTo().getAlgebraic());
	}
	
	@Override
	public String toString() {
		final String algebraicFrom = GameUtils.algebraicFromIndex(mainModification.getFrom());
		final String algebraicTo = GameUtils.algebraicFromIndex(mainModification.getTo());
		if (isCapture()) {
			return String.format("%s moves %s from %s to %s and captures %s", getPiece(), mainModification.getMovement(), algebraicFrom, algebraicTo, sideModification.getPieceId());
		}
		return String.format("%s moves %s from %s to %s", getPiece(), mainModification.getMovement(), algebraicFrom, algebraicTo);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(player.name());
		dest.writeParcelable(mainModification, 0);
		dest.writeParcelable(sideModification, 0);
	}
	
}
