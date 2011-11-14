package me.FatherTime.RiftCraft;

import me.FatherTime.RiftCraft.RiftCraftP2PRequest.RCTravelType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RiftCraftP2PHandler
{
	public static Map< String, RiftCraftP2PRequest> ActiveP2PRequest = new HashMap< String, RiftCraftP2PRequest>();
	
	public static void InitiateP2PRequest( String requestername, String requestedname, RCTravelType traveltype )
	{
		Player requester = Bukkit.getPlayer(requestername);
		Player requested = Bukkit.getPlayer(requestedname);
		if( requested != null && requested.isOnline() )
		{
			RiftCraftP2PRequest request = new RiftCraftP2PRequest(requestername, requestedname, traveltype);
			ActiveP2PRequest.put(requestedname, request);
			requester.sendMessage(ChatColor.YELLOW + "You have sent a travel request to " + requestedname + ", awaiting response.");
			requested.sendMessage(ChatColor.YELLOW + requestername + " has requested to " + traveltype.toString() + " to your location.");
			requested.sendMessage(ChatColor.YELLOW + "Type " + ChatColor.WHITE + "/rc accept" + ChatColor.YELLOW + " to accept the request, and " + ChatColor.WHITE + "/rc deny" + ChatColor.YELLOW + " to reject it.");
		}
		else
		requester.sendMessage( ChatColor.RED + requestedname + "'s spiritual energy is too weak to locate." );
	}
	public static void InitiateRequestPurgeTimer()
	{
		Bukkit.getServer().getScheduler().scheduleAsyncRepeatingTask( Bukkit.getPluginManager().getPlugin("RiftCraft"), new Runnable(){public void run() 
		{
			List<String> purgebuffer = new ArrayList<String>();
			for (Map.Entry< String, RiftCraftP2PRequest> KPV : ActiveP2PRequest.entrySet() )
			{
				String playername = KPV.getKey();
				RiftCraftP2PRequest request = KPV.getValue();
				if( request.HasExpired() )
					purgebuffer.add(playername);
			}
			
			for( String player : purgebuffer )
				ActiveP2PRequest.remove(player);

		} }, 20L, 20L );
	}
	
	public static void InitiateP2PAccept( String playername )
	{
		Player player = Bukkit.getPlayer(playername);
		if( ActiveP2PRequest.containsKey(playername) )
		{
			RiftCraftP2PRequest request = ActiveP2PRequest.get(playername);
			RiftCraft.InitiateRiftTo( request.Requester, playername);
			ActiveP2PRequest.remove(playername);
		}
		else
			player.sendMessage(ChatColor.YELLOW + "You have not been sent a RiftCraft request.");
	}

	public static void InitiateP2PDeny( String playername )
	{
		Player requested = Bukkit.getPlayer(playername);
		if( ActiveP2PRequest.containsKey(playername) )
		{
			RiftCraftP2PRequest request = ActiveP2PRequest.get(playername);
			Player requester = Bukkit.getPlayer(request.Requester);
			requested.sendMessage(ChatColor.YELLOW + "You denied " + request.Requester + "'s RiftCraft request.");
			requester.sendMessage(ChatColor.YELLOW + playername + " has denied your RiftCraft request.");
			ActiveP2PRequest.remove(playername);
		}
		else
			requested.sendMessage(ChatColor.YELLOW + "You have not been sent a RiftCraft request.");
	}
}
