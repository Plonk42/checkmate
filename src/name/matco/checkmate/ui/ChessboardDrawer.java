package name.matco.checkmate.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChessboardDrawer {
	
	// TODO make this a preference in settings
	public static int FPS = 50;
	private static int FRAME_TIME = 1000 / FPS;
	
	final private ScheduledExecutorService drawerScheduler;
	final private ScheduledExecutorService cancellerScheduler;
	
	final private Runnable cancellerTask;
	final private Runnable drawerTask;
	
	private ScheduledFuture<?> drawerFuture;
	private ScheduledFuture<?> cancellerFuture;
	
	public ChessboardDrawer(final Chessboard drawer) {
		this.drawerScheduler = Executors.newSingleThreadScheduledExecutor();
		this.cancellerScheduler = Executors.newSingleThreadScheduledExecutor();
		this.drawerTask = new Runnable() {
			@Override
			public void run() {
				// if (drawerScheduler.isTerminated()) {
				drawer.getContainer().runOnUiThread(drawer);
				// }
			}
		};
		this.cancellerTask = new Runnable() {
			@Override
			public void run() {
				drawStop();
				// run one last time to finish animation
				drawer.run();
			}
		};
	}
	
	public void shutdown() {
		drawerScheduler.shutdown();
	}
	
	public void drawStart() {
		// cancel current cancellerFuture
		if (cancellerFuture != null) {
			cancellerFuture.cancel(false);
		}
		// create drawerFuture if it does not exists
		if (drawerFuture == null) {
			drawerFuture = drawerScheduler.scheduleAtFixedRate(drawerTask, 0, FRAME_TIME, TimeUnit.MILLISECONDS);
		}
	}
	
	public void drawFor(final int milliseconds) {
		drawStart();
		
		// create a new cancellerFuture
		cancellerFuture = cancellerScheduler.schedule(cancellerTask, milliseconds, TimeUnit.MILLISECONDS);
	}
	
	public void drawNow() {
		drawerTask.run();
	}
	
	public void drawStop() {
		if (drawerFuture != null) {
			drawerFuture.cancel(false);
			drawerFuture = null;
		}
	}
	
}
