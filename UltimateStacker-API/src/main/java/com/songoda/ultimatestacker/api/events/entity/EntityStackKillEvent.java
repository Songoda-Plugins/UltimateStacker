package com.songoda.ultimatestacker.api.events.entity;

import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Called when an entity is killed by a player which is stacked
 */
public class EntityStackKillEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final EntityStack entityStack;
    private final boolean instantKill;

    public EntityStackKillEvent(EntityStack entityStack) {
        this.entityStack = entityStack;
        this.instantKill = false;
    }

    public EntityStackKillEvent(EntityStack entityStack, boolean instantKill) {
        this.entityStack = entityStack;
        this.instantKill = instantKill;
    }

    /**
     * Get the host entity of the stack
     *
     * @return Entity
     */
    public LivingEntity getEntity() {
        return entityStack.getHostEntity();
    }

    /**
     * Returns true if the entity was killed instantly
     *
     * @return true if the entity was killed instantly false otherwise
     */
    public boolean isInstantKill() {
        return instantKill;
    }

    /**
     * Get the current size of the entity stack
     *
     * @return stack size
     */
    public int getStackSize() {
        return entityStack.getAmount();
    }

    /**
     * Get the new size of the entity stack
     *
     * @return new stack size or 0 if instant killed
     */
    public int getNewStackSize() {
        return instantKill ? 0 : entityStack.getAmount() - 1;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
