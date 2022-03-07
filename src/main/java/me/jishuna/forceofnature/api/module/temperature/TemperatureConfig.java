package me.jishuna.forceofnature.api.module.temperature;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.module.ExtensionConfig;
import me.jishuna.forceofnature.api.utils.FileUtils;

public class TemperatureConfig extends ExtensionConfig {

	@Override
	public void reload(ForceOfNature plugin) {
		FileUtils.loadResource(plugin, "modules/temperature/" + TemperatureModule.NAME + ".yml").ifPresent(config -> {
			loadDefaults(config);
		});
	}
}
