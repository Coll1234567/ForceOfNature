package me.jishuna.forceofnature.runnables;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.WeatherType;
import me.jishuna.forceofnature.api.WorldData;
import me.jishuna.forceofnature.api.biomes.SeasonalBiome;
import me.jishuna.forceofnature.api.biomes.SeasonalBiomeGroup;
import me.jishuna.forceofnature.api.weather.HeavyRainWeather;
import me.jishuna.forceofnature.api.weather.SnowWeather;
import me.jishuna.forceofnature.api.weather.Weather;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.biome.BiomeBase;

public class WeatherRunnable extends BukkitRunnable {
	private static final IRegistryWritable<BiomeBase> BIOME_REGISTRY = ((CraftServer) Bukkit.getServer()).getServer().l
			.b(IRegistry.aO);

	private static final SnowWeather SNOW_WEATHER = new SnowWeather();
	private static final HeavyRainWeather HEAVY_RAIN_WEATHER = new HeavyRainWeather();

	private final Queue<PlacementData> blockQueue = new ConcurrentLinkedQueue<>();
	private AtomicInteger toCheck = new AtomicInteger();

	private final ForceOfNature plugin;
	private final Random random = new Random();

	public WeatherRunnable(ForceOfNature plugin) {
		this.plugin = plugin;

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			int count = toCheck.get();
			for (int i = 0; i < count; i++) {
				PlacementData placementData = blockQueue.poll();
				if (placementData == null)
					break;

				Block block = placementData.block;
				int cx = block.getX() >> 4;
				int cz = block.getZ() >> 4;

				if (block.getWorld().isChunkLoaded(cx, cz)) {
					block.setBlockData(placementData.data, false);
				}
			}
		}, 0, 2);

	}

	@Override
	public void run() {

		for (World world : Bukkit.getWorlds()) {
			WorldData data = plugin.getWorldManager().getWorldData(world);
			if (data == null)
				continue;

			WorldServer serverWorld = ((CraftWorld) world).getHandle();

			for (Chunk chunk : world.getLoadedChunks()) {
				if (!world.isChunkLoaded(chunk))
					continue;

				int x = chunk.getX() * 16 + random.nextInt(16);
				int z = chunk.getZ() * 16 + random.nextInt(16);
				int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);

				BiomeBase biome = serverWorld.getBiome(x >> 2, y, z >> 2);
				String key = BIOME_REGISTRY.getKey(biome).toString();

				SeasonalBiomeGroup group = data.getRegistry().getBiomeGroup(key);

				if (group == null)
					continue;

				SeasonalBiome seasonalBiome = group.getBiomeForSeason(data.getSeason());

				if (seasonalBiome == null)
					continue;

				WeatherType type = group.getWeather();
				Block blockA = world.getBlockAt(x, y, z);
				Block blockB = world.getBlockAt(x, y + 1, z);

				for (Block block : new Block[] { blockA, blockB }) {
					processWeather(serverWorld, block, chunk, type, seasonalBiome);
				}
			}
		}
		this.toCheck.lazySet(Math.max(10, this.blockQueue.size() / 5));
	}

	private void processWeather(WorldServer serverWorld, Block block, Chunk chunk, WeatherType type,
			SeasonalBiome seasonalBiome) {

		if (type == WeatherType.SNOW) {
			handleActiveWeather(SNOW_WEATHER, serverWorld, block, chunk, type, seasonalBiome);
		} else {
			handleInactiveWeather(SNOW_WEATHER, serverWorld, block, chunk, type, seasonalBiome);
		}

		if (type == WeatherType.HEAVY_RAIN) {
			handleActiveWeather(HEAVY_RAIN_WEATHER, serverWorld, block, chunk, type, seasonalBiome);
		} else {
			handleInactiveWeather(HEAVY_RAIN_WEATHER, serverWorld, block, chunk, type, seasonalBiome);
		}
	}

	private void handleActiveWeather(Weather weather, WorldServer serverWorld, Block block, Chunk chunk,
			WeatherType type, SeasonalBiome seasonalBiome) {
		BlockData blockData = weather.handleActive(serverWorld, chunk, block, seasonalBiome,
				seasonalBiome.getPrecipitationType(), random);

		if (blockData != null) {
			this.blockQueue.add(new PlacementData(block, blockData));
		}
	}

	private void handleInactiveWeather(Weather weather, WorldServer serverWorld, Block block, Chunk chunk,
			WeatherType type, SeasonalBiome seasonalBiome) {
		BlockData blockData = weather.handleInactive(serverWorld, chunk, block, seasonalBiome,
				seasonalBiome.getPrecipitationType(), random);

		if (blockData != null) {
			this.blockQueue.add(new PlacementData(block, blockData));
		}
	}

	public static record PlacementData(Block block, BlockData data) {
	}

}
