package me.jishuna.forceofnature.api.event;

import java.util.function.Consumer;

import org.bukkit.event.Event;

public class EventWrapper<T extends Event> {

	private Consumer<T> handler;
	private Class<T> eventClass;

	public EventWrapper(Class<T> eventClass, Consumer<T> handler) {
		this.handler = handler;
		this.eventClass = eventClass;
	}

	public void consume(Event event) {
		handler.accept(this.eventClass.cast(event));
	}
}
