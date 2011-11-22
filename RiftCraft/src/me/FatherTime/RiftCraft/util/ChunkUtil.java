package me.FatherTime.RiftCraft.util;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public class ChunkUtil {
	
	public static Set<Chunk> getChunks(Location location, double reach){
		final Set<Chunk> chunks = Collections.synchronizedSet(new LinkedHashSet<Chunk>());
		final World world = location.getWorld();
		final int radius = Location.locToBlock(reach) + 1;
		final int xbase = location.getBlockX();
		final int zbase = location.getBlockZ();
		
		for(int x = -radius; x<=radius; x++){
			for(int z = -radius; y<=radius; y++){
				//TODO figure this out!
				if()
					chunks.add(world.getChunkAt(x, z));
			}
		}
		
		
		return chunks;
	}
}
