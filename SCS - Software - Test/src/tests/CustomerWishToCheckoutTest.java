package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ca.seng300.software.UseCase;

public class CustomerWishToCheckoutTest extends UseCase {

	@Test
	public void customerWishToCheckOutTest() {
		setCustomerWantsCheckout();
		assertTrue("Customer checkout does not worked", customerCheckout());
	}

	@Test
	public void scannerDisableTest() {
		setCustomerWantsCheckout();
		assertTrue("Scanner is not disabled.", getSCS().mainScanner.isDisabled());
	}
}
