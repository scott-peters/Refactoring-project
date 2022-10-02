package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.ElectronicScale;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import ca.seng300.software.scaleSoftware;

public class ScaleSoftwareTest {

	private SelfCheckoutStation station;
	private ElectronicScale scale;
	private scaleSoftware software;
	private BarcodedProduct testProduct;
	private BarcodedItem testItem;
	private BarcodedItem overloadItem;
	private BarcodedItem heavyItem;
	private BarcodedItem lightItem;
	
	//constructor
	public ScaleSoftwareTest() {
		Currency curr = Currency.getInstance(Locale.CANADA);
		int[] bankNoteDenom = {5, 10, 20, 50, 100};
		BigDecimal[] coinDenom = {BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.25), BigDecimal.valueOf(1.00), BigDecimal.valueOf(2.00)};
		int maxWeight = 10000;
		int sensitivity = 10;

		this.station = new SelfCheckoutStation( curr, bankNoteDenom, coinDenom, maxWeight, sensitivity);
		this.scale = station.baggingArea;
		this.software = new scaleSoftware(scale);

		Numeral[] n1 = new Numeral[]{Numeral.valueOf((byte)1),Numeral.valueOf((byte)2),Numeral.valueOf((byte)3)};
		Barcode b1 = new Barcode(n1);
		BigDecimal price1 = new BigDecimal("6.95");
		testProduct = new BarcodedProduct(b1, "cereal", price1, 300);
		testItem = new BarcodedItem(b1, 534);
		overloadItem = new BarcodedItem(b1, 1000000);
		heavyItem = new BarcodedItem(b1, 800);
		lightItem = new BarcodedItem(b1, 200);
		
	}
	
	@Test
	public void testScaleEnabled() {
		scale.enable();
		assertTrue(software.getScaleEnabled());
	}
	
	@Test
	public void testScaleDisabled() {
		scale.disable();
		assertFalse(software.getScaleEnabled());
	}
	
	@Test
	public void testScaleOverload() {
		scale.enable();
		scale.add(overloadItem);
		scale.remove(overloadItem);
	}
	
	@Test
	public void testWeightChangeCorrect() {
		scale.enable();
		scale.add(testItem);
		boolean testBool = software.itemBaggedCorrectly(testItem, testItem.getWeight(), scale.getSensitivity());
		assertTrue(testBool);
		scale.remove(testItem);
	}
	
	@Test(expected = SimulationException.class)
	public void testWeightChangeTooLittle() {
		scale.enable();
		boolean testBool = software.itemBaggedCorrectly(testItem, lightItem.getWeight(), scale.getSensitivity());
		assertFalse(testBool);
		scale.remove(lightItem);
	}
	
	@Test(expected = SimulationException.class)
	public void testWeightChangeTooMuch() {
		scale.enable();
		scale.add(heavyItem);
		boolean testBool = software.itemBaggedCorrectly(testItem, heavyItem.getWeight(), scale.getSensitivity());
		assertFalse(testBool);
		scale.remove(heavyItem);
	}

}
