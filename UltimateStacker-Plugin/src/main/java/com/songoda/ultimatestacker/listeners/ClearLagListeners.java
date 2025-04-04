package com.songoda.ultimatestacker.listeners;

import com.songoda.ultimatestacker.UltimateStacker;
import me.minebuilders.clearlag.events.EntityRemoveEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClearLagListeners implements Listener {

    private final UltimateStacker plugin;

    public ClearLagListeners(UltimateStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClearLaggTask(EntityRemoveEvent event) {
        for (Entity entity : event.getWorld().getEntities()) {
            if (entity instanceof LivingEntity && plugin.getEntityStackManager().isStackedEntity(entity)) {
                plugin.getEntityStackManager().getStackedEntity((LivingEntity) entity).destroy();
                event.addEntity(entity);
            }
        }
    }
}
