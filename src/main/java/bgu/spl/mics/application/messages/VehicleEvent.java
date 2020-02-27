package bgu.spl.mics.application.messages;
import bgu.spl.mics.Future;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class VehicleEvent  implements Event <Future<DeliveryVehicle>> {
    private Customer customer;



    /**
     * CONSTRUCTOR
     * @param customer
     */

    public VehicleEvent (Customer customer){this.customer=customer;}

    /**
     *
     * @return customer
     */
     public Customer getCustomer(){
        return customer;}
    }
