package ca.seng300.software;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lsmr.selfcheckout.devices.AbstractDevice;
import org.lsmr.selfcheckout.devices.BanknoteSlot;
import org.lsmr.selfcheckout.devices.BanknoteValidator;
import org.lsmr.selfcheckout.devices.CardReader;
import org.lsmr.selfcheckout.devices.CoinValidator;
import org.lsmr.selfcheckout.devices.DisabledException;
import org.lsmr.selfcheckout.devices.EmptyException;
import org.lsmr.selfcheckout.devices.OverloadException;
import org.lsmr.selfcheckout.devices.SelfCheckoutStation;
import org.lsmr.selfcheckout.devices.SimulationException;
import org.lsmr.selfcheckout.devices.observers.AbstractDeviceObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteSlotObserver;
import org.lsmr.selfcheckout.devices.observers.BanknoteValidatorObserver;
import org.lsmr.selfcheckout.devices.observers.CardReaderObserver;
import org.lsmr.selfcheckout.devices.observers.CoinValidatorObserver;

import ca.seng300.software.stubs.BankStub;

import org.lsmr.selfcheckout.*;
import org.lsmr.selfcheckout.Card.CardData;

// combining payment into one thing
public class PaymentLogic implements BanknoteSlotObserver, BanknoteValidatorObserver, CoinValidatorObserver, CardReaderObserver{
	
	private SelfCheckoutStation scs;
	private UseCase uc;
	private BankStub bankStub;
	
	private BigDecimal total;
	private BigDecimal paid;
	private boolean completed;
	
	private BigDecimal cardPay = BigDecimal.ZERO;
	
	// these should be in descending order
	private BigDecimal[] coinDenominations;
	private int[] banknoteDenominations;
		
	private BigDecimal remainingPrice;
	
	private int[] numBankNotes;
	private int[] numCoins;
	
	private int curBanknoteDenom;
	// OBJECTS

	// ------------------------ CONSTRUCTOR -------------------------------//

	public PaymentLogic(SelfCheckoutStation selfCheckoutStation, UseCase useCase) {
		// Assign our initialized variable to the given parameters for calling purposes.
		this.scs = selfCheckoutStation;
		this.uc = useCase;
		this.paid = new BigDecimal(0);
		this.setCompleted(false);
		
		banknoteDenominations = scs.banknoteDenominations;
		coinDenominations = scs.coinDenominations.toArray(new BigDecimal[0]);
		
		Coin.DEFAULT_CURRENCY = UseCase.CURRENCY;
		
		this.scs.coinValidator.attach(this);
		this.scs.banknoteValidator.attach(this);
		
		bankStub = new BankStub();
		update_owed();

	}
	
	public void update_owed() {
		total = uc.getOwedAmount();
	}
	
	// make payment with cash/coin
	// once paid is more than amount owed
	// as payments with banknotes and coins are made they are being updated into paid
	//---------------------------------BANKNOTE/COIN--------------------------------------------------//
	public void pay(Coin coin) throws DisabledException{
		scs.coinSlot.accept(coin);
		getUseCase().subtractOwedAmount(coin.getValue());
		getPaid().add(coin.getValue());
		update_owed();
	}
	
	public void pay(Coin... coins) throws DisabledException{
		for (Coin coin : coins) {
			this.pay(coin);
		}
	}
	
	public void pay(List<Coin> coins) throws DisabledException{
		for (Coin coin : coins) {
			this.pay(coin);
		}
	}
	
	public void pay(Banknote bankNote) throws DisabledException, OverloadException{
		if (bankNote == null) {
			throw new SimulationException(new NullPointerException("Banknote is empty"));
		}

		scs.banknoteValidator.accept(bankNote);
		BigDecimal value = new BigDecimal(bankNote.getValue());
		getUseCase().subtractOwedAmount(value);
		getPaid().add(value);
		update_owed();
	}
	
