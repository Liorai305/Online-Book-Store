package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 *
 */
public class BookInventoryInfo implements Serializable {
	private String bookTitle;
	private AtomicInteger amountInInventory = new AtomicInteger();
	private int price;

	public BookInventoryInfo(String title, int amount, int price){
		bookTitle=title;
		amountInInventory.addAndGet(amount);
		this.price=price;
	}
	/**
	 * Retrieves the title of this book.
	 * <p>
	 * @return The title of this book.
	 */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		return amountInInventory.intValue();
	}

	/**
	 * This function reduces book's amount in Inventory by one
	 */
	public void reduceAmountInInventory() {
		amountInInventory.getAndDecrement();
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}
	
	

	
}
