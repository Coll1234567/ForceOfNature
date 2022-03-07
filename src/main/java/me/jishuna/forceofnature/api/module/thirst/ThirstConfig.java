package me.jishuna.forceofnature.api.module.thirst;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.ItemBuilder;
import me.jishuna.forceofnature.api.PluginKeys;
import me.jishuna.forceofnature.api.module.ExtensionConfig;
import me.jishuna.forceofnature.api.utils.FileUtils;
import me.jishuna.forceofnature.api.utils.StringUtils;

public class ThirstConfig extends ExtensionConfig {

	private ItemStack saltWaterItem;
	private ItemStack freshWaterItem;
	private ItemStack purifiedWaterItem;

	private Set<String> saltyBiomes;
	private Map<Material, Float> thirstMaterials;

	@Override
	public void reload(ForceOfNature plugin) {
		FileUtils.loadResource(plugin, "modules/" + ThirstModule.NAME + ".yml").ifPresent(config -> {
			loadDefaults(config);

			this.saltWaterItem = makeWaterItem(config.getConfigurationSection("salt-water"), -1, 33);
			this.freshWaterItem = makeWaterItem(config.getConfigurationSection("fresh-water"), 2, 33);
			this.purifiedWaterItem = makeWaterItem(config.getConfigurationSection("purified-water"), 3, 0);

			this.saltyBiomes = new HashSet<>(config.getStringList("salt-water-biomes"));

			loadThirstMaterials(config.getConfigurationSection("thirst-items"));
		});
	}

	private void loadThirstMaterials(ConfigurationSection section) {
		this.thirstMaterials = new HashMap<>();
		for (String key : section.getKeys(false)) {
			Material material = Material.matchMaterial(key.toUpperCase());
			if (material == null)
				continue;

			thirstMaterials.put(material, (float) section.getDouble(key, 1));
		}
	}

	private ItemStack makeWaterItem(ConfigurationSection section, int defThirst, int defSick) {
		return new ItemBuilder(Material.POTION).flags(ItemFlag.HIDE_POTION_EFFECTS)
				.potionData(new PotionData(PotionType.WATER)).potionColor(section.getString("color"))
				.persistantData(PluginKeys.THIRST, PersistentDataType.INTEGER, section.getInt("thirst", defThirst))
				.persistantData(PluginKeys.SICK_CHANCE, PersistentDataType.FLOAT,
						(float) section.getDouble("sickness-chance", defSick))
				.name(StringUtils.colorize(section.getString("name")))
				.lore(StringUtils.colorize(section.getStringList("lore"))).build();
	}

	public float getMaterialThirst(Material material) {
		return this.thirstMaterials.getOrDefault(material, 0f);
	}

	public boolean isSaltyBiome(Biome biome) {
		return isSaltyBiome(biome.getKey().toString());
	}

	public boolean isSaltyBiome(String string) {
		return this.saltyBiomes.contains(string);
	}

	public ItemStack getSaltWaterItem() {
		return saltWaterItem.clone();
	}

	public ItemStack getFreshWaterItem() {
		return freshWaterItem.clone();
	}

	public ItemStack getPurifiedWaterItem() {
		return purifiedWaterItem.clone();
	}
}
