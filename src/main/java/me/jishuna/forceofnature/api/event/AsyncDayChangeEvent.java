package me.jishuna.forceofnature.api.event;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncDayChangeEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	private final World world;
	private final int oldDay;
	private final int newDay;

	public AsyncDayChangeEvent(World world, int oldDay, int newDay) {
		super(true);
		this.world = world;
		this.oldDay = oldDay;
		this.newDay = newDay;
	}

	public World getWorld() {
		return world;
	}

	public int getOldDay() {
		return oldDay;
	}

	public int getNewDay() {
		return newDay;
	}

	@Override
	public HandlerList getHandlers() {
		return getHandlerList();
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}
}
