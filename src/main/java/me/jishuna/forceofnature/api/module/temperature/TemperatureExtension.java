package me.jishuna.forceofnature.api.module.temperature;

import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.common.primitives.Ints;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import me.jishuna.forceofnature.api.Characters;
import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.AsyncTickingExtension;
import me.jishuna.forceofnature.api.module.PlayerExtension;
import me.jishuna.forceofnature.api.player.SurvivalPlayer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class TemperatureExtension extends PlayerExtension<TemperatureModule> implements AsyncTickingExtension {
	public static final float MAX_TEMP = 50;
	public static final float MIN_TEMP = -50;

	@Expose
	private volatile double temperature = 20;

	@Override
	public void save(JsonObject json) {
		json.add(TemperatureModule.NAME, GsonHandler.serialize(this));
	}

	public List<BaseComponent> getDisplayComponents(SurvivalPlayer survivalPlayer) {
		StringBuilder builder = new StringBuilder(Characters.TEMP_BAR);
		builder.append("\uF801");

		int index = getIndicatorIndex();
		builder.append(Characters.TEMP_MARKERS.get(index));
		
		TextComponent component = new TextComponent(builder.toString());
		component.setFont("forceofnature:fonfont");
		survivalPlayer.getPlayer().sendMessage("temp: " + this.temperature);
		return Arrays.asList(component);
	}

	@Override
	public void tickAsync(SurvivalPlayer survivalPlayer, int tick) {
		if (tick % 20 != 0)
			return;

		Player player = survivalPlayer.getPlayer();
		handleTemperatureChange(player);
	}

	public void handleTemperatureChange(Player player) {
		double temp = this.getModule().getLocationTemperature(player.getLocation(), true);

		this.temperature += (temp - this.temperature) / this.getModule().getConfig().getChangeFactor();
	}

	public int getIndicatorIndex() {
		double range = MAX_TEMP - MIN_TEMP;
		double temp = this.temperature - MIN_TEMP;

		int index = (int) Math.round((temp / range) * 58);
		return Ints.constrainToRange(index, 0, 58);
	}
}
