package me.jishuna.forceofnature.api.weather;

import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import me.jishuna.forceofnature.api.PrecipitationType;
import me.jishuna.forceofnature.api.biomes.SeasonalBiome;
import net.minecraft.server.level.WorldServer;

public abstract class Weather {
	public abstract BlockData handleActive(WorldServer world, Chunk chunk, Block block, SeasonalBiome biome,
			PrecipitationType type, Random random);

	public abstract BlockData handleInactive(WorldServer world, Chunk chunk, Block block, SeasonalBiome biome,
			PrecipitationType type, Random random);
}
