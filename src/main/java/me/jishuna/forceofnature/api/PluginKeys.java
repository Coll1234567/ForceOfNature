package me.jishuna.forceofnature.api;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.forceofnature.ForceOfNature;

public class PluginKeys {
	
	public static final NamespacedKey THIRST = makeKey("thirst");
	public static final NamespacedKey SICK_CHANCE = makeKey("sick_chance");

	private static NamespacedKey makeKey(String name) {
		return new NamespacedKey(JavaPlugin.getPlugin(ForceOfNature.class), name);
	}
}
