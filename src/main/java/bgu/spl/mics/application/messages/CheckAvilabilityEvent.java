package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class CheckAvilabilityEvent<Integer> implements  Event{
    private String book_title;
    private String name ="CheckAvilabilityEvent";


    /**
     * CONSTRUCTOR
     */
    public CheckAvilabilityEvent (){}

    /**
     * CONSTRUCTOR
     */
    public CheckAvilabilityEvent (String book_title) {
    this.book_title = book_title;
    }

    /**
     *
     * @return the representing String "CheckAvilabilityEvent"
     */
    public String getName(){
        return name;
    }

    /**
     *
     * @return String that represents book title
     */
    public String getBook_title(){
        return book_title;
    }

}
