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
import android.util.Log;

public class Game {
	
	private final Square[] board = new Square[64];
	private final List<Piece> capturedPieces = new ArrayList<Piece>();
	private List<Move> moves = new ArrayList<Move>();
	private int progression = 0;
	
	private Player activePlayer = Player.WHITE;
	
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
		moves.clear();
		capturedPieces.clear();
		progression = 0;
		
		for (int i = 0; i < 64; i++) {
			try {
				board[i] = new Square(this, i % 8, i / 8);
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
	
	public Square getSquareAt(Coordinate coordinate) {
		return board[GameUtils.coordinateToIndex(coordinate)];
	}
	
	public Square getSquareAt(int x, int y) throws OutOfBoardCoordinateException {
		return getSquareAt(new Coordinate(x, y));
	}
	
	public Square[] getBoard() {
		return board;
	}
	
	public void addPiece(int index, Piece piece) {
		Square place = board[index];
		place.setPiece(piece);
		piece.setSquare(place);
	}
	
	public List<Piece> getCapturedPieces() {
		return capturedPieces;
	}
	
	public void movePieceTo(Piece p, Square s) {
		// keep log of movement
		Move m = new Move(p, p.getSquare().getMovementTo(s), s.getPiece());
		Log.i(getClass().getName(), String.format("Add move %s", m));
		if (progression < moves.size()) {
			moves = moves.subList(0, progression);
			progression = moves.size();
		}
		moves.add(m);
		progression++;
		
		movePieceToWithoutLog(p, s);
	}
	
	private void movePieceToWithoutLog(Piece p, Square s) {
		// change active player
		activePlayer = activePlayer.next();
		
		// move piece
		p.getSquare().setPiece(null);
		s.setPiece(p);
		p.setSquare(s);
	}
	
	public Player getActivePlayer() {
		return activePlayer;
	}
	
	public boolean goPrevious() {
		if (progression > 0) {
			Move m = moves.get(--progression);
			try {
				Log.i(getClass().getName(), String.format("Moving piece %s back using movement %s", m.getPiece(), m.getMovement().withInversion()));
				Square from = m.getPiece().getSquare();
				movePieceToWithoutLog(m.getPiece(), from.apply(m.getMovement().withInversion()));
				from.setPiece(m.getCapturedPiece());
			} catch (OutOfBoardCoordinateException e) {
				// no move could have been done outside board
			}
			return true;
		}
		return false;
	}
	
	public boolean goNext() {
		if (progression < moves.size()) {
			Move m = moves.get(progression++);
			try {
				movePieceToWithoutLog(m.getPiece(), m.getPiece().getSquare().apply(m.getMovement()));
			} catch (OutOfBoardCoordinateException e) {
				// no move could have been done outside board
			}
			return true;
		}
		return false;
	}
	
}
