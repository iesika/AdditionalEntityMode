package com.iesika.aem.util;

import net.minecraft.util.EnumFacing;

public class FacingUtil {
	public static String getFacingString(EnumFacing facing) {
		switch (facing) {
		case DOWN:
			return "aem.text.down";
		case UP:
			return "aem.text.up";
		case NORTH:
			return "aem.text.north";
		case SOUTH:
			return "aem.text.south";
		case EAST:
			return "aem.text.east";
		case WEST:
			return "aem.text.west";
		default:
			return "aem.text.default";
		}
	}

	public static EnumFacing getNextFacing(EnumFacing facing) {
		switch (facing) {
		case DOWN:
			return EnumFacing.UP;
		case UP:
			return EnumFacing.NORTH;
		case NORTH:
			return EnumFacing.SOUTH;
		case SOUTH:
			return EnumFacing.EAST;
		case EAST:
			return EnumFacing.WEST;
		case WEST:
			return EnumFacing.DOWN;
		default:
			return EnumFacing.DOWN;
		}
	}

}
