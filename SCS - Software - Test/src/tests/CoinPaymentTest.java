package tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

import ca.seng300.software.*;
import ca.seng300.software.stubs.BanknotePaymentStub;
import ca.seng300.software.stubs.CoinPaymentStub;

public class CoinPaymentTest {
	
	int scaleMaximumWeight = 100;
    int scaleSensitivity = 1;
	
	UseCase uc = new UseCase();
    SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(UseCase.CURRENCY, UseCase.BANKNOTE_DENOMINATIONS, UseCase.COIN_DENOMINATIONS,
            scaleMaximumWeight, scaleSensitivity);
    PaymentLogic payLogic = new PaymentLogic(selfCheckoutStation, this.uc);
	private Coin coin = new Coin(new BigDecimal(1));

	/*private static CoinPayment coinPayment = new CoinPayment(
			new SelfCheckoutStation(UseCase.CURRENCY, 
					UseCase.BANKNOTE_DENOMINATIONS, 
					UseCase.COIN_DENOMINATIONS, 2000, 50),
			new BigDecimal(10.0));
*/

	public void insertCoin(PaymentLogic payment, Coin coin) throws DisabledException, OverloadException {
		CoinPaymentStub stub = new CoinPaymentStub();
        payment.getSCS().coinValidator.attach(stub);

        // Repeatedly trying to input the coin, in case the validator thinks it's invalid
        while (!stub.isValid) {
            payment.pay(coin);
        }
    }

	@Test
	public void testCoinPayment() {
		 try {
	            payLogic.pay(coin);
	        } catch (Exception e) {
	            fail("Customers should be able to pay with coins.");
	        }
	}

	/**
	 * @throws DisabledException
	 * @throws OverloadException 
	 */
	@Test
	public void testInsertCoinCoin() throws DisabledException, OverloadException {
		payLogic.getUseCase().setTotalAmountOwed(BigDecimal.TEN);
		Coin ci = new Coin(BigDecimal.TEN);
		payLogic.pay(ci);
		assertEquals(payLogic.getTotal(), BigDecimal.ZERO);
	}
	
	/**
	 * @throws DisabledException
	 */
	@Test
	public void testInsertCoinCoinArray() throws DisabledException {
		payLogic.getUseCase().setTotalAmountOwed(BigDecimal.TEN);
		Coin[] coins = new Coin[1];
		coins[0] = new Coin(new BigDecimal("0.1"));
		payLogic.pay(coins);
		
		assertTrue("coinPayment insertCoin does not worked", payLogic.getTotal().equals(new BigDecimal("9.9")) );
	}

	/**
	 * @throws DisabledException
	 */
	@Test
	public void testInsertCoinListOfCoin() throws DisabledException {
		payLogic.getUseCase().setTotalAmountOwed(BigDecimal.TEN);
		List<Coin> coins = new ArrayList<Coin>();
		coins.add(new Coin(new BigDecimal("0.5")));
		coins.add(new Coin(new BigDecimal("0.3")));
		coins.add(new Coin(new BigDecimal("0.2")));
		coins.add(new Coin(new BigDecimal("1.0")));
		coins.add(new Coin(new BigDecimal("0.8")));
		payLogic.pay(coins);

		/*PaymentLogic payment = new PaymentLogic(
				new SelfCheckoutStation(UseCase.CURRENCY,
						UseCase.BANKNOTE_DENOMINATIONS,
						UseCase.COIN_DENOMINATIONS, 2000, 50),
				new BigDecimal(10.0));*/

		//payLogic.pay(coins);
		assertTrue("coinPayment insertCoin does not worked", payLogic.getTotal().equals(new BigDecimal("7.2")));
		//assertEquals("Customer should have paid $0.5", new BigDecimal(0.5), payLogic.getPaid());
		//assertEquals("Customer should be returned $0.0", new BigDecimal(0.0), payLogic.returnChange());
	}
	
	 // Test with an empty test node.
    @Test
    public void testcoinNull() {
        Coin coin = null; //Just testing a null coin.
       
        PaymentLogic payLogic = new PaymentLogic(selfCheckoutStation, this.uc);

        try {
            insertCoin(payLogic, coin);
        } catch (SimulationException e) {
            assertTrue("Banknote is not empty.", true);
        } catch (Exception e) {
            fail("Customers should be able to pay with coins.");
        }
    }
    
    // Test with a coin that is equivalent to 0 dollars to see what happens
    @Test
    public void testZeroValue() {
        PaymentLogic payLog = new PaymentLogic(selfCheckoutStation, this.uc);
        try {
            Coin coin = new Coin(new BigDecimal(0));
            insertCoin(payLog, coin);
        } catch (SimulationException e) {
            assertTrue("coin value is not zero.", true);
        } catch (Exception e) {
            fail("Customers should be able to pay with coins.");
        }
    }

	@Test
	public void testGetChangeAmount() {
		SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(UseCase.CURRENCY,
                UseCase.BANKNOTE_DENOMINATIONS, UseCase.COIN_DENOMINATIONS,
                scaleMaximumWeight, scaleSensitivity);
        PaymentLogic coinPay = new PaymentLogic(selfCheckoutStation, this.uc);
        uc.setTotalAmountOwed(BigDecimal.valueOf(100));
        coinPay.update_owed();
        Coin coin = new Coin(new BigDecimal(5));
        
        try {
            payLogic.pay(coin);
        } catch (Exception e) {
            fail("Customers should be able to pay with Coins.");
        }

        assertEquals("The amount of change for customer should be 0.", BigDecimal.ZERO, coinPay.getChangeAmount());
	}

	@Test
	public void testGetPaid() {
		payLogic.getPaid();
		assertTrue("coinPayment insertCoin does not worked", payLogic.getPaid().intValue() <= 10.0);
	}

	@Test
	public void testGetTotal() throws DisabledException {
		SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(UseCase.CURRENCY,
                UseCase.BANKNOTE_DENOMINATIONS, UseCase.COIN_DENOMINATIONS,
                scaleMaximumWeight, scaleSensitivity);
        PaymentLogic coinPayment = new PaymentLogic(selfCheckoutStation, this.uc);
        uc.setTotalAmountOwed(BigDecimal.TEN);
        coinPayment.update_owed();
		assertEquals("Total amount different from actual amount.", new BigDecimal(10), coinPayment.getTotal());
	}


}
