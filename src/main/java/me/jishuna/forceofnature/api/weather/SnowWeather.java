package me.jishuna.forceofnature.api.weather;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.Snow;

import me.jishuna.forceofnature.api.PrecipitationType;
import me.jishuna.forceofnature.api.Utils;
import me.jishuna.forceofnature.api.biomes.SeasonalBiome;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class SnowWeather extends Weather {
	private static final BlockData AIR = Material.AIR.createBlockData();
	private static final BlockData ICE = Material.ICE.createBlockData();
	private static final BlockData WATER = Material.WATER.createBlockData();

	private static final IBlockData SNOW_DATA = Blocks.ck.getBlockData();

	public BlockData handleActive(WorldServer world, Chunk chunk, Block block, SeasonalBiome biome,
			PrecipitationType type, Random random) {
		if (random.nextInt(100) >= biome.getFreezeChance())
			return null;

		Material material = block.getType();

		if (material == Material.WATER) {
			Levelled levelled = (Levelled) block.getBlockData();

			if (levelled.getLevel() == 0) {
				return ICE;
			} else {
				return null;
			}
		}

		if ((block.getType().isAir() || material == Material.SNOW)
				&& SNOW_DATA.canPlace(world, Utils.toBlockPos(block.getLocation()))) {
			Snow snow;

			if (material == Material.SNOW) {
				snow = (Snow) block.getBlockData();
				if (snow.getLayers() < biome.getMaxSnowHeight())
					snow.setLayers(Math.min(snow.getLayers() + 1, snow.getMaximumLayers()));
			} else {
				snow = (Snow) Material.SNOW.createBlockData();
			}
			return snow;
		}
		return null;
	}

	public BlockData handleInactive(WorldServer world, Chunk chunk, Block block, SeasonalBiome biome,
			PrecipitationType type, Random random) {
		if (type == PrecipitationType.SNOW || !biome.snowMelts())
			return null;

		Material material = block.getType();

		if (material == Material.ICE) {
			return WATER;
		}

		if (material == Material.SNOW) {
			Snow snow = (Snow) block.getBlockData();
			int layers = snow.getLayers();

			if (layers > 1) {
				snow.setLayers(snow.getLayers() - 1);
				return snow;
			} else {
				return AIR;
			}
		}
		return null;
	}
}
