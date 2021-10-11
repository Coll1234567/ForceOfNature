package me.jishuna.forceofnature;

import org.bukkit.configuration.ConfigurationSection;

import com.mojang.serialization.Lifecycle;

import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeBase.Geography;
import net.minecraft.world.level.biome.BiomeFog;

public class SeasonalBiome {
	private int numericId;

	public SeasonalBiome(ConfigurationSection section, IRegistryWritable<BiomeBase> biomeRegistry) {
		BiomeBase baseBiome = biomeRegistry.fromId(0);

		MinecraftKey mcKey = new MinecraftKey("forceofnature", section.getString("name"));
		ResourceKey<BiomeBase> key = ResourceKey.a(IRegistry.aO, mcKey);

		Integer grassColor = Integer.decode(section.getString("grass-color"));
		Integer foliageColor = Integer.decode(section.getString("foliage-color"));
		Integer waterColor = Integer.decode(section.getString("water-color"));
		Integer skyColor = Integer.decode(section.getString("sky-color"));
		Integer fogColor = Integer.decode(section.getString("fog-color"));
		Integer underwaterFogColor = Integer.decode(section.getString("underwater-fog-color"));

		BiomeFog fog = new BiomeFog.a().a(fogColor).b(waterColor).c(underwaterFogColor).d(skyColor).e(foliageColor).f(grassColor).a();

		BiomeBase biome = new BiomeBase.a().a(BiomeBase.Precipitation.c).a(0.0f).b(0.0f).c(0.0f).d(0.0f).a(Geography.a)
				.a(baseBiome.b()).a(baseBiome.e()).a(fog).a();

		biomeRegistry.a(key, biome, Lifecycle.experimental());
		this.numericId = biomeRegistry.getId(biome);

	}

	public int getNumericId() {
		return numericId;
	}

}
