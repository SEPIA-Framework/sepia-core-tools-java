package net.b07z.sepia.server.core.tools;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * This class aims to manage threads in a central way or at least keep a rough overview of what's going on in the framework.
 * 
 * @author Florian Quirin
 *
 */
public class ThreadManager {
	
	private static AtomicInteger activeThreads = new AtomicInteger(0);		//NOTE: ServiceBackgroundTasks are separate, see below
	private static int maxActiveThreads = 0;
	private static Map<String, Future<?>> futuresMap = new ConcurrentHashMap<>();
	private static AtomicLong lastUsedBaseId = new AtomicLong(0); 	//NOTE: this will reset on server start but task don't survive restart anyways
	
	/**
	 * Keep a reference of some sort to the running thread (TBD).
	 */
	public static class ThreadInfo {
		//TBD
	}
	
	//Handling of scheduled tasks
	private static String getNewFutureId(){
		return "future" + lastUsedBaseId.incrementAndGet() + "_" + System.currentTimeMillis();
	}
	private static void addToFutureMap(String taskId, Future<?> fut){
		futuresMap.put(taskId, fut);
	}
	private static void removeFromFutureMap(String taskId){
		futuresMap.remove(taskId);
	}
	/**
	 * Get the number of scheduled tasks, waiting to be run or currently running (they are removed after they finish).
	 */
	public static int getNumberOfScheduledTasks(){
		return futuresMap.size();
	}
	
	/**
	 * Get the number of active threads (NOTE: does only include threads created via this class).
	 */
	public static int getNumberOfCurrentlyActiveThreads(){
		int n1 = activeThreads.get();
		return n1;
	}
	/**
	 * Get the maximum number of active threads tracked so far (NOTE: does only include threads created via this class).
	 */
	public static int getMaxNumberOfActiveThreads(){
		int n1 = maxActiveThreads;
		return n1;
	}
	private static void increaseThreadCounter(){
		int n = activeThreads.incrementAndGet();
		if (n > maxActiveThreads) maxActiveThreads = n;
	}
	private static void decreaseThreadCounter(){
		activeThreads.decrementAndGet();
	}

	/**
	 * Run a simple action in a separate background thread.
	 * @param action - a {@link Runnable}
	 */
	public static ThreadInfo run(Runnable action){
		Thread worker = new Thread(() -> { 			//TODO: use shared pool? (see below)
			increaseThreadCounter();
			try{
				action.run();
				decreaseThreadCounter();
			
			}catch (Exception e){
				decreaseThreadCounter();
				throw e;
			}
		});
		worker.start();
		return new ThreadInfo();
		/*
		//Create one executor
		ExecutorService threadPool = getNewOrSharedThreadPool(1);
		try{
			//Run
			threadPool.execute(() -> { 
				increaseThreadCounter();
				try{
					action.run();
					decreaseThreadCounter();
				
				}catch (Exception e){
					decreaseThreadCounter();
					throw e;
				}
			});
			//Await termination
			int leftOverTasks = awaitTerminationAfterShutdown(pool, timeout);
			return (leftOverTasks == 0);
			
	    }catch (InterruptedException ex){
	        ex.printStackTrace();
	        return false;
	    }
		 */
	}
	
	/**
	 * Run a task that is planned to stay active in background for quite some time or indefinitely.
	 * @param task - task to run
	 * @return reference to thread
	 */
	public static ThreadInfo runForever(Runnable task){
		//TODO: To be implemented
		run(task);
		return new ThreadInfo();
	}
	
