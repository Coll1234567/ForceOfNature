package me.jishuna.forceofnature.api;

import org.bukkit.Location;

import net.minecraft.core.BlockPosition;

public class Utils {

	public static BlockPosition toBlockPos(Location location) {
		return new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

}
