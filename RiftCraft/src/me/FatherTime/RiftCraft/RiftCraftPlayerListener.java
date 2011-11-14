package me.FatherTime.RiftCraft;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPortalEvent;

public class RiftCraftPlayerListener extends PlayerListener 
{   	
	@Override  
	public void onPlayerInteract(PlayerInteractEvent event)
	{
        Player player = event.getPlayer();
        if( player.getItemInHand().getType().equals(Material.COMPASS) )
        {
        	if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
    	    {
            	event.setCancelled(true);
        		Block targetblock = event.getClickedBlock();
        		Block markblock = targetblock.getRelative(BlockFace.UP);
        		RiftCraft.InitiateMarkDestroy( event, markblock );
    	    }
        }
	}
	@Override
	public void onPlayerPortal(PlayerPortalEvent event)
	{
		RiftCraftPortal.HandlePlayerPortal(event);
	}
}