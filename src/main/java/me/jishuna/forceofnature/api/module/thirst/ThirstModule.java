package me.jishuna.forceofnature.api.module.thirst;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonObject;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.FONModule;
import me.jishuna.forceofnature.api.player.PlayerManager;

public class ThirstModule extends FONModule<ThirstConfig> {
	public static final String NAME = "thirst";
	public static final NamespacedKey KEY = new NamespacedKey(JavaPlugin.getPlugin(ForceOfNature.class), NAME);

	private final ThirstEvents events;
	private List<FurnaceRecipe> recipes;

	public ThirstModule(ForceOfNature plugin, ThirstConfig config) {
		super(plugin, config);

		this.events = new ThirstEvents(this);

		addEventHandler(EntityExhaustionEvent.class, events::onExhaustion);
		addEventHandler(PlayerItemConsumeEvent.class, events::onConsume);
		addEventHandler(PlayerInteractEvent.class, EventPriority.HIGH, events::onInteract);
	}

	@Override
	public void reload() {
		super.reload();

		if (recipes != null) {
			recipes.forEach(recipe -> Bukkit.removeRecipe(recipe.getKey()));
		}

		if (!this.getConfig().isEnabled())
			return;

		recipes = new ArrayList<>();

		ItemStack saltWater = this.getConfig().getSaltWaterItem();
		ItemStack freshWater = this.getConfig().getFreshWaterItem();
		ItemStack pureWater = this.getConfig().getPurifiedWaterItem();

		FurnaceRecipe saltWaterRecipe = new FurnaceRecipe(NamespacedKey.fromString("forceofnature:saltwater_recipe"),
				pureWater, new RecipeChoice.ExactChoice(saltWater), 5, 200);
		FurnaceRecipe freshWaterRecipe = new FurnaceRecipe(NamespacedKey.fromString("forceofnature:freshwater_recipe"),
				pureWater, new RecipeChoice.ExactChoice(freshWater), 5, 200);

		Bukkit.addRecipe(saltWaterRecipe);
		Bukkit.addRecipe(freshWaterRecipe);
		recipes.add(saltWaterRecipe);
		recipes.add(freshWaterRecipe);
	}

	@Override
	public NamespacedKey getKey() {
		return KEY;
	}

	@Override
	public ThirstExtension createExtension(JsonObject json) {
		ThirstExtension extension = GsonHandler.deserialize(json.get(NAME), ThirstExtension.class,
				ThirstExtension::new);

		extension.setModule(this);
		return extension;
	}

	@Override
	public void tick(int tick, PlayerManager manager) {
	}
}
