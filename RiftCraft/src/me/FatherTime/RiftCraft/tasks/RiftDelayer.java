/**
 * 
 */
package me.FatherTime.RiftCraft.tasks;

import me.FatherTime.RiftCraft.RiftBook;
import me.FatherTime.RiftCraft.RiftCraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * 
 * @author Scriblon
 */
public class RiftDelayer implements Runnable {
	
	private final RiftCraft riftCraft;
	private final Player player;
	private final Location initiate;
	private final RiftBook book;
	private final String inscription;
	private final long delay;
	
	private int iD;
	
	public RiftDelayer(RiftCraft riftCraft, Player player, RiftBook book, String inscription, long delay){
		this.riftCraft = riftCraft;
		this.player = player;
		this.initiate = player.getLocation();
		this.book = book;
		this.inscription = inscription;
		this.delay = delay;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		//Checks if player has moved.
		if(!initiate.equals(player.getLocation())){
			player.sendMessage(ChatColor.YELLOW + "Rift couldn't stabalize, you have moved.");
			return;
		}
		riftCraft.FinishRift(player.getName(), book, inscription, player.getInventory());
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
