package ca.seng300.software;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import ca.seng300.software.stubs.ScanItemStub;



public class ScanItem extends UseCase {
	private SelfCheckoutStation scs = getSCS();
	private ScanItemStub stub = new ScanItemStub();
	
	public ScanItem() {
		scs.mainScanner.attach(stub);
	}
	
	public void itemScanned(Barcode barcode)
	{		
		if(memberDatabase.lookupMemberByBarcode(barcode) == null) {
		setPutItemInBag(false);
		if (scs.mainScanner.isDisabled() || getPutItemInBag())
			throw new SimulationException(
					new IllegalStateException("This method may not be called during machine is disable."));
		else
		{
			if (productDatabase.lookUpBarcode(barcode) != null) {
				BarcodedProduct barcodedProduct = productDatabase.lookUpBarcode(barcode);
				BarcodedItem item = databaseBarcodedItem.get(barcode);
				addToCartByUnit(barcodedProduct);
				updateOwedAmount(barcodedProduct.getPrice());
				while (!stub.itemScanned)
					scs.mainScanner.scan(item);
				setCurrentItem(item);
				scs.mainScanner.disable();
			}
		}
		}
		else {
			membershipScanned(barcode);
		}
	}
	
	public void barCodedItemWithWeight(Barcode barcode)
	{
		if(memberDatabase.lookupMemberByBarcode(barcode) == null) {
		setPutItemInBag(false);
		if (scs.mainScanner.isDisabled() || getPutItemInBag())
			throw new SimulationException(
					new IllegalStateException("This method may not be called during machine is disable"));
		else
		{
			if (productDatabase.lookUpBarcode(barcode) != null) {
				BarcodedProduct barcodedProduct = productDatabase.lookUpBarcode(barcode);
				BarcodedItem barcodedItem = databaseBarcodedItem.get(barcode);
				double itemWeight = barcodedItem.getWeight()/1000.0;
				BigDecimal result = new BigDecimal(itemWeight).multiply(barcodedProduct.getPrice());
				addToCartByWeight(barcodedProduct.getDescription(), result);
				updateOwedAmount(result);
				while (!stub.itemScanned)
					scs.mainScanner.scan(barcodedItem);
				setCurrentItem(barcodedItem);
				scs.mainScanner.disable();
			}
		}
		}
		else {
			membershipScanned(barcode);
		}
	}
	
	private void membershipScanned(Barcode memberScanned) {
		this.setCurrentMember(memberDatabase.lookupMemberByBarcode(memberScanned));
	}
	
	public void setStubFalseAfterScan() {
		stub.itemScanned = false;
	}
	
	public ScanItemStub getStub() {
		return stub;
	}
}
