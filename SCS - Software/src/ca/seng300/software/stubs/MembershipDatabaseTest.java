package ca.seng300.software.stubs;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;

import ca.seng300.software.Member;
import ca.seng300.software.MemberDatabase;
import ca.seng300.software.MembershipCard;

public class MembershipDatabaseTest {

	Member testMember;
	MembershipCard testCard;
	MemberDatabase testDatabase;
	
	
	@Before
	public void setup() {
		testDatabase = new MemberDatabase();
		
		Numeral[] memberID = {Numeral.eight,Numeral.four,Numeral.seven};
		testMember = new Member("Name SecondName",memberID);
		
		
		Numeral[] memberBarcode = {Numeral.six,Numeral.eight,Numeral.four,Numeral.two};
		testCard = new MembershipCard(testMember, new Barcode(memberBarcode));
	}
	
	@Test
	public void testMemberConstructor() {
		Numeral[] memberID = {Numeral.two,Numeral.six,Numeral.seven};
		testMember = new Member("Name SecondName",memberID);
		
		Assert.assertTrue(testMember.getName() == "Name SecondName");
		Assert.assertTrue(testMember.getID() == memberID);

	}
	
	@Test 
	public void testMemberCardConstructor() {
		Numeral[] memberBarcode = {Numeral.six,Numeral.eight,Numeral.four,Numeral.two};
		Barcode barcode = new Barcode(memberBarcode);
		testCard = new MembershipCard(testMember, barcode);
		
		Assert.assertTrue(testCard.getMember().getName() == "Name SecondName");
		
		
		Numeral[] memberID = {Numeral.eight,Numeral.four,Numeral.seven};
		testCard = new MembershipCard("Name SecondName",memberID, barcode);
		Assert.assertTrue(testCard.getBarcode() == barcode);
		

	}
	
	
	@Test
	public void testAddMember() {
		testDatabase.addMember(testMember);
		
		Assert.assertTrue(testDatabase.lookUpMemberByName(testMember.getName()) != null);
		testDatabase.addMember(testMember);

		
		Numeral[] newMemberID = {Numeral.four,Numeral.zero,Numeral.four};

		testDatabase.addMember("Member", newMemberID);
		Assert.assertTrue(testDatabase.lookUpMemberByName("Member") != null);

	}
	
	
	@Test
	
	public void testRemoveMember() {
		testDatabase.addMember(testMember);
		Assert.assertTrue(testDatabase.lookUpMemberByName(testMember.getName()) != null);

		testDatabase.removeMember(testMember);
		Assert.assertTrue(testDatabase.lookUpMemberByName(testMember.getName()) == null);
		testDatabase.removeMember(testMember);
		Assert.assertTrue(testDatabase.lookUpMemberByName(testMember.getName()) == null);


	}
	
	@Test
	public void testAddMemberCard() {
		testDatabase.addMembershipCard(testCard);
		Assert.assertTrue(testDatabase.lookupMemberByBarcode(testCard.getBarcode()) == testMember);
	}
	
	@Test
	public void testLookupCardByMember() {
		testDatabase.addMembershipCard(testCard);

		Assert.assertTrue(testDatabase.lookupMembershipCardByMember(testMember) == testCard);
	}
	
	
	
	
	

}
