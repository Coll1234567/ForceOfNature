package me.jishuna.forceofnature.api.biomes;

import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import com.mojang.serialization.Lifecycle;

import me.jishuna.commonlib.random.WeightedRandom;
import me.jishuna.forceofnature.api.PrecipitationType;
import me.jishuna.forceofnature.api.WeatherType;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeBase.Geography;
import net.minecraft.world.level.biome.BiomeBase.Precipitation;
import net.minecraft.world.level.biome.BiomeFog;

public class SeasonalBiome {
	private static final String DEFAULT = "default";

	private WeightedRandom<WeatherType> weatherTypes = new WeightedRandom<>();

	private int numericId;
	private PrecipitationType precipitation;

	private int maxSnowHeight;
	private int freezeChance;
	private int puddleChance;

	private boolean snowMelts;

	public SeasonalBiome(World world, ConfigurationSection section, String baseBiomeKey,
			IRegistryWritable<BiomeBase> biomeRegistry) {
		this.maxSnowHeight = section.getInt("max-snow-height", 5);
		this.freezeChance = section.getInt("freeze-chance", 2);
		this.puddleChance = section.getInt("puddle-chance", 1);
		this.snowMelts = section.getBoolean("snow-melts", true);

		ConfigurationSection weathersection = section.getConfigurationSection("weather");
		if (weathersection != null) {
			for (String key : weathersection.getKeys(false)) {
				WeatherType type = WeatherType.valueOf(key.toUpperCase());
				this.weatherTypes.add(weathersection.getDouble(key, 1.0d), type);
			}
		}

		BiomeBase baseBiome = biomeRegistry.get(new MinecraftKey(baseBiomeKey));

		if (baseBiome == null)
			return;

		BiomeFog baseFog = baseBiome.l();

		MinecraftKey mcKey = new MinecraftKey("fon", world.getName() + "." + section.getString("name"));
		ResourceKey<BiomeBase> key = ResourceKey.a(IRegistry.aO, mcKey);

		String grassColorString = section.getString("grass-color");
		String foliageColorString = section.getString("foliage-color");
		String waterColorString = section.getString("water-color");
		String skyColorString = section.getString("sky-color");
		String fogColorString = section.getString("fog-color");
		String underwaterFogColorString = section.getString("underwater-fog-color");

		Integer grassColor = grassColorString.equalsIgnoreCase(DEFAULT) ? baseFog.f().orElse(Integer.decode("#FFFFFF"))
				: Integer.decode(grassColorString);
		Integer foliageColor = foliageColorString.equalsIgnoreCase(DEFAULT)
				? baseFog.e().orElse(Integer.decode("#FFFFFF"))
				: Integer.decode(foliageColorString);
		Integer waterColor = waterColorString.equalsIgnoreCase(DEFAULT) ? baseFog.b()
				: Integer.decode(waterColorString);
		Integer skyColor = skyColorString.equalsIgnoreCase(DEFAULT) ? baseFog.d() : Integer.decode(skyColorString);
		Integer fogColor = fogColorString.equalsIgnoreCase(DEFAULT) ? baseFog.a() : Integer.decode(fogColorString);
		Integer underwaterFogColor = underwaterFogColorString.equalsIgnoreCase(DEFAULT) ? baseFog.c()
				: Integer.decode(underwaterFogColorString);

		String weatherString = section.getString("precipitation-type");
		this.precipitation = PrecipitationType.valueOf(weatherString.toUpperCase());
		float temp;

		Precipitation precip;

		switch (this.precipitation) {
		case NONE:
		default:
			precip = Precipitation.a;
			temp = 2.0f;
			break;
		case RAIN:
			temp = 0.9f;
			precip = Precipitation.b;
			break;
		case SNOW:
			temp = 0.0f;
			precip = Precipitation.c;
			break;
		}

		BiomeFog fog = new BiomeFog.a().a(fogColor).b(waterColor).c(underwaterFogColor).d(skyColor).e(foliageColor)
				.f(grassColor).a();

		BiomeBase biome = new BiomeBase.a().a(precip).a(0.0f).b(0.0f).c(temp).d(0.0f).a(Geography.a).a(baseBiome.b())
				.a(baseBiome.e()).a(fog).a();

		biomeRegistry.a(key, biome, Lifecycle.experimental());
		this.numericId = biomeRegistry.getId(biome);
	}

	public WeatherType getRandomWeather() {
		return this.weatherTypes.poll();
	}

	public int getNumericId() {
		return numericId;
	}

	public PrecipitationType getPrecipitationType() {
		return precipitation;
	}

	public int getMaxSnowHeight() {
		return maxSnowHeight;
	}

	public int getFreezeChance() {
		return freezeChance;
	}

	public int getPuddleChance() {
		return puddleChance;
	}

	public boolean snowMelts() {
		return snowMelts;
	}

}
