package com.iesika.aem.util;

public class FacingUtil {
	public static String getFacingString(byte facing){
//	    DOWN(0, 1, 0, -1, 0),
//	    UP(1, 0, 0, 1, 0),
//	    NORTH(2, 3, 0, 0, -1),
//	    SOUTH(3, 2, 0, 0, 1),
//	    EAST(4, 5, -1, 0, 0),
//	    WEST(5, 4, 1, 0, 0);
		String s;
		switch(facing){
			case (byte) 0:
				s = "aem.text.down";
				break;
			case (byte) 1:
				s = "aem.text.up";
				break;
			case (byte) 2:
				s = "aem.text.north";
				break;
			case (byte) 3:
				s = "aem.text.south";
				break;
			case (byte) 4:
				s = "aem.text.east";
				break;
			case (byte) 5:
				s = "aem.text.west";
				break;
			default:
				s = "aem.text.down";
		}
		return s;
	}
}
