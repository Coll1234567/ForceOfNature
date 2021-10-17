package me.jishuna.forceofnature.api.weather;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Snow;

import me.jishuna.forceofnature.api.Utils;
import me.jishuna.forceofnature.api.biomes.SeasonalBiome;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class SnowWeather extends Weather {

	private static final IBlockData SNOW_DATA = Blocks.ck.getBlockData();

	public BlockData handleActive(WorldServer world, Chunk chunk, Block block, SeasonalBiome biome, Random random) {
		if (random.nextInt(100) >= biome.getSnowChance())
			return null;

		Material material = block.getType();

		if ((material.isAir() || material == Material.SNOW)
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

	public BlockData handleInactive(WorldServer world, Chunk chunk, Block block, SeasonalBiome biome, Random random) {
		return null;
	}
}
