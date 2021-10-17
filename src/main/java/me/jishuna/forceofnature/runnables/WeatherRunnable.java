package me.jishuna.forceofnature.runnables;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Snow;
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

	private static final BlockData AIR = Material.AIR.createBlockData();
	private static final BlockData ICE = Material.ICE.createBlockData();
	private static final BlockData WATER = Material.WATER.createBlockData();

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
				int y2 = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);

				BiomeBase biome = serverWorld.getBiome(x >> 2, y, z >> 2);
				String key = BIOME_REGISTRY.getKey(biome).toString();

				SeasonalBiomeGroup group = data.getRegistry().getBiomeGroup(key);

				if (group == null)
					continue;

				SeasonalBiome seasonalBiome = group.getBiomeForSeason(data.getSeason());

				if (seasonalBiome == null)
					continue;

				Block blockA = world.getBlockAt(x, y + 1, z);
				Block blockB = world.getBlockAt(x, y, z);
				Block blockC = world.getBlockAt(x, y2 + 1, z);

				if (seasonalBiome.getTemperature() <= 0.15f) {
					freezeBlocks(blockB, seasonalBiome);
				} else {
					thawBlocks(blockA, blockB, seasonalBiome);
				}

				WeatherType type = group.getWeather();
				processWeather(serverWorld, blockA, chunk, type, seasonalBiome);
				processWeather(serverWorld, blockB, chunk, type, seasonalBiome);

				if (blockC.getY() != blockA.getY())
					processWeather(serverWorld, blockC, chunk, type, seasonalBiome);
			}
		}
		this.toCheck.lazySet(Math.max(10, this.blockQueue.size() / 5));
	}

	private void freezeBlocks(Block block, SeasonalBiome biome) {
		if (random.nextInt(100) >= biome.getIceChance())
			return;

		if (block.getType() == Material.WATER) {
			Levelled levelled = (Levelled) block.getBlockData();

			if (levelled.getLevel() == 0)
				this.blockQueue.add(new PlacementData(block, ICE));
		}
	}

	private void thawBlocks(Block blockA, Block blockB, SeasonalBiome biome) {
		if (blockB.getType() == Material.ICE) {
			this.blockQueue.add(new PlacementData(blockB, WATER));
		}

		World world = blockA.getWorld();
		int x = blockA.getX();
		int z = blockA.getZ();
		int y2 = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING_NO_LEAVES);
		Block blockC = world.getBlockAt(x, y2 + 1, z);

		List<Block> blocks = new ArrayList<>();
		blocks.add(blockA);
		if (blockC.getY() != blockA.getY())
			blocks.add(blockC);

		for (Block block : blocks) {
			if (block.getType() == Material.SNOW) {
				Snow snow = (Snow) block.getBlockData();
				int layers = snow.getLayers();

				if (layers > 1) {
					snow.setLayers(snow.getLayers() - 1);
					this.blockQueue.add(new PlacementData(block, snow));
				} else {
					this.blockQueue.add(new PlacementData(block, AIR));
				}
			}
		}
	}

	private void processWeather(WorldServer serverWorld, Block block, Chunk chunk, WeatherType type,
			SeasonalBiome seasonalBiome) {

		if (type == WeatherType.SNOW) {
			handleActiveWeather(SNOW_WEATHER, serverWorld, block, chunk, seasonalBiome);
		} else {
			handleInactiveWeather(SNOW_WEATHER, serverWorld, block, chunk, seasonalBiome);
		}

		if (type == WeatherType.HEAVY_RAIN) {
			handleActiveWeather(HEAVY_RAIN_WEATHER, serverWorld, block, chunk, seasonalBiome);
		} else {
			handleInactiveWeather(HEAVY_RAIN_WEATHER, serverWorld, block, chunk, seasonalBiome);
		}
	}

	private void handleActiveWeather(Weather weather, WorldServer serverWorld, Block block, Chunk chunk,
			SeasonalBiome seasonalBiome) {
		BlockData blockData = weather.handleActive(serverWorld, chunk, block, seasonalBiome, random);

		if (blockData != null) {
			this.blockQueue.add(new PlacementData(block, blockData));
		}
	}

	private void handleInactiveWeather(Weather weather, WorldServer serverWorld, Block block, Chunk chunk,
			SeasonalBiome seasonalBiome) {
		BlockData blockData = weather.handleInactive(serverWorld, chunk, block, seasonalBiome, random);

		if (blockData != null) {
			this.blockQueue.add(new PlacementData(block, blockData));
		}
	}

	public static record PlacementData(Block block, BlockData data) {
	}

}
