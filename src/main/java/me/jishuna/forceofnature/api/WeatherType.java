package me.jishuna.forceofnature.api;

public enum WeatherType {
	CLEAR(false), RAIN(true), SNOW(true), HEAVY_RAIN(true);

	private final boolean hasDownfall;

	WeatherType(boolean hasDownfall) {
		this.hasDownfall = hasDownfall;
	}

	public boolean hasDownfall() {
		return hasDownfall;
	}
}
