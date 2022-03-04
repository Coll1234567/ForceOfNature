package me.jishuna.forceofnature;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	private final ForceOfNature plugin;

	public PlayerListener(ForceOfNature plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		plugin.getPlayerManager().createPlayer(event.getPlayer());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Bukkit.getScheduler().runTaskAsynchronously(this.plugin,
				() -> plugin.getPlayerManager().removePlayer(event.getPlayer()));
	}
}
