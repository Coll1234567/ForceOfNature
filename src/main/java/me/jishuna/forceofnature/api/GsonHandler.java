package me.jishuna.forceofnature.api;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Supplier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GsonHandler {

	private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	public static JsonElement serialize(Object object) {
		return gson.toJsonTree(object);
	}

	public static <T> T deserialize(JsonElement json, Class<T> type) {
		return gson.fromJson(json, type);
	}

	public static <T> T deserialize(JsonElement json, Class<T> type, Supplier<T> def) {
		T object = gson.fromJson(json, type);
		return object == null ? def.get() : object;
	}

	public static void writeToFile(File file, JsonObject json) {
		try (FileWriter writer = new FileWriter(file)) {
			gson.toJson(json, writer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static JsonObject readFromFile(File file) {
		JsonObject json = new JsonObject();
		try (FileReader reader = new FileReader(file)) {
			JsonElement element = JsonParser.parseReader(reader);
			if (element != null && element.isJsonObject()) {
				json = element.getAsJsonObject();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return json;
	}
}
