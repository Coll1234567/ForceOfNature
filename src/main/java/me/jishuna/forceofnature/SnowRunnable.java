package me.jishuna.forceofnature;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.HeightMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.api.PrecipitationType;
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

	private final ForceOfNature plugin;

	public SnowRunnable(ForceOfNature plugin) {
		this.plugin = plugin;
	}

	@Override
	public void run() {
		Random random = ThreadLocalRandom.current();

		for (Player player : Bukkit.getOnlinePlayers()) {
			final Map<Block, Material> changeMap = new HashMap<>();

			Chunk middle = player.getLocation().getChunk();
			World world = player.getWorld();
			int distance = world.getViewDistance();

			for (int i = -distance; i <= distance; i++) {
				for (int j = -distance; j <= distance; j++) {
					if (random.nextInt(20) > 0)
						continue;

					int x = (middle.getX() + i) * 16 + random.nextInt(16);
					int z = (middle.getZ() + j) * 16 + random.nextInt(16);
					int y = world.getHighestBlockYAt(x, z, HeightMap.MOTION_BLOCKING) + 1;
					WorldServer serverWorld = ((CraftWorld) world).getHandle();
					BiomeBase biome = serverWorld.getBiome(x >> 2, y, z >> 2);
					String key = BIOME_REGISTRY.getKey(biome).toString();

					SeasonalBiomeGroup group = plugin.getGroupRegistry().getBiomeGroup(key);

					if (group == null)
						continue;

					SeasonalBiome seasonalBiome = group.getBiomeForSeason(plugin.getSeasonManager().getSeason(world));

					if (seasonalBiome == null)
						continue;

					PrecipitationType type = seasonalBiome.getWeather();
					Block block = world.getBlockAt(x, y, z);
					BlockPosition pos = new BlockPosition(x, y, z);

					if (type == PrecipitationType.SNOW && block.getType().isAir()
							&& SNOW_DATA.canPlace(serverWorld, pos)) {
						changeMap.put(block, Material.SNOW);
					}

					if (type != PrecipitationType.SNOW && block.getType() == Material.SNOW) {
						changeMap.put(block, Material.AIR);
					}
				}
			}

			Bukkit.getScheduler().runTask(plugin, () -> {
				changeMap.forEach((block, type) -> block.setType(type));
			});
		}
	}

}
