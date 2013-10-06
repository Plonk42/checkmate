package name.matco.rookoid.ui;

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
	
	private ScheduledFuture<?> handler;
	private ScheduledFuture<?> canceller;
	
	public ChessboardDrawer(final Runnable drawer) {
		this.drawer = drawer;
		this.scheduler = Executors.newSingleThreadScheduledExecutor();
	}
	
	public void shutdown() {
		this.scheduler.shutdown();
	}
	
	public void drawFor(final int milliseconds) {
		// cancel current canceller
		if (canceller != null) {
			canceller.cancel(false);
		}
		// create handler if it does not exists
		if (handler == null) {
			handler = scheduler.scheduleAtFixedRate(drawer, 0, FRAME_TIME, TimeUnit.MILLISECONDS);
		}
		// create a new canceller
		canceller = scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				handler.cancel(false);
				handler = null;
			}
		}, milliseconds, TimeUnit.MILLISECONDS);
	}
	
	public void drawNow() {
		this.drawer.run();
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
	
	public void drawStop() {
		handler.cancel(false);
		handler = null;
	}
	
}
