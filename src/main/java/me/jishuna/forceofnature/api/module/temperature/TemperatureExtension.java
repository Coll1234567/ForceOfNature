package me.jishuna.forceofnature.api.module.temperature;

import com.google.gson.JsonObject;

import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.PlayerExtension;

public class TemperatureExtension extends PlayerExtension<TemperatureModule> {

	@Override
	public void save(JsonObject json) {
		json.add(TemperatureModule.NAME, GsonHandler.serialize(this));
	}
}
