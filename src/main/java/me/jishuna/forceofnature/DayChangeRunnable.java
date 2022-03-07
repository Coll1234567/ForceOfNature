package me.jishuna.forceofnature;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.api.module.ModuleRegistry;

public class DayChangeRunnable extends BukkitRunnable {

	private static final World world = Bukkit.getWorlds().get(0);
	private long day = -1;

	@Override
	public void run() {
		long day = world.getFullTime() / 24000;

		if (day != this.day) {
			this.day = day;

			ModuleRegistry.TEMPERATURE.handleDayChange(day);
		}

	}

}
