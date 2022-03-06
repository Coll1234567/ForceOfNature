package me.jishuna.forceofnature.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;

public class ItemBuilder {

	private ItemStack item;
	private ItemMeta meta;

	private ItemBuilder() {
	}

	public ItemBuilder(Material material) {
		this(material, 1);
	}

	public ItemBuilder(Material material, int amount) {
		this.item = new ItemStack(material, amount);
		this.meta = this.item.getItemMeta();
	}

	public static ItemBuilder modifyItem(ItemStack item) {
		ItemBuilder builder = new ItemBuilder();
		builder.item = item;
		builder.meta = item.getItemMeta();

		return builder;
	}

	public ItemBuilder enchantment(Enchantment enchantment, int level) {
		this.meta.addEnchant(enchantment, level, true);

		return this;
	}

	public ItemBuilder storedEnchantment(Enchantment enchantment, int level) {
		if (!(this.meta instanceof EnchantmentStorageMeta))
			return this;

		((EnchantmentStorageMeta) this.meta).addStoredEnchant(enchantment, level, true);
		return this;
	}

	public ItemBuilder name(String name) {
		this.meta.setDisplayName(name);

		return this;
	}

	public ItemBuilder lore(List<String> lore) {
		List<String> itemLore = getLore();
		itemLore.addAll(lore);

		meta.setLore(itemLore);

		return this;
	}

	public ItemBuilder lore(String... lore) {
		List<String> itemLore = getLore();
		itemLore.addAll(Arrays.asList(lore));

		meta.setLore(itemLore);

		return this;
	}

	public ItemBuilder setLore(List<String> lore) {
		this.meta.setLore(lore);
		return this;
	}

	public ItemBuilder flags(ItemFlag... flags) {
		this.meta.addItemFlags(flags);

		return this;
	}

	public <T, Z> ItemBuilder persistantData(NamespacedKey key, PersistentDataType<T, Z> type, Z value) {
		this.meta.getPersistentDataContainer().set(key, type, value);

		return this;
	}

	public ItemBuilder modelData(int index) {
		this.meta.setCustomModelData(index);

		return this;
	}

	public ItemBuilder dyeColor(Color color) {
		if (!(this.meta instanceof LeatherArmorMeta))
			return this;

		((LeatherArmorMeta) this.meta).setColor(color);
		return this;
	}

	public ItemBuilder potionColor(String rgb) {
		if (!(this.meta instanceof PotionMeta))
			return this;

		String[] data = rgb.split(",");

		if (data.length != 3) {
			return this;
		}

		int red;
		int green;
		int blue;

		try {
			red = Integer.parseInt(data[0]);
			green = Integer.parseInt(data[1]);
			blue = Integer.parseInt(data[2]);
		} catch (NumberFormatException ex) {
			return this;
		}

		return potionColor(red, green, blue);
	}

	public ItemBuilder potionColor(int red, int green, int blue) {
		if (!(this.meta instanceof PotionMeta))
			return this;

		((PotionMeta) this.meta).setColor(Color.fromRGB(red, green, blue));
		return this;
	}
	
	public ItemBuilder potionData(PotionData data) {
		if (!(this.meta instanceof PotionMeta))
			return this;

		((PotionMeta) this.meta).setBasePotionData(data);
		return this;
	}

	public ItemStack build() {
		ItemStack finalItem = this.item;
		finalItem.setItemMeta(this.meta);

		return finalItem;
	}

	private List<String> getLore() {
		return meta.hasLore() ? meta.getLore() : new ArrayList<>();
	}

}
