package me.jishuna.forceofnature;

import java.util.HashMap;
import java.util.Map;

public class SeasonalBiomeGroupRegistry {

	private Map<String, SeasonalBiomeGroup> biomeGroups = new HashMap<>();

	public void registerBiomeGroup(SeasonalBiomeGroup group) {
		group.getTargetBiomes().forEach(name -> this.biomeGroups.put(name, group));
	}

	public SeasonalBiomeGroup getBiomeGroup(String key) {
		return this.biomeGroups.get(key);
	}

}
