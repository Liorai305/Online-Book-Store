package bgu.spl.mics;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 */
public class MessageBusImpl implements MessageBus {


	private ConcurrentHashMap<MicroService,LinkedBlockingQueue<Message>>  LOOP=new ConcurrentHashMap<>();;
	private ConcurrentHashMap<Class, LinkedBlockingQueue<MicroService>> subscribed_to_events= new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class, LinkedBlockingQueue<MicroService>> subscribed_to_broadcasts= new ConcurrentHashMap<>();
	private ConcurrentHashMap<Event, Future> active_events= new ConcurrentHashMap<>();

	/**
	 * private constructor
	 */
	private MessageBusImpl(){}

	/**
	 * private class that creates single instance of Inventory
	 * only reachable from inside the class
	 */
	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	/**
	 * adds MicroService m to the queue of Class type
	 */
	public  <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		//locks type in order to prevent the creation of several different queues for the same type
		synchronized (type) {
			if (subscribed_to_events.get(type) == null) {
				LinkedBlockingQueue event_queue = new LinkedBlockingQueue();
				event_queue.add(m);
				subscribed_to_events.put(type, event_queue);
			} else
				subscribed_to_events.get(type).add(m);
		}
	}

	/**
	 * adds MicroService m to the queue of Class type
	 */
	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		//locks type in order to prevent the creation of several different queues for the same type
		synchronized (type) {
			if (subscribed_to_broadcasts.get(type) == null) {
				LinkedBlockingQueue broadcast_queue = new LinkedBlockingQueue();
				broadcast_queue.add(m);
				subscribed_to_broadcasts.put(type, broadcast_queue);
			}
			else
			subscribed_to_broadcasts.get(type).add(m);
		}
	}

	@Override
	/**
	 * Notifies the MessageBus that the event {@code e} is completed and its
	 * result was {@code result}.
	 * When this method is called, the message-bus will resolve the {@link Future}
	 * object associated with {@link Event} {@code e}.
	 * <p>
	 * @param <T>    The type of the result expected by the completed event.
	 * @param e      The completed event.
	 * @param result The resolved result of the completed event.
	 */
	public <T> void complete(Event<T> e, T result) {
	active_events.get(e).resolve(result);


	}

	/**
	 * Adds the {@link Broadcast} {@code b} to the message queues of all the
	 * micro-services subscribed to {@code b.getClass()}.
	 * <p>
	 * @param b 	The message to added to the queues.
	 */
	@Override
	public void sendBroadcast(Broadcast b) {
		//finds all the micro services that subscribed to b
		LinkedBlockingQueue<MicroService> temp = subscribed_to_broadcasts.get(b.getClass());
			Iterator<MicroService> iter = temp.iterator();
			while (iter.hasNext()) {
				MicroService temp1 = iter.next();
				//adds the broadcast to each micro service
				LOOP.get(temp1).add(b);
			}
		}

	/**
	 * Adds the {@link Event} {@code e} to the message queue of one of the
	 * micro-services subscribed to {@code e.getClass()} in a round-robin
	 * fashion.
	 * <p>
	 * @param <T>    	The type of the result expected by the event and its corresponding future object.
	 * @param e     	The event to add to the queue.
	 * @return {@link Future<T>} object to be resolved once the processing is complete,
	 * 	       null in case no micro-service has subscribed to {@code e.getClass()}.
	 */
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		//adds the event to the active event map
		Future future=new <T>Future();
		active_events.put(e,future);
		//finds all the micro services that subscribed to e
		LinkedBlockingQueue<MicroService> temp=subscribed_to_events.get(e.getClass());
		if(temp==null||temp.size()==0)
			return null;
		//locks the queue of subscribed micro services to event e, in order to prevent problems when unregistering a micro service
		synchronized (subscribed_to_events.get(e.getClass())){
			MicroService m = subscribed_to_events.get(e.getClass()).poll();  //taking from the head
			if(m==null)
				return null;
			subscribed_to_events.get(e.getClass()).add(m); //inserting at the tail
			LOOP.get(m).add(e);

			}
			return future;
		}

	/**
	 * Allocates a message-queue for the {@link MicroService} {@code m}.
	 * <p>
	 * @param m the micro-service to create a queue for.
	 */
	public void register(MicroService m) {
		LinkedBlockingQueue <Message> queue= new LinkedBlockingQueue<>();
		LOOP.put(m,queue);
	}

	/**
	 * Removes the message queue allocated to {@code m} via the call to
	 * {@link #register(bgu.spl.mics.MicroService)} and cleans all references
	 * related to {@code m} in this message-bus. If {@code m} was not
	 * registered, nothing should happen.
	 * <p>
	 * @param m the micro-service to unregister.
	 */

	@Override
	public void unregister(MicroService m) {
		for (Class z : subscribed_to_events.keySet()) {
			//locks the queue of subscribed micro services to event e, in order to prevent problems when sending event to micro service
			synchronized (subscribed_to_events.get(z)) {
				Iterator iter1 = subscribed_to_events.get(z).iterator();
				while (iter1.hasNext()) {
					if (((MicroService) iter1.next()).getName().equals(m.getName()))
						subscribed_to_events.get(z).remove(m);
				}
			}
		}
	//removes m queue from the LOOP, makes sure all the event in his queue resolved
			if (LOOP.get(m) != null) {
				Iterator iter3 = LOOP.get(m).iterator();
				while (iter3.hasNext()) {
					active_events.get(iter3.next()).resolve(null);
				}
				LOOP.remove(m);
			}
		}


	/**
	 *
	 * @param m The micro-service requesting to take a message from its message
	 *          queue.
	 * @return
	 * @throws InterruptedException
	 */
	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message message;
		if(LOOP.get(m)==null)
			throw new IllegalStateException("micro service was never registered");
		message=LOOP.get(m).take();
	return message;

	}

	

}

