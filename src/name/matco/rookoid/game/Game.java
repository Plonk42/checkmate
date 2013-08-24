package name.matco.rookoid.game;

import java.util.ArrayList;
import java.util.List;

import name.matco.rookoid.game.exception.OutOfBoardCoordinateException;
import name.matco.rookoid.game.piece.Bishop;
import name.matco.rookoid.game.piece.King;
import name.matco.rookoid.game.piece.Knight;
import name.matco.rookoid.game.piece.Pawn;
import name.matco.rookoid.game.piece.Piece;
import name.matco.rookoid.game.piece.Queen;
import name.matco.rookoid.game.piece.Rook;

public class Game {
	
	private final Case[] board = new Case[64];
	private final List<Piece> capturedPieces = new ArrayList<Piece>();
	
	// singleton
	private static Game instance;
	
	private Game() {
		init();
	}
	
	public static synchronized Game getInstance() {
		if (instance == null) {
			instance = new Game();
		}
		return instance;
	}
	
	private void init() {
		capturedPieces.clear();
		
		for (int i = 0; i < 64; i++) {
			try {
				board[i] = new Case(this, i % 8, i / 8);
			} catch (OutOfBoardCoordinateException e) {
				// cannot be raised here
			}
		}
		
		// white player
		addPiece(0, new Rook(Player.WHITE));
		addPiece(1, new Knight(Player.WHITE));
		addPiece(2, new Bishop(Player.WHITE));
		addPiece(3, new King(Player.WHITE));
		addPiece(4, new Queen(Player.WHITE));
		addPiece(5, new Bishop(Player.WHITE));
		addPiece(6, new Knight(Player.WHITE));
		addPiece(7, new Rook(Player.WHITE));
		
		for (int i = 8; i < 16; i++) {
			addPiece(i, new Pawn(Player.WHITE));
		}
		
		// black player
		addPiece(63 - 0, new Rook(Player.BLACK));
		addPiece(63 - 1, new Knight(Player.BLACK));
		addPiece(63 - 2, new Bishop(Player.BLACK));
		addPiece(63 - 3, new Queen(Player.BLACK));
		addPiece(63 - 4, new King(Player.BLACK));
		addPiece(63 - 5, new Bishop(Player.BLACK));
		addPiece(63 - 6, new Knight(Player.BLACK));
		addPiece(63 - 7, new Rook(Player.BLACK));
		
		for (int i = 8; i < 16; i++) {
			addPiece(63 - i, new Pawn(Player.BLACK));
		}
	}
	
	public void reset() {
		init();
	}
	
	public Case getCaseAt(Coordinate coordinate) {
		return board[GameUtils.coordinateToIndex(coordinate)];
	}
	
	public Case[] getBoard() {
		return board;
	}
	
	public void addPiece(int index, Piece piece) {
		Case place = board[index];
		place.setPiece(piece);
		piece.setPlace(place);
	}
	
	public List<Piece> getCapturedPieces() {
		return capturedPieces;
	}
	
}
