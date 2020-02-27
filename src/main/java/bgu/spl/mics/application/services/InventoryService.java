package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CheckAvilabilityEvent;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TakeEvent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store..
 */

public class InventoryService extends MicroService {
	private Inventory inventory = Inventory.getInstance();

	/**
	 * constructor
	 * @param name
	 */
	public InventoryService(String name) {
		super(name);
	}

	/**
	 * creates callbacks and subscribes for checkAvilabilityEvent and FinalTickBroadcast
	 */
	@Override
	protected void initialize() {
		CheckAvilabilityEvent checkAvilabilityEvent = new CheckAvilabilityEvent();
		//subscribes to checkAvilabilityEvent, checks whether the book is available when called
		subscribeEvent(checkAvilabilityEvent.getClass(), (event) -> {
			int price = inventory.checkAvailabiltyAndGetPrice(event.getBook_title());
			complete((CheckAvilabilityEvent) event, price);
		});
		//subscribes to TakeEvent, checks whether the book was taken when called
		TakeEvent takeEvent = new TakeEvent();
		subscribeEvent(takeEvent.getClass(), (event) -> {
			OrderResult result = inventory.take((event).getBook_title());
			complete((TakeEvent) event, result);
		});
		//subscribes to FinalTickBroadcast, terminates when called
		FinalTickBroadcast finalTickBroadcast=new FinalTickBroadcast();
		subscribeBroadcast(finalTickBroadcast.getClass(), (broadcast)->{
			terminate();
		});

	}
}
