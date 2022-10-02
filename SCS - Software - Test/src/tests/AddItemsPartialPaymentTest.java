package tests;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ca.seng300.software.UseCase;
import ca.seng300.software.PaymentLogic;
import org.lsmr.selfcheckout.products.BarcodedProduct;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.Banknote;

import java.math.BigDecimal;

public class AddItemsPartialPaymentTest {
	
	private UseCase system;
	private PaymentLogic payment;
	private BarcodedProduct testProduct1;
	private BarcodedProduct testProduct2;
	private BarcodedProduct testProduct3;

	@Before
	public void setUp() throws Exception {
		system = new UseCase();
		payment = system.getPaymentSystem();
		Numeral[] n1 = new Numeral[]{Numeral.valueOf((byte)1),Numeral.valueOf((byte)2),Numeral.valueOf((byte)3)};
		Barcode b1 = new Barcode(n1);
		
		//Create different products for testing
		testProduct1 = new BarcodedProduct(b1, "cereal", BigDecimal.valueOf(5.00), 20);
		testProduct2 = new BarcodedProduct(b1, "milk", BigDecimal.valueOf(6.00), 30);
		testProduct3 = new BarcodedProduct(b1, "shrimp", BigDecimal.valueOf(7.00), 2);
		
		//Set up printer to avoid errors
		system.getSCS().printer.addPaper(100);
		system.getSCS().printer.addInk(1000);
	}

	@Test
	public void addItemTest() {
		system.addToCartByUnit(testProduct1);
		Assert.assertEquals(1, system.getNumberOfItems());
		Assert.assertEquals(5, system.getOwedAmount().intValue());
	}

	@Test
	public void addItemAfterPartialPaymentTest() {
		//Customer adds 2 items
		system.addToCartByUnit(testProduct1);
		system.addToCartByUnit(testProduct2);
		Assert.assertEquals(2, system.getNumberOfItems());
		Assert.assertEquals(11, system.getOwedAmount().intValue());
		
		//Customer decides to check out
		system.setCustomerWantsCheckout();
		Assert.assertTrue(system.customerCheckout());
		payMoney(payment, 5);
		
		//Customer decides to want to add another item
		system.setCustomerAddsMoreItems();
		Assert.assertFalse(system.customerCheckout());
		system.addToCartByUnit(testProduct3);
		Assert.assertEquals(3, system.getNumberOfItems());
		Assert.assertEquals(13, system.getOwedAmount().intValue());
		
		//Customer decides to check out again
		system.setCustomerWantsCheckout();
		Assert.assertTrue(system.customerCheckout());
		payMoney(payment, 10);
		Assert.assertEquals(0, payment.getChangeAmount().intValue());
	}
	
	//Helper for paying money
	private void payMoney(PaymentLogic payment, int ammount) {
		Banknote fiveDollars = new Banknote(UseCase.CURRENCY, ammount);
		try {
			payment.pay(fiveDollars);
        } catch (Exception e) {
            fail("Customers should be able to pay with banknotes.");
        }
	}
}
