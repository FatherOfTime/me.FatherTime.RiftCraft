package me.FatherTime.RiftCraft;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;

public class RiftDisruptor {
	
	public static final Material STANDARD_MATERIAL = Material.REDSTONE;
	public static final int STANDARD_REACH = 5;
	
	private final long iD;
	private final Location location;
	private final double reachSquared; 
	
	private boolean enabled;
	
	public RiftDisruptor(long iD, Location location, double reach){
		this.iD = iD;
		this.location = location;
		this.reachSquared = Math.pow(reach, 2.0);
	}
	
	public boolean isInReach(Location otherLocation){
		if(!enabled)
			return false;
		
		if(!location.getWorld().equals(otherLocation.getWorld()))
			return false;
		
		return reachSquared <= location.distanceSquared(otherLocation);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean status) {
		this.enabled = status;
	}

	public Location getLocation() {
		return location;
	}

	public double getReachSquared() {
		return reachSquared;
	}
	
	public Chunk getChunk(){
		return location.getWorld().getChunkAt(location);
	}
	
	public long getID(){
		return iD;
	}
	
	
}
