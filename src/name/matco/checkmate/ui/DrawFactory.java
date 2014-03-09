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
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class DrawFactory {
	
	private final Hashtable<Integer, Drawable> drawableCache = new Hashtable<Integer, Drawable>();
	private static Drawable blackSquareDrawable;
	private static Drawable whiteSquareDrawable;
	
	private static Paint backgroundPainter;
	private static Paint hightlightPainter;
	static {
		backgroundPainter = new Paint();
		backgroundPainter.setAntiAlias(true);
		backgroundPainter.setStyle(Style.FILL);
		backgroundPainter.setARGB(255, 0, 0, 0);
		hightlightPainter = new Paint();
		hightlightPainter.setAntiAlias(true);
		hightlightPainter.setStyle(Style.FILL);
		hightlightPainter.setARGB(96, 255, 255, 255);
	}
	
	private static final int SQUARE_MARGIN = 1;
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
	
	public boolean[] getSquaresInRect(final Rect rect) {
		final boolean[] results = new boolean[GameUtils.CHESSBOARD_SIZE * GameUtils.CHESSBOARD_SIZE];
		for (int i = 0; i < GameUtils.CHESSBOARD_SIZE * GameUtils.CHESSBOARD_SIZE; i++) {
			results[i] = Rect.intersects(rect, getSquareBounds(i));
		}
		return results;
	}
	
	public Rect getSquareBounds(final Square square) {
		return getSquareBounds(square.getIndex());
	}
	
	public Rect getSquareBounds(final int index) {
		final int x = index % GameUtils.CHESSBOARD_SIZE;
		final int y = index / GameUtils.CHESSBOARD_SIZE;
		final int left = x * squareSize;
		final int right = left + squareSize;
		final int top = boardSize - (y + 1) * squareSize;
		final int bottom = top + squareSize;
		final Rect ret = new Rect(isometricScaleFactor * left,
				isometricScaleFactor * top,
				isometricScaleFactor * right,
				isometricScaleFactor * bottom);
		ret.inset(SQUARE_MARGIN, SQUARE_MARGIN);
		return ret;
	}
	
	public void draw(final Canvas canvas, final Square square) {
		draw(canvas, square, false);
	}
	
	public void draw(final Canvas canvas, final Square square, final boolean highlighted) {
		final Drawable drawable = getSquareDrawable(square);
		
		final Rect bound = getSquareBounds(square);
		drawable.setBounds(bound);
		drawable.draw(canvas);
		
		if (highlighted) {
			canvas.drawRect(bound, hightlightPainter);
		}
	}
	
	public Rect getPieceBounds(final Piece piece) {
		final Rect ret = getSquareBounds(piece.getSquare());
		ret.inset(PIECE_MARGIN, PIECE_MARGIN);
		return ret;
	}
	
	public void draw(final Canvas canvas, final Piece piece) {
		draw(canvas, piece, false);
	}
	
	public void draw(final Canvas canvas, final Piece piece, final boolean flipped) {
		final Drawable drawable = getPieceImage(piece);
		
		final Rect bound = getPieceBounds(piece);
		if (flipped) {
			canvas.save();
			canvas.rotate(180, bound.left - PIECE_MARGIN, bound.top - PIECE_MARGIN);
			canvas.translate(-squareSize, -squareSize);
		}
		
		drawable.setBounds(bound);
		
		drawable.draw(canvas);
		
		if (flipped) {
			canvas.restore();
		}
	}
	
	public void drawMovement(final Canvas canvas, final PieceMovement move, final float coeff, final boolean flipped) {
		final Drawable drawable = getPieceImage(move.getPiece());
		
		final int index = move.getFrom().getIndex();
		final int x = index % GameUtils.CHESSBOARD_SIZE;
		final int y = index / GameUtils.CHESSBOARD_SIZE;
		final int left = x * squareSize + PIECE_MARGIN;
		final int right = left + squareSize - 2 * PIECE_MARGIN;
		final int top = boardSize - (y + 1) * squareSize + PIECE_MARGIN;
		final int bottom = top + squareSize - 2 * PIECE_MARGIN;
		
		final Movement m = move.getFrom().getMovementTo(move.getTo());
		final int dx = (int) (coeff * m.dx * squareSize);
		final int dy = (int) (-coeff * m.dy * squareSize);
		final int factor = (int) (Math.sin(coeff * Math.PI) * squareSize / 3);
		
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
	
	public void clearDirtyRegion(final Canvas canvas, final Rect dirtyRegion) {
		canvas.drawRect(dirtyRegion, backgroundPainter);
	}
	
}
