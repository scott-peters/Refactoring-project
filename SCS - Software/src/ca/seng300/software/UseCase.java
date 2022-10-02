package ca.seng300.software;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Item;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.products.BarcodedProduct;


public class UseCase {
	public static final int[] BANKNOTE_DENOMINATIONS = { 5, 10, 20, 50, 100 };
	public static final BigDecimal[] COIN_DENOMINATIONS = {
			new BigDecimal("0.05"),
			new BigDecimal("0.1"),
			new BigDecimal("0.25"),
			new BigDecimal("0.5"),
			new BigDecimal("1.0"),
			new BigDecimal("2.0")
	};
	public static final Currency CURRENCY = Currency.getInstance("CAD");

	private final SelfCheckoutStation scs;
	public HashMap<Barcode, BarcodedItem> databaseBarcodedItem = new HashMap<Barcode, BarcodedItem> ();
	public ProductDatabase productDatabase = new ProductDatabase();
	public MemberDatabase memberDatabase = new MemberDatabase();
	public ReceiptLogic receiptLogic;
	private static Item currentScanItem;
	private static Member currentMember;
	private BigDecimal amountOwed = new BigDecimal(0.0);
	private Map<String, BigDecimal> cart = new HashMap<String, BigDecimal>();
	private ArrayList<Double> itemWeightOnScale = new ArrayList<Double>();
	private boolean wishToCheckout = false;
	private boolean putItemInBag = false;
	private boolean bagPlaced = false;
	private CardReader cardReader;
	private PaymentLogic payment;
	
	public UseCase() {
		this.scs = new SelfCheckoutStation(CURRENCY, BANKNOTE_DENOMINATIONS, COIN_DENOMINATIONS, 2000, 50);
		
		payment = new PaymentLogic(scs, this);
		// Instantiating and attaching cardSoftware to scs cardReader
		this.cardReader = scs.cardReader;
		scs.cardReader.attach(payment);
		
		this.receiptLogic = new ReceiptLogic(scs.printer);
		scs.printer.attach(this.receiptLogic);
		
		scs.mainScanner.enable();
		scs.handheldScanner.enable();
		scs.coinSlot.enable();
		scs.banknoteInput.enable();
		this.currentMember = null;
	}

	/**
	 * @param barcodedProduct
	 */
	public void addToCartByUnit(BarcodedProduct barcodedProduct)
	{
		this.cart.put(barcodedProduct.getDescription(), barcodedProduct.getPrice());
		updateOwedAmount(barcodedProduct.getPrice());
	}
	
	public void addToCartByWeight(String name, BigDecimal price)
	{
		this.cart.put(name, price);
		updateOwedAmount(price);
	}

	/**
	 * @param amount
	 */
	public void setTotalAmountOwed(BigDecimal amount) {
		this.amountOwed = amount;
	}

	/**
	 * @param weight
	 */
	public void updateWeightOnScale(double weight) {
		itemWeightOnScale.add(weight);
	}

	/**
	 * @param amount
	 */
	public void updateOwedAmount(BigDecimal amount) {
		this.amountOwed = this.amountOwed.add(amount);
	}
	
	public void subtractOwedAmount(BigDecimal amount) {
		this.amountOwed = this.amountOwed.subtract(amount);
	}
	
	public void setCurrentItem(Item item)
	{
		currentScanItem = item;
	}
	
	public Item getCurrentScanItem()
	{
		return currentScanItem;
	}

	
	public void setCurrentMember(Member member) {
		currentMember = member;
	}
	
	public Member getCurrentMember() {
		return currentMember;
	}
	
	
	/**
	 * @return HashMap<String, BigDecimal>
	 */
	public Map<String, BigDecimal> getCart() {
		return this.cart;
	}

	/**
	 * @return ArrayList<Double>
	 */
	public ArrayList<Double> getWeightOnScale() {
		return itemWeightOnScale;
	}

	/**
	 * @return BigDecimal
	 */
	public BigDecimal getTotalPrice() {
		// For each cart item, add the price to the total price
		BigDecimal totalPrice = new BigDecimal(0.0);
		for (Map.Entry<String, BigDecimal> item : cart.entrySet()) {
			totalPrice = totalPrice.add(item.getValue());
		}
		return totalPrice;
	}

	/**
	 * @return int
	 */
	public int getNumberOfItems() {
		// Get cart items
		return this.cart.size();
	}

	/**
	 * @return BigDecimal
	 */
	public BigDecimal getOwedAmount() {
		return amountOwed;
	}

	/**
	 * @param wish
	 */
	public void setCustomerWantsCheckout() {
		this.wishToCheckout = true;
		scs.mainScanner.disable();
		scs.handheldScanner.disable();
		scs.coinSlot.enable();
		scs.banknoteInput.enable();
		payment.update_owed();
		receiptLogic.printReceipt(getTotalPrice(),cart, currentMember);
	}
	
	public void setCustomerAddsMoreItems() {
		this.wishToCheckout = false;
		scs.mainScanner.enable();
		scs.handheldScanner.enable();
		scs.coinSlot.disable();
		scs.banknoteInput.disable();
	}

	/**
	 * @return boolean
	 */
	public boolean customerCheckout() {
		return wishToCheckout;
	}


	
	
	/**
	 * @param putInBagArea
	 */
	public void setPutItemInBag(boolean putInBagArea) {
		this.putItemInBag = putInBagArea;
	}

	/**
	 * @return boolean
	 */
	public boolean getPutItemInBag() {
		return putItemInBag;
	}
	
	/**
	 * @param setBagPlaced
	 */
	public void setBagPlaced(boolean bagPlaced) {
		this.bagPlaced = bagPlaced;
	}

	/**
	 * @param weight
	 */
	public void removeFromScale(double weight) {
		Iterator<Double> itr = itemWeightOnScale.iterator();
		while (itr.hasNext()) {
			double data = (Double) itr.next();
			if (data == weight)
				itr.remove();
		}
	}

	/**
	 * @return SelfCheckoutStation
	 */
	public SelfCheckoutStation getSCS() {
		return scs;
	}
	
	public PaymentLogic getPaymentSystem() {
		return payment;
	}
	
	public CardReader getCardReader() {
		return cardReader;
	}
}
