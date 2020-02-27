package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent <Integer> implements Event {
    private DeliveryVehicle vehicle;


    /**
     * CONSTRUCTOR
     */
    public ReleaseVehicleEvent(){
    }

    /**
     * CONSTRUCTOR
     * @param vehicle
     */
    public ReleaseVehicleEvent(DeliveryVehicle vehicle){
        this.vehicle=vehicle;
    }

    /**
     *
     * @return vehicle
     */
    public DeliveryVehicle getVehicle(){
        return vehicle;
    }
}
