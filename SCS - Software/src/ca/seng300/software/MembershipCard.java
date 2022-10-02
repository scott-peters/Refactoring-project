package ca.seng300.software;

import org.lsmr.selfcheckout.Barcode;
import org.lsmr.selfcheckout.Numeral;

public class MembershipCard {

	private Member member;
	private Barcode memberBarcode;
	
	
	// Declare a new card with a member to attach it to
	public MembershipCard( Member member, Barcode memberBarcode) {
		this.member = member;
		this.memberBarcode = memberBarcode;
	}
	
	//Specify a new member to be created with their barcode
	public MembershipCard(String name, Numeral[] ID, Barcode memberBarcode) {
		this(new Member(name,ID),memberBarcode);
	}
	
	//Get member from this class
	public Member getMember() {
		return member;
	}
	//Get the barcode from this class
	public Barcode getBarcode() {
		return memberBarcode;
	}
	
	
}
