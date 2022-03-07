package me.jishuna.forceofnature.api.utils;

import java.util.List;

import net.md_5.bungee.api.ChatColor;

public class StringUtils {
	
	public static String colorize(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}
	
	public static List<String> colorize(List<String> list) {
		return list.stream().map(StringUtils::colorize).toList();
	}

}
