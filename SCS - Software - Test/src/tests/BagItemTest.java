package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.PLUCodedItem;
import org.lsmr.selfcheckout.PriceLookupCode;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import ca.seng300.software.*;
import ca.seng300.software.stubs.BagItemStub;

public class BagItemTest {
    BagItem bagitem;
    ScanItem itemToScan;
    BarcodedItem cheetos;
    BarcodedItem juice;
    double weight;
    BarcodedProduct barcodedCheetos;
    BarcodedProduct barcodedJuice;
    double weightPLUItem;
    PLUCodedItem pluCodedapple;
    Barcode barcodeCheetos;
    Barcode barcodeJuice;
    Barcode barcodeApple;
    BarcodedItem apple;
    BarcodedProduct barcodedApple;
    BigDecimal price;
    PriceLookupCode priceLookupCode;
    Numeral[] digits;
    UseCase uc = new UseCase();

    @Before
    public void SetUp() {
        bagitem = new BagItem();
        itemToScan = new ScanItem();
        digits = new Numeral[3];
        digits[0] = Numeral.one;
        digits[1] = Numeral.two;
        digits[2] = Numeral.three;
        barcodeCheetos = new Barcode(digits);
        weight = 550;
        price = new BigDecimal(5.999);

        cheetos = new BarcodedItem(barcodeCheetos, weight);
        barcodedCheetos = new BarcodedProduct(barcodeCheetos, "cheetos", price, weight);
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
        barcodedJuice = new BarcodedProduct(barcodeJuice, "Juice", price, weight);
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
        barcodedApple = new BarcodedProduct(barcodeApple, "Apple", price, weight);
        itemToScan.productDatabase.addProduct(barcodedApple);
        itemToScan.databaseBarcodedItem.put(barcodeApple, apple);
    }

    @Test
    public void bagItemTest() throws OverloadException {
        BagItemStub stub = new BagItemStub();
        bagitem.getSCS().baggingArea.attach(stub);
        bagitem.getSCS().baggingArea.endConfigurationPhase();
        itemToScan.getSCS().mainScanner.endConfigurationPhase();
        itemToScan.itemScanned(barcodeJuice);
        itemToScan.setStubFalseAfterScan();
        
        //uc.setPutItemInBag(true);
        //uc.setCurrentItem(juice);
        
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(juice);
        bagitem.bagItem(juice);
        itemToScan.getSCS().mainScanner.enable();
        itemToScan.itemScanned(barcodeCheetos);
        itemToScan.setStubFalseAfterScan();
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(cheetos);
        bagitem.bagItem(cheetos);
        assertEquals(juice.getWeight() + cheetos.getWeight(), bagitem.getSCS().baggingArea.getCurrentWeight(), 1.0);
    }

    @Test
    public void bagItemOverload() throws OverloadException {
        BagItemStub stub = new BagItemStub();
        bagitem.getSCS().baggingArea.attach(stub);
        bagitem.getSCS().baggingArea.endConfigurationPhase();
        itemToScan.getSCS().mainScanner.endConfigurationPhase();
        itemToScan.itemScanned(barcodeApple);
        itemToScan.setStubFalseAfterScan();
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(apple);
        bagitem.bagItem(apple);
        itemToScan.getSCS().mainScanner.enable();
        itemToScan.barCodedItemWithWeight(barcodeApple);
        itemToScan.setStubFalseAfterScan();
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(juice);
        bagitem.bagItem(juice);
        assertTrue("Not in overload state", stub.overload);

        // System should not be in an Overloaded state.
        bagitem.removeBagItem(apple);
    }

    @Test
    public void removeBagItemTest() throws OverloadException {
        BagItemStub stub = new BagItemStub();
        bagitem.getSCS().baggingArea.attach(stub);
        bagitem.getSCS().baggingArea.endConfigurationPhase();
        itemToScan.getSCS().mainScanner.endConfigurationPhase();
        itemToScan.itemScanned(barcodeCheetos);
        itemToScan.setStubFalseAfterScan();
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(cheetos);
        bagitem.bagItem(cheetos);
        itemToScan.getSCS().mainScanner.enable();
        itemToScan.itemScanned(barcodeCheetos);
        itemToScan.setStubFalseAfterScan();
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(juice);
        bagitem.bagItem(juice);

        bagitem.removeBagItem(cheetos);

        assertEquals(juice.getWeight(), bagitem.getSCS().baggingArea.getCurrentWeight(), 1.0);
    }

    @Test
    public void removeItemNotInBagTest() throws OverloadException {
        BagItemStub stub = new BagItemStub();
        bagitem.getSCS().baggingArea.attach(stub);
        bagitem.getSCS().baggingArea.endConfigurationPhase();

        try {
            bagitem.removeBagItem(cheetos);
        } catch (Exception e) {
            assertTrue("SimulationException expected which has not been thrown", e instanceof SimulationException);
        }
    }

    @Test
    public void outOfOverloadTest() throws OverloadException {
        BagItemStub stub = new BagItemStub();
        bagitem.getSCS().baggingArea.attach(stub);
        bagitem.getSCS().baggingArea.endConfigurationPhase();
        itemToScan.getSCS().mainScanner.endConfigurationPhase();
        itemToScan.itemScanned(barcodeJuice);
        itemToScan.setStubFalseAfterScan();
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(juice);
        bagitem.bagItem(juice);
        itemToScan.getSCS().mainScanner.enable();
        itemToScan.barCodedItemWithWeight(barcodeApple);
        itemToScan.setStubFalseAfterScan();
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(apple);
        bagitem.bagItem(apple);
        assertTrue("Not in overload state", stub.overload);
        bagitem.removeBagItem(apple);
        assertTrue("Not out of overload state.", stub.outOfOverload);
    }

    @Test
    public void notifyWeightChangeTest() throws OverloadException {
        BagItemStub stub = new BagItemStub();
        bagitem.getSCS().baggingArea.attach(stub);
        itemToScan.getSCS().mainScanner.endConfigurationPhase();
        itemToScan.itemScanned(barcodeJuice);
        itemToScan.setStubFalseAfterScan();
        bagitem.setCustomerAddsMoreItems();
        bagitem.setCurrentItem(juice);
        bagitem.bagItem(juice);
        assertTrue("Weight has not been changed yet.", stub.weightChanged);

        stub.weightChanged = false;

        bagitem.removeBagItem(juice);
        assertTrue("Weight has not been changed yet.", stub.weightChanged);
    }

    @Test
    public void putItemWithoutScan() throws OverloadException {
        BagItemStub stub = new BagItemStub();
        bagitem.getSCS().baggingArea.attach(stub);
        bagitem.getSCS().baggingArea.endConfigurationPhase();
        itemToScan.getSCS().mainScanner.endConfigurationPhase();
        try {
        	
            bagitem.bagItem(juice);
            fail("Expected SimulationException to be thrown, no ExceptionThrown");
        } catch (SimulationException e) {
            assertTrue("expected simulationException to be thrown", e instanceof SimulationException);
        } catch (Exception e) {
            fail("Expected simulationExpection to be thrown, instead " + e + " was thrown");
        }
    }
}
