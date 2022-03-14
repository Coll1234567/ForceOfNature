package me.jishuna.forceofnature;

import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.api.player.PlayerManager;
import me.jishuna.forceofnature.api.player.SurvivalPlayer;

public class AsyncTickRunnable extends BukkitRunnable {

	private final PlayerManager playerManager;
	int tick = 0;

	public AsyncTickRunnable(ForceOfNature plugin) {
		this.playerManager = plugin.getPlayerManager();
	}

	@Override
	public void run() {
		this.tick = (tick + 1) % 60;
		for (SurvivalPlayer player : this.playerManager.getPlayers()) {
			player.tickAsync(tick);
		}
	}
}
