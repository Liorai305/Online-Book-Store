package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

import bgu.spl.mics.application.passiveObjects.Customer;


public class DeliveryEvent <Integer> implements  Event {
    private Customer customer;
    private String name ="DeliveryEvent";

    /**
     * CONSTRUCTOR
     */

    public DeliveryEvent(){}

    /**
     * CONSTRUCTOR
     * @param customer
     */

    public DeliveryEvent(Customer customer){
        this.customer=customer;
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
     * @return customer to make a delivery to
     */
    public Customer getCustomer (){
        return customer;
    }
}
