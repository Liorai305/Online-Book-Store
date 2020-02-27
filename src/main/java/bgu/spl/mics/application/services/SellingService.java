package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderResult;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister=MoneyRegister.getInstance();
	private int currentTick;

	/**
	 * CONSTRUCTOR
	 * @param name
	 */
	public SellingService(String name) {
		super(name);
	}


	/**
	 * creates callbacks and subscribes for BookOrderEvent, FinalTickBroadcast and TickBroadcast
	 */
	@Override
	protected void initialize() {
		//subscribes to BookOrderEvent, handles book order when called
		BookOrderEvent bookOrderEvent = new BookOrderEvent();
		subscribeEvent(bookOrderEvent.getClass(),  (event)-> {
			boolean flag=false;
			//creating CheckAvilabilityEvent and waits for response whether the book available
			CheckAvilabilityEvent checkAvilabilityEvent = new CheckAvilabilityEvent(event.getTitle());
			Future <Integer> Check_future = sendEvent(checkAvilabilityEvent);
			if(Check_future!=null) {
				if (Check_future.get()!=null&&(int) Check_future.get() != -1) {
					//locks customer in order to prevent troubles when charging his credit card
					synchronized (event.getCustomer()) {
						if (event.getCustomer().getAvailableCreditAmount() >= (int)Check_future.get()) {
							//creates TakeEvent in order to take the desired book from the inventory, waits for response whether the take was successful
							TakeEvent takeEvent = new TakeEvent(event.getTitle());
							Future Take_future = sendEvent(takeEvent);
							//if successfully taken the book, charges the customer's credit card
							if (Take_future.get() == OrderResult.SUCCESSFULLY_TAKEN) {
								moneyRegister.chargeCreditCard(event.getCustomer(), ((int) Check_future.get()));
								flag = true;
							}
						}
					}
					//if successfully taken the book and charged the customer's credit card creates deliveryEvent, doesn't wait
					if (flag) {
						DeliveryEvent deliveryEvent = new DeliveryEvent(event.getCustomer());
						sendEvent(deliveryEvent);
						OrderReceipt orderReceipt = new OrderReceipt(0, this.getName(), event.getCustomer().getId(), event.getTitle(), (int) Check_future.get(), currentTick, event.getOrderTick(), currentTick);
						complete((BookOrderEvent) event, orderReceipt);
						moneyRegister.file(orderReceipt);
					}
					else
						complete((BookOrderEvent) event, null);
				}
				else
					complete((BookOrderEvent) event, null);
			}
			else
				complete((BookOrderEvent) event, null);
		});

		TickBroadcast tickBroadcast=new TickBroadcast();
		subscribeBroadcast(tickBroadcast.getClass(),(event)->{
			this.currentTick=event.getCurrentTick();

		});

		FinalTickBroadcast finalTickBroadcast=new FinalTickBroadcast();
		subscribeBroadcast(finalTickBroadcast.getClass(), (broadcast)->{
			terminate();
		});
	}
	public int getCurrentTick(){
		return currentTick;
	}

}
