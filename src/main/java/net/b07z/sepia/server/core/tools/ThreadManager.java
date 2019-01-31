package net.b07z.sepia.server.core.tools;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A class to easily run threaded tasks.
 * 
 * @author Florian Quirin
 *
 */
public class ThreadManager {
	
	/**
	 * Interface to define a task. Has one method 'run(Object o)' that should make appropriate type casting of the object.   
	 * Usually used via lambda expression, e.g.:<br>
	 * <br>
	 * (Object o) -> { System.out.println(o.toString()); }
	 */
	public static interface ThreadManagerTask {
		public void run(Object o);
	}
	
	/**
	 * Run a given task ({@link ThreadManagerTask}) with parameters in a certain number of threads
	 * and wait for completion or timeout.
	 * @param numOfThreads - number of threads to use
	 * @param myTask - {@link ThreadManagerTask} implementation (has to overwrite run(Object o) method).
	 * @param parameters - List of objects used in myTask (one for each task)
	 * @param timeoutMs - wait at most this time (ms)
	 * @return
	 * @throws Exception
	 */
	public static boolean runTasks(int numOfThreads, ThreadManagerTask myTask, List<?> parameters,
			long timeoutMs) throws Exception {
		
		//Create executor
		ExecutorService threadPool = Executors.newFixedThreadPool(numOfThreads);
		
		//Run all tasks
		for (Object o : parameters){
			Runnable worker = () -> { 
				myTask.run(o);
			};
			threadPool.execute(worker);
		}
		
		//Await termination
		try{
			awaitTerminationAfterShutdown(threadPool, timeoutMs);
			return true;
			
	    }catch (InterruptedException ex){
	        ex.printStackTrace();
	        return false;
	    }
	}
	/**
	 * Await thread pool termination or force quit on timeout.
	 */
	private static void awaitTerminationAfterShutdown(ExecutorService threadPool, 
			long timeoutMs) throws InterruptedException {
	    threadPool.shutdown();
	    try{
	        if (!threadPool.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS)) {
	            threadPool.shutdownNow();
	        }
	    }catch (InterruptedException ex){
	        threadPool.shutdownNow();
	        Thread.currentThread().interrupt();
	        throw ex;
	    }
	}

}
