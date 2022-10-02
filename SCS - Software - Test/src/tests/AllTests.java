package tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ AddItemsPartialPaymentTest.class, 
				BagItemTest.class,
				BanknotePaymentTest.class,
				CardPaymentTest.class,
				CoinPaymentTest.class,
				CustomerWishToCheckoutTest.class,
				ReceiveChangeTest.class,
				ScaleSoftwareTest.class,
				ScanItemTest.class 
				})

public class AllTests {

}
