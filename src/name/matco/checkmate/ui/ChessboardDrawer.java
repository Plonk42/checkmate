package name.matco.checkmate.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChessboardDrawer {
	
	// TODO make this a preference in settings
	public static int FPS = 60;
	private static int FRAME_TIME = 1000 / FPS;
	
	final private Chessboard drawer;
	final private ScheduledExecutorService scheduler;
	final private Runnable cancellerTask;
	final private Runnable drawerTask;
	
	private ScheduledFuture<?> handler;
	private ScheduledFuture<?> canceller;
	
	public ChessboardDrawer(final Chessboard drawer) {
		this.drawer = drawer;
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.drawerTask = new Runnable() {
			@Override
			public void run() {
				// if (scheduler.isTerminated()) {
				drawer.getContainer().runOnUiThread(drawer);
				// }
			}
		};
		this.cancellerTask = new Runnable() {
			@Override
			public void run() {
				handler.cancel(false);
				handler = null;
			}
		};
	}
	
	public void shutdown() {
		scheduler.shutdown();
	}
	
	public void drawStart() {
		// cancel current canceller
		if (canceller != null) {
			canceller.cancel(false);
		}
		// create handler if it does not exists
		if (handler == null) {
			handler = scheduler.scheduleAtFixedRate(drawerTask, 0, FRAME_TIME, TimeUnit.MILLISECONDS);
		}
	}
	
	public void drawFor(final int milliseconds) {
		drawStart();
		
		// create a new canceller
		canceller = scheduler.schedule(cancellerTask, milliseconds, TimeUnit.MILLISECONDS);
	}
	
	public void drawNow() {
		drawerTask.run();
	}
	
	public void drawStop() {
		if (handler != null) {
			handler.cancel(false);
			handler = null;
		}
	}
	
}
