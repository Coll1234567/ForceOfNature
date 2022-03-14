package me.jishuna.forceofnature;

import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.api.module.FONModule;
import me.jishuna.forceofnature.api.module.ModuleRegistry;
import me.jishuna.forceofnature.api.player.PlayerManager;
import me.jishuna.forceofnature.api.player.SurvivalPlayer;

public class SyncTickRunnable extends BukkitRunnable {

	private final ModuleRegistry moduleRegistry;
	private final PlayerManager playerManager;
	int tick = 0;

	public SyncTickRunnable(ForceOfNature plugin) {
		this.moduleRegistry = plugin.getModuleRegistry();
		this.playerManager = plugin.getPlayerManager();
	}

	@Override
	public void run() {
		this.tick = (tick + 1) % 60;
		for (FONModule<?> module : this.moduleRegistry.getModules()) {
			module.tick(tick, this.playerManager);
		}

		// TODO distribute over 20 ticks
		for (SurvivalPlayer player : this.playerManager.getPlayers()) {
			player.tick(tick);
		}
	}
}
