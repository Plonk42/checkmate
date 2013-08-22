package name.matco.rookoid.ui;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import name.matco.rookoid.game.Case;
import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.piece.Piece;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Chessboard extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final int BOARD_MARGIN = 10;
	private static final int PIECE_MARGIN = 5;
	
	private static Paint blackPainter;
	private static Paint whitePainter;
	private Timer paintTimer;
	
	private Hashtable<Piece, Drawable> drawableCache = new Hashtable<Piece, Drawable>();
	
	private int x0;
	private int y0;
	private int caseSize;
	
	private Game game;
	private Piece selectedPiece;
	
	static {
		blackPainter = new Paint();
		blackPainter.setAntiAlias(true);
		blackPainter.setStyle(Style.FILL);
		blackPainter.setARGB(255, 209, 139, 71);
		
		whitePainter = new Paint();
		whitePainter.setAntiAlias(true);
		whitePainter.setStyle(Style.FILL);
		whitePainter.setARGB(255, 255, 206, 158);
	}
	
	public Chessboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.i(getClass().getName(), "Chessboard instantiated [context = " + context + ", attrs = " + attrs);
		getHolder().addCallback(this);
		
		game = new Game();
		game.init();
		
		buildDrawableCache();
	}
	
	private void buildDrawableCache() {
		for (Case c : game.getBoard()) {
			Piece p = c.getPiece();
			if (p != null) {
				drawableCache.put(p, getContext().getResources().getDrawable(p.getResource()));
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(getClass().getName(), String.format("Touch Event [x = %.1f, y = %.1f, action = %d]", event.getX(), event.getY(), event.getAction()));
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return false;
		}
		
		Case c = getCaseAt(event.getX(), event.getY());
		if (selectedPiece != null) {
			for (Case previous : game.getBoard()) {
				if (previous.getPiece() == selectedPiece) {
					previous.setPiece(null);
				}
			}
			c.setPiece(selectedPiece);
			selectedPiece = null;
		} else {
			selectedPiece = c.getPiece();
			String str = selectedPiece != null ? selectedPiece.getDescription() : "[none]";
			Log.i(getClass().getName(), String.format("Selected piece : %s", str));
		}
		return true;
	}
	
	private Case getCaseAt(float x, float y) {
		int caseX = (int) ((x - x0) / caseSize);
		int caseY = (int) ((y - y0) / caseSize);
		return game.getBoard()[caseY * 8 + caseX];
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		int width = (canvas.getWidth() - BOARD_MARGIN * 2) / 8;
		int height = (canvas.getHeight() - BOARD_MARGIN * 2) / 8;
		caseSize = Math.min(width, height);
		
		Log.d(getClass().getName(), String.format("Draw canvas [dimension = %dx%d, size = %d]", width, height, caseSize));
		
		x0 = (canvas.getWidth() - caseSize * 8) / 2;
		y0 = (canvas.getHeight() - caseSize * 8) / 2;
		canvas.translate(x0, y0);
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int left = x * caseSize;
				int right = left + caseSize;
				int top = y * caseSize;
				int bottom = top + caseSize;
				
				canvas.drawRect(left, top, right, bottom, ((x + y) % 2 == 0) ? whitePainter : blackPainter);
			}
		}
		
		float isometricScaleFactor = 1;
		for (int i = 0; i < game.getBoard().length; i++) {
			Piece p = game.getBoard()[i].getPiece();
			if (p == null) {
				continue;
			}
			
			int x = i % 8;
			int y = i / 8;
			int left = x * caseSize + PIECE_MARGIN;
			int right = left + caseSize - 2 * PIECE_MARGIN;
			int top = y * caseSize + PIECE_MARGIN;
			int bottom = top + caseSize - 2 * PIECE_MARGIN;
			
			Drawable drawable = drawableCache.get(p);
			drawable.setBounds(
					(int) (isometricScaleFactor * left),
					(int) (isometricScaleFactor * top),
					(int) (isometricScaleFactor * right),
					(int) (isometricScaleFactor * bottom));
			drawable.draw(canvas);
		}
	}
	
	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface created");
		
		paintTimer = new Timer();
		paintTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				Canvas theCanvas = holder.lockCanvas();
				if (theCanvas != null) {
					try {
						draw(theCanvas);
					} finally {
						holder.unlockCanvasAndPost(theCanvas);
					}
				}
			}
		}, 0, 100);
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i(getClass().getName(), "Surface changed");
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface destroyed");
		
		paintTimer.cancel();
		paintTimer = null;
	}
	
}
