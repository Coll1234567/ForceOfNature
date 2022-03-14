package me.jishuna.forceofnature.api.module.temperature;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;

import io.netty.util.internal.ThreadLocalRandom;
import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.FONModule;
import me.jishuna.forceofnature.api.module.seasons.Season;
import me.jishuna.forceofnature.api.module.temperature.data.ClimateTemperatureData;
import me.jishuna.forceofnature.api.module.temperature.data.DailyTemperatureData;
import me.jishuna.forceofnature.api.module.temperature.data.SeasonTemperatureData;
import me.jishuna.forceofnature.api.utils.MathUtils;

public class TemperatureModule extends FONModule<TemperatureConfig> {
	public static final String NAME = "temperature";
	public static final NamespacedKey KEY = new NamespacedKey(JavaPlugin.getPlugin(ForceOfNature.class), NAME);

	private volatile DailyTemperatureData defaultData;
	private final Map<String, DailyTemperatureData> temperatureData = new ConcurrentHashMap<>();
	private final BiomeTemperatureConfig biomeConfig;

	public TemperatureModule(ForceOfNature plugin, TemperatureConfig config) {
		super(plugin, config);
		this.biomeConfig = new BiomeTemperatureConfig();
	}

	@Override
	public void reload() {
		super.reload();
		this.biomeConfig.reload(getPlugin());
	}

	public void handleDayChange(long day) {
		Season current = Season.getSeason(day);
		Season other;

		int daysElapsed = (int) (day % 20);
		float progress = daysElapsed / 20f;

		if (progress < 0.5) {
			progress += 0.5;
			other = current;
			current = current.getPreviousSeason();
		} else {
			progress -= 0.5;
			other = current.getNextSeason();
		}

		this.defaultData = calculateTemperatures(this.biomeConfig.getDefaultData(), current, other, progress);

		for (ClimateTemperatureData data : this.biomeConfig.getClimates()) {
			DailyTemperatureData dailyData = calculateTemperatures(data, current, other, progress);

			for (String biome : data.getBiomes()) {
				this.temperatureData.put(biome, dailyData);
			}
		}
	}

	public DailyTemperatureData calculateTemperatures(ClimateTemperatureData data, Season current, Season other,
			float progress) {
		SeasonTemperatureData currentData = data.getSeasonData(current);
		ThreadLocalRandom random = ThreadLocalRandom.current();

		if (other == null || other == current) {
			double high = random.nextDouble(currentData.dailyLow(), currentData.dailyHigh());
			double low = random.nextDouble(currentData.nightlyLow(), currentData.nightlyHigh());

			return new DailyTemperatureData(high, low);
		}

		SeasonTemperatureData otherData = data.getSeasonData(other);

		double dailyHigh = MathUtils.interpolate(currentData.dailyHigh(), otherData.dailyHigh(), progress);
		double dailyLow = MathUtils.interpolate(currentData.dailyLow(), otherData.dailyLow(), progress);
		double high = random.nextDouble(dailyLow, dailyHigh);

		double nightlyHigh = MathUtils.interpolate(currentData.nightlyHigh(), otherData.nightlyHigh(), progress);
		double nightlyLow = MathUtils.interpolate(currentData.nightlyLow(), otherData.nightlyLow(), progress);
		double low = random.nextDouble(nightlyLow, nightlyHigh);

		return new DailyTemperatureData(high, low);
	}

	public double getRawTemperature(String key, long time) {
		DailyTemperatureData data = this.temperatureData.getOrDefault(key, defaultData);
		return data.getTemperature(time);
	}

	public double getLocationTemperature(Location location, boolean checkBlocks) {
		World world = location.getWorld();
		Biome biome = location.getBlock().getBiome();

		double temp = getRawTemperature(biome.getKey().toString(), world.getTime());

		if (world.getClearWeatherDuration() == 0 && world.getWeatherDuration() > 0) {
			temp += this.getConfig().getRainModifier();
		}

		return temp;
	}

	@Override
	public NamespacedKey getKey() {
		return KEY;
	}

	@Override
	public TemperatureExtension createExtension(JsonObject json) {
		TemperatureExtension extension = GsonHandler.deserialize(json.get(NAME), TemperatureExtension.class,
				TemperatureExtension::new);

		extension.setModule(this);
		return extension;
	}
}
