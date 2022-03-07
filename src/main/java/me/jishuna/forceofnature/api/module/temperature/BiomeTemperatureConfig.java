package me.jishuna.forceofnature.api.module.temperature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.module.ExtensionConfig;
import me.jishuna.forceofnature.api.module.seasons.Season;
import me.jishuna.forceofnature.api.module.temperature.data.ClimateTemperatureData;
import me.jishuna.forceofnature.api.module.temperature.data.SeasonTemperatureData;
import me.jishuna.forceofnature.api.utils.FileUtils;

public class BiomeTemperatureConfig extends ExtensionConfig {

	private List<ClimateTemperatureData> biomeData = Collections.synchronizedList(new ArrayList<>());
	private volatile ClimateTemperatureData defaultData;

	@Override
	public void reload(ForceOfNature plugin) {
		FileUtils.loadResource(plugin, "modules/temperature/biomes.yml").ifPresent(config -> {
			this.defaultData = loadDefault(config.getConfigurationSection("default"));

			this.biomeData.clear();

			for (Map<?, ?> map : config.getMapList("biomes")) {
				this.biomeData.add(loadBiomeData((Map<String, Object>) map));
			}
		});
	}

	private ClimateTemperatureData loadDefault(ConfigurationSection section) {
		Map<String, Object> map = section.getValues(false);
		ClimateTemperatureData data = new ClimateTemperatureData(Collections.emptyList());

		for (Season season : Season.values()) {
			ConfigurationSection seasonSection = (ConfigurationSection) map.get(season.toString());
			double dailyHigh = seasonSection.getDouble("daily-high");
			double dailyLow = seasonSection.getDouble("daily-low");
			double nightlyHigh = seasonSection.getDouble("nightly-high");
			double nightlyLow = seasonSection.getDouble("nightly-low");

			data.addSeasonData(season, new SeasonTemperatureData(dailyHigh, dailyLow, nightlyHigh, nightlyLow));
		}
		return data;
	}

	//TODO fix unchecked casts
	private ClimateTemperatureData loadBiomeData(Map<String, Object> map) {
		ClimateTemperatureData data = new ClimateTemperatureData((List<String>) map.get("biome-list"));

		for (Season season : Season.values()) {
			Map<String, Number> seasonMap = (Map<String, Number>) map.get(season.toString());
			SeasonTemperatureData seasonData = this.defaultData.getSeasonData(season);
			Number dailyHigh = seasonMap.getOrDefault("daily-high", seasonData.dailyHigh());
			Number dailyLow = seasonMap.getOrDefault("daily-low", seasonData.dailyLow());
			Number nightlyHigh = seasonMap.getOrDefault("nightly-high", seasonData.nightlyHigh());
			Number nightlyLow = seasonMap.getOrDefault("nightly-low", seasonData.nightlyLow());

			data.addSeasonData(season, new SeasonTemperatureData(dailyHigh.doubleValue(), dailyLow.doubleValue(), nightlyHigh.doubleValue(), nightlyLow.doubleValue()));
		}
		return data;
	}

	public ClimateTemperatureData getDefaultData() {
		return defaultData;
	}

	public List<ClimateTemperatureData> getClimates() {
		return biomeData;
	}
}
