package bgu.spl.mics;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The MicroService is an abstract class that any micro-service in the system
 * must extend. The abstract MicroService class is responsible to get and
 * manipulate the singleton {@link MessageBus} instance.
 * <p>
 */
public abstract class MicroService implements Runnable {

    private boolean terminated = false;
    private final String name;
    private MessageBusImpl messageBus=MessageBusImpl.getInstance();
    private ConcurrentHashMap<Class,Callback> callbacks =new ConcurrentHashMap<>();
    private InitializationCount count=InitializationCount.getInstance();

    /**
     * @param name the micro-service name
     */
    public MicroService(String name) {
        this.name = name;
    }

    /**
     * micro service subscribes itself to event type in the Message Bus, and adds a callback to the HashMap that holds it
     * @param type
     * @param callback
     * @param <T>
     * @param <E>
     */
    protected final <T, E extends Event<T>> void subscribeEvent(Class<E> type, Callback<E> callback) {
        callbacks.put(type,callback);
        messageBus.subscribeEvent(type,this);
    }

    /**
     * micro service subscribes itself to broadcast type in the Message Bus, and adds a callback to the HashMap that holds it
     * @param <B>      The type of broadcast message to subscribe to
     * @param type     The {@link Class} representing the type of broadcast
     *                 message to subscribe to.
     * @param callback The callback that should be called when messages of type
     *                 {@code type} are taken from this micro-service message
     *                 queue.
     */
    protected final <B extends Broadcast> void subscribeBroadcast(Class<B> type, Callback<B> callback) {
        messageBus.subscribeBroadcast(type,this);
        callbacks.put(type,callback);
    }

    /**
     * Sends the event {@code e} using the message-bus and receive a {@link Future<T>}
     * object that may be resolved to hold a result. This method must be Non-Blocking since
     * there may be events which do not require any response and resolving.
     * <p>
     * @param <T>       The type of the expected result of the request
     *                  {@code e}
     * @param e         The event to send
     * @return  		{@link Future<T>} object that may be resolved later by a different
     *         			micro-service processing this event.
     * 	       			null in case no micro-service has subscribed to {@code e.getClass()}.
     */
    protected final <T> Future<T> sendEvent(Event<T> e) {
        return messageBus.sendEvent(e);
    }

    /**
     * A Micro-Service calls this method in order to send the broadcast message {@code b} using the message-bus
     * to all the services subscribed to it.
     * <p>
     * @param b The broadcast message to send
     */
    protected final void sendBroadcast(Broadcast b) {
        messageBus.sendBroadcast(b);
    }

    /**
     * Completes the received request {@code e} with the result {@code result}
     * using the message-bus.
     * <p>
     * @param <T>    The type of the expected result of the processed event
     *               {@code e}.
     * @param e      The event to complete.
     * @param result The result to resolve the relevant Future object.
     *               {@code e}.
     */
    protected final <T> void complete(Event<T> e, T result) {
        messageBus.complete(e,result);
    }

    /**
     * implemented in all micro service differently
     */
    protected abstract void initialize();

    /**
     * Signals the event loop that it must terminate after handling the current
     * message.
     * unregister itself from the message bus
     */
    protected final void terminate() {
        this.terminated = true;
        messageBus.unregister(this);
    }

    /**
     * @return the name of the service - the service name is given to it in the
     *         construction time.
     */
    public final String getName() {
        return name;
    }

    /**
     *the actual work of each micro service
     * registers the micro service to the Message Bus
     * initialize and increases the count(counts how much services has registered)
     * while micro service not terminated tries to take a message from his queue in the Message Bus.
     * calls the callback that matches the message
     */
    @Override
    public final void run() {
        messageBus.register(this);
        initialize();
        count.increaseCount();

        while (!terminated) {
        try {
            Message message=messageBus.awaitMessage(this);
            callbacks.get(message.getClass()).call(message);
        }catch(InterruptedException e){
            terminated=true;
        }
        }

    }


}
