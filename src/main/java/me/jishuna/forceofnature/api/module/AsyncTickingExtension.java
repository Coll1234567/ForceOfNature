package me.jishuna.forceofnature.api.module;

import me.jishuna.forceofnature.api.player.SurvivalPlayer;

public interface AsyncTickingExtension {
	
	public void tickAsync(SurvivalPlayer survivalPlayer, int tick);

}
