package com.songoda.ultimatestacker.api.events.spawner;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Called when a spawner has been placed in the world
 */
public class SpawnerPlaceEvent extends SpawnerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private boolean canceled = false;

    public SpawnerPlaceEvent(Player player, Block block, EntityType spawnerType, int amount) {
        super(player, block, spawnerType, amount);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.canceled;
    }

    @Override
    public void setCancelled(boolean canceled) {
        this.canceled = canceled;
    }

}
