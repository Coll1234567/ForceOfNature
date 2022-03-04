package me.jishuna.forceofnature.api.module;

import com.google.gson.JsonObject;

public abstract class PlayerExtension<T extends ExtensionConfig> {

	private T config;

	public abstract void save(JsonObject json);

	public T getConfig() {
		return config;
	}

	public void setConfig(T config) {
		this.config = config;
	}
}
