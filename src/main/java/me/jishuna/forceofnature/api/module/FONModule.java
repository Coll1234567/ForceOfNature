package me.jishuna.forceofnature.api.module;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.gson.JsonObject;

import me.jishuna.forceofnature.ForceOfNature;
import me.jishuna.forceofnature.api.event.EventWrapper;
import me.jishuna.forceofnature.api.player.PlayerManager;
import me.jishuna.forceofnature.api.player.SurvivalPlayer;

public abstract class FONModule<T extends ExtensionConfig> {

	private final Table<Class<? extends Event>, EventPriority, EventWrapper<? extends Event>> eventTable = HashBasedTable
			.create();
	private final ForceOfNature plugin;
	private final T config;

	protected FONModule(ForceOfNature plugin, T config) {
		this.plugin = plugin;
		this.config = config;
		
		this.reload();
	}
	
	public void reload() {
		this.config.reload(this.plugin);
	}

	public void tick(int tick, PlayerManager manager) {
	}

	@SuppressWarnings("rawtypes")
	public abstract PlayerExtension createExtension(JsonObject json);
	
	public abstract NamespacedKey getKey();

	public <E extends Event> void handleEvent(Class<E> eventClass, EventPriority priority, E event) {
		this.getEventHandler(eventClass, priority).ifPresent(handler -> handler.consume(event));
	}

	public <E extends Event> void addEventHandler(Class<E> type, Consumer<E> consumer) {
		addEventHandler(type, EventPriority.NORMAL, consumer);
	}

	public <E extends Event> void addEventHandler(Class<E> type, EventPriority priority, Consumer<E> consumer) {
		this.plugin.getEventManager().registerListener(type, priority);
		this.eventTable.put(type, priority, new EventWrapper<>(type, consumer));
	}

	public <E extends Event> Optional<EventWrapper<?>> getEventHandler(Class<E> type,
			EventPriority priority) {
		return Optional.ofNullable(this.eventTable.get(type, priority));
	}

	public Optional<SurvivalPlayer> getSurvivalPlayer(Player player) {
		return getSurvivalPlayer(player.getUniqueId());
	}

	public Optional<SurvivalPlayer> getSurvivalPlayer(UUID id) {
		return this.plugin.getPlayerManager().getPlayer(id);
	}

	public T getConfig() {
		return config;
	}

	public ForceOfNature getPlugin() {
		return plugin;
	}

}
