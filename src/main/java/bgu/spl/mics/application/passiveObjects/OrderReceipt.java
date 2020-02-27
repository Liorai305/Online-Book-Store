package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Passive data-object representing a receipt that should 
 * be sent to a customer after the completion of a BookOrderEvent.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class OrderReceipt implements Serializable {

	private int id;
	private String seller;
	private int customerId;
	private String bookTitle;
	private int price;
	private int IssuedTick;
	private int OrderTick;
	private int ProcessTick;

	/**
	 * CONSTRUCTOR
	 */
	public OrderReceipt(){}

	/**
	 * CONSTRUCTOR
	 * @param id
	 * @param seller
	 * @param customerId
	 * @param bookTitle
	 * @param price
	 * @param IssuedTick
	 * @param OrderTick
	 * @param ProcessTick
	 */
	public OrderReceipt(int id, String seller, int customerId, String bookTitle, int price, int IssuedTick, int OrderTick, int ProcessTick){
		this.id=id;
		this.seller=seller;
		this.customerId=customerId;
		this.bookTitle=bookTitle;
		this.IssuedTick=IssuedTick;
		this.price=price;
		this.OrderTick=OrderTick;
		this.ProcessTick=ProcessTick;
	}
	/**
     * Retrieves the orderId of this receipt.
     */
	public int getOrderId() {
		return id;
	}
	
	/**
     * Retrieves the name of the selling service which handled the order.
     */
	public String getSeller() {
		return seller;
	}
	
	/**
     * Retrieves the ID of the customer to which this receipt is issued to.
     * <p>
     * @return the ID of the customer
     */
	public int getCustomerId() {
		return customerId;
	}
	
	/**
     * Retrieves the name of the book which was bought.
     */
	public String getBookTitle() {
		return bookTitle;
	}
	
	/**
     * Retrieves the price the customer paid for the book.
     */
	public int getPrice() {
		return price;
	}
	
	/**
     * Retrieves the tick in which this receipt was issued.
     */
	public int getIssuedTick() {
		return IssuedTick;
	}
	
	/**
     * Retrieves the tick in which the customer sent the purchase request.
     */
	public int getOrderTick() {
		return OrderTick;
	}
	
	/**
     * Retrieves the tick in which the treating selling service started 
     * processing the order.
     */
	public int getProcessTick() {
		return ProcessTick;
	}
}
