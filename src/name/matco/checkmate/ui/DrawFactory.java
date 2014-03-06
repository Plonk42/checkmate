package name.matco.checkmate.ui;

import java.util.Hashtable;

import name.matco.checkmate.R;
import name.matco.checkmate.game.GameUtils;
import name.matco.checkmate.game.Movement;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.Square;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.game.piece.PieceType;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class DrawFactory {
	
	private final Hashtable<Integer, Drawable> drawableCache = new Hashtable<Integer, Drawable>();
	private static Drawable blackSquareDrawable;
	private static Drawable whiteSquareDrawable;
	
	private static Paint hightlightPainter;
	static {
		hightlightPainter = new Paint();
		hightlightPainter.setAntiAlias(true);
		hightlightPainter.setStyle(Style.FILL);
		hightlightPainter.setARGB(96, 255, 255, 255);
	}
	
	private static final int PIECE_MARGIN = 5;
	
	// TODO : to be used?
	private final int isometricScaleFactor = 1;
	
	private int boardSize;
	private int squareSize = 96;
	
	public DrawFactory(final Context context) {
		buildDrawableCache(context);
		blackSquareDrawable = context.getResources().getDrawable(R.drawable.black_square);
		whiteSquareDrawable = context.getResources().getDrawable(R.drawable.white_square);
	}
	
	private void buildDrawableCache(final Context context) {
		for (final Player player : Player.values()) {
			for (final PieceType pieceType : PieceType.values()) {
				final int resourceId = pieceType.getImageResource(player);
				drawableCache.put(resourceId, context.getResources().getDrawable(resourceId));
			}
		}
	}
	
	public Drawable getPieceImage(final Piece piece) {
		return drawableCache.get(piece.getImageResource());
	}
	
	public void setBoardSize(final int boardSize) {
		this.boardSize = boardSize;
		this.squareSize = boardSize / GameUtils.CHESSBOARD_SIZE;
	}
	
	public int getBoardSize() {
		return boardSize;
	}
	
	public int getSquareSize() {
		return squareSize;
	}
	
	public int getIsometricScaleFactor() {
		return isometricScaleFactor;
	}
	
	private Drawable getSquareDrawable(final Square square) {
		switch (square.getColor()) {
			case BLACK:
				return blackSquareDrawable;
			case WHITE:
				return whiteSquareDrawable;
		}
		throw new RuntimeException("Invalid square color : " + square.getColor());
	}
	
	public void draw(final Canvas canvas, final Piece piece) {
		draw(canvas, piece, false);
	}
	
	public void draw(final Canvas canvas, final Piece piece, final boolean flipped) {
		final int index = piece.getSquare().getIndex();
		final Drawable drawable = getPieceImage(piece);
		
		final int x = index % GameUtils.CHESSBOARD_SIZE;
		final int y = index / GameUtils.CHESSBOARD_SIZE;
		final int left = x * squareSize + PIECE_MARGIN;
		final int right = left + squareSize - 2 * PIECE_MARGIN;
		final int top = boardSize - (y + 1) * squareSize + PIECE_MARGIN;
		final int bottom = top + squareSize - 2 * PIECE_MARGIN;
		
		if (flipped) {
			canvas.save();
			canvas.rotate(180, left - PIECE_MARGIN, top - PIECE_MARGIN);
			canvas.translate(-squareSize, -squareSize);
		}
		
		drawable.setBounds(
				isometricScaleFactor * left,
				isometricScaleFactor * top,
				isometricScaleFactor * right,
				isometricScaleFactor * bottom);
		drawable.draw(canvas);
		
		if (flipped) {
			canvas.restore();
		}
	}
	
	public void draw(final Canvas canvas, final Square square) {
		draw(canvas, square, false);
	}
	
	public void draw(final Canvas canvas, final Square square, final boolean highlighted) {
		final int index = square.getIndex();
		final Drawable drawable = getSquareDrawable(square);
		
		final int x = index % GameUtils.CHESSBOARD_SIZE;
		final int y = index / GameUtils.CHESSBOARD_SIZE;
		final int left = x * squareSize;
		final int right = left + squareSize;
		final int top = boardSize - (y + 1) * squareSize;
		final int bottom = top + squareSize;
		
		drawable.setBounds(
				left + 1,
				top + 1,
				right - 1,
				bottom - 1);
		drawable.draw(canvas);
		
		if (highlighted) {
			canvas.drawRect(left, top, right, bottom, hightlightPainter);
		}
	}
	
	public void drawMovement(final Canvas canvas, final PieceMovement move, final boolean way, final float coeff, final boolean flipped) {
		final Drawable drawable = getPieceImage(move.getPiece());
		
		final Square s = way ? move.getTo() : move.getFrom();
		final Movement pam = move.getFrom().getMovementTo(move.getTo());
		final Movement m = way ? pam : pam.withInversion();
		
		final int dx = (int) ((coeff - 1.0) * m.dx * squareSize);
		final int dy = (int) ((1.0 - coeff) * m.dy * squareSize);
		final int factor = (int) (Math.sin(coeff * Math.PI) * squareSize / 3);
		
		final int index = s.getIndex();
		final int x = index % GameUtils.CHESSBOARD_SIZE;
		final int y = index / GameUtils.CHESSBOARD_SIZE;
		final int left = x * squareSize + PIECE_MARGIN;
		final int right = left + squareSize - 2 * PIECE_MARGIN;
		final int top = boardSize - (y + 1) * squareSize + PIECE_MARGIN;
		final int bottom = top + squareSize - 2 * PIECE_MARGIN;
		
		Log.v(getClass().getName(), "Painting movement : coeff = " + coeff + ", dx = " + dx + ", dy = " + dy + ", factor " + factor);
		
		drawable.setBounds(
				isometricScaleFactor * (left + dx - factor),
				isometricScaleFactor * (top + dy - factor),
				isometricScaleFactor * (right + dx + factor),
				isometricScaleFactor * (bottom + dy + factor));
		
		if (flipped) {
			canvas.save();
			canvas.rotate(180, left - PIECE_MARGIN + dx, top - PIECE_MARGIN + dy);
			canvas.translate(-squareSize, -squareSize);
		}
		drawable.draw(canvas);
		if (flipped) {
			canvas.restore();
		}
	}
	
}
