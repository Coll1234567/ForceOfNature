package me.jishuna.forceofnature.api.module.temperature;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.module.ExtensionConfig;
import me.jishuna.forceofnature.api.utils.FileUtils;

public class TemperatureConfig extends ExtensionConfig {
	
	private double rainModifier;
	private double changeFactor;

	@Override
	public void reload(ForceOfNature plugin) {
		FileUtils.loadResource(plugin, "modules/temperature/" + TemperatureModule.NAME + ".yml").ifPresent(config -> {
			loadDefaults(config);
			
			this.rainModifier = config.getDouble("rain-modifier", -2);
			this.changeFactor = config.getDouble("change-factor", 20);
		});
	}

	public double getRainModifier() {
		return rainModifier;
	}

	public double getChangeFactor() {
		return changeFactor;
	}
}
