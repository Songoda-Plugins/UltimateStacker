package com.songoda.ultimatestacker.listeners;

import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.api.stack.entity.EntityStackManager;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.stackable.entity.Split;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.SheepDyeWoolEvent;

public class SheepDyeListeners implements Listener {

    private final UltimateStacker plugin;

    public SheepDyeListeners(UltimateStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDye(SheepDyeWoolEvent event) {
        LivingEntity entity = event.getEntity();

        EntityStackManager stackManager = plugin.getEntityStackManager();
        if (!stackManager.isStackedEntity(entity)) return;

        if (Settings.SPLIT_CHECKS.getStringList().stream().noneMatch(line -> Split.valueOf(line) == Split.SHEEP_DYE))
            return;

        EntityStack stack = stackManager.getStackedEntity(entity);
        if (stack == null) return;
        stack.releaseHost();
    }
}
