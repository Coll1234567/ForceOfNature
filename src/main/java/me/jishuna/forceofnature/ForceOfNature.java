package me.jishuna.forceofnature;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;

import me.jishuna.commonlib.utils.FileUtils;
import me.jishuna.forceofnature.api.SeasonalBiomeGroupRegistry;
import me.jishuna.forceofnature.api.WorldData;
import me.jishuna.forceofnature.api.WorldManager;
import me.jishuna.forceofnature.api.biomes.SeasonalBiomeGroup;
import me.jishuna.forceofnature.listeners.WorldListener;
import me.jishuna.forceofnature.runnables.PlayerWeatherRunnable;
import me.jishuna.forceofnature.runnables.TimeCheckRunnable;
import me.jishuna.forceofnature.runnables.WeatherRunnable;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.world.level.biome.BiomeBase;

public class ForceOfNature extends JavaPlugin {
	private static final String PATH = "SeasonGroups";

	private WorldManager worldManager;
	private YamlConfiguration configuration;

	@Override
	public void onEnable() {
		worldManager = new WorldManager();

		loadConfiguration();

		Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);

		Bukkit.getScheduler().runTask(this, () -> {
			for (World world : Bukkit.getWorlds())
				loadWorld(world);
		});

		new TimeCheckRunnable(this).runTaskTimerAsynchronously(this, 0, 10);
		new WeatherRunnable(this).runTaskTimerAsynchronously(this, 0, 10);
		new PlayerWeatherRunnable(worldManager).runTaskTimer(this, 0, 10);

		injectPacketListener();
	}

	@SuppressWarnings("resource")
	private void injectPacketListener() {
		IRegistryWritable<BiomeBase> biomeRegistry = ((CraftServer) Bukkit.getServer()).getServer().l.b(IRegistry.aO);

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.MAP_CHUNK) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				int[] biomes = packet.getIntegerArrays().readSafely(0);
				if (biomes == null)
					return;

				World world = event.getPlayer().getWorld();
				WorldData data = worldManager.getWorldData(world);
				if (data == null)
					return;

				for (int index = 0; index < biomes.length; index++) {
					int id = biomes[index];
					String key = biomeRegistry.getKey(biomeRegistry.fromId(id)).toString();
					SeasonalBiomeGroup group = data.getRegistry().getBiomeGroup(key);

					if (group == null)
						continue;

					biomes[index] = group.getBiomeForSeason(data.getSeason()).getNumericId();
				}
				packet.getIntegerArrays().writeSafely(0, biomes);
				event.setPacket(packet);
			}
		});
	}

	public void loadWorld(World world) {
		@SuppressWarnings("resource")
		IRegistryWritable<BiomeBase> biomeRegistry = ((CraftServer) Bukkit.getServer()).getServer().l.b(IRegistry.aO);
		File worldsFolder = new File(this.getDataFolder() + File.separator + "worlds");

		if (!worldsFolder.exists())
			worldsFolder.mkdirs();

		String worldName = world.getName();
		ConfigurationSection section = this.getConfiguration().getConfigurationSection("worlds");

		if (section == null)
			return;

		if (!section.getKeys(false).contains(worldName))
			return;

		File seasonFolder = new File(worldsFolder + File.separator + worldName + File.separator + PATH);
		
		sendDebugMessage("Loading world " + worldName);
		if (!seasonFolder.exists()) {
			seasonFolder.mkdirs();

			sendDebugMessage("Creating world folder for world " + worldName);
			copyDefaultFiles("worlds" + File.separator + worldName);
		}

		WorldData data = new WorldData(new SeasonalBiomeGroupRegistry());

		for (File file : seasonFolder.listFiles()) {
			if (!file.getName().endsWith(".yml"))
				continue;

			YamlConfiguration biomeConfig = YamlConfiguration.loadConfiguration(file);
			data.getRegistry().registerBiomeGroup(new SeasonalBiomeGroup(world, biomeConfig, biomeRegistry));
		}
		this.getWorldManager().setWorldData(world, data);
	}

	private void copyDefaultFiles(String target) {
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) {
			try (final JarFile jar = new JarFile(jarFile);) {
				final Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					final String name = entries.nextElement().getName();
					if (name.startsWith(PATH + "/")) {
						if (!name.endsWith(".yml"))
							continue;

						FileUtils.loadResource(this, name, target + File.separator + name);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadConfiguration() {
		if (!this.getDataFolder().exists())
			this.getDataFolder().mkdirs();

		FileUtils.loadResource(this, "config.yml").ifPresent(config -> this.configuration = config);
	}
	
	public void sendDebugMessage(String message) {
		if (this.configuration.getBoolean("debug", false))
			this.getLogger().info(message);
	}

	public WorldManager getWorldManager() {
		return worldManager;
	}

	public YamlConfiguration getConfiguration() {
		return this.configuration;
	}

}
