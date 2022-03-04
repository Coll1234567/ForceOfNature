package me.jishuna.forceofnature.api;

import net.md_5.bungee.api.ChatColor;

public class StringUtils {
	
	public static String colorize(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

}
