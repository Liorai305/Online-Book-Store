package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent<OrderReciept> implements  Event {
    private String name = "BookOrderEvent";
    private String Title;
    private Customer customer;
    private int orderTick;


    public  BookOrderEvent () {

    }

    /**
     * CONSTRUCTOR
     * @param Title
     * @param customer
     * @param orderTick
     */

    public  BookOrderEvent (String Title, Customer customer,int orderTick) {
        this.Title = Title;
        this.customer = customer;
        this.orderTick=orderTick;
    }

    /**
     *
     * @return String that represents book title
     */

    public String getTitle () {
        return Title;
    }

    /**
     *
     * @return customer that wishes to order a book
     */

    public Customer getCustomer () {
        return customer;
    }

    /**
     *
     * @return the representing String "BookOrderEvent"
     */

    public String getName() {
        return name;
    }


    /**
     *
     * @return Retrieves the tick in which the customer sent the purchase request.
     */

    public int getOrderTick(){ return orderTick;}
    }


