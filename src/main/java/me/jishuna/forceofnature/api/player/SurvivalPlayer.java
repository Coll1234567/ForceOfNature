package me.jishuna.forceofnature.api.player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.Config;
import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.PlayerExtension;

public class SurvivalPlayer {

	private final Player player;
	private final ForceOfNature plugin;

	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends PlayerExtension>, PlayerExtension> extensions = new HashMap<>();

	public SurvivalPlayer(Player player, ForceOfNature plugin) {
		this.player = player;
		this.plugin = plugin;
	}

	public void addExtension(PlayerExtension<?> extension) {
		this.extensions.put(extension.getClass(), extension);
	}

	public <T extends PlayerExtension<?>> Optional<T> getExtension(Class<T> type) {
		return Optional.ofNullable(this.extensions.get(type));
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
