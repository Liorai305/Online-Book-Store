
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InventoryTest {

    private Inventory InventoryTest;
    private BookInventoryInfo[] books=new BookInventoryInfo[2];
    @Before
    public void setUp()  {
        this.InventoryTest=Inventory.getInstance();
        BookInventoryInfo book1=new BookInventoryInfo("HARRY POTTER 1", 3, 30);
        BookInventoryInfo book2=new BookInventoryInfo("HARRY POTTER 2", 3, 40);
        this.books[0]=book1;
        this.books[1]=book2;
    }

    @Test
    public void getInstance() {
        Inventory inventory1=Inventory.getInstance();
        assertEquals(InventoryTest,inventory1);
    }

    @Test
    public void load() {
        try {
            InventoryTest.load(books);
        } catch (Exception e) {
            fail("Unexpected exception: ");
        }
        if(InventoryTest.take("HARRY POTTER 1")==OrderResult.NOT_IN_STOCK)
            fail("BOOK SHOULD BE IN THE INVENTORY");
        if(InventoryTest.take("HARRY POTTER 2")== OrderResult.NOT_IN_STOCK)
            fail("BOOK SHOULD BE IN THE INVENTORY");
        if(InventoryTest.take("HARRY POTTER 3")==OrderResult.SUCCESSFULLY_TAKEN)
            fail("BOOK SHOULD NOT BE IN THE INVENTORY");
}

    @Test
    public void take() {
        try {
            InventoryTest.take(null);
            fail("Exception expected!");
        }   catch(Exception e){
        }
        assertEquals(InventoryTest.take("HARRY POTTER 1") ,OrderResult.SUCCESSFULLY_TAKEN);
        assertEquals(InventoryTest.take("HARRY POTTER 3") ,OrderResult.NOT_IN_STOCK);
    }

    @Test
    public void checkAvailabiltyAndGetPrice(){
        try {
            InventoryTest.checkAvailabiltyAndGetPrice(null);
            fail("Exception expected!");
        } catch(Exception e){
        }
        assertEquals(InventoryTest.checkAvailabiltyAndGetPrice("HARRY POTTER 1") ,30);
        assertEquals(InventoryTest.checkAvailabiltyAndGetPrice("HARRY POTTER 3") ,-1);
    }

    @Test
    public void printInventoryToFile() {
    }
}