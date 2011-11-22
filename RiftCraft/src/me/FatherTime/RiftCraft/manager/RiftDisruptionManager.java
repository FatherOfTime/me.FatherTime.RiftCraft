package me.FatherTime.RiftCraft.manager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.FatherTime.RiftCraft.RiftDisruptor;
import me.FatherTime.RiftCraft.util.ChunkUtil;

import org.bukkit.Chunk;
import org.bukkit.Location;


public class RiftDisruptionManager {
	
	private final Map<Chunk, Map<Long, RiftDisruptor>> disruptors = new HashMap<Chunk, Map<Long, RiftDisruptor>>();
	
	public RiftDisruptionManager(){
		
	}
	
	public void add(List<RiftDisruptor> invoer){
		for(RiftDisruptor disruptor : invoer){
			this.add(disruptor);
		}
	}
	
	public void add(RiftDisruptor disruptor){
		final Set<Chunk> chunks = ChunkUtil.getChunks(disruptor.getLocation(), disruptor.getReachSquared());
		for(Chunk chunk : chunks){
			if(!disruptors.containsKey(chunk))
				disruptors.put(chunk, Collections.synchronizedMap(new HashMap<Long, RiftDisruptor>()));
			disruptors.get(chunk).put(disruptor.getID(), disruptor);
		}
	}
	
	public void removeDisruptor(RiftDisruptor disruptor){
		final Chunk chunk = disruptor.getChunk();
		if(disruptors.containsKey(chunk)){
			final Map<Long, RiftDisruptor> disruptorMap = disruptors.get(chunk);
			final long iD = disruptor.getID();
			if(disruptorMap.containsKey(iD))
				disruptorMap.remove(iD);
			if(disruptorMap.isEmpty())
				disruptors.remove(chunk);
		}
	}
	
	public boolean isInstable(Location location){
		final Chunk chunk = location.getWorld().getChunkAt(location);
		
		if(!disruptors.containsKey(chunk))
			return false;
		
		for(RiftDisruptor disruptor : disruptors.get(chunk).values()){
			if(disruptor.isInReach(location))
				return true;
		}
		
		return false;
	}
}
