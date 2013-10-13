package name.matco.checkmate.ui;

import name.matco.checkmate.game.Game;
import name.matco.checkmate.game.Player;
import name.matco.checkmate.game.piece.Piece;
import name.matco.checkmate.ui.listeners.CaptureListener;
import name.matco.checkmate.ui.listeners.GameStateListener;
import name.matco.checkmate.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CapturedPieces extends SurfaceView implements SurfaceHolder.Callback, GameStateListener, CaptureListener {
	
	private Game game;
	private Chessboard chessboard;
	private Player player;
	
	public CapturedPieces(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		getHolder().addCallback(this);
	}
	
	public void setGame(final Game game) {
		this.game = game;
		game.addGameStateListeners(this);
		game.addCaptureListener(this);
	}
	
	public void setChessboard(final Chessboard chessboard) {
		this.chessboard = chessboard;
	}
	
	public void setPlayer(final Player player) {
		this.player = player;
	}
	
	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		draw();
	}
	
	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		// nothing to do
	}
	
	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		// nothing to do
	}
	
	@Override
	public void onGameInit() {
		draw();
	}
	
	@Override
	public void onPlayerChange(final Player player) {
		// nothing to do
	}
	
	@Override
	public void onCapture(final Piece piece) {
		// FIXME only draw new piece
		if (piece.is(player)) {
			draw();
		}
	}
	
	@Override
	public void onRelease(final Piece piece) {
		// FIXME only undraw released piece
		if (piece.is(player)) {
			draw();
		}
	}
	
	private void drawCapturedPieces(final Canvas canvas) {
		final float isometricScaleFactor = chessboard.isometricScaleFactor;
		final int pieceSize = chessboard.squareSize / 2;
		int offset = 0;
		
		synchronized (game.getCapturedPieces()) {
			canvas.drawColor(getResources().getColor(R.color.grey));
			for (final Piece p : game.getCapturedPieces()) {
				if (p.is(player)) {
					Log.i(getClass().getName(), String.format("Draw captured piece %s", p));
					final Drawable drawable = chessboard.getPieceImage(p);
					drawable.setBounds(
							(int) isometricScaleFactor * offset,
							0,
							(int) isometricScaleFactor * (offset + pieceSize),
							(int) isometricScaleFactor * pieceSize);
					drawable.draw(canvas);
					offset += pieceSize;
				}
			}
		}
	}
	
	private void draw() {
		final SurfaceHolder holder = getHolder();
		final Canvas canvas = holder.lockCanvas();
		if (canvas != null) {
			try {
				drawCapturedPieces(canvas);
			} finally {
				holder.unlockCanvasAndPost(canvas);
			}
		}
	}
	
}
