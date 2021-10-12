package me.jishuna.forceofnature.api.event;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.jishuna.forceofnature.api.Season;

public class AsyncSeasonChangeEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final World world;
	private final Season oldSeason;
	private final Season newSeason;

	public AsyncSeasonChangeEvent(World world, Season oldSeason, Season newSeason) {
		this.world = world;
		this.oldSeason = oldSeason;
		this.newSeason = newSeason;
	}

	public World getWorld() {
		return world;
	}

	public Season getOldSeason() {
		return oldSeason;
	}

	public Season getNewSeason() {
		return newSeason;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
