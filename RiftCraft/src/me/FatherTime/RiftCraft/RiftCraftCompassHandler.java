package me.FatherTime.RiftCraft;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
		
class RiftCraftCompassHandler
{
	public static void FindNearestPlayersMark( String castername, String targetname, RiftBook book )
	{
		Player Caster = Bukkit.getPlayer(castername);
		Location nearest = book.GetClosestMark(castername,  book);
		Caster.setCompassTarget(nearest);
		Caster.sendMessage(ChatColor.YELLOW + "Your compass is now pointing to " + targetname + " nearest mark.");
	}
	
	public static void FindSpawn( String castername )
	{
		Player Caster = Bukkit.getPlayer(castername);
		Location spawn = Caster.getWorld().getSpawnLocation();
		Caster.setCompassTarget(spawn);
		Caster.sendMessage(ChatColor.YELLOW + "Your compass is now pointing to your spawn.");
	}
}
