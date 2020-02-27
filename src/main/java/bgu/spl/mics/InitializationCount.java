package bgu.spl.mics;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class manages the count of the initialized services
 * holds AtomicInteger in order to make safe count
 * this class is a Singelton
 */
public class InitializationCount {

    private AtomicInteger count=new AtomicInteger();

    /**
     * private class that creates single instance of InitializationCount
     * only reachable from inside the class
     */
    private static class SingletonHolder {
        private static InitializationCount instance = new InitializationCount();
    }

    /**
     * private CONSTRUCTOR
     */
    private InitializationCount() {
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static InitializationCount getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * increases count by one
     */
    public void increaseCount(){
        count.incrementAndGet();
    }

    /**
     *
     * @return count
     */
    public int getCount(){
        return count.intValue();
    }

}
