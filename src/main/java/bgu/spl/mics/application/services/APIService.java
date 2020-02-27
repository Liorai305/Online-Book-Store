package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 */
public class APIService extends MicroService{

	private ConcurrentHashMap<Integer,LinkedBlockingQueue <BookOrderEvent>> scheduleMap =new ConcurrentHashMap<>();
	private Customer customer;
	private int currentTick;
	ArrayList<Future> ArrayFuture=new ArrayList<>();

	/**
	 * CONSTRUCTOR
	 * @param name: a string that represents the name of the specific APIService
	 * @param orderSchedule
	 * @param customer: customer who wishes to order books from the store
	 */
	public APIService(String name, LinkedBlockingQueue <BookOrderEvent> orderSchedule, Customer customer){
		super(name);
		//initializing HashMap from the orderSchedule, the key is a specific tick, the value is a queue of orders scheduled to this tick
		Iterator iter=orderSchedule.iterator();
		while (iter.hasNext()){
			BookOrderEvent temp=(BookOrderEvent)iter.next();
			if(scheduleMap.get(temp.getOrderTick())==null) {
				LinkedBlockingQueue<BookOrderEvent> same_tick_list=new LinkedBlockingQueue<>();
				same_tick_list.add(temp);
				scheduleMap.put(temp.getOrderTick(),same_tick_list);
			}
			else
				scheduleMap.get(temp.getOrderTick()).add(temp);
		}
		this.customer = customer;
	}

	@Override
	/**
	 * creates callbacks and subscribes for TickBroadcast and FinalTickBroadcast
	 */
	protected void initialize() {
		TickBroadcast tickBroadcast = new TickBroadcast();
		subscribeBroadcast(tickBroadcast.getClass(), (event) -> {
			this.currentTick = event.getCurrentTick();
			//checks if there is orders for the current tick
			if (scheduleMap.get(event.getCurrentTick()) != null) {
				//goes over the orders that been scheduled to the current tick, for each order creates BookOrderEvent and sends it
				Iterator iter = scheduleMap.get(event.getCurrentTick()).iterator();
				while (iter.hasNext()) {
					BookOrderEvent temp = (BookOrderEvent) iter.next();
					Future future = sendEvent(temp);
					if (future != null)
						//does not wait for the future to be resolved, continues to the next order
						ArrayFuture.add(future);
				}
				//in each tick we go over the futures that we hold, add the resolved ones to the customer and remove them from the list.
				for (int i = 0; i < ArrayFuture.size(); i++) {
					if (ArrayFuture.get(i).isDone()) {
						if (ArrayFuture.get(i).get() != null)
							customer.addRecipt((OrderReceipt) ArrayFuture.get(i).get());
						ArrayFuture.remove(i);


					}
				}
			}
		});
		FinalTickBroadcast finalTickBroadcast = new FinalTickBroadcast();
		subscribeBroadcast(finalTickBroadcast.getClass(), (broadcast) -> {
			//checks at the final tick if there any resolved future left before terminating
			for (int i = 0; i < ArrayFuture.size(); i++) {
				if (ArrayFuture.get(i).isDone()) {
					if (ArrayFuture.get(i).get() != null)
						customer.addRecipt((OrderReceipt) ArrayFuture.get(i).get());
				}
			}
			terminate();

		});
	}
	public int getCurrentTick(){
		return currentTick;
	}

}
