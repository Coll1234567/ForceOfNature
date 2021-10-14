package me.jishuna.forceofnature;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.api.PrecipitationType;
import me.jishuna.forceofnature.api.WeatherType;
import me.jishuna.forceofnature.api.biomes.SeasonalBiome;
import me.jishuna.forceofnature.api.biomes.SeasonalBiomeGroup;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class SnowRunnable extends BukkitRunnable {
	private static final IRegistryWritable<BiomeBase> BIOME_REGISTRY = ((CraftServer) Bukkit.getServer()).getServer().l
			.b(IRegistry.aO);
	private static final IBlockData SNOW_DATA = Blocks.ck.getBlockData();

	private static final BlockData AIR = Material.AIR.createBlockData();
	private static final BlockData ICE = Material.ICE.createBlockData();
	private static final BlockData WATER = Material.WATER.createBlockData();

	private static final Queue<PlacementData> blockQueue = new ConcurrentLinkedQueue<>();

	private final ForceOfNature plugin;
	private final Random random = new Random();

	public SnowRunnable(ForceOfNature plugin) {
		this.plugin = plugin;

		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			while (!blockQueue.isEmpty()) {
				PlacementData data = blockQueue.poll();
				data.block.setBlockData(data.data);
			}
		}, 0, 20);
	}

	@Override
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {

			Chunk middle = player.getLocation().getChunk();
			World world = player.getWorld();
			int distance = world.getViewDistance();

			for (int i = -distance; i <= distance; i++) {
				for (int j = -distance; j <= distance; j++) {

					int x = (middle.getX() + i) * 16 + random.nextInt(16);
					int z = (middle.getZ() + j) * 16 + random.nextInt(16);
					int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING);

					processLiquid(world, x, y, z);
					processBlock(world, x, y + 1, z);
				}
			}
		}
	}

	private void processLiquid(World world, int x, int y, int z) {
		WorldServer serverWorld = ((CraftWorld) world).getHandle();
		BiomeBase biome = serverWorld.getBiome(x >> 2, y, z >> 2);
		String key = BIOME_REGISTRY.getKey(biome).toString();

		SeasonalBiomeGroup group = plugin.getGroupRegistry().getBiomeGroup(key);

		if (group == null)
			return;

		SeasonalBiome seasonalBiome = group.getBiomeForSeason(plugin.getSeasonManager().getSeason(world));

		if (seasonalBiome == null)
			return;

		PrecipitationType type = seasonalBiome.getWeather();
		Block block = world.getBlockAt(x, y, z);

		if (type == PrecipitationType.SNOW) {
			if (group.getWeather() != WeatherType.DOWNFALL || random.nextInt(100) >= seasonalBiome.getFreezeChance() || block.getType() != Material.WATER)
				return;
			Levelled levelled = (Levelled) block.getBlockData();

			if (levelled.getLevel() == 0)
				blockQueue.add(new PlacementData(block, ICE));
		} else {
			if (random.nextInt(100) >= seasonalBiome.getMeltChance() || block.getType() != Material.ICE)
				return;

			blockQueue.add(new PlacementData(block, WATER));
		}
	}

	private void processBlock(World world, int x, int y, int z) {
		WorldServer serverWorld = ((CraftWorld) world).getHandle();
		BiomeBase biome = serverWorld.getBiome(x >> 2, y, z >> 2);
		String key = BIOME_REGISTRY.getKey(biome).toString();

		SeasonalBiomeGroup group = plugin.getGroupRegistry().getBiomeGroup(key);

		if (group == null)
			return;

		SeasonalBiome seasonalBiome = group.getBiomeForSeason(plugin.getSeasonManager().getSeason(world));

		if (seasonalBiome == null)
			return;

		PrecipitationType type = seasonalBiome.getWeather();
		Block block = world.getBlockAt(x, y, z);
		BlockPosition pos = new BlockPosition(x, y, z);

		if (type == PrecipitationType.SNOW) {
			if (group.getWeather() != WeatherType.DOWNFALL ||  random.nextInt(100) >= seasonalBiome.getFreezeChance())
				return;
			if ((block.getType().isAir() || block.getType() == Material.SNOW) && SNOW_DATA.canPlace(serverWorld, pos)) {
				Snow snow;

				if (block.getType() == Material.SNOW) {
					snow = (Snow) block.getBlockData();
					if (snow.getLayers() < seasonalBiome.getMaxSnowHeight())
						snow.setLayers(Math.min(snow.getLayers() + 1, snow.getMaximumLayers()));
				} else {
					snow = (Snow) Material.SNOW.createBlockData();

					blockQueue.add(new PlacementData(block, snow));
				}
			}
		} else {
			if (random.nextInt(100) >= seasonalBiome.getMeltChance() || block.getType() != Material.SNOW)
				return;
			Snow snow = (Snow) block.getBlockData();
			int layers = snow.getLayers();

			if (layers > 1) {
				snow.setLayers(snow.getLayers() - 1);
				blockQueue.add(new PlacementData(block, snow));
			} else {
				blockQueue.add(new PlacementData(block, AIR));
			}
		}
	}

	public static record PlacementData(Block block, BlockData data) {
	}

}
