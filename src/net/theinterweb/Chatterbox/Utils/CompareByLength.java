package net.theinterweb.Chatterbox.Utils;

public class CompareByLength implements java.util.Comparator<String>{
	
	private int referenceLength;
	
	public CompareByLength (String reference) {
		super();
		this.referenceLength = reference.length();
	}
	
	public int compare (String s1, String s2) {
		int dist1 = Math.abs(s1.length() - referenceLength);
		int dist2 = Math.abs(s2.length() - referenceLength);
		
		return dist2 - dist1;
	}
}
