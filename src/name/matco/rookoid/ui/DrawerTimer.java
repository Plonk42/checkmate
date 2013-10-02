package name.matco.rookoid.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import android.util.Log;

public class DrawerTimer implements Runnable {
	
	private final static int FPS = 50;
	
	private final GameDrawer drawer;
	
	private final AtomicBoolean isStarted = new AtomicBoolean(false);
	private final AtomicBoolean isStopped = new AtomicBoolean(false);
	
	private final AtomicBoolean isForced = new AtomicBoolean(false);
	
	// each Callable trigger at least one draw
	private final List<Callable<Boolean>> conditions = new ArrayList<Callable<Boolean>>();
	
	// DrawerTimer cannot be restarted
	public DrawerTimer(final GameDrawer drawer) {
		this.drawer = drawer;
	}
	
	public void start() {
		if (!isStarted.get()) {
			isStarted.set(true);
			new Thread(this).start();
		}
	}
	
	public void stop() {
		if (isStarted.get()) {
			isStopped.set(true);
		}
	}
	
	@Override
	public void run() {
		while (!isStopped.get()) {
			if (!isForced.get()) {
				boolean shouldDraw = false;
				synchronized (this) {
					final Iterator<Callable<Boolean>> it = conditions.iterator();
					while (it.hasNext()) {
						shouldDraw = true;
						final Callable<Boolean> c = it.next();
						try {
							if (!c.call()) {
								it.remove();
							}
						} catch (final Exception e) {
							Log.w(getClass().getName(), "Exception during drawer condition call", e);
							it.remove(); // remove it by safety
						}
					}
					if (!shouldDraw) {
						try {
							wait();
						} catch (final InterruptedException e) {
							Log.i(getClass().getName(), "DrawerTimer interrupted while waiting", e);
						}
					}
				}
			}
			drawer.doDraw();
			try {
				Thread.sleep(1000 / FPS);
			} catch (final InterruptedException e) {
				Log.i(getClass().getName(), "DrawerTimer interrupted while waiting", e);
			}
		}
	}
	
	public void force() {
		isForced.set(true);
	}
	
	public void unforce() {
		isForced.set(false);
	}
	
	public void addCondition(final Callable<Boolean> condition) {
		synchronized (this) {
			conditions.add(condition);
			notifyAll();
		}
	}
	
	public void removeCondition(final Callable<Boolean> condition) {
		synchronized (this) {
			conditions.remove(condition);
		}
	}
	
	public void drawOnce() {
		addCondition(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return false;
			}
		});
	}
	
	public void drawFor(final long ms) {
		addCondition(new Callable<Boolean>() {
			
			private final long start = System.currentTimeMillis();
			
			@Override
			public Boolean call() throws Exception {
				return System.currentTimeMillis() - start < ms;
			}
		});
	}
	
}