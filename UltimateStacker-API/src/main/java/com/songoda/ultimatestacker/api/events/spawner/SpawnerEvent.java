package com.songoda.ultimatestacker.api.events.spawner;

import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

/**
 * Represents an abstract spawner event
 */
public abstract class SpawnerEvent extends PlayerEvent {

    protected final Block block;
    protected final EntityType spawnerType;
    protected final int amount;

    public SpawnerEvent(Player who, Block block, EntityType spawnerType, int amount) {
        super(who);
        this.block = block;
        this.spawnerType = spawnerType;
        this.amount = amount;
    }

    /**
     * Get the {@link Block} involved in this event
     *
     * @return the block
     */
    public Block getBlock() {
        return this.block;
    }

    /**
     * Get the {@link EntityType} of the spawner
     *
     * @return the spawner type
     */
    public EntityType getSpawnerType() {
        return this.spawnerType;
    }

    /**
     * Get the amount of spawners affected in this event
     *
     * @return the amount
     */
    public int getAmount() {
        return this.amount;
    }

}
