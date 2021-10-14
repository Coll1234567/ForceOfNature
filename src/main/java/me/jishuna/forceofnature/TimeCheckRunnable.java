package me.jishuna.forceofnature;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.api.Season;
import me.jishuna.forceofnature.api.SeasonalBiomeGroupRegistry;
import me.jishuna.forceofnature.api.WorldManager;
import me.jishuna.forceofnature.api.event.AsyncSeasonChangeEvent;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.server.level.EntityPlayer;

public class TimeCheckRunnable extends BukkitRunnable {

	private final WorldManager manager;
	private final SeasonalBiomeGroupRegistry groupRegistry;

	public TimeCheckRunnable(ForceOfNature plugin) {
		this.manager = plugin.getSeasonManager();
		this.groupRegistry = plugin.getGroupRegistry();
	}

	@Override
	public void run() {
		for (World world : Bukkit.getWorlds()) {
			int oldDay = manager.getDay(world);
			int day = (int) (world.getFullTime() / 24000);

			if (oldDay == day)
				return;

			manager.setDay(world, day);

			int seasonIndex = (day / 5) % 4;

			Season newSeason = Season.values()[seasonIndex];
			Season oldSeason = manager.getSeason(world);

			if (newSeason != oldSeason) {
				AsyncSeasonChangeEvent event = new AsyncSeasonChangeEvent(world, oldSeason, newSeason);
				Bukkit.getPluginManager().callEvent(event);

				manager.setSeason(world, newSeason);
				refreshChunks(world);
			}
			
			groupRegistry.onNewDay(newSeason);
		}
	}

	private void refreshChunks(World world) {
		for (Chunk chunk : world.getLoadedChunks()) {
			PacketPlayOutMapChunk newChunkPacket = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle());
			for (EntityPlayer ep : ((CraftWorld) world).getHandle().getPlayers()) {
				int viewDistance = (Bukkit.getServer().getViewDistance() + 3) << 4;
				double distanceX = Math.abs(ep.locX() - (chunk.getX() << 4));
				double distanceZ = Math.abs(ep.locZ() - (chunk.getZ() << 4));
				if (distanceX <= viewDistance && distanceZ <= viewDistance) {
					ep.b.sendPacket(newChunkPacket);
				}
			}
		}
	}

}
