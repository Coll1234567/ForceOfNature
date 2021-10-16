package me.jishuna.forceofnature.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;

public class WorldManager {

	private Map<String, WorldData> worldMap = new HashMap<>();

	public void setWorldData(World world, WorldData data) {
		this.worldMap.put(world.getName(), data);
	}

	public WorldData getWorldData(World world) {
		return worldMap.get(world.getName());
	}

	public void removeWorldData(World world) {
		this.worldMap.remove(world.getName());
	}

}
