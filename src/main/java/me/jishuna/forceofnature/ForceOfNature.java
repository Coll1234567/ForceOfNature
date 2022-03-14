package me.jishuna.forceofnature;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.forceofnature.api.event.EventManager;
import me.jishuna.forceofnature.api.module.ModuleRegistry;
import me.jishuna.forceofnature.api.player.PlayerManager;
import me.jishuna.forceofnature.api.player.SurvivalPlayer;

public class ForceOfNature extends JavaPlugin {

	private ModuleRegistry moduleRegistry;
	private EventManager eventManager;
	private PlayerManager playerManager;

	@Override
	public void onEnable() {
		this.eventManager = new EventManager(this);

		this.moduleRegistry = new ModuleRegistry();
		this.moduleRegistry.reload();

		this.playerManager = new PlayerManager(this);

		Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);

		new SyncTickRunnable(this).runTaskTimer(this, 0, 1);
		new AsyncTickRunnable(this).runTaskTimerAsynchronously(this, 0, 1);
		new DayChangeRunnable().runTaskTimerAsynchronously(this, 0, 20);

		Bukkit.getOnlinePlayers().forEach(this.playerManager::createPlayer);
	}

	@Override
	public void onDisable() {
		this.playerManager.getPlayers().forEach(SurvivalPlayer::save);
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
