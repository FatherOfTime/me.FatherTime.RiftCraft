package me.FatherTime.RiftCraft;

import java.util.Map;

// import org.bukkit.block.BlockFace;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;

public class RiftCraftBlockListener extends BlockListener
{
	@Override
	public void onBlockPhysics(BlockPhysicsEvent event)
	{
		if (!event.isCancelled())
		{
			for( Map.Entry<Block, RiftCraftPortal> KPV : RiftCraft.ActivePortals.entrySet() )
			{
				Block portal = KPV.getKey();
				Block eventblock = event.getBlock();
				if( eventblock.equals(portal)  )
				{
					event.setCancelled(true);
					continue;
				}
			}
		}
	}
}