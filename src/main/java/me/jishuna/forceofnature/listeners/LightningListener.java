package me.jishuna.forceofnature.listeners;

import org.bukkit.Bukkit;
import org.bukkit.HeightMap;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.LightningStrikeEvent.Cause;

import me.jishuna.forceofnature.api.WeatherType;
import me.jishuna.forceofnature.api.WorldData;
import me.jishuna.forceofnature.api.WorldManager;
import me.jishuna.forceofnature.api.biomes.SeasonalBiomeGroup;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.biome.BiomeBase;

public class LightningListener implements Listener {

	private final WorldManager manager;
	private final IRegistryWritable<BiomeBase> biomeRegistry = ((CraftServer) Bukkit.getServer()).getServer().l
			.b(IRegistry.aO);

	public LightningListener(WorldManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onStrike(LightningStrikeEvent event) {
		if (event.getCause() != Cause.WEATHER && event.getCause() != Cause.TRIDENT)
			return;
		
		Location location = event.getLightning().getLocation();
		World world = location.getWorld();

		WorldData data = manager.getWorldData(world);
		if (data == null)
			return;

		WorldServer serverWorld = ((CraftWorld) world).getHandle();
		int x = location.getBlockX();
		int z = location.getBlockZ();
		int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);

		BiomeBase biome = serverWorld.getBiome(x >> 2, y, z >> 2);
		String key = biomeRegistry.getKey(biome).toString();

		SeasonalBiomeGroup group = data.getRegistry().getBiomeGroup(key);

		if (group == null)
			return;

		if (group.getWeather() != WeatherType.HEAVY_RAIN && group.getWeather() != WeatherType.RAIN) {
			event.setCancelled(true);
		}
	}

}
