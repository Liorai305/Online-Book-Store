package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	private String name;
	private int id;
	private String adress;
	private int distance;
	private ArrayList<OrderReceipt> custumersReceiptList;
	private AtomicInteger AvailableCreditAmount;
	private int CreditNumber;


	/**
	 * CONSTRUCTOR
	 * @param name
	 * @param id
	 * @param adress
	 * @param distance
	 * @param AvailableCreditAmout
	 * @param CreditNumber
	 */
	public Customer(String name,int id,String adress, int distance, int AvailableCreditAmout, int CreditNumber){
		this.name=name;
		this.id=id;
		this.adress=adress;
		this.distance=distance;
		this.custumersReceiptList=new ArrayList<>();
		this.AvailableCreditAmount= new AtomicInteger(AvailableCreditAmout);
		this.CreditNumber=CreditNumber;
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return adress;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public ArrayList<OrderReceipt> getCustomerReceiptList() {
		return custumersReceiptList;
	}

	/**
	 * adds recipt to customer
	 * @param receipt
	 */
	public void addRecipt (OrderReceipt receipt){
		custumersReceiptList.add(receipt);
	}
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return AvailableCreditAmount.intValue();
	}

	/**
	 * reduces "amount" from available credit amount
	 * @param amount
	 */
	public void reduceAvailableCreditAmount(int amount) {
		int temp=AvailableCreditAmount.intValue()-amount;
		AvailableCreditAmount.set(temp);
	}

	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return CreditNumber;
	}
	
}