	/**
	 * Run several (usually short lived, 1-10s) actions in parallel with 'fixedPoolSize' or as many threads as possible.
	 * @param <T> - item class
	 * @param customTag - any tag describing this call (handy to find errors)
	 * @param items - collection of items
	 * @param action - action to execute for each item
	 * @param timeout - max. wait until all actions have to complete
	 * @param fixedPoolSize - fixed number of pools or 0 for cached pool (0 = creates as many as needed)
	 * @return true if all tasks completed in time
	 */
	public static <T> boolean runParallelAndWait(String customTag, Collection<T> items, Consumer<T> action, long timeout, int fixedPoolSize){
		ExecutorService pool = getNewOrSharedThreadPool(fixedPoolSize);		//0 = cached pool, x > 0 = fixed size pool
		try{
			for (T item : items){
				pool.execute(() -> {
					try{
						increaseThreadCounter();
						action.accept(item);
						decreaseThreadCounter();
					
					}catch (Exception e){
						decreaseThreadCounter();
						throw e;
					}
				});
			}
			pool.shutdown();
			int leftOverTasks = awaitTerminationAfterShutdown(pool, timeout);
			return (leftOverTasks == 0);
			//TODO: what if 'leftOverTasks' is >0 ?
		
		}catch (Exception e){
			Debugger.println("ThreadManager - one or more tasks had errors - Tag: " + customTag + " - Msg.: " + e.getMessage(), 1);
			Debugger.printStackTrace(e, 3);
			return false;
		}
	}
	
	/**
	 * Schedule a task to run in background after certain delay and then forget about it. Keep in mind that this means you cannot stop it once scheduled.<br>
	 * <br>
	 * CAREFUL: The user is responsible for the task ESPECIALLY that it will not get stuck in a loop and does not run FOREVER!<br>
	 * <br>
	 * For a more advanced version of this check 'net.b07z.sepia.server.assist.workers.ServiceBackgroundTaskManager#runOnceInBackground'.
	 * @param delayMs - execute after this time in milliseconds
	 * @param task - Runnable to run
	 * @return true if the task was scheduled (NOT executed)
	 */
	public static boolean scheduleBackgroundTaskAndForget(long delayMs, Runnable task){
		String taskId = getNewFutureId();
		int corePoolSize = 1;
	    final ScheduledThreadPoolExecutor executor = getNewOrSharedScheduledThreadPool(corePoolSize);
	    executor.setRemoveOnCancelPolicy(true);
	    ScheduledFuture<?> future =	executor.schedule(() -> {
	    	//run task and...
	    	try{
	    		increaseThreadCounter();
	    		task.run();
	    		decreaseThreadCounter();
	    	}catch (Exception e){
	    		decreaseThreadCounter();
			}
	    	//... remove yourself from manager
	    	removeFromFutureMap(taskId);
	    	executor.purge();
	    	executor.shutdown();
	    	return;
	    }, delayMs, TimeUnit.MILLISECONDS);
	    //track
	    addToFutureMap(taskId, future);
	    return true;
	}
	
	/**
	 * Await thread pool termination or force quit on timeout.
	 * @return number of threads still waiting for completion after shutdown or timeout.
	 */
	private static int awaitTerminationAfterShutdown(ExecutorService threadPool, 
			long timeoutMs) throws InterruptedException {
	    threadPool.shutdown();
	    try{
	        if (!threadPool.awaitTermination(timeoutMs, TimeUnit.MILLISECONDS)) {
	            return threadPool.shutdownNow().size();
	        }else{
	        	return 0;
	        }
	    }catch (InterruptedException ex){
	        threadPool.shutdownNow();
	        Thread.currentThread().interrupt();
	        throw ex;
	    }
	}
	
	//---------------------------------------------------------
	
	//TODO: these thread pools should probably be shared globally
	
	/**
	 * Return an existing (TODO) or new thread pool executor.<br>
	 * NOTE: Threads created with this pool will NOT automatically be included in active threads tracker.
	 * @param fixPoolSize - fixed size of pool (or 0 for cached pool)
	 */
	public static ExecutorService getNewOrSharedThreadPool(int fixPoolSize){
		if (fixPoolSize > 0){
			return Executors.newFixedThreadPool(fixPoolSize);
		}else{
			return Executors.newCachedThreadPool();
		}
	}
	
	/**
	 * Return an existing (TODO) or new scheduled thread pool executor.<br>
	 * NOTE: Threads created with this pool will NOT automatically be included in active threads tracker.
	 * @param corePoolSize - size of pool
	 */
	public static ScheduledThreadPoolExecutor getNewOrSharedScheduledThreadPool(int corePoolSize){
		return new ScheduledThreadPoolExecutor(corePoolSize);
	}
}
