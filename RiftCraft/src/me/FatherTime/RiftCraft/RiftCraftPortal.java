package me.FatherTime.RiftCraft;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.plugin.Plugin;

public class RiftCraftPortal
{
	private Location Destination;
	private Block Portal;

	public RiftCraftPortal( Plugin plugin, Location dest, Block portal )
	{
		Destination = dest;
		Portal = portal;
		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask( plugin, new Runnable()
		{
			public void run() 
			{
				Dispel();
			}
		}, 20L * RiftCraft.PortalLife );
	}

	public static void GenerateRiftCraftPortal( Plugin plugin, Location portalloc, Location destloc )
	{		
		for( int i = 0; i < 2; i++ )
		{
			World world = portalloc.getWorld();
			double PortalY =  portalloc.getY() + (double)i;
			Location blockloc = new Location(world, portalloc.getX(), PortalY, portalloc.getZ());
			Block block = world.getBlockAt(blockloc);
			RiftCraft.ActivePortals.put( block, new RiftCraftPortal( plugin, destloc, block ));
			block.setTypeId(90);
		}
	}
	
	public static void DispelAllGates()
	{
		for( Map.Entry<Block, RiftCraftPortal> KPV : RiftCraft.ActivePortals.entrySet() )
		{
			Block portal = KPV.getKey();
			portal.setTypeId(0);
		}
	}

	public void Dispel()
	{
		RiftCraft.ActivePortals.remove(Portal);
		Portal.setTypeId(0);
	}
	
	public static void HandlePlayerPortal(PlayerPortalEvent event) 
	{
		Location portloc = event.getFrom();
		World world = portloc.getWorld();
		Block center = world.getBlockAt( portloc );
		List<Block> tocheck  = new ArrayList<Block>();
		tocheck.add(center);
		tocheck.add(center.getRelative(BlockFace.EAST));
		tocheck.add(center.getRelative(BlockFace.EAST_NORTH_EAST));
		tocheck.add(center.getRelative(BlockFace.EAST_SOUTH_EAST));
		tocheck.add(center.getRelative(BlockFace.NORTH));
		tocheck.add(center.getRelative(BlockFace.NORTH_EAST));
		tocheck.add(center.getRelative(BlockFace.NORTH_NORTH_EAST));
		tocheck.add(center.getRelative(BlockFace.NORTH_NORTH_WEST));
		tocheck.add(center.getRelative(BlockFace.NORTH_WEST));
		tocheck.add(center.getRelative(BlockFace.SELF));
		tocheck.add(center.getRelative(BlockFace.SOUTH));
		tocheck.add(center.getRelative(BlockFace.SOUTH_EAST));
		tocheck.add(center.getRelative(BlockFace.SOUTH_SOUTH_EAST));
		tocheck.add(center.getRelative(BlockFace.SOUTH_SOUTH_WEST));
		tocheck.add(center.getRelative(BlockFace.SOUTH_WEST));
		tocheck.add(center.getRelative(BlockFace.WEST));
		tocheck.add(center.getRelative(BlockFace.WEST_NORTH_WEST));
		tocheck.add(center.getRelative(BlockFace.WEST_SOUTH_WEST));

		for( Map.Entry<Block, RiftCraftPortal> KPV : RiftCraft.ActivePortals.entrySet() )
		{
			Block portal = KPV.getKey();
			for( int i = 0; i < tocheck.size(); i++ )
			{
				Block eventblock = tocheck.get(i);
				if( eventblock.equals(portal)  )
				{
					RiftCraftPortal data = RiftCraft.ActivePortals.get(portal);
					Location destination = data.Destination;
					TravelAgent agent = event.getPortalTravelAgent();
					agent.setCanCreatePortal(false);
					event.setTo(destination);
					continue;
				}				
			}
		}
	}
}