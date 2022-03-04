package me.jishuna.forceofnature.api.module.thirst;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.FileUtils;
import me.jishuna.forceofnature.api.ItemBuilder;
import me.jishuna.forceofnature.api.PluginKeys;
import me.jishuna.forceofnature.api.StringUtils;
import me.jishuna.forceofnature.api.module.ExtensionConfig;

public class ThirstConfig extends ExtensionConfig {

	private ItemStack saltWaterItem;
	private ItemStack freshWaterItem;
	private ItemStack purifiedWaterItem;

	private Set<String> saltyBiomes;

	@Override
	public void reload(ForceOfNature plugin) {
		FileUtils.loadResource(plugin, "modules/" + ThirstModule.NAME + ".yml").ifPresent(config -> {
			loadDefaults(config);

			this.saltWaterItem = makeWaterItem(config.getConfigurationSection("salt-water"), -10);
			this.freshWaterItem = makeWaterItem(config.getConfigurationSection("fresh-water"), 25);
			this.purifiedWaterItem = makeWaterItem(config.getConfigurationSection("purified-water"), 30);

			this.saltyBiomes = new HashSet<>(config.getStringList("salt-water-biomes"));
		});
	}

	private ItemStack makeWaterItem(ConfigurationSection section, int def) {
		return new ItemBuilder(Material.POTION).flags(ItemFlag.HIDE_POTION_EFFECTS)
				.potionColor(section.getString("color"))
				.persistantData(PluginKeys.THIRST, PersistentDataType.INTEGER, section.getInt("thirst", def))
				.name(StringUtils.colorize(section.getString("name")))
				.lore(StringUtils.colorize(section.getString("lore"))).build();
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