	public BigDecimal getChangeAmount() {
		BigDecimal change = this.paid.subtract(this.total);

		if (change.compareTo(BigDecimal.ZERO) < 0) {
			return BigDecimal.ZERO;
		}

		return change;
	}
	
//----------------------------------NEW GET CHANGE-------------------------------------------//
	
// Returns the change for the remainingprice.
	// Precondition: remainingprice is non negative.
	// Returns:
	// 	nothing, but it may throw from the underlying hardware (e.g. empty exception when there are no more coins)
	public void returnChange() throws OverloadException, EmptyException, DisabledException {
		remainingPrice = getChangeAmount();
		// if remaining Price is less than amount it just returns silently
		if (remainingPrice.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		
		numBankNotes = computeNumberOfDenominations(banknoteDenominations, new BigDecimal(this.remainingPrice.toString()));
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
	
	
	//----------------------------------CARD PAYMENTS-----------------------------------------------//
	
	// make payment with card
	// make payment with card and another card
	
	public void pay_card(BigDecimal amount) {
		cardPay = amount;
	}
	
	@Override
	public void cardDataRead(CardReader reader, CardData data) {
		BigDecimal remainingCost = BigDecimal.ZERO;
		remainingCost = bankStub.verifyData(data, cardPay);
		paid.add(cardPay.subtract(remainingCost));
		
	}
	
	
	
	
	// could be used for recipt or smt
	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public BigDecimal getTotal() {
		update_owed();
		return this.total;
	}
	public BigDecimal getPaid() {
		return this.paid;
	}
	
	public SelfCheckoutStation getSCS() {
		return this.scs;
	}
	
	public UseCase getUseCase() {
		return this.uc;
	}

	@Override
	public void enabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disabled(AbstractDevice<? extends AbstractDeviceObserver> device) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validBanknoteDetected(BanknoteValidator validator, Currency currency, int value) {
		this.paid.add(new BigDecimal(value));
	}
		

	@Override
	public void invalidBanknoteDetected(BanknoteValidator validator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void validCoinDetected(CoinValidator validator, BigDecimal value) {
		// Add insert amount
		this.paid.add(value);
		
	}

	@Override
	public void invalidCoinDetected(CoinValidator validator) {
		// Do nothing, since the coin is returned to the tray, for user to collect back
		
	}

	@Override
	public void cardInserted(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardRemoved(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardTapped(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cardSwiped(CardReader reader) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknoteInserted(BanknoteSlot slot) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void banknoteEjected(BanknoteSlot slot) {
		// TODO Auto-generated method stub
		
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
/*
//-------------------------GET CHANGE (OLD) just in case we need it-------------------------------//
	
	 * @throws OverloadException
	 *                           If the output channel is unable to accept another
	 *                           coin.
	 * @throws EmptyException
	 *                           If no coins are present in the dispenser to
	 *                           release.
	 * @throws DisabledException
	 *                           If the dispenser is currently disabled.
	 *
	public void getChange() throws OverloadException, DisabledException, EmptyException {
		Map<BigDecimal, Integer> changes = this.getChangeCoinCombination(0);

		if (changes == null) {
			throw new EmptyException();
		}

		for (Map.Entry<BigDecimal, Integer> pair : changes.entrySet()) {
			BigDecimal denomination = pair.getKey();
			int count = pair.getValue();

			for (int t = 0; t < count; t++) {
				scs.coinDispensers.get(denomination).emit();
			}
		}
	}

	
	 * @param denominationIndex
	 * @return Map<BigDecimal, Integer> A dict representing pairs of coin
	 *         denomination and count of coins
	 * @see: https://stackoverflow.com/questions/44213144/coin-change-with-limited-coins-complexity
	 
	public Map<BigDecimal, Integer> getChangeCoinCombination(int denominationIndex) {
		BigDecimal change = this.getChangeAmount();

		if (change.compareTo(BigDecimal.ZERO) == 0) {
			return new HashMap<BigDecimal, Integer>();
		}

		if (denominationIndex >= UseCase.COIN_DENOMINATIONS.length) {
			return null;
		}

		BigDecimal denomination = UseCase.COIN_DENOMINATIONS[denominationIndex];

		int amount = Math.min(change.divide(denomination).intValue(), scs.coinDispensers.get(denomination).size());

		for (int t = amount; t >= 0; t--) {
			Map<BigDecimal, Integer> coins = this.getChangeCoinCombination(denominationIndex + 1);
			if (coins != null) {
				if (t != 0) {
					coins.put(denomination, t);
				}

				return coins;
			}
		}

		return null;
	}
	
	
	
	 * @return BigDecimal
	 
	public BigDecimal getChangeAmount() {
		BigDecimal change = paid.subtract(this.total);

		if (change.compareTo(BigDecimal.ZERO) < 0) {
			return BigDecimal.ZERO;
		}
		
		setCompleted(true);
		return change;
	}
*/
