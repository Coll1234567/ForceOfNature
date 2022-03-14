package me.jishuna.forceofnature.api.player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.Characters;
import me.jishuna.forceofnature.api.Config;
import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.AsyncTickingExtension;
import me.jishuna.forceofnature.api.module.PlayerExtension;
import me.jishuna.forceofnature.api.module.SyncTickingExtension;
import me.jishuna.forceofnature.api.module.temperature.TemperatureExtension;
import me.jishuna.forceofnature.api.module.thirst.ThirstExtension;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class SurvivalPlayer {

	private final Player player;
	private final ForceOfNature plugin;

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends PlayerExtension>, PlayerExtension<?>> extensions = new HashMap<>();

	public SurvivalPlayer(Player player, ForceOfNature plugin) {
		this.player = player;
		this.plugin = plugin;
	}

	public void addExtension(PlayerExtension<?> extension) {
		this.extensions.put(extension.getClass(), extension);
	}

	@SuppressWarnings("unchecked")
	public <T extends PlayerExtension<?>> Optional<T> getExtension(Class<T> type) {
		return (Optional<T>) Optional.ofNullable(this.extensions.get(type));
	}

	public void tick(int tick) {
		this.extensions.values().forEach(extension -> {
			if (extension instanceof SyncTickingExtension ticking) {
				ticking.tick(this, tick);
			}
		});
	}

	public void tickAsync(int tick) {
		this.extensions.values().forEach(extension -> {
			if (extension instanceof AsyncTickingExtension ticking) {
				ticking.tickAsync(this, tick);
			}
		});
		renderHUD();
	}

	public void renderHUD() {
		TextComponent spaceComponent = new TextComponent(Characters.ACTION_BAR_SPACE);
		spaceComponent.setFont("forceofnature:fonfont");

		List<BaseComponent> components = new ArrayList<>();
		components.add(spaceComponent);

		getExtension(ThirstExtension.class)
				.ifPresent(extension -> components.addAll(extension.getDisplayComponents(this)));
		components.add(new TextComponent(" "));
		getExtension(TemperatureExtension.class)
				.ifPresent(extension -> components.addAll(extension.getDisplayComponents(this)));

		getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, components.toArray(BaseComponent[]::new));

	}

	public void save() {
		JsonObject json = new JsonObject();

		this.extensions.values().forEach(extension -> extension.save(json));

		File dataFile = new File(plugin.getDataFolder() + Config.playerDataPath,
				this.player.getUniqueId().toString() + ".yml");
		GsonHandler.writeToFile(dataFile, json);
	}

	public Player getPlayer() {
		return player;
	}
}
