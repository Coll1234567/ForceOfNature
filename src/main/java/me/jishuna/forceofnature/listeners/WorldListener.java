package me.jishuna.forceofnature.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;

import me.jishuna.forceofnature.ForceOfNature;

public class WorldListener implements Listener {
	private final ForceOfNature plugin;

	public WorldListener(ForceOfNature plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onLoad(WorldLoadEvent event) {
		this.plugin.loadWorld(event.getWorld());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUnload(WorldUnloadEvent event) {
		this.plugin.sendDebugMessage("Unloading world " + event.getWorld().getName());
		this.plugin.getWorldManager().removeWorldData(event.getWorld());
	}
}
