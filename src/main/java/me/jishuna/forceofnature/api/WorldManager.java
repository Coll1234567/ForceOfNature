package me.jishuna.forceofnature.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;

public class WorldManager {

	private Map<UUID, Season> seasonMap = new HashMap<>();
	private Map<UUID, Integer> dayMap = new HashMap<>();
	
	public int getDay(World world) {
		return this.dayMap.getOrDefault(world.getUID(), 0);
	}
	
	public void setDay(World world, int day) {
		this.dayMap.put(world.getUID(), day);
	}

	public Season getSeason(World world) {
		return this.seasonMap.get(world.getUID());
	}

	public void setSeason(World world, Season season) {
		this.seasonMap.put(world.getUID(), season);
	}

}
