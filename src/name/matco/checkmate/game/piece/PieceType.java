package name.matco.checkmate.game.piece;

import name.matco.checkmate.R;
import name.matco.checkmate.game.Player;

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
		public Class<? extends Piece> getPieceClass() {
			return Pawn.class;
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_pawn : R.drawable.white_pawn;
		}
		
		@Override
		public int getNameResource() {
			return R.string.pawn;
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
		public Class<? extends Piece> getPieceClass() {
			return Rook.class;
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_rook : R.drawable.white_rook;
		}
		
		@Override
		public int getNameResource() {
			return R.string.rook;
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
		public Class<? extends Piece> getPieceClass() {
			return Knight.class;
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_knight : R.drawable.white_knight;
		}
		
		@Override
		public int getNameResource() {
			return R.string.knight;
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
		public Class<? extends Piece> getPieceClass() {
			return Bishop.class;
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_bishop : R.drawable.white_bishop;
		}
		
		@Override
		public int getNameResource() {
			return R.string.bishop;
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
		public Class<? extends Piece> getPieceClass() {
			return Queen.class;
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_queen : R.drawable.white_queen;
		}
		
		@Override
		public int getNameResource() {
			return R.string.queen;
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
		public Class<? extends Piece> getPieceClass() {
			return King.class;
		}
		
		@Override
		public int getImageResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_king : R.drawable.white_king;
		}
		
		@Override
		public int getNameResource() {
			return R.string.king;
		}
	};
	
	abstract public String getAlgebraic();
	
	abstract public Class<? extends Piece> getPieceClass();
	
	abstract public int getImageResource(Player player);
	
	abstract public int getNameResource();
	
	public static PieceType fromAlgebraic(final String algebraic) {
		for (final PieceType pt : PieceType.values()) {
			if (pt.equals(algebraic)) {
				return pt;
			}
		}
		return PieceType.PAWN;
	}
}
