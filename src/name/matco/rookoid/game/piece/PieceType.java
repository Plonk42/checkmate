package name.matco.rookoid.game.piece;

public enum PieceType {
	
	PAWN {
		@Override
		public String toString() {
			return "Pawn";
		}
	},
	ROOK {
		@Override
		public String toString() {
			return "Rook";
		}	
	},
	KNIGHT {
		@Override
		public String toString() {
			return "Knight";
		}	
	},
	BISHOP {
		@Override
		public String toString() {
			return "Bishop";
		}
	},
	QUEEN {
		@Override
		public String toString() {
			return "Queen";
		}	
	},
	KING {
		@Override
		public String toString() {
			return "King";
		}	
	}
	
}
