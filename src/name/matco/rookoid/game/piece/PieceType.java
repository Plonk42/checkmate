package name.matco.rookoid.game.piece;

import name.matco.rookoid.R;
import name.matco.rookoid.game.Player;

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
		int getResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_pawn : R.drawable.white_pawn;
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
		int getResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_rook : R.drawable.white_rook;
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
		int getResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_knight : R.drawable.white_knight;
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
		int getResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_bishop : R.drawable.white_bishop;
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
		public int getResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_queen : R.drawable.white_queen;
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
		public int getResource(final Player player) {
			return Player.BLACK.equals(player) ? R.drawable.black_king : R.drawable.white_king;
		}
	};
	
	abstract public String getAlgebraic();
	
	abstract public Class<? extends Piece> getPieceClass();
	
	abstract int getResource(Player player);
}
