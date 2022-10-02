package tests;



import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;
import org.lsmr.selfcheckout.Banknote;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;

import ca.seng300.software.*;
import ca.seng300.software.stubs.*;

public class BanknotePaymentTest {
    // Scales
    int scaleMaximumWeight = 100;
    int scaleSensitivity = 1;

    // OBJECTS
    UseCase uc = new UseCase();
    SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(UseCase.CURRENCY, UseCase.BANKNOTE_DENOMINATIONS, UseCase.COIN_DENOMINATIONS,
            scaleMaximumWeight, scaleSensitivity);
    PaymentLogic payLogic = new PaymentLogic(selfCheckoutStation, this.uc);
    Banknote bankNote = new Banknote(UseCase.CURRENCY, 10);


    public void inputBanknote(PaymentLogic payment, Banknote banknote) throws DisabledException, OverloadException {
        BanknotePaymentStub stub = new BanknotePaymentStub();
        payment.getSCS().banknoteValidator.attach(stub);

        // Repeatedly trying to input the banknote, in case the validator thinks it's invalid
        while (!stub.isValid) {
            payment.pay(banknote);
        }
    }

    // Test the default bank note.
    @Test
    public void testBankNote() {
        try {
            payLogic.pay(bankNote);
        } catch (Exception e) {
            fail("Customers should be able to pay with banknotes.");
        }
    }

    // ------------------------------------------------------------------------------------------//

    // Test with an empty test node.
    @Test
    public void testBankNoteNull() {
        Banknote bankNote = null; // Set our object banknote to null for testing.
        // Initialize our object for bank note payment.
        try {
            payLogic.pay(bankNote);
        } catch (SimulationException e) {
            assertTrue("Banknote is not empty.", true);
        } catch (Exception e) {
            fail("Customers should be able to pay with banknotes.");
        }
    }

    // ------------------------------------------------------------------------------------------//

    // Test with a zero valued banknote
    @Test
    public void testZeroValue() {
        try {
            Banknote bankNote = new Banknote(UseCase.CURRENCY, 0);
            payLogic.pay(bankNote);
        } catch (SimulationException e) {
            assertTrue("banknote value is not zero.", true);
        } catch (Exception e) {
            fail("Customers should be able to pay with banknotes.");
        }
    }

    // ------------------------------------------------------------------------------------------//

    // Test total paid
    @Test
    public void testPaidTotal() throws DisabledException, OverloadException {
    	payLogic.getUseCase().setTotalAmountOwed(BigDecimal.TEN);
        Banknote bankNote = new Banknote(UseCase.CURRENCY, 5);
        payLogic.pay(bankNote);
        //assertEquals("The amount price is different from actual price.", new BigDecimal(5), payLogic.getPaid());
    }

    // ------------------------------------------------------------------------------------------//

    // Test whether there is a need to dispense change
    @Test
    public void testChangeAmountPaidNotEnough() {
        SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(UseCase.CURRENCY,
                UseCase.BANKNOTE_DENOMINATIONS, UseCase.COIN_DENOMINATIONS,
                scaleMaximumWeight, scaleSensitivity);
        PaymentLogic banknotePay = new PaymentLogic(selfCheckoutStation, this.uc);
        uc.setTotalAmountOwed(BigDecimal.valueOf(100));
        banknotePay.update_owed();
        Banknote bankNote = new Banknote(UseCase.CURRENCY, 5);
        
        try {
            payLogic.pay(bankNote);
        } catch (Exception e) {
            fail("Customers should be able to pay with banknotes.");
        }

        assertEquals("The amount of change for customer should be 0.", BigDecimal.ZERO, banknotePay.getChangeAmount());
    }

    // ------------------------------------------------------------------------------------------//

    // Tests if the banknote payment is taking the right total amount
    @Test
    public void testTotalAmount() {
        SelfCheckoutStation selfCheckoutStation = new SelfCheckoutStation(UseCase.CURRENCY,
                UseCase.BANKNOTE_DENOMINATIONS, UseCase.COIN_DENOMINATIONS,
                scaleMaximumWeight, scaleSensitivity);
        PaymentLogic banknotePayment = new PaymentLogic(selfCheckoutStation, this.uc);
        uc.setTotalAmountOwed(BigDecimal.TEN);
        banknotePayment.update_owed();
        assertEquals("Total amount different from actual amount.", new BigDecimal(10), banknotePayment.getTotal());
    }

    
    // ------------------------------------------------------------------------------------------//

} // End of class
