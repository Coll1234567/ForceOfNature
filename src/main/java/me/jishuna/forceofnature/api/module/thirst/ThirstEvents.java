package me.jishuna.forceofnature.api.module.thirst;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExhaustionEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import me.jishuna.forceofnature.api.PluginKeys;

public class ThirstEvents {

	private final ThirstModule module;

	public ThirstEvents(ThirstModule thirstModule) {
		this.module = thirstModule;
	}

	protected void onInteract(PlayerInteractEvent event) {
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
			return;

		ItemStack item = event.getItem();
		if (item == null || item.getType() != Material.GLASS_BOTTLE)
			return;

		event.setCancelled(true);

		Player player = event.getPlayer();

		Block target = player.getTargetBlockExact(5, FluidCollisionMode.ALWAYS);
		if (target == null)
			return;

		if (target.getType() == Material.WATER) {
			item.setAmount(item.getAmount() - 1);

			if (this.module.getConfig().isSaltyBiome(target.getBiome())) {
				player.getInventory().addItem(this.module.getConfig().getSaltWaterItem());
			} else {
				player.getInventory().addItem(this.module.getConfig().getFreshWaterItem());
			}
		}
	}

	protected void onExhaustion(EntityExhaustionEvent event) {
		HumanEntity entity = event.getEntity();

		if (entity.getExhaustion() + event.getExhaustion() > 4.0) {
			module.getSurvivalPlayer(entity.getUniqueId()).ifPresent(player -> {
				player.getExtension(ThirstExtension.class).ifPresent(extension -> extension.takeThirst(0.5f));
			});
		}
	}

	protected void onConsume(PlayerItemConsumeEvent event) {
		ItemStack item = event.getItem();

		float materialThirst = module.getConfig().getMaterialThirst(event.getItem().getType());

		if (materialThirst != 0) {
			module.getSurvivalPlayer(event.getPlayer()).ifPresent(player -> player.getExtension(ThirstExtension.class)
					.ifPresent(extension -> extension.giveThirst(materialThirst)));
			return;
		}

		if (!item.hasItemMeta())
			return;

		PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
		if (!container.has(PluginKeys.THIRST, PersistentDataType.INTEGER))
			return;

		int thirst = container.get(PluginKeys.THIRST, PersistentDataType.INTEGER);

		module.getSurvivalPlayer(event.getPlayer()).ifPresent(player -> player.getExtension(ThirstExtension.class)
				.ifPresent(extension -> extension.giveThirst(thirst)));
	}

}
