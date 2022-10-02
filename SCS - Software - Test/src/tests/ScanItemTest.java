package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import ca.seng300.software.ScanItem;

public class ScanItemTest {
	ScanItem itemToScan;
	BarcodedItem cheetos;
	BarcodedItem juice;
	double weight;
	BarcodedProduct barcodedCheetos;
	BarcodedProduct barcodedJuice;
	double weightPLUItem;
	Barcode barcodeCheetos;
	Barcode barcodeJuice;
	Barcode barcodeApple;
	BarcodedItem apple;
	BarcodedProduct barcodedApple;
	BigDecimal price;
	Numeral[] digits;

	@Before
	public void setUp()
	{
		itemToScan = new ScanItem();
		digits = new Numeral[3];
		digits[0] = Numeral.one;
		digits[1] = Numeral.two;
		digits[2] = Numeral.three;
		barcodeCheetos = new Barcode(digits);
		weight = 550;
		price = new BigDecimal(5.999);
		
		cheetos = new BarcodedItem(barcodeCheetos, weight);
		barcodedCheetos = new BarcodedProduct(barcodeCheetos, "cheetos", price, 300);
		itemToScan.productDatabase.addProduct(barcodedCheetos);
		itemToScan.databaseBarcodedItem.put(barcodeCheetos, cheetos);
		
		digits = new Numeral[3];
		digits[0] = Numeral.three;
		digits[1] = Numeral.four;
		digits[2] = Numeral.five;
		barcodeJuice = new Barcode(digits);
		weight = 1000;
		price = new BigDecimal(2.5);
		
		juice = new BarcodedItem(barcodeJuice, weight);
		barcodedJuice = new BarcodedProduct(barcodeJuice, "Juice", price, 750);
		itemToScan.productDatabase.addProduct(barcodedJuice);
		itemToScan.databaseBarcodedItem.put(barcodeJuice, juice);
		
		digits = new Numeral[3];
		digits[0] = Numeral.five;
		digits[1] = Numeral.six;
		digits[2] = Numeral.seven;
		barcodeApple = new Barcode(digits);
		weight = 2000;
		price = new BigDecimal(3.0);
		
		apple = new BarcodedItem(barcodeApple, weight);
		barcodedApple = new BarcodedProduct(barcodeApple, "Apple", price, 80);
		itemToScan.productDatabase.addProduct(barcodedApple);
		itemToScan.databaseBarcodedItem.put(barcodeApple, apple);
	}
	
	@Test
	public void priceNumberOfItemsTest()
	{
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.barCodedItemWithWeight(barcodeApple);
		itemToScan.setStubFalseAfterScan();
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeJuice);
		itemToScan.setStubFalseAfterScan();
		BigDecimal expectedPrice = barcodedApple.getPrice().multiply(new BigDecimal(apple.getWeight()).divide(new BigDecimal(1000))).add(barcodedJuice.getPrice());
		
		int totalItems = itemToScan.getNumberOfItems();
	}
	
	@Test
	public void weightItemInCart()
	{
        boolean isItemInCart = false;
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.barCodedItemWithWeight(barcodeApple);
		itemToScan.setStubFalseAfterScan();
        Iterator<Entry<String, BigDecimal>> iterator = itemToScan.getCart().entrySet().iterator();

        while (iterator.hasNext()) {

        // Get the entry at this iteration
        	Entry<String, BigDecimal> entry = iterator.next();

        // Check if this key is the required key
        	if ("Apple" == entry.getKey()) {
        		isItemInCart = true;
        	}
        }
        
       
	}
	
	@Test
	public void scanTest()
	{	
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeCheetos);
		itemToScan.setStubFalseAfterScan();
	}
	
	@Test
	public void priceTest() {
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeCheetos);
		itemToScan.setStubFalseAfterScan();
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeJuice);
		itemToScan.setStubFalseAfterScan();
		BigDecimal expectedCheetosPrice = barcodedCheetos.getPrice();
		BigDecimal expectedJuicePrice = barcodedJuice.getPrice();
		
		BigDecimal expectedTotalPrice = expectedCheetosPrice.add(expectedJuicePrice);
	
	}
	
	@Test
	public void itemInCart()
	{
        boolean isItemInCart = false;
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeCheetos);
		itemToScan.setStubFalseAfterScan();
        Iterator<Entry<String, BigDecimal>> iterator = itemToScan.getCart().entrySet().iterator();

        while (iterator.hasNext()) {

        // Get the entry at this iteration
        	Entry<String, BigDecimal> entry = iterator.next();

        // Check if this key is the required key
        	if ("cheetos" == entry.getKey()) {
        		isItemInCart = true;
        	}
        }
        
        
	}
	
	@Test
	public void totalAmountOwed()
	{
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeCheetos);
		itemToScan.setStubFalseAfterScan();
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeJuice);
		itemToScan.setStubFalseAfterScan();
		BigDecimal expectedCheetosPrice = barcodedCheetos.getPrice();
		BigDecimal expectedJuicePrice = barcodedJuice.getPrice();
		
		BigDecimal totalAmountOwed = expectedCheetosPrice.add(expectedJuicePrice);

	
	}
	
	@Test
	public void totalItemTest()
	{
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeCheetos);
		itemToScan.setStubFalseAfterScan();
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeJuice);
		itemToScan.setStubFalseAfterScan();
	}
	
	@Test
	public void scanWithDisableTest()
	{
		itemToScan.getSCS().mainScanner.disable();
		try {
			itemToScan.itemScanned(barcodeJuice);
			itemToScan.setStubFalseAfterScan();
		}
		catch (SimulationException e)
		{
			assertTrue("expected simulationException to be thrown", e instanceof SimulationException);
		}
		catch(Exception e) {
			fail("Expected simulationExpection to be thrown, instead " + e + " was thrown");
		}
	}
	
	@Test
	public void disableAfterScanTest()
	{
		itemToScan.getSCS().mainScanner.enable();
		itemToScan.itemScanned(barcodeJuice);
		itemToScan.setStubFalseAfterScan();
		
	}
}
