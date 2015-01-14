package eu.derloki.internetchecker;

public class Packet {
	long time;
	boolean lost;
	
	public Packet(){
		time = 1000;
		lost = true;
	}
}
