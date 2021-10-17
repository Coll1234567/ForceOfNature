package me.jishuna.forceofnature.api.weather;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;

import me.jishuna.forceofnature.api.biomes.SeasonalBiome;
import net.minecraft.server.level.WorldServer;

public class HeavyRainWeather extends Weather {
	private static final BlockData AIR = Material.AIR.createBlockData();
	private static final BlockData WATER = Material.WATER.createBlockData();

	static {
		((Levelled) WATER).setLevel(7);
	}

	public BlockData handleActive(WorldServer world, Chunk chunk, Block block, SeasonalBiome biome, Random random) {
		if (random.nextInt(100) >= biome.getPuddleChance())
			return null;

		Material material = block.getType();

		if (material.isAir() && block.getRelative(BlockFace.DOWN).getType().isOccluding()) {
			return WATER;
		}
		return null;
	}

	public BlockData handleInactive(WorldServer world, Chunk chunk, Block block, SeasonalBiome biome, Random random) {

		Material material = block.getType();

		if (material == Material.WATER) {
			Levelled levelled = (Levelled) block.getBlockData();

			if (levelled.getLevel() == 7) {
				return AIR;
			} else {
				return null;
			}
		}
		return null;
	}
}
