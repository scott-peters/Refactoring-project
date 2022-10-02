package ca.seng300.software;

import org.lsmr.selfcheckout.Numeral;

public class Member {

	private String name;
	private Numeral[] ID;
	
	// Declare a new member
	public Member(String name, Numeral[] ID) {
		this.name = name;
		this.ID = ID;
	}
	
	//Get name of member eg. John Smith
	public String getName() {
		return name;
	}
	
	//Get ID from member
	public Numeral[] getID() {
		return ID;
	}
	
	
	
}
