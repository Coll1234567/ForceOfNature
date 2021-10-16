package me.jishuna.forceofnature.api;

public class WorldData {
	private Season season;
	private int day;
	private final SeasonalBiomeGroupRegistry registry;

	public WorldData(SeasonalBiomeGroupRegistry registry) {
		this.registry = registry;
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

	public SeasonalBiomeGroupRegistry getRegistry() {
		return registry;
	}

}
