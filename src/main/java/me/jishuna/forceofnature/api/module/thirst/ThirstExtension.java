package me.jishuna.forceofnature.api.module.thirst;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import me.jishuna.forceofnature.api.Characters;
import me.jishuna.forceofnature.api.GsonHandler;
import me.jishuna.forceofnature.api.module.PlayerExtension;
import me.jishuna.forceofnature.api.player.SurvivalPlayer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ThirstExtension extends PlayerExtension<ThirstModule> {

	@Expose
	private float thirst = 20;

	@Override
	public void save(JsonObject json) {
		json.add(ThirstModule.NAME, GsonHandler.serialize(this));
	}

	public float getThirst() {
		return thirst;
	}

	public void takeThirst(float amount) {
		this.thirst = Math.max(0, this.thirst - amount);
	}

	public void giveThirst(float amount) {
		this.thirst = Math.min(20, this.thirst + amount);
	}

	public void render(SurvivalPlayer survivalPlayer) {
		Player player = survivalPlayer.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;

		boolean inWater = player.getRemainingAir() < player.getMaximumAir();
		StringBuilder builder = new StringBuilder();
		int full = (int) (this.thirst / 2);
		int half = (int) (this.thirst % 2);
		int empty = 10 - full - half;

		builder.append((inWater ? Characters.DROP_EMPTY_ALT : Characters.DROP_EMPTY).repeat(empty));
		builder.append((inWater ? Characters.DROP_HALF_ALT : Characters.DROP_HALF).repeat(half));
		builder.append((inWater ? Characters.DROP_FULL_ALT : Characters.DROP_FULL).repeat(full));

		TextComponent spaceComponent = new TextComponent(Characters.ACTION_BAR_SPACE);
		TextComponent thirstComponent = new TextComponent(builder.toString());

		spaceComponent.setFont("forceofnature:fonfont");
		thirstComponent.setFont("forceofnature:fonfont");

		survivalPlayer.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, spaceComponent, thirstComponent);
	}
}
