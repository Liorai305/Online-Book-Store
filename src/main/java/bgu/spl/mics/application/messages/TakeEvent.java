package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class TakeEvent<OrderResult> implements Event {
    private String book_title;

    /**
     * CONSTRUCTOR
     */
    public TakeEvent(){}

    /**
     * CONSTRUCTOR
     * @param book_title
     */
    public TakeEvent(String book_title){
        this.book_title=book_title;
    }

    /**
     *
     * @return String that represents book title
     */
    public String getBook_title() {
        return book_title;
    }
}
