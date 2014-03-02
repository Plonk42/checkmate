package name.matco.checkmate.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChessboardDrawer {
	
	// TODO make this a preference in settings
	public static int FPS = 60;
	private static int FRAME_TIME = 1000 / FPS;
	
	final private Runnable drawer;
	final private ScheduledExecutorService scheduler;
	final private Runnable cancellerTask;
	
	private ScheduledFuture<?> handler;
	private ScheduledFuture<?> canceller;
	
	public ChessboardDrawer(final Runnable drawer) {
		this.drawer = drawer;
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
		this.cancellerTask = new Runnable() {
			@Override
			public void run() {
				handler.cancel(false);
				handler = null;
			}
		};
	}
	
	public void shutdown() {
		this.scheduler.shutdown();
	}
	
	public void drawStart() {
		// cancel current canceller
		if (canceller != null) {
			canceller.cancel(false);
		}
		// create handler if it does not exists
		if (handler == null) {
			handler = scheduler.scheduleAtFixedRate(drawer, 0, FRAME_TIME, TimeUnit.MILLISECONDS);
		}
	}
	
	public void drawFor(final int milliseconds) {
		drawStart();
		
		// create a new canceller
		canceller = scheduler.schedule(cancellerTask, milliseconds, TimeUnit.MILLISECONDS);
	}
	
	public void drawNow() {
		this.drawer.run();
	}
	
	public void drawStop() {
		if (handler != null) {
			handler.cancel(false);
			handler = null;
		}
	}
	
}
