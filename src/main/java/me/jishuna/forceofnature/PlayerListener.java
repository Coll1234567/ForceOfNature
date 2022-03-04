package me.jishuna.forceofnature;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.jishuna.forceofnature.api.player.PlayerManager;

public class PlayerListener implements Listener {
	
	private final PlayerManager manager;

	public PlayerListener(PlayerManager manager) {
		this.manager = manager;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		manager.createPlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		manager.removePlayer(event.getPlayer());
	}
}
