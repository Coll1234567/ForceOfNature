package me.jishuna.forceofnature.runnables;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.api.WeatherType;
import me.jishuna.forceofnature.api.WorldData;
import me.jishuna.forceofnature.api.WorldManager;
import me.jishuna.forceofnature.api.biomes.SeasonalBiomeGroup;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.biome.BiomeBase;

public class PlayerWeatherRunnable extends BukkitRunnable {
	private static final IRegistryWritable<BiomeBase> BIOME_REGISTRY = ((CraftServer) Bukkit.getServer()).getServer().l
			.b(IRegistry.aO);

	private final Map<UUID, WeatherType> typeMap = new HashMap<>();
	private final WorldManager manager;

	public PlayerWeatherRunnable(WorldManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			WorldData data = manager.getWorldData(player.getWorld());

			if (data == null)
				return;

			Location loc = player.getLocation();

			WorldServer serverWorld = ((CraftWorld) player.getWorld()).getHandle();
			BiomeBase biome = serverWorld.getBiome(loc.getBlockX() >> 2, loc.getBlockY(), loc.getBlockZ() >> 2);
			String key = BIOME_REGISTRY.getKey(biome).toString();

			SeasonalBiomeGroup group = data.getRegistry().getBiomeGroup(key);

			if (group == null) {
				if (this.typeMap.get(player.getUniqueId()) != null) {
					this.typeMap.put(player.getUniqueId(), null);
				}

				org.bukkit.WeatherType worldType = getWeather(player.getWorld());
				if (player.getPlayerWeather() != worldType)
					player.setPlayerWeather(worldType);

				return;
			}

			WeatherType prev = this.typeMap.get(player.getUniqueId());
			WeatherType type = group.getWeather();

			if (prev != type) {
				this.typeMap.put(player.getUniqueId(), type);

				if (type.hasDownfall()) {
					player.setPlayerWeather(org.bukkit.WeatherType.DOWNFALL);
				} else {
					player.setPlayerWeather(org.bukkit.WeatherType.CLEAR);
				}
			}
		}
	}

	private org.bukkit.WeatherType getWeather(World world) {
		return world.getClearWeatherDuration() > 0 ? org.bukkit.WeatherType.CLEAR : org.bukkit.WeatherType.DOWNFALL;
	}

}
