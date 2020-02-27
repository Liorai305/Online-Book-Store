package bgu.spl.mics.application.messages;
import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private String name ="TickBroadcast";
    private int currentTick;

    /**
     * CONSTRUCTOR
     */
    public TickBroadcast( ){}

    /**
     * CONSTRUCTOR
     */
    public TickBroadcast( int currentTick){
        this.currentTick=currentTick;
    }

    /**
     *
     * @return the representing String "DeliveryEvent"
     */

    public String getName(){
        return name;
    }

    /**
     *
     * @return current tick
     */

    public int getCurrentTick(){
        return currentTick;
    }
}
