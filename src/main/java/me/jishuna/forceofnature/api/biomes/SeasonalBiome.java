package me.jishuna.forceofnature.api.biomes;

import org.bukkit.configuration.ConfigurationSection;

import com.mojang.serialization.Lifecycle;

import me.jishuna.forceofnature.api.PrecipitationType;
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
	private int numericId;
	private PrecipitationType weather;

	public SeasonalBiome(ConfigurationSection section, String baseBiomeKey,
			IRegistryWritable<BiomeBase> biomeRegistry) {
		BiomeBase baseBiome = biomeRegistry.get(new MinecraftKey(baseBiomeKey));

		if (baseBiome == null)
			return;
		
		BiomeFog baseFog = baseBiome.l();

		MinecraftKey mcKey = new MinecraftKey("fon", section.getString("name"));
		ResourceKey<BiomeBase> key = ResourceKey.a(IRegistry.aO, mcKey);
		
		String grassColorString = section.getString("grass-color");
		String foliageColorString = section.getString("foliage-color");
		String waterColorString = section.getString("water-color");
		String skyColorString = section.getString("sky-color");
		String fogColorString = section.getString("fog-color");
		String underwaterFogColorString = section.getString("underwater-fog-color");
		
		Integer grassColor = grassColorString.equalsIgnoreCase(DEFAULT) ? baseFog.f().orElse(Integer.decode("#FFFFFF")) : Integer.decode(grassColorString);
		Integer foliageColor = foliageColorString.equalsIgnoreCase(DEFAULT) ? baseFog.e().orElse(Integer.decode("#FFFFFF")) : Integer.decode(foliageColorString);
		Integer waterColor = waterColorString.equalsIgnoreCase(DEFAULT) ? baseFog.b() : Integer.decode(waterColorString);
		Integer skyColor = skyColorString.equalsIgnoreCase(DEFAULT) ? baseFog.d() : Integer.decode(skyColorString);
		Integer fogColor = fogColorString.equalsIgnoreCase(DEFAULT) ? baseFog.a() : Integer.decode(fogColorString);
		Integer underwaterFogColor = underwaterFogColorString.equalsIgnoreCase(DEFAULT) ? baseFog.c() : Integer.decode(underwaterFogColorString);

		String weatherString = section.getString("weather-type");
		this.weather = PrecipitationType.valueOf(weatherString.toUpperCase());
		float temp;

		Precipitation precip;

		switch (this.weather) {
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

	public int getNumericId() {
		return numericId;
	}

	public PrecipitationType getWeather() {
		return weather;
	}

}
