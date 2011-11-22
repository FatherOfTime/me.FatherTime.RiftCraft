package me.FatherTime.RiftCraft;

import org.bukkit.Location;
import org.bukkit.Material;

public class RiftDisruptor {
	
	public static final Material STANDARD_MATERIAL = Material.REDSTONE;
	public static final int STANDARD_REACH = 5;
	
	private final Location location;
	private final double reachSquared; 
	
	private boolean status;
	
	public RiftDisruptor(Location location, double reach){
		this.location = location;
		this.reachSquared = Math.pow(reach, 2.0);
	}
	
	public boolean isInReach(Location otherLocation){
		if(!status)
			return false;
		
		if(!location.getWorld().equals(otherLocation.getWorld()))
			return false;
		
		final double distance = location.distanceSquared(otherLocation);
		
		return reachSquared <= distance;
	}

}
