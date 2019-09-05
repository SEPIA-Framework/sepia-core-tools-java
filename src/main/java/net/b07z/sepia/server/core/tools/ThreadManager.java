package net.b07z.sepia.server.core.tools;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
	 * Run a single task in one thread with no extras. Calls 'threadPool.shutdownNow' on timeout ... which is not guaranteed to actually kill the process I guess.
	 * @param myTask - {@link ThreadManagerTask} implementation (has to overwrite run(Object o) method). Note: 'Object o' will be null in this case.
	 * @param timeoutMs - wait at most this time (ms) (until threadPool.shutdownNow will be called) 
	 * @return true if thread finished in time
	 * @throws Exception
	 */
	public static boolean runSingleTask(ThreadManagerTask myTask, long timeoutMs) throws Exception {
		//Create one executor
		ExecutorService threadPool = Executors.newFixedThreadPool(1);
		//Run
		threadPool.execute(() -> { 
			myTask.run(null);
		});
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
	 * Run a given task ({@link ThreadManagerTask}) with different parameters in a certain number of threads
	 * and wait for completion or timeout. Calls 'threadPool.shutdownNow' on timeout ... which is not guaranteed to actually kill the process I guess.
	 * @param numOfThreads - number of threads to use
	 * @param myTask - {@link ThreadManagerTask} implementation (has to overwrite run(Object o) method).
	 * @param parameters - List of objects used in myTask (one for each task). Cannot be null!
	 * @param timeoutMs - wait at most this time (ms) (until threadPool.shutdownNow will be called)
	 * @return true if thread finished in time
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
	
	/**
	 * Run a task in background after certain delay.<br>
	 * For a more advanced version of this check 'net.b07z.sepia.server.assist.workers.ServiceBackgroundTaskManager'.
	 * @param delayMs - execute after this time in milliseconds
	 * @param task - Runnable to run
	 * @return a {@link ScheduledFuture}
	 */
	public static ScheduledFuture<?> scheduleTaskToRunOnceInBackground(long delayMs, Runnable task){
		int corePoolSize = 1;
	    final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(corePoolSize);
	    executor.setRemoveOnCancelPolicy(true);
	    ScheduledFuture<?> future = executor.schedule(() -> {
	    	//run task and purge
	    	task.run();
	    	executor.purge();
	    	executor.shutdown();
	    	return;
	    }, delayMs, TimeUnit.MILLISECONDS);
	    return future;
	}

}
