package me.FatherTime.RiftCraft;

import org.bukkit.Location;

public class RiftCraftTeleportInfo
{
	public Location StartLoc;
	public Long StartCast;
	public RiftCraftTeleportInfo( Location startloc, Long startcast )
	{
		StartLoc = startloc;
		StartCast = startcast;
	}
}