package me.FatherTime.RiftCraft;
import org.bukkit.Location;

public class RiftCraftUtilities
{
	public static String Capitalize(final String string)
	{
		if (string == null)
			throw new NullPointerException("string");
		if (string.equals(""))
			throw new NullPointerException("string");
	
		return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}
	 
	public static String LocationToCoords( Location loc )
	{
		return "[" + loc.getX() + "," + loc.getBlockY() + "," + loc.getZ() + "]";
	}
}
