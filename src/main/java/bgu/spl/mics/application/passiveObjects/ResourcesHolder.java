package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 */
public class ResourcesHolder {

	private LinkedBlockingQueue<DeliveryVehicle> AvailableVehicle = new LinkedBlockingQueue<DeliveryVehicle>();
	private LinkedBlockingQueue<Future<DeliveryVehicle>> waitingFuture = new LinkedBlockingQueue<>();
	private Semaphore manager;

	/**
	 * private class that creates single instance of ResourcesHolder
	 * only reachable from inside the class
	 */
	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	/**
	 * private constructor
	 */

	private ResourcesHolder () {}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}

	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public  Future<DeliveryVehicle> acquireVehicle() {
		Future <DeliveryVehicle> future = new Future<>();
		if (!manager.tryAcquire())
				waitingFuture.add(future);
		else{
				DeliveryVehicle temp = AvailableVehicle.poll();
				future.resolve(temp);
		}
		return future;

	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		Future <DeliveryVehicle> waitFuture = waitingFuture.poll();
		if (waitFuture == null){
				AvailableVehicle.add(vehicle);
				manager.release();
			}
		else
			//in this case we won't release to the semaphore, we'll give the vehicle to a waiting delivery
			waitFuture.resolve(vehicle);
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (int i = 0; i < vehicles.length; i++){
			try {
				AvailableVehicle.put(vehicles[i]);
			} catch (InterruptedException e) {
			}
		}
		//initialize Semaphore with the number of vehicles loaded to the resource holder
		manager=new Semaphore(AvailableVehicle.size());
	}

}
