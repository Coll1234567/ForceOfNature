package me.jishuna.forceofnature.api.module.thirst;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.PlayerExtension;

public class ThirstExtension extends PlayerExtension<ThirstConfig> {

	@Expose
	private int thirst = 100;

	@Override
	public void save(JsonObject json) {
		json.add(ThirstModule.NAME, GsonHandler.serialize(this));
	}

	public int getThirst() {
		return thirst;
	}

	public void takeThirst(int amount) {
		this.thirst = Math.max(0, this.thirst - amount);
	}

	public void giveThirst(int amount) {
		this.thirst = Math.min(100, this.thirst + amount);
	}
}
