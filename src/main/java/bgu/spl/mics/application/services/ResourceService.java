package bgu.spl.mics.application.services;

import bgu.spl.mics.InitializationCount;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.VehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;
//import java.util.concurrent.Future;
import bgu.spl.mics.Future;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * Holds a LinkedBlockingQueue that holds Future <DeliveryVehicle> that waits for vehicle
 */
public class ResourceService extends MicroService{

	private ResourcesHolder resourcesHolder;
	private LinkedBlockingQueue<Future<DeliveryVehicle>> waitingFuture = new LinkedBlockingQueue<>();


	/**
	 * CONSTRUCTOR
	 * @param name
	 */
	public ResourceService(String name) {
		super(name);
		resourcesHolder= ResourcesHolder.getInstance();

	}

	/**
	 * creates callbacks and subscribes for VehicleEvent and FinalTickBroadcast
	 */
	@Override
	protected void initialize() {
		//subscribes to VehicleEvent, will try to acquire vehicle when called, always completes
		VehicleEvent vehicleEvent = new VehicleEvent(null);
		subscribeEvent( vehicleEvent.getClass(), (event) -> {
			Future <DeliveryVehicle> future = resourcesHolder.acquireVehicle();
			if(!future.isDone())
				waitingFuture.add(future);
			complete( event,future);
		});
		//subscribes to releaseVehicleEvent, will realese a given vehicle when called
		ReleaseVehicleEvent releaseVehicleEvent=new ReleaseVehicleEvent();
		subscribeEvent(releaseVehicleEvent.getClass(), (event)->{
			resourcesHolder.releaseVehicle( event.getVehicle());
			complete((ReleaseVehicleEvent)event, 1);
		});
		//subscribes to FinalTickBroadcast, terminates when called
		FinalTickBroadcast finalTickBroadcast=new FinalTickBroadcast();
		subscribeBroadcast(finalTickBroadcast.getClass(), (broadcast)->{
			//before terminating resolved all the futures who wait for a vehicle
			Iterator iter=waitingFuture.iterator();
			while (iter.hasNext()) {
			Future<DeliveryVehicle> temp=(Future<DeliveryVehicle>)iter.next();
				if (!temp.isDone())
					temp.resolve(null);
			}
			terminate();
		});
	}

}
