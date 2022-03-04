package me.jishuna.forceofnature.api.module;

import org.bukkit.configuration.ConfigurationSection;

import me.jishuna.forceofnature.ForceOfNature;

public abstract class ExtensionConfig {
	
	private boolean enabled;
	
	public abstract void reload(ForceOfNature plugin);

	public void loadDefaults(ConfigurationSection section) {
		this.enabled = section.getBoolean("enabled");
	}

	public boolean isEnabled() {
		return enabled;
	}

}
