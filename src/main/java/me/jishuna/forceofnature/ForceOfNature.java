package me.jishuna.forceofnature;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.forceofnature.api.event.EventManager;
import me.jishuna.forceofnature.api.module.ModuleRegistry;
import me.jishuna.forceofnature.api.player.PlayerManager;

public class ForceOfNature extends JavaPlugin {

	private ModuleRegistry moduleRegistry;
	private EventManager eventManager;
	private PlayerManager playerManager;

	@Override
	public void onEnable() {
		this.eventManager = new EventManager(this);
		this.moduleRegistry = new ModuleRegistry(this);
		this.playerManager = new PlayerManager(this);

		Bukkit.getPluginManager().registerEvents(new PlayerListener(playerManager), this);

		new PlayerTickRunnable(this).runTaskTimer(this, 0, 1);
	}

	public ModuleRegistry getModuleRegistry() {
		return moduleRegistry;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public PlayerManager getPlayerManager() {
		return playerManager;
	}

}
