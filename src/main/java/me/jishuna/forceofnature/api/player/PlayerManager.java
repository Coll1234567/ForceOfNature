package me.jishuna.forceofnature.api.player;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.google.gson.JsonObject;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.Config;
import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.FONModule;

public class PlayerManager {

	private final ForceOfNature plugin;
	private final Map<UUID, SurvivalPlayer> players = new HashMap<>();

	public PlayerManager(ForceOfNature plugin) {
		this.plugin = plugin;
	}

	public Optional<SurvivalPlayer> getPlayer(UUID id) {
		return Optional.ofNullable(this.players.get(id));
	}

	public Collection<SurvivalPlayer> getPlayers() {
		return this.players.values();
	}

	public void createPlayer(Player player) {
		UUID id = player.getUniqueId();
		File dataFile = new File(this.plugin.getDataFolder() + Config.PLAYER_DATA_PATH, id.toString() + ".yml");
		if (!dataFile.exists()) {
			try {
				dataFile.getParentFile().mkdirs();
				dataFile.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		SurvivalPlayer survivalPlayer = new SurvivalPlayer(player, this.plugin);

		JsonObject json = GsonHandler.readFromFile(dataFile);
		for (FONModule<?, ?> module : this.plugin.getModuleRegistry().getModules()) {
			if (module.getConfig().isEnabled()) {
				survivalPlayer.addExtension(module.createExtension(json));
			}
		}
		this.players.put(id, survivalPlayer);
	}

	public void removePlayer(Player player) {
		SurvivalPlayer survivalPlayer = this.players.remove(player.getUniqueId());
		if (survivalPlayer == null)
			return;

		survivalPlayer.save();
	}

}
