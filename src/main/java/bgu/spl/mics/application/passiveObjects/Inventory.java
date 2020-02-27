package bgu.spl.mics.application.passiveObjects;
import java.io.*;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 */
public class Inventory implements Serializable {

	private ConcurrentHashMap<String, BookInventoryInfo> map = new ConcurrentHashMap<>();
	private HashMap<String, Integer> outputMap = new HashMap<>();

	/**
	 * private class that creates single instance of Inventory
	 * only reachable from inside the class
	 */
	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}

	/**
	 * private constructor
	 */
	private Inventory() {
	}

	/**
	 * Retrieves the single instance of this class.
	 */

	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}


	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
		for (int i=0; i<inventory.length; i++) {
			map.put(inventory[i].getBookTitle(), inventory[i]);
		}
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		if(book==null)
			throw new IllegalArgumentException();
		BookInventoryInfo temp= map.get(book);
		if (temp == null)
			return OrderResult.NOT_IN_STOCK;
		//locks the desired book,in order to prevent taking a book that is already taken
		synchronized (temp) {
			if (temp.getAmountInInventory() == 0)
				return OrderResult.NOT_IN_STOCK;
			temp.reduceAmountInInventory();
			return OrderResult.SUCCESSFULLY_TAKEN;
		}
	}

	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {

		BookInventoryInfo temp= map.get(book);
		if(temp==null||temp.getAmountInInventory()==0)
			return -1;
		synchronized (temp) {
			if (temp == null||temp.getAmountInInventory()==0)
				return -1;
			return temp.getPrice();

		}
}

	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a 
     * Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		for (String z : map.keySet()){
			outputMap.put(z,map.get(z).getAmountInInventory());
		}
		try {
			FileOutputStream file = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(file);

			out.writeObject(outputMap);
			out.close();
			file.close();
		}
		catch (IOException e) {}

	}
}
