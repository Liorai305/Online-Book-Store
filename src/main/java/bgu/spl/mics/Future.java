package bgu.spl.mics;

import java.util.concurrent.TimeUnit;


/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 */
public class Future<T> {

	private T result;
	private boolean is_done;

	/**
	 * CONSTRUCTOR
	 */
	public Future() {
		result=null;
		is_done=false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public synchronized T get() {
		if(is_done)
			return result;
			try {
				while (!is_done)
					wait();
			} catch (InterruptedException e) { }
			return result;
		}

	/**
     * Resolves the result of this Future object.
     */
	public synchronized void resolve (T result) {
		this.result=result;
		is_done=true;
		//the state of the future has changed, we need to wake all the threads who sleeps
		notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return is_done;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {
	if(is_done)
		return result;
	try{
		unit.sleep(timeout);
	}catch(InterruptedException e){}
		return result;
	}

}
