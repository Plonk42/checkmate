package name.matco.rookoid.game.piece;

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
	};
	
	abstract public String getAlgebraic();
	
}
