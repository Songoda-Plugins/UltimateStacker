package com.songoda.ultimatestacker.api.events.entity;

import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Called when an entity is killed by a player which is stacked.
 * When canceled, the plugin won't run custom death logic for the stacked entity.
 */
public class EntityStackKillEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final EntityDeathEvent originalEvent;
    private boolean cancelled = false;
    private final EntityStack entityStack;
    private final boolean instantKill;
    private Integer newStackSize;

    public EntityStackKillEvent(EntityStack entityStack, EntityDeathEvent originalEvent) {
        this.originalEvent = originalEvent;
        this.entityStack = entityStack;
        this.instantKill = false;
    }

    public EntityStackKillEvent(EntityStack entityStack, boolean instantKill, EntityDeathEvent originalEvent) {
        this.entityStack = entityStack;
        this.instantKill = instantKill;
        this.originalEvent = originalEvent;
    }

    /**
     * Get the original EntityDeathEvent
     *
     * @return EntityDeathEvent
     */
    public EntityDeathEvent getOriginalEvent() {
        return originalEvent;
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
        return newStackSize != null ? newStackSize : instantKill ? 0 : entityStack.getAmount() - 1;
    }

    /**
     * Set the new size of the entity stack
     *
     * @param newStackSize new stack size or null to use default logic
     */
    public void setNewStackSize(@Nullable Integer newStackSize) {
        this.newStackSize = newStackSize;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Prevents the default death logic from occurring for the stacked entity.
     * All logic should be handled by the cancelling plugin like managing drops and experience dropped.
     * @param cancelled true to cancel the event false otherwise
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
