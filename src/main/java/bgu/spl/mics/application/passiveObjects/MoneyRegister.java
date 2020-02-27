package bgu.spl.mics.application.passiveObjects;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management.
 * It should hold a list of receipts issued by the store.
 */
public class MoneyRegister implements Serializable {


	private AtomicInteger TotalEarnings = new AtomicInteger();
	private LinkedBlockingQueue<OrderReceipt> recipts = new LinkedBlockingQueue<OrderReceipt>() ;
	private ArrayList <OrderReceipt> PrintRecipts = new ArrayList<>() ;

	/**
	 * private class that creates single instance of MoneyRegister
	 * only reachable from inside the class
	 */

	private static class SingletonHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MoneyRegister getInstance() {
		return SingletonHolder.instance;
	}

	/**
	 * private constructor
	 */
	private MoneyRegister () {
	}


	/**
	 * Saves an order receipt in the money register.
	 * <p>
	 * @param r		The receipt to save in the money register.
	 */
	public void file (OrderReceipt r) {
		recipts.add(r);
		TotalEarnings.getAndAdd((int)r.getPrice());
	}

	/**
	 * Retrieves the current total earnings of the store.
	 */
	public int getTotalEarnings() {
		return TotalEarnings.intValue();
	}

	/**
	 * Charges the credit card of the customer a certain amount of money.
	 * <p>
	 * @param amount 	amount to charge
	 */
	public void chargeCreditCard(Customer c, int amount) {
		c.reduceAvailableCreditAmount(amount);
	}

	/**
	 * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts
	 * currently in the MoneyRegister
	 * This method is called by the main method in order to generate the output.
	 */
	public void printOrderReceipts(String filename) {
		Iterator iter = recipts.iterator();
		while (iter.hasNext()){
			OrderReceipt recipt = (OrderReceipt) iter.next();
			PrintRecipts.add(recipt);
		}
		try {
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);

			out.writeObject(PrintRecipts);
			out.close();
			file.close();
		}
		catch (IOException e) {}
	}
}


