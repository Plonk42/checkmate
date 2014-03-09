package name.matco.checkmate.ui;

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.SystemClock;
import android.util.Log;

public class ChessboardDrawer implements Runnable {
	
	// TODO make this a preference in settings
	public static int FPS = 60;
	private static long FRAME_TIME = 1000 / FPS;
	private static long POLLING_TIME = 10; // time between two post tries
	
	private final AtomicBoolean drawing = new AtomicBoolean(false);
	private long stopTime = -1;
	private boolean run = true;
	
	// logging
	private int actualDraws = 0;
	private int skippedDraws = 0;
	private long drawingTime = 0;
	
	final private Chessboard chessboard;
	
	public ChessboardDrawer(final Chessboard chessboard) {
		this.chessboard = chessboard;
		new Thread(this).start();
	}
	
	public void shutdown() {
		run = false;
	}
	
	@Override
	public void run() {
		while (run) {
			while (run && SystemClock.uptimeMillis() < stopTime) {
				final long sleepTime = doDraw(false) ? FRAME_TIME : POLLING_TIME;
				try {
					Thread.sleep(sleepTime);
				} catch (final InterruptedException e) {
					// propagate the interruption and stop
					Thread.currentThread().interrupt();
					return;
				}
			}
			
			doDraw(true); // force a draw after timeout to ensure animation completeness
			
			synchronized (ChessboardDrawer.this) {
				try {
					wait();
				} catch (final InterruptedException e) {
					// propagate the interruption and stop
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}
	
	private boolean doDraw(final boolean forceSchedule) {
		if (forceSchedule || !drawing.getAndSet(true)) {
			final boolean posted = chessboard.post(new Runnable() {
				@Override
				public void run() {
					actualDraws++;
					long start, stop;
					start = SystemClock.uptimeMillis();
					chessboard.myDraw();
					stop = SystemClock.uptimeMillis();
					drawingTime += stop - start;
					// TODO : conditional log to avoid String computation
					Log.v(getClass().getName(), String.format("Frame %d (%d skipped = %d%%), %dms, avg = %dms", actualDraws, skippedDraws, 100 * skippedDraws / (actualDraws + skippedDraws), stop - start, drawingTime / actualDraws));
					drawing.set(false);
				}
			});
			if (!posted) { // won't be executed: reset flag
				skippedDraws++;
				drawing.set(false);
			}
			return posted;
		}
		skippedDraws++;
		return false;
	}
	
	/**
	 * Immediately schedule a draw
	 */
	public void drawNow() {
		doDraw(false);
	}
	
	public synchronized void drawContinuous() {
		stopTime = Long.MAX_VALUE;
		notifyAll();
	}
	
	/**
	 * Refresh drawing for the given time, in milliseconds.
	 * At least one draw is executed after the timeout
	 * 
	 * @param milliseconds
	 */
	public synchronized void drawFor(final int milliseconds) {
		stopTime = SystemClock.uptimeMillis() + milliseconds;
		notifyAll();
	}
}
