package com.songoda.ultimatestacker.listeners;

import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.api.stack.entity.EntityStackManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class BreedListeners implements Listener {

    private final UltimateStacker plugin;
    private final EntityStackManager entityStackManager;

    public BreedListeners(UltimateStacker plugin) {
        this.plugin = plugin;
        this.entityStackManager = plugin.getEntityStackManager();
    }

    @EventHandler
    public void onBread(EntityBreedEvent event) {
        EntityStack stackedMother = entityStackManager.getStackedEntity(event.getMother());
        EntityStack stackedFather = entityStackManager.getStackedEntity(event.getFather());

        plugin.getBreedingTask().addBreedingTicket(event.getMother(), event.getFather());

        if (stackedMother != null) {
            EntityStack stack = entityStackManager.getStackedEntity(event.getMother());
            if (stack.getAmount() <= 1) return;
            stack.releaseHost();
        }

        if (stackedFather != null) {
            EntityStack stack = entityStackManager.getStackedEntity(event.getFather());
            if (stack.getAmount() <= 1) return;
            stack.releaseHost();
        }
    }
}
