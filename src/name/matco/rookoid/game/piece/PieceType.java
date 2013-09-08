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
		
		@Override
		public Class<? extends Piece> getPieceClass() {
			return Pawn.class;
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
	};
	
	abstract public String getAlgebraic();
	
	abstract public Class<? extends Piece> getPieceClass();
}
