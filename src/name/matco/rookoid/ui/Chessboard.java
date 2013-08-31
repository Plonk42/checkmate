package name.matco.rookoid.ui;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import name.matco.rookoid.game.Game;
import name.matco.rookoid.game.GameUtils;
import name.matco.rookoid.game.Movement;
import name.matco.rookoid.game.Player;
import name.matco.rookoid.game.Square;
import name.matco.rookoid.game.piece.Piece;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Chessboard extends SurfaceView implements SurfaceHolder.Callback {
	
	private static final int BOARD_MARGIN = 10;
	private static final int PIECE_MARGIN = 5;
	
	private static final int MOVE_DURATION = 200; // ms
	
	private static Paint blackPainter;
	private static Paint whitePainter;
	
	private static Paint hightlightPainter;
	private Timer paintTimer;
	private final Object drawLock = new Object();
	
	private boolean isMoving = false;
	private long startMovingMillis = 0;
	
	private final Hashtable<Integer, Drawable> drawableCache = new Hashtable<Integer, Drawable>();
	
	private int x0;
	private int y0;
	private int squareSize;
	
	private final Game game;
	
	private Piece selectedPiece;
	private final List<Square> highlightedSquares = new ArrayList<Square>();
	
	float isometricScaleFactor = 1;
	
	static {
		blackPainter = new Paint();
		blackPainter.setAntiAlias(true);
		blackPainter.setStyle(Style.FILL);
		blackPainter.setARGB(255, 209, 139, 71);
		
		whitePainter = new Paint();
		whitePainter.setAntiAlias(true);
		whitePainter.setStyle(Style.FILL);
		whitePainter.setARGB(255, 255, 206, 158);
		
		hightlightPainter = new Paint();
		hightlightPainter.setAntiAlias(true);
		hightlightPainter.setStyle(Style.FILL);
		hightlightPainter.setARGB(96, 255, 255, 255);
	}
	
	public Chessboard(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		Log.i(getClass().getName(), "Chessboard instantiated [context = " + context + ", attrs = " + attrs);
		getHolder().addCallback(this);
		
		game = Game.getInstance();
		buildDrawableCache();
		// setBackgroundColor(getResources().getColor(R.color.darker_gray));
		
	}
	
	private void buildDrawableCache() {
		for (final Square c : game.getBoard()) {
			final Piece p = c.getPiece();
			if (p != null) {
				drawableCache.put(p.getResource(), getContext().getResources().getDrawable(p.getResource()));
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		Log.i(getClass().getName(), String.format("Touch Event [x = %.1f, y = %.1f, action = %d]", event.getX(), event.getY(), event.getAction()));
		if (event.getAction() != MotionEvent.ACTION_DOWN || isMoving) {
			return false;
		}
		
		highlightedSquares.clear();
		
		final Square c = getSquareAt(event.getX(), event.getY());
		
		synchronized (drawLock) {
			if (c != null) {
				final Piece p = c.getPiece();
				
				if (selectedPiece != null) {
					if (selectedPiece.getAllowedPositions(game).contains(c)) {
						if (p != null) {
							if (!p.getPlayer().equals(selectedPiece.getPlayer())) {
								moveSelectedPieceTo(c);
							}
						}
						else {
							moveSelectedPieceTo(c);
						}
						isMoving = true;
						startMovingMillis = System.currentTimeMillis();
						Log.i(getClass().getName(), "Move started at " + startMovingMillis);
					}
					selectedPiece = null;
				}
				else {
					if (p != null && game.getActivePlayer().equals(p.getPlayer())) {
						selectedPiece = p;
						if (selectedPiece != null) {
							highlightedSquares.addAll(selectedPiece.getAllowedPositions(game));
						}
						
						final String str = selectedPiece != null ? selectedPiece.getDescription() : "[none]";
						Log.i(getClass().getName(), String.format("Selected piece : %s", str));
					}
				}
			}
			else {
				selectedPiece = null;
			}
		}
		doDraw();
		return true;
	}
	
	private void playCheckSound() {
		final float duration = 0.2f; // seconds
		final int sampleRate = 8000;
		final int numSamples = (int) (duration * sampleRate);
		final double sample[] = new double[numSamples];
		final double freqOfTone = 15000; // hz
		
		final byte generatedSnd[] = new byte[2 * numSamples];
		
		// generate tone
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}
		
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			
		}
		
		// play tone
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRate, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
				AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, generatedSnd.length);
		audioTrack.play();
	}
	
	private void playCheckmateSound() {
		final float duration = 4f; // seconds
		final int sampleRate = 8000;
		final int numSamples = (int) (duration * sampleRate);
		final double sample[] = new double[numSamples];
		final double freqOfTone = 440; // hz
		
		final byte generatedSnd[] = new byte[2 * numSamples];
		
		// generate tone
		// fill out the array
		for (int i = 0; i < numSamples; ++i) {
			sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / freqOfTone));
		}
		
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : sample) {
			// scale to maximum amplitude
			final short val = (short) ((dVal * 32767));
			// in 16 bit wav PCM, first byte is the low order byte
			generatedSnd[idx++] = (byte) (val & 0x00ff);
			generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
			
		}
		
		// play tone
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRate, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
				AudioTrack.MODE_STATIC);
		audioTrack.write(generatedSnd, 0, generatedSnd.length);
		audioTrack.play();
	}
	
	private void moveSelectedPieceTo(final Square c) {
		game.movePieceTo(selectedPiece, c);
		Log.i(getClass().getName(), String.format("Check check for player %s", game.getActivePlayer()));
		if (game.isCheck(game.getActivePlayer())) {
			Log.i(getClass().getName(), String.format("Check"));
			if (game.isCheckmate(game.getActivePlayer())) {
				Log.i(getClass().getName(), String.format("Checkmate"));
				playCheckmateSound();
			}
			else {
				playCheckSound();
			}
		}
	}
	
	private Square getSquareAt(final float x, final float y) {
		if (x < x0 || x > x0 + squareSize * GameUtils.CHESSBOARD_SIZE || y < y0 || y > y0 + squareSize * GameUtils.CHESSBOARD_SIZE) {
			return null;
		}
		
		final int squareX = (int) ((x - x0) / squareSize);
		final int squareY = (int) ((y - y0) / squareSize);
		return game.getBoard()[squareY * 8 + squareX];
	}
	
	@Override
	public void draw(final Canvas canvas) {
		super.draw(canvas);
		
		final int width = (canvas.getWidth() - BOARD_MARGIN * 2) / 8;
		final int height = (canvas.getHeight() - BOARD_MARGIN * 2) / 8;
		squareSize = Math.min(width, height);
		
		Log.d(getClass().getName(), String.format("Draw canvas [dimension = %dx%d, size = %d]", width, height, squareSize));
		
		x0 = (canvas.getWidth() - squareSize * 8) / 2;
		y0 = (canvas.getHeight() - squareSize * 8) / 2;
		
		canvas.translate(x0, y0);
		for (final Square c : game.getBoard()) {
			final int x = c.getCoordinate().x;
			final int y = c.getCoordinate().y;
			final int left = x * squareSize;
			final int right = left + squareSize;
			final int top = y * squareSize;
			final int bottom = top + squareSize;
			
			canvas.drawRect(left, top, right, bottom, ((x + y) % 2 == 0) ? whitePainter : blackPainter);
			if (highlightedSquares.contains(c)) {
				canvas.drawRect(left, top, right, bottom, hightlightPainter);
			}
		}
		
		synchronized (drawLock) {
			for (int i = 0; i < game.getBoard().length; i++) {
				final Piece p = game.getBoard()[i].getPiece();
				if (p == null) {
					continue;
				}
				
				final Drawable drawable = drawableCache.get(p.getResource());
				
				final int x = i % 8;
				final int y = i / 8;
				final int left = x * squareSize + PIECE_MARGIN;
				final int right = left + squareSize - 2 * PIECE_MARGIN;
				final int top = y * squareSize + PIECE_MARGIN;
				final int bottom = top + squareSize - 2 * PIECE_MARGIN;
				
				final long now = System.currentTimeMillis();
				
				if (isMoving && p.equals(game.getLastMove().getPiece())) {
					final float coeff;
					if (now >= startMovingMillis + MOVE_DURATION) {
						isMoving = false;
						coeff = 1;
						Log.i(getClass().getName(), "Move finished at " + now);
					} else {
						coeff = (float) (now - startMovingMillis) / MOVE_DURATION;
					}
					
					final Movement m = game.getLastMove().getMovement();
					final int dx = (int) ((coeff - 1.0) * m.dx * squareSize);
					final int dy = (int) ((coeff - 1.0) * m.dy * squareSize);
					Log.v(getClass().getName(), "Painting movement : coeff = " + coeff + ", dx = " + dx + ", dy = " + dy);
					drawable.setBounds(
							(int) (isometricScaleFactor * (left + dx)),
							(int) (isometricScaleFactor * (top + dy)),
							(int) (isometricScaleFactor * (right + dx)),
							(int) (isometricScaleFactor * (bottom + dy)));
				} else {
					// FIXME : reset offset to 0 when unselected
					final int offset = p.equals(selectedPiece) ? (int) (8.0 * Math.cos(now / 200.0) + 8.0) : 0;
					drawable.setBounds(
							(int) (isometricScaleFactor * left),
							(int) (isometricScaleFactor * top - offset),
							(int) (isometricScaleFactor * right),
							(int) (isometricScaleFactor * bottom - offset));
				}
				drawable.draw(canvas);
				
				drawCapturedPieces(canvas);
			}
		}
	}
	
	private void drawCapturedPieces(final Canvas canvas) {
		final int capturedWhiteY = -squareSize / 2;
		int offsetWhite = 0;
		
		final int capturedBlackY = GameUtils.CHESSBOARD_SIZE * squareSize;
		int offsetBlack = 0;
		synchronized (game.getCapturedPieces()) {
			for (final Piece p : game.getCapturedPieces()) {
				final Drawable drawable = drawableCache.get(p.getResource());
				if (Player.WHITE.equals(p.getPlayer())) {
					drawable.setBounds((int) (isometricScaleFactor * offsetWhite), (int) (isometricScaleFactor * capturedWhiteY), (int) (isometricScaleFactor * (offsetWhite + squareSize / 2)),
							(int) (isometricScaleFactor * (capturedWhiteY + squareSize / 2)));
					offsetWhite += squareSize / 2;
				}
				else {
					drawable.setBounds((int) (isometricScaleFactor * offsetBlack), (int) (isometricScaleFactor * capturedBlackY), (int) (isometricScaleFactor * (offsetBlack + squareSize / 2)),
							(int) (isometricScaleFactor * (capturedBlackY + squareSize / 2)));
					offsetBlack += squareSize / 2;
				}
				drawable.draw(canvas);
			}
		}
	}
	
	private void doDraw() {
		final SurfaceHolder holder = getHolder();
		final Canvas theCanvas = holder.lockCanvas();
		if (theCanvas != null) {
			try {
				draw(theCanvas);
			} finally {
				holder.unlockCanvasAndPost(theCanvas);
			}
		}
	}
	
	public void refresh() {
		doDraw();
	}
	
	public void reset() {
		synchronized (drawLock) {
			isMoving = false;
			selectedPiece = null;
			highlightedSquares.clear();
		}
		
		refresh();
	}
	
	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface created");
		
		doDraw();
		
		paintTimer = new Timer();
		paintTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (selectedPiece != null || isMoving) {
					doDraw();
				}
			}
		}, 0, 25);
	}
	
	@Override
	public void surfaceChanged(final SurfaceHolder holder, final int format, final int width, final int height) {
		Log.i(getClass().getName(), "Surface changed");
	}
	
	@Override
	public void surfaceDestroyed(final SurfaceHolder holder) {
		Log.i(getClass().getName(), "Surface destroyed");
		
		paintTimer.cancel();
		paintTimer = null;
	}
	
}
