package ca.seng300.software.stubs;

import java.math.BigDecimal;

import org.lsmr.selfcheckout.Card;

/**
 * @author Owen
 * This class simulates a bank account that can
 * be stored at a bank or financial institution
 * 
 */

public class BankAccountStub {
	private String name;
	private Card debitCard;
	private Card creditCard;
	private BigDecimal debitBalance;
	private BigDecimal creditBalance;
	private BigDecimal creditLimit;
	private BigDecimal availableCredit;
	
	public BankAccountStub(String n, Card debit, Card credit, BigDecimal dBalance, BigDecimal cBalance, BigDecimal credLimit) {
		this.name = n;
		this.debitCard = debit;
		this.creditCard = credit;
		this.debitBalance = dBalance;
		this.creditBalance = cBalance;
		this.creditLimit = credLimit;
		this.availableCredit = creditLimit.subtract(creditBalance);
	}
	
	public String getName() {
		return name;
	}
	
	public Card getDebit() {
		return debitCard;
	}
	
	public Card getCredit() {
		return creditCard;
	}
	
	public BigDecimal getDebitBalance() {
		return debitBalance;
	}
	
	public BigDecimal getCreditBalance() {
		return creditBalance;
	}
	
	public BigDecimal chargeDebit(BigDecimal charge) {
		int result = charge.compareTo(debitBalance);
		if(result == -1 || result == 0) {
			debitBalance.subtract(charge);
		} else {
			charge.subtract(debitBalance);
			debitBalance = BigDecimal.ZERO;
			return charge;
		}
		return BigDecimal.ZERO;
	}
	
	public BigDecimal chargeCredit(BigDecimal charge) {
		int result = charge.compareTo(availableCredit);
		if(result == -1 || result == 0) {
			creditBalance.add(charge);
			availableCredit = creditLimit.subtract(creditBalance);
		} else {
			charge.subtract(availableCredit);
			availableCredit = BigDecimal.ZERO;
			creditBalance = creditLimit;
			return charge;
		}
		return BigDecimal.ZERO;
	}
}
