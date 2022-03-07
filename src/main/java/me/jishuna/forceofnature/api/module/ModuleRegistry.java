package me.jishuna.forceofnature.api.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.module.temperature.TemperatureConfig;
import me.jishuna.forceofnature.api.module.temperature.TemperatureModule;
import me.jishuna.forceofnature.api.module.thirst.ThirstConfig;
import me.jishuna.forceofnature.api.module.thirst.ThirstModule;

public class ModuleRegistry {
	private final Map<NamespacedKey, FONModule<?>> registry = new HashMap<>();
	
	private static final ForceOfNature plugin = JavaPlugin.getPlugin(ForceOfNature.class);
	
	public static final ThirstModule THIRST = new ThirstModule(plugin, new ThirstConfig());
	public static final TemperatureModule TEMPERATURE = new TemperatureModule(plugin, new TemperatureConfig());

	public ModuleRegistry() {
		registerModule(THIRST);
		registerModule(TEMPERATURE);
	}

	public void registerModule(FONModule<?> module) {
		this.registry.put(module.getKey(), module);
	}

	public Collection<FONModule<?>> getModules() {
		return this.registry.values();
	}
	
	public void reload() {
		this.registry.values().forEach(FONModule::reload);
	}

}
