package ca.seng300.software;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.ReceiptPrinter;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.ReceiptPrinterObserver;

public class ReceiptLogic implements ReceiptPrinterObserver{

	
	private ReceiptPrinter printer;
	
	
	private boolean hasPaper = false;
	private boolean hasInk = false;
	private boolean isEnabled = false;
	private boolean printWaiting = false;
	
	private Map<String, BigDecimal> cart;
	private BigDecimal currentPrice;
	private Member currentMember;
	
	
	//Constructor for ReceiptLogic
	public ReceiptLogic(ReceiptPrinter printer) {
		this.printer = printer;
		
	}
	
	
	// Initiate printing and set values, and begin printing process (if printer is ready)
	public void printReceipt(BigDecimal currentPrice,Map<String, BigDecimal> cart, Member currentMember) {
		this.currentMember = currentMember;
		this.cart = cart;
		this.currentPrice = currentPrice;
		this.printWaiting = true;
		
		proceedToPrint();
	}
	
	//Print receipt
	private void proceedToPrint() {
		if(okToPrint()) {
			String receiptString = "Receipt for Purchase";
			
			if(this.currentMember != null) {
				receiptString+= "Member: "+currentMember.getName();
				receiptString+= "ID: "+currentMember.getID();
			}
			
			for(Map.Entry<String, BigDecimal> entry :cart.entrySet()) {
				receiptString+= "Item: " + entry.getKey() +"\n Price: "+entry.getValue();
			}
				
			
			receiptString+= "\nAmount Paid For:\n" + this.currentPrice;
			
			
			char[] charsToPrint = receiptString.toCharArray();
			
			
			for(char currentChar: charsToPrint) {
				this.printer.print(currentChar);
			}
			
			
			this.printer.cutPaper();
			
			}
			
			
		
	}
	
	//Check if the printer is ready 
	private boolean okToPrint() {
		if(hasPaper && hasInk && isEnabled && printWaiting) {
			return true;
		}
		return false;
	}
	
	
	//Notify if enabled
	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		isEnabled = true;
		proceedToPrint();
	}

	//Notify if disabled
	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		isEnabled = false;
	}

	
	//Notify if out of paper
	@Override
	public void outOfPaper(ReceiptPrinter printer) {
		hasPaper = false;
	}

	//Notify if out of ink
	@Override
	public void outOfInk(ReceiptPrinter printer) {
		hasInk = false;
	}

	//Notify if paper added
	@Override
	public void paperAdded(ReceiptPrinter printer) {
		hasPaper = true;
		proceedToPrint();
	}

	
	//Notify if ink added
	@Override
	public void inkAdded(ReceiptPrinter printer) {
		hasInk = true;
		proceedToPrint();
	}
	

}
