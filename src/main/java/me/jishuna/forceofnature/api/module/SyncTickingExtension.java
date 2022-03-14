package me.jishuna.forceofnature.api.module;

import me.jishuna.forceofnature.api.player.SurvivalPlayer;

public interface SyncTickingExtension {
	
	public void tick(SurvivalPlayer survivalPlayer, int tick);

}
