package tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Card;

import ca.seng300.software.*;
import ca.seng300.software.stubs.BankAccountStub;
import ca.seng300.software.stubs.BankStub;

public class CardPaymentTest {
	private UseCase uc = new UseCase();
	private PaymentLogic payLogic = new PaymentLogic(uc.getSCS(), this.uc);
	private Card debitCard = new Card("debit", "123456", "me", "123", "1234", true, true);
	private Card creditCard = new Card("credit", "567890", "me", "321", "4321", true, true);
	private BankStub bankStub = new BankStub();
	private BankAccountStub bankAccount = new BankAccountStub("me", debitCard, creditCard, BigDecimal.valueOf(1000), BigDecimal.valueOf(100), BigDecimal.valueOf(1000));
	
	
	@Before
	public void setup() {
		bankStub.addAccount(bankAccount);
		payLogic.pay_card(BigDecimal.valueOf(100));
	}
	
	@Test
	public void testEnabled() {
		payLogic.getSCS().cardReader.enable();
		assertFalse(payLogic.getSCS().cardReader.isDisabled());
		
	}
	
	@Test
	public void testDisabled() {
		payLogic.getSCS().cardReader.disable();
		assertTrue(payLogic.getSCS().cardReader.isDisabled());
	}
	
	@Test
	public void testInserted() throws IOException {
		payLogic.getSCS().cardReader.insert(debitCard, "1234");
	}

	@Test
	public void testRemoved() {
		payLogic.getSCS().cardReader.remove();
	}
	
	@Test
	public void testTapped() throws IOException {
		payLogic.getSCS().cardReader.tap(debitCard);
	}
	
	@Test
	public void testSwiped() throws IOException {
		payLogic.getSCS().cardReader.swipe(debitCard);
	}
}
