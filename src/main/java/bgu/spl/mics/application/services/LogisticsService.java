package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.VehicleEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 */
public class LogisticsService extends MicroService {

	/**
	 * CONSTRUCTOR
	 * @param name
	 */
	public LogisticsService(String name) {
		super(name);
	}

	/**
	 *creates callbacks and subscribes for DeliveryEvent and FinalTickBroadcast
	 */
	@Override
	protected void initialize() {
		//subscribes to DeliveryEvent, manage delivery when called
		DeliveryEvent deliveryEvent = new DeliveryEvent();
		subscribeEvent(deliveryEvent.getClass(), (event) -> {
			//creates vehicleEvent in order to acquire vehicle, waits for the vehicle
			VehicleEvent vehicleEvent = new VehicleEvent(event.getCustomer());
			Future <Future<DeliveryVehicle>> future = sendEvent(vehicleEvent);
			if(future!=null) {
				if (future.get() != null) {
					if (future.get().get()!=null) {
						//makes the delivery
						future.get().get().deliver(event.getCustomer().getAddress(), event.getCustomer().getDistance());
						//creates releaseVehicleEvent in order to release the delivery vehicle
						ReleaseVehicleEvent releaseVehicleEvent = new ReleaseVehicleEvent(future.get().get());
						sendEvent(releaseVehicleEvent);
						complete((DeliveryEvent) event, 1);
					}
					else
						complete((DeliveryEvent) event,null);
				}
				else
					complete((DeliveryEvent) event,null);
			}else
				complete((DeliveryEvent) event,null);
		});
		//subscribes to FinalTickBroadcast, terminates when called
		FinalTickBroadcast finalTickBroadcast=new FinalTickBroadcast();
		subscribeBroadcast(finalTickBroadcast.getClass(), (broadcast)->{
			terminate();
		});
	}
}
