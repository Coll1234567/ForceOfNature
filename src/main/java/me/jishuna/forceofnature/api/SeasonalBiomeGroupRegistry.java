package me.jishuna.forceofnature.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.jishuna.forceofnature.api.biomes.SeasonalBiomeGroup;

public class SeasonalBiomeGroupRegistry {

	private List<SeasonalBiomeGroup> groups = new ArrayList<>();
	private Map<String, SeasonalBiomeGroup> groupMap = new HashMap<>();

	public void registerBiomeGroup(SeasonalBiomeGroup group) {
		group.getTargetBiomes().forEach(name -> this.groupMap.put(name, group));
		this.groups.add(group);
	}

	public SeasonalBiomeGroup getBiomeGroup(String key) {
		return this.groupMap.get(key);
	}
	
	public void onNewDay(Season season) {
		for (SeasonalBiomeGroup group : this.groups) {
			group.generateWeather(season);
		}
	}

}
