package me.FatherTime.RiftCraft;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

class RiftBook
{
	public Map<String, Location> Inscriptions = new HashMap<String, Location>();
	public Date LastUsed;

	public RiftBook()
	{
	}
	
	public String BlockIsInscription( Block block )
	{
		String inscription = null;
		
		for (Map.Entry<String, Location> KPV : Inscriptions.entrySet())
		{
			String ToCompareLocName = KPV.getKey();
			Location ToCompareLoc = KPV.getValue();
			World ToCompareWorld = ToCompareLoc.getWorld();
			Block ToCompareBlock = ToCompareWorld.getBlockAt(ToCompareLoc);
			
			if( block.equals(ToCompareBlock))
			{
				inscription = ToCompareLocName;
				continue;
			}
		}
		return inscription;
	}

	public void MarkLocation( Location loc, String name )
	{
		Inscriptions.put(name, loc);
	}
	
	public void RiftToLocation( String playername, String inscription )
	{
		Location destination = GetLocation( inscription );
		Player player = Bukkit.getPlayer(playername);
		player.teleport(destination);
	}

	public Location GetLocation( String name )
	{
		return Inscriptions.get( name );
	}
	
	public boolean HasInscription( String name )
	{
		return Inscriptions.containsKey(name.toLowerCase());
	}
	
	public void RemoveInscription( String inscription )
	{
		Inscriptions.remove(inscription);
	}
	
	public boolean HasMarkNearby( String playername )
	{
		Player player = Bukkit.getPlayer(playername);
		Location playerloc = player.getLocation();
		
		for( Map.Entry<String, Location> KPV : Inscriptions.entrySet() )
		{
			Location LocToCompare = KPV.getValue();
			if( playerloc.getWorld() == LocToCompare.getWorld() )
			{
				double distance = playerloc.distance(LocToCompare);
				if( distance <= 50.0 )
					return true;
			}
		}
		return false;		
	}
	
	public Location GetClosestMark( String castername, RiftBook book )
	{
		Player player = Bukkit.getPlayer(castername);
		Location playerloc = player.getLocation();
		Location closest = playerloc;
		double distance = Double.MAX_VALUE;
		
		for( Map.Entry<String, Location> KPV : book.Inscriptions.entrySet() )
		{
			Location LocToCompare = KPV.getValue();
			if( playerloc.getWorld() == LocToCompare.getWorld() )
			{
				double DistToCompare = playerloc.distance(LocToCompare);
				if( DistToCompare < distance )
				{
					closest = LocToCompare;
					distance = DistToCompare;
				}
			}
		}
		return closest;
	}
}