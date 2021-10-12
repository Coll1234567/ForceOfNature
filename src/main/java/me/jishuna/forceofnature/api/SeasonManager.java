package me.jishuna.forceofnature.api;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.World;

public class SeasonManager {

	private Map<UUID, Season> seasonMap = new HashMap<>();

	public Season getSeason(World world) {
		return this.seasonMap.get(world.getUID());
	}

	public void setSeason(World world, Season season) {
		this.seasonMap.put(world.getUID(), season);
	}

}
