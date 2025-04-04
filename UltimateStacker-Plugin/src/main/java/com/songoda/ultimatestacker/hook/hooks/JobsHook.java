package com.songoda.ultimatestacker.hook.hooks;

import com.songoda.core.hooks.jobs.JobsPlayerHandler;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.hook.StackerHook;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class JobsHook implements StackerHook {

    @Override
    public void applyExperience(Player player, EntityStack entityStack) {
        if (player.getGameMode().equals(GameMode.CREATIVE) || entityStack.getHostEntity() == null)
            return;

        JobsPlayerHandler jPlayer = com.songoda.core.hooks.JobsHook.getPlayer(player);
        if (jPlayer == null)
            return;

        for (int i = 1; i < entityStack.getAmount(); i++) {
            LivingEntity entity = entityStack.getHostEntity();
            jPlayer.killEntity(entity);
        }
    }
}
