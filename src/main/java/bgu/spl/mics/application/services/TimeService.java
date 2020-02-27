package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinalTickBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;


/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.

 */
public class TimeService extends MicroService{
	private int currentTick;
	private int speed;
	private int duration;

	/**
	 * CONSTRUCTOR
	 * @param speed
	 * @param duration
	 */
	public TimeService(int speed, int duration){
		super("TimeService");
		this.speed=speed;
		this.duration=duration;
		currentTick=1;
	}

	/**
	 * creates TickBroadcast that contains current tick
	 * and sends it every @speed to the MessageBus
	 * when reaches the final tick, creates FinalTickBroadcast, sends it and terminates
	 */
	@Override
	protected void initialize() {
			while(currentTick<duration){
				TickBroadcast tickBroadcast= new TickBroadcast(currentTick);
				sendBroadcast(tickBroadcast);
				try {
					Thread.currentThread().sleep(speed);
				}catch(InterruptedException e){}
				currentTick=currentTick+1;
			}
			FinalTickBroadcast finalTickBroadcast=new FinalTickBroadcast();
			sendBroadcast(finalTickBroadcast);
			terminate();

	}

}

