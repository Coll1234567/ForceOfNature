package me.jishuna.forceofnature.api.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.module.thirst.ThirstConfig;
import me.jishuna.forceofnature.api.module.thirst.ThirstModule;

public class ModuleRegistry {
	@SuppressWarnings("rawtypes")
	private final Map<Class<? extends FONModule>, FONModule<?, ?>> registry = new HashMap<>();

	public ModuleRegistry(ForceOfNature plugin) {
		registerModule(new ThirstModule(plugin, new ThirstConfig()));
	}

	public void registerModule(FONModule<?, ?> module) {
		this.registry.put(module.getClass(), module);
	}

	public Collection<FONModule<?, ?>> getModules() {
		return this.registry.values();
	}

}
