package me.jishuna.forceofnature.api;

import java.io.File;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class FileUtils {

	@Nonnull
	public static Optional<File> loadResourceFile(Plugin source, String resourceName) {
		File resourceFile = new File(source.getDataFolder(), resourceName);

		// Copy file if needed
		if (!resourceFile.exists() && source.getResource(resourceName) != null) {
			source.saveResource(resourceName, false);
		}

		// File still doesn't exist, return empty
		if (!resourceFile.exists()) {
			return Optional.empty();
		}
		return Optional.of(resourceFile);
	}

	@Nonnull
	public static Optional<YamlConfiguration> loadResource(Plugin source, String resourceName) {
		Optional<File> optional = loadResourceFile(source, resourceName);

		if (optional.isPresent()) {
			return Optional.of(YamlConfiguration.loadConfiguration(optional.get()));
		} else {
			return Optional.empty();
		}
	}
}
