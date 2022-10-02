package ca.seng300.software;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteSlotObserver;

// This implements the receive change use case. 

public class ReceiveChange implements BanknoteSlotObserver {
	private SelfCheckoutStation scs;
	
	// these should be in descending order
	private BigDecimal[] coinDenominations;
	private int[] banknoteDenominations;
	
	// internal member variable for returning the remaining price.
	private BigDecimal remainingPrice;
	
	// internal member variable for the number of coinDenominations and bankNoteDenominations to return.
	private int[] numBankNotes;
	private int[] numCoins;
	
	// index in the numBankNotes array which maintains the invariant that we have returned all the banknotes to the left of this.
	// Why is this so complicated?
	// well, we can only return more bank notes to the user if the current dangling banknote has been removed.. see the discussion
	// https://d2l.ucalgary.ca/d2l/le/422926/discussions/threads/1631159/View here by Prof Robert Walker.
	private int curBanknoteDenom;

	// Precondition:
	// the self checkout station should have denominations in descending order for which the denominations have at most precision in the 100s.
	public ReceiveChange(SelfCheckoutStation inputscs) {
		scs = inputscs;
		
		banknoteDenominations = scs.banknoteDenominations;
		coinDenominations = scs.coinDenominations.toArray(new BigDecimal[0]);
	}
	
	// Returns the change for the remainingprice.
	// Precondition: remainingprice is non negative.
	// Returns:
	// 	nothing, but it may throw from the underlying hardware (e.g. empty exception when there are no more coins)
	public void returnChange(BigDecimal remainingprice) throws OverloadException, EmptyException, DisabledException {
		
		numBankNotes = computeNumberOfDenominations(banknoteDenominations, new BigDecimal(remainingprice.toString()));
		numCoins = computeNumberOfDenominations(coinDenominations, new BigDecimal(this.remainingPrice.toString()));
				
		// emit all the coins that we should give as change.
		// i.e., for every single possible denomination (
		for (int i = 0; i < numCoins.length; ++i) {
			for (int j = 0; j < numCoins[i]; ++j)
				scs.coinDispensers.get(coinDenominations[i]).emit();
			
		}

		// find the first banknote to return..
		for (curBanknoteDenom = 0; 
				curBanknoteDenom < numBankNotes.length && numBankNotes[curBanknoteDenom] <= 0; 
				curBanknoteDenom++) {}
		
		
		// if there is nothing to return, then we just return...
		if (curBanknoteDenom >= numBankNotes.length)
			return;

		
		// emit the banknote to return
		scs.banknoteDispensers.get(banknoteDenominations[curBanknoteDenom]).emit();
		// AND we actually do the book keeping to remember that we actually did decrement this.
		--numBankNotes[curBanknoteDenom];

	}
	
	// Expects as input
	//	banknoteDemoniation from least to greatest
	//  coinDemoniation from least to greatest
	//  totalprice as the total price (this must be a multiple of 5 cents.
	// 
	// Preconditions:
	//  all inputs are non negative.
	//
	// returns:
	// 	the array which holds an int for the number of that indices demonination.
	//
	// This algorithm is just a greedy algorithm.
	public int[] computeNumberOfDenominations(int[] denomination, BigDecimal totalprice) {

		BigDecimal ntotalprice = totalprice;
		// ntotalprice.setScale(0);
		ntotalprice.setScale(2, RoundingMode.FLOOR);

		
		int[] numChange = new int[denomination.length];
		for (int i = denomination.length - 1; i >= 0; --i) {
			
			BigDecimal n = ntotalprice.divide(new BigDecimal (denomination[i]));
			numChange[i] = n.intValue();
			
			ntotalprice = ntotalprice.subtract(new BigDecimal(denomination[i]).multiply( new BigDecimal(numChange[i]) ));
		}
		
		remainingPrice = ntotalprice;
		
		return numChange;
	}
	// more or less the same as the overload, but with a BigDecimal array type.
	public int[] computeNumberOfDenominations(BigDecimal[] denomination, BigDecimal totalprice) {
		BigDecimal ntotalprice = totalprice;
		ntotalprice.setScale(2, RoundingMode.FLOOR);

		
		int[] numChange = new int[denomination.length];
		for (int i = denomination.length - 1; i >= 0; --i) {
			
			BigDecimal n = ntotalprice.divide(denomination[i], RoundingMode.FLOOR);
			numChange[i] = n.intValue();
			
			ntotalprice = ntotalprice.subtract(denomination[i].multiply( new BigDecimal(numChange[i]) ));
		}
		remainingPrice = ntotalprice;

		return numChange;
	}


	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// we don't need this
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// we don't need this
	}

	@Override
	public void banknoteInserted(BanknoteSlot slot) {
		// we don't need this
	}

	@Override
	public void banknoteEjected(BanknoteSlot slot) {
		// we don't need this		
	}

	@Override
	public void banknoteRemoved(BanknoteSlot slot) {
		
		// If this listener is called, then the user removed a banknote... so in this case, we find the next banknote we should return as change
		for (; curBanknoteDenom < numBankNotes.length && numBankNotes[curBanknoteDenom] <= 0; 
				curBanknoteDenom++) {}
			
		// if there is nothing left to return as change, we just return
		if (curBanknoteDenom >= numBankNotes.length)
			return;
		
		// emit the banknote to return, and do the book keeping to ensure that we REALLY have actually sent out the bank notes.
		try {
			scs.banknoteDispensers.get(banknoteDenominations[curBanknoteDenom]).emit();
			--numBankNotes[curBanknoteDenom];
		} catch (EmptyException | DisabledException | OverloadException e) {
			// we just need to do this.
			e.printStackTrace();
		}
		
	}


}
