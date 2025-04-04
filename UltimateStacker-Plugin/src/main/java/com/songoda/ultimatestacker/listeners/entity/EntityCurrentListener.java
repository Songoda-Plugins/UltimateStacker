package com.songoda.ultimatestacker.listeners.entity;

import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.api.stack.entity.EntityStackManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;

public class EntityCurrentListener implements Listener {

    private final UltimateStacker plugin;

    public EntityCurrentListener(UltimateStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawn(EntityTransformEvent event) {
        EntityStackManager stackManager = plugin.getEntityStackManager();
        if (stackManager.isStackedEntity(event.getEntity())
                && event.getEntity() instanceof LivingEntity
                && event.getTransformedEntity() instanceof LivingEntity) {
            if (event.getTransformReason().equals(EntityTransformEvent.TransformReason.SPLIT)) {
                event.setCancelled(true);
                return;
            }
            EntityStack stack = stackManager.updateStack((LivingEntity) event.getEntity(), (LivingEntity) event.getTransformedEntity());
            if (stack == null) return;
            stack.releaseHost();
        }
    }
}
