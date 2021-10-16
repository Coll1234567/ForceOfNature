package me.jishuna.forceofnature.api;

public class WorldData {
	private Season season;
	private int day;
	private int seasonLength;
	private final SeasonalBiomeGroupRegistry registry;

	public WorldData(SeasonalBiomeGroupRegistry registry, int seasonLength) {
		this.registry = registry;
		this.seasonLength = seasonLength;
	}

	public Season getSeason() {
		return season;
	}

	public void setSeason(Season season) {
		this.season = season;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getSeasonLength() {
		return seasonLength;
	}

	public SeasonalBiomeGroupRegistry getRegistry() {
		return registry;
	}

}
