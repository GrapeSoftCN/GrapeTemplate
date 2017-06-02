package test;

import httpServer.booter;

public class testtemp {
	public static void main(String[] args) {
		booter booter = new booter();
		 try {
		 System.out.println("Grapetemp!");
		 System.setProperty("AppName", "Grapetemp");
		 booter.start(1002);
		} catch (Exception e) {
		}
	}
}
