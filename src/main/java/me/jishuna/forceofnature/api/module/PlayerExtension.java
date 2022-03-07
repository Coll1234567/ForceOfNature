package me.jishuna.forceofnature.api.module;

import com.google.gson.JsonObject;

public abstract class PlayerExtension<T extends FONModule<?>> {

	private T module;

	public abstract void save(JsonObject json);

	public T getModule() {
		return module;
	}

	public void setModule(T module) {
		this.module = module;
	}
}
