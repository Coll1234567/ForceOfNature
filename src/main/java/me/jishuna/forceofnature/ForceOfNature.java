package me.jishuna.forceofnature;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.Bukkit;
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
import me.jishuna.forceofnature.api.WorldManager;
import me.jishuna.forceofnature.api.SeasonalBiomeGroupRegistry;
import me.jishuna.forceofnature.api.biomes.SeasonalBiomeGroup;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.world.level.biome.BiomeBase;

public class ForceOfNature extends JavaPlugin {

	private SeasonalBiomeGroupRegistry groupRegistry;
	private WorldManager seasonManager;

	@SuppressWarnings("resource")
	@Override
	public void onEnable() {
		groupRegistry = new SeasonalBiomeGroupRegistry();
		seasonManager = new WorldManager();

		IRegistryWritable<BiomeBase> biomeRegistry = ((CraftServer) Bukkit.getServer()).getServer().l.b(IRegistry.aO);

		final String path = "SeasonGroups";
		final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());

		if (jarFile.isFile()) {
			try (final JarFile jar = new JarFile(jarFile);) {
				final Enumeration<JarEntry> entries = jar.entries();
				while (entries.hasMoreElements()) {
					final String name = entries.nextElement().getName();
					if (name.startsWith(path + "/")) {
						if (!name.endsWith(".yml"))
							continue;

						FileUtils.loadResource(this, name).ifPresent(config -> {
							this.groupRegistry.registerBiomeGroup(new SeasonalBiomeGroup(config, biomeRegistry));
						});
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		new TimeCheckRunnable(this).runTaskTimerAsynchronously(this, 0, 20);
		new SnowRunnable(this).runTaskTimerAsynchronously(this, 0, 20);
		new PlayerWeatherRunnable(groupRegistry).runTaskTimer(this, 0, 20);

		ProtocolManager manager = ProtocolLibrary.getProtocolManager();
		manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Server.MAP_CHUNK) {
			@Override
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				int[] biomes = packet.getIntegerArrays().readSafely(0);
				if (biomes == null)
					return;
				for (int index = 0; index < biomes.length; index++) {
					int id = biomes[index];
					String key = biomeRegistry.getKey(biomeRegistry.fromId(id)).toString();
					SeasonalBiomeGroup group = groupRegistry.getBiomeGroup(key);

					if (group == null)
						continue;

					biomes[index] = group.getBiomeForSeason(seasonManager.getSeason(event.getPlayer().getWorld()))
							.getNumericId();
				}
				packet.getIntegerArrays().writeSafely(0, biomes);
				event.setPacket(packet);
			}
		});
	}

	public SeasonalBiomeGroupRegistry getGroupRegistry() {
		return groupRegistry;
	}

	public WorldManager getSeasonManager() {
		return seasonManager;
	}

}
