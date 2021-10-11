package me.jishuna.forceofnature;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import net.minecraft.core.IRegistryWritable;
import net.minecraft.world.level.biome.BiomeBase;

public class SeasonalBiomeGroup {

	private Set<String> targetBiomes = new HashSet<>();
	private EnumMap<Season, SeasonalBiome> seasonMap = new EnumMap<>(Season.class);

	public SeasonalBiomeGroup(ConfigurationSection section, IRegistryWritable<BiomeBase> biomeRegistry) {
		this.targetBiomes.addAll(section.getStringList("biomes"));

		this.seasonMap.put(Season.SPRING,
				new SeasonalBiome(section.getConfigurationSection("seasons.spring"), biomeRegistry));
		this.seasonMap.put(Season.SUMMER,
				new SeasonalBiome(section.getConfigurationSection("seasons.summer"), biomeRegistry));
		this.seasonMap.put(Season.FALL,
				new SeasonalBiome(section.getConfigurationSection("seasons.fall"), biomeRegistry));
		this.seasonMap.put(Season.WINTER,
				new SeasonalBiome(section.getConfigurationSection("seasons.winter"), biomeRegistry));
	}
	
	public SeasonalBiome getBiomeForSeason(Season season) {
		return this.seasonMap.get(season);
	}

	public Set<String> getTargetBiomes() {
		return targetBiomes;
	}
}
