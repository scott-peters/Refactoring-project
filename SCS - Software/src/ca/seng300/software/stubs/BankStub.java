package ca.seng300.software.stubs;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.lsmr.selfcheckout.Card.CardData;
/**
 * @author Owen
 * This class simulates a bank or financial institution
 * storing bank accounts
 * 
 */


public class BankStub {

	private ArrayList<BankAccountStub> allAccounts;
	
	
	public BankStub() {
		allAccounts = new ArrayList<BankAccountStub>();
	}
	
	public void addAccount(BankAccountStub account) {
		allAccounts.add(account);
		
	}
	
	public void removeAccount(BankAccountStub account) {
		allAccounts.remove(account);
	}
	
	public BigDecimal verifyData(CardData data, BigDecimal cost) {
		BankAccountStub account = null;
		BigDecimal remainingCost = new BigDecimal(0);
		
		for(BankAccountStub possibleAccount: allAccounts) {
			if(data.equals(possibleAccount.getCredit())) {	//credit card is valid
				remainingCost = possibleAccount.chargeCredit(cost);			//need cost
			} else if(data.equals(possibleAccount.getDebit())) { //debit is valid
				remainingCost = possibleAccount.chargeDebit(cost);
			}
		}
		return remainingCost;
	}
	
}
