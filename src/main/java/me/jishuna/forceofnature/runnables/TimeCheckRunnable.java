package me.jishuna.forceofnature.runnables;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.scheduler.BukkitRunnable;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.Season;
import me.jishuna.forceofnature.api.WorldData;
import me.jishuna.forceofnature.api.WorldManager;
import me.jishuna.forceofnature.api.event.AsyncDayChangeEvent;
import me.jishuna.forceofnature.api.event.AsyncSeasonChangeEvent;
import net.minecraft.network.protocol.game.PacketPlayOutMapChunk;
import net.minecraft.server.level.EntityPlayer;

public class TimeCheckRunnable extends BukkitRunnable {

	private final WorldManager manager;

	public TimeCheckRunnable(ForceOfNature plugin) {
		this.manager = plugin.getWorldManager();
	}

	@Override
	public void run() {
		for (World world : Bukkit.getWorlds()) {
			WorldData data = manager.getWorldData(world);

			if (data == null)
				return;
			int oldDay = data.getDay();
			int day = (int) (world.getFullTime() / 24000);

			if (oldDay == day)
				return;

			AsyncDayChangeEvent dayEvent = new AsyncDayChangeEvent(world, oldDay, day);
			Bukkit.getPluginManager().callEvent(dayEvent);

			data.setDay(day);

			int seasonIndex = (day / 5) % 4;

			Season newSeason = Season.values()[seasonIndex];
			Season oldSeason = data.getSeason();

			if (newSeason != oldSeason) {
				AsyncSeasonChangeEvent seasonEvent = new AsyncSeasonChangeEvent(world, oldSeason, newSeason);
				Bukkit.getPluginManager().callEvent(seasonEvent);

				data.setSeason(newSeason);
				refreshChunks(world);
			}

			data.getRegistry().onNewDay(newSeason);
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
