package me.jishuna.forceofnature.api.module.temperature.data;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import me.jishuna.forceofnature.api.module.seasons.Season;

public class ClimateTemperatureData {

	private final List<String> biomes;
	private final Map<Season, SeasonTemperatureData> seasonData = new EnumMap<>(Season.class);

	public ClimateTemperatureData(List<String> biomes) {
		this.biomes = biomes;
	}

	public void addSeasonData(Season season, SeasonTemperatureData seasonTemperatureData) {
		this.seasonData.put(season, seasonTemperatureData);
	}

	public SeasonTemperatureData getSeasonData(Season season) {
		return this.seasonData.get(season);
	}

	public List<String> getBiomes() {
		return biomes;
	}

}
