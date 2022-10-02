package tests;

import ca.seng300.software.ReceiveChange;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.BarcodedItem;
import org.lsmr.selfcheckout.Card;
import org.lsmr.selfcheckout.Coin;
import org.lsmr.selfcheckout.Numeral;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.products.BarcodedProduct;

import ca.seng300.software.UseCase;
import ca.seng300.software.stubs.BankAccountStub;
import ca.seng300.software.stubs.BankStub;

public class ReceiveChangeTest {
	private UseCase useCase; 
	
	private ReceiveChange receiveChange;
	
	BigDecimal nickel = UseCase.COIN_DENOMINATIONS[0];
	BigDecimal dime = UseCase.COIN_DENOMINATIONS[1];
	
	int fiveDollars = UseCase.BANKNOTE_DENOMINATIONS[0];
	Banknote fiveDollarsNote;
	
	// for this test case, just make sure that we have the self checkout station all 
	// loaded properly
	@Before
	public void setup() {
		useCase = new UseCase();
		receiveChange = new ReceiveChange(useCase.getSCS());
		
		fiveDollarsNote = new Banknote(UseCase.CURRENCY, fiveDollars);
	}


	// Certainly, we should test if there are coins (and bank notes) this means that 
	// we should throw the empty exception.
	@Test(expected = EmptyException.class)
	public void testEmptyException() throws OverloadException, EmptyException, DisabledException {
		receiveChange.returnChange(new BigDecimal(10));
	}
	
	// This tests if normal usage of returning coins does not throw an exception, and we return the right amount of coins.
	@Test
	public void testNoExceptionCoin() throws OverloadException, EmptyException, DisabledException {
				
		useCase.getSCS().coinSlot.accept(new Coin(nickel));
		receiveChange.returnChange(  nickel   );
		List<Coin> lst = useCase.getSCS().coinTray.collectCoins();
	

		assertTrue(lst.get(0).getValue().subtract(nickel).abs().compareTo(new BigDecimal("0.0001")) <= 0);
		
	}
	
	
	// slightly more complicated version of the above test
	@Test
	public void testNoExceptionCoin2() throws OverloadException, EmptyException, DisabledException {
				
		useCase.getSCS().coinSlot.accept(new Coin(nickel));
		useCase.getSCS().coinSlot.accept(new Coin(dime));

		
		receiveChange.returnChange(  nickel.add(dime)  );
		List<Coin> lst = useCase.getSCS().coinTray.collectCoins();		
		
		BigDecimal acc = new BigDecimal("0");
		for (Coin c : lst) {
			if (c != null)
				acc = acc.add(c.getValue());
		}
		
		assertTrue(acc.subtract(nickel.add(dime)).abs().compareTo(new BigDecimal("0.0001")) <= 0);

	}
	
	// tests if normal usage of a bank note doesn't throw any exceptions, and if we return the amount of money that we put in
	@Test
	public void testNoExceptionBanknote() throws OverloadException, EmptyException, DisabledException {

		useCase.getSCS().banknoteDispensers.get(5).load( fiveDollarsNote, fiveDollarsNote);
		
		receiveChange.returnChange(  new BigDecimal(fiveDollars ) );
		
		assertTrue(useCase.getSCS().banknoteOutput.removeDanglingBanknote().getValue() == this.fiveDollars);

	}

	
	
	

}
