
import bgu.spl.mics.Future;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest<T> {

    private Future<String> Future_test;


    @Before
    public void setUp() throws Exception {
        this.Future_test=createFuture();
    }

    public Future<String> createFuture(){
        return new Future<String>();
    }



    @Test
    public void get() {
        String st= "check";
        Future_test.resolve(st);
        String result = Future_test.get();
        assertEquals(st,result);
    }

    @Test
    public void resolve() {
        String st= "check";
        assertFalse(Future_test.isDone());
        Future_test.resolve(st);
        assertTrue(Future_test.isDone());
        assertEquals(Future_test.get(),st);
    }

    @Test
    public void isDone() {
        assertFalse(Future_test.isDone());
        String st= "check";
        Future_test.resolve(st);
        assertTrue(Future_test.isDone());

    }

    @Test
    public void get1() {
        assertFalse((Future_test.isDone()));
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        long timeout=timeUnit.convert(1000,timeUnit);
        assertNull(Future_test.get(timeout, timeUnit));
        Future_test.resolve("check");
        assertEquals(Future_test.get(timeout, timeUnit), "check");
        }

    }
