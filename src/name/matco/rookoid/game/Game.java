package name.matco.rookoid.game;

import name.matco.rookoid.game.piece.Bishop;
import name.matco.rookoid.game.piece.King;
import name.matco.rookoid.game.piece.Knight;
import name.matco.rookoid.game.piece.Pawn;
import name.matco.rookoid.game.piece.Queen;
import name.matco.rookoid.game.piece.Rook;

public class Game {
	
	private Case[] board = new Case[64];
	
	public void init() {
		for (int i = 0; i < 64; i++) {
			board[i] = new Case(i % 8, i / 8);
		}
		
		// white player
		board[0].setPiece(new Rook(Player.WHITE));
		board[1].setPiece(new Knight(Player.WHITE));
		board[2].setPiece(new Bishop(Player.WHITE));
		board[3].setPiece(new King(Player.WHITE));
		board[4].setPiece(new Queen(Player.WHITE));
		board[5].setPiece(new Bishop(Player.WHITE));
		board[6].setPiece(new Knight(Player.WHITE));
		board[7].setPiece(new Rook(Player.WHITE));
		
		for (int i = 8; i < 16; i++) {
			board[i].setPiece(new Pawn(Player.WHITE));
		}
		
		// black player
		board[63 - 0].setPiece(new Rook(Player.BLACK));
		board[63 - 1].setPiece(new Knight(Player.BLACK));
		board[63 - 2].setPiece(new Bishop(Player.BLACK));
		board[63 - 3].setPiece(new Queen(Player.BLACK));
		board[63 - 4].setPiece(new King(Player.BLACK));
		board[63 - 5].setPiece(new Bishop(Player.BLACK));
		board[63 - 6].setPiece(new Knight(Player.BLACK));
		board[63 - 7].setPiece(new Rook(Player.BLACK));
		
		for (int i = 8; i < 16; i++) {
			board[63 - i].setPiece(new Pawn(Player.BLACK));
		}
	}
	
	public Case[] getBoard() {
		return board;
	}
	
}
