package net.b07z.sepia.server.core.tools;

/**
 * A tic-toc method and other helpers.
 * @author Florian Quirin
 */
public final class Timer {
	
	private Timer() {
	}

	/**
	 * Get a tic.
	 */
	public static long tic(){
		return System.currentTimeMillis();
	}

	/**
	 * Get a toc from a tic :-)
	 */
	public static long toc(long tic){
		return System.currentTimeMillis()-tic;
	}
	
	/**
	 * Trigger thread-sleep and fail on error.
	 */
	public static void threadSleep(long ms){
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException("Thread.sleep failed! This should never happen oO");
		}
	}

}
