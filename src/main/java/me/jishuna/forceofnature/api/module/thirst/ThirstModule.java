package me.jishuna.forceofnature.api.module.thirst;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.google.gson.JsonObject;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.PluginKeys;
import me.jishuna.forceofnature.api.module.FONModule;
import me.jishuna.forceofnature.api.player.PlayerManager;
import me.jishuna.forceofnature.api.player.SurvivalPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ThirstModule extends FONModule<ThirstConfig, ThirstExtension> {
	public static final String NAME = "thirst";

	private List<FurnaceRecipe> recipes;

	public ThirstModule(ForceOfNature plugin, ThirstConfig config) {
		super(plugin, config);

		addEventHandler(EntityExhaustionEvent.class, this::onExhaustion);
		addEventHandler(PlayerItemConsumeEvent.class, this::onConsume);
		addEventHandler(PlayerInteractEvent.class, EventPriority.HIGH, this::onInteract);
	}

	@Override
	public void reload() {
		super.reload();

		if (recipes != null) {
			recipes.forEach(recipe -> Bukkit.removeRecipe(recipe.getKey()));
		}
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

	private void onExhaustion(EntityExhaustionEvent event) {
		HumanEntity entity = event.getEntity();

		if (entity.getExhaustion() + event.getExhaustion() > 4.0) {
			getSurvivalPlayer(entity.getUniqueId()).ifPresent(player -> {
				player.getExtension(ThirstExtension.class).ifPresent(extension -> extension.takeThirst(1));
				entity.getInventory().addItem(this.getConfig().getSaltWaterItem());
				entity.getInventory().addItem(this.getConfig().getFreshWaterItem());
				entity.getInventory().addItem(this.getConfig().getPurifiedWaterItem());
			});
		}
	}

	private void onConsume(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();
		if (!item.hasItemMeta())
			return;

		PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
		if (!container.has(PluginKeys.THIRST, PersistentDataType.INTEGER))
			return;

		int thirst = container.get(PluginKeys.THIRST, PersistentDataType.INTEGER);

		getSurvivalPlayer(event.getPlayer()).ifPresent(player -> {
			player.getExtension(ThirstExtension.class).ifPresent(extension -> extension.giveThirst(thirst));
		});
	}

	private void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
			return;

		ItemStack item = event.getItem();
		if (item == null || item.getType() != Material.GLASS_BOTTLE)
			return;

		event.setCancelled(true);

		Player player = event.getPlayer();

		Block target = player.getTargetBlockExact(4, FluidCollisionMode.ALWAYS);
		if (target == null)
			return;

		if (target.getType() == Material.WATER) {
			item.setAmount(item.getAmount() - 1);

			if (this.getConfig().isSaltyBiome(target.getBiome())) {
				player.getInventory().addItem(this.getConfig().getSaltWaterItem());
			} else {
				player.getInventory().addItem(this.getConfig().getFreshWaterItem());
			}
		}
	}

	@Override
	public ThirstExtension createExtension(JsonObject json) {
		ThirstExtension extension = GsonHandler.deserialize(json.get(NAME), ThirstExtension.class,
				() -> new ThirstExtension());

		extension.setConfig(this.getConfig());

		return extension;
	}

	@Override
	public void tick(int tick, PlayerManager manager) {
		if (tick % 10 == 0) {
			for (SurvivalPlayer player : manager.getPlayers()) {
				player.getExtension(ThirstExtension.class).ifPresent(extension -> {
					player.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR,
							new TextComponent("Thirst: " + extension.getThirst()));
				});
			}
		}
	}
}
