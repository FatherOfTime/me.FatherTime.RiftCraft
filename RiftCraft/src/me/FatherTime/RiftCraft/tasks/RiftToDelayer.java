/**
 * 
 */
package me.FatherTime.RiftCraft.tasks;

import me.FatherTime.RiftCraft.RiftCraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * 
 * @author Scriblon
 */
public class RiftToDelayer implements Runnable {
	
	private final RiftCraft riftCraft;
	private final Player requester;
	private final Player requested;
	private final Location initiateRequester;
	private final Location destination;
	private final long delay;
	
	private int iD;
	
	public RiftToDelayer(RiftCraft riftCraft, Player requester, Player requested, long delay){
		this.riftCraft = riftCraft;
		this.requester = requester;
		this.initiateRequester = requester.getLocation();
		this.requested = requested;
		this.destination = requested.getLocation();
		this.delay = delay;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		if(!destination.equals(requested.getLocation())){
			requested.sendMessage(ChatColor.YELLOW + "Rift couldn't stabalize, you moved.");
			requester.sendMessage(ChatColor.YELLOW + "Rift couldn't stabalize, caster moved.");
			return;
		}
		if(!initiateRequester.equals(requester.getLocation())){
			requested.sendMessage(ChatColor.YELLOW + "Rift couldn't stabalize, subject moved.");
			requester.sendMessage(ChatColor.YELLOW + "Rift couldn't stabalize, you moved.");
			return;
		}
		
		RiftCraft.FinishRiftTo(requester.getName().toLowerCase(), requested.getName().toLowerCase(), requester.getInventory(), destination);
	}
	
	public void scheduleMe(){
		iD = riftCraft.getServer().getScheduler().scheduleSyncDelayedTask(riftCraft, this, delay);
	}
	
	public void scheduleMe(long customDelay){
		iD = riftCraft.getServer().getScheduler().scheduleSyncDelayedTask(riftCraft, this, customDelay);
	}
	
	public void cancelMe(){
		riftCraft.getServer().getScheduler().cancelTask(iD);
	}

}