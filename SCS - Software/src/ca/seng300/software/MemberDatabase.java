package ca.seng300.software;

import java.util.ArrayList;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;

public class MemberDatabase {

	private ArrayList<Member> allMembers;
	private ArrayList<MembershipCard> allMemberCards;
	
	// Constructor
	public MemberDatabase() {
		allMembers = new ArrayList<Member>();
		allMemberCards = new ArrayList<MembershipCard>();
	}
	
	//Add a new member
	public void addMember(Member memberToAdd) {
		if(lookUpMemberByID(memberToAdd.getID()) == null) {
		allMembers.add(memberToAdd);
		}
	}
	//Add a new member (which didn't exist previously)
	public void addMember(String name, Numeral[] ID) {
		this.addMember(new Member(name,ID));
	}
	
	//Remove a member
	public void removeMember(Member memberToRemove) {
		if(allMembers.contains(memberToRemove)) {
			allMembers.remove(memberToRemove);
		}
	}
	
	//Lookup a member by their name eg. John Smith
	public Member lookUpMemberByName(String name) {
		Member currentMember = null;
		
		for(Member potentialMember:allMembers) {
			if(potentialMember.getName() == name) {
				currentMember = potentialMember;
			}
		}
		
		return currentMember;
	}
	
	//Look up a member by their ID
	public Member lookUpMemberByID(Numeral[] ID) {
		Member currentMember = null;
		
		for(Member potentialMember: allMembers) {
			if(potentialMember.getID() == ID) {
				currentMember = potentialMember;
			}
		}
		return currentMember;
		
	}
	
	//Add a new membership card
	public void addMembershipCard(MembershipCard currentCard) {
		addMember(currentCard.getMember());
		if(lookupMemberByBarcode(currentCard.getBarcode()) ==null) {
		allMemberCards.add(currentCard);
		}
		
		
		
	}
	
	//Look up a member by their Barcode
	public Member lookupMemberByBarcode(Barcode currentBarcode) {
		Member currentMember = null;
		
		for(MembershipCard potentialCard: allMemberCards) {
			if(potentialCard.getBarcode().hashCode() == currentBarcode.hashCode()) {
				currentMember = potentialCard.getMember();
			}
		}
		return currentMember;
	}
	
	
	//Look up a card by its member
	public MembershipCard lookupMembershipCardByMember(Member currentMember) {
		MembershipCard currentCard = null;
		
		for(MembershipCard potentialCard: allMemberCards) {
			if(potentialCard.getMember() == currentMember) {
				currentCard = potentialCard;
			}
		}
		
		return currentCard;
	}
	
	
	
	
}
