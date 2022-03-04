package me.jishuna.forceofnature.api.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.NamespacedKey;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.module.thirst.ThirstConfig;
import me.jishuna.forceofnature.api.module.thirst.ThirstModule;

public class ModuleRegistry {
	private final Map<NamespacedKey, FONModule<?>> registry = new HashMap<>();

	public ModuleRegistry(ForceOfNature plugin) {
		registerModule(new ThirstModule(plugin, new ThirstConfig()));
	}

	public void registerModule(FONModule<?> module) {
		this.registry.put(module.getKey(), module);
	}

	public Collection<FONModule<?>> getModules() {
		return this.registry.values();
	}

}
