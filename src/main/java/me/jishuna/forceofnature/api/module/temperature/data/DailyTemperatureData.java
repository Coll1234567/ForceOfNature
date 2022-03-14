package me.jishuna.forceofnature.api.module.temperature.data;

public record DailyTemperatureData(double high, double low) {

	public double getTemperature(long time) {
		time = (time + 6000) % 24000;

		return low + (high - low) * (1 - Math.abs(time - 12000f) / 12000f);
	}
}
