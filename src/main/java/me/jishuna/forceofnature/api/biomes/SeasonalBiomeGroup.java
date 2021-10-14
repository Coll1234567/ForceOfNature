package me.jishuna.forceofnature.api.biomes;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import me.jishuna.forceofnature.api.Season;
import me.jishuna.forceofnature.api.WeatherType;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.world.level.biome.BiomeBase;

public class SeasonalBiomeGroup {

	private Set<String> targetBiomes = new HashSet<>();
	private EnumMap<Season, SeasonalBiome> seasonMap = new EnumMap<>(Season.class);

	private WeatherType weather = WeatherType.CLEAR;

	public SeasonalBiomeGroup(ConfigurationSection section, IRegistryWritable<BiomeBase> biomeRegistry) {
		String baseBiome = section.getString("base-biome");
		this.targetBiomes.addAll(section.getStringList("biomes"));

		this.seasonMap.put(Season.SPRING,
				new SeasonalBiome(section.getConfigurationSection("seasons.spring"), baseBiome, biomeRegistry));
		this.seasonMap.put(Season.SUMMER,
				new SeasonalBiome(section.getConfigurationSection("seasons.summer"), baseBiome, biomeRegistry));
		this.seasonMap.put(Season.FALL,
				new SeasonalBiome(section.getConfigurationSection("seasons.fall"), baseBiome, biomeRegistry));
		this.seasonMap.put(Season.WINTER,
				new SeasonalBiome(section.getConfigurationSection("seasons.winter"), baseBiome, biomeRegistry));
	}

	public SeasonalBiome getBiomeForSeason(Season season) {
		return this.seasonMap.get(season);
	}

	public Set<String> getTargetBiomes() {
		return targetBiomes;
	}

	public WeatherType getWeather() {
		return weather;
	}

	public void generateWeather(Season season) {
		SeasonalBiome biome = getBiomeForSeason(season);

		if (biome == null) {
			this.weather = WeatherType.CLEAR;
			return;
		}
		WeatherType type = biome.getRandomWeather();
		this.weather = type;
		System.out.println(type);
	}
}
