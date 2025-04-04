package com.songoda.ultimatestacker.api.stack.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface EntityStackManager {

    /**
     * Creates a new stack with the given entity and amount.
     * @param entity The entity to create the stack with.
     * @param amount The amount to create the stack with.
     * @return The EntityStack created.
     */
    EntityStack createStackedEntity(LivingEntity entity, int amount);

    /**
     * @return if the entity is a stacked entity.
     */
    boolean isStackedEntity(Entity entity);

    /**
     * Used to get the EntityStack of a stacked entity.
     * @param entityUUID The UUID of the entity to get the stack of.
     * @return The EntityStack of the entity or null if the entity is not stacked.
     */
    EntityStack getStackedEntity(UUID entityUUID);

    /**
     * Used to get the EntityStack of a stacked entity.
     * @param entity The entity to get the stack of.
     * @return The EntityStack of the entity or null if the entity is not stacked.
     */
    EntityStack getStackedEntity(LivingEntity entity);

    /**
     * Used to get the last player to damage the entity.
     * @param entity The entity to get the last player to damage it.
     * @return The player's name or null if no player has damaged the entity.
     */
    String getLastPlayerDamage(Entity entity);

    /**
     * Used to set the last player who damaged the entity.
     * @param entity The entity to set the last player to damage it.
     * @param player The player who damaged the entity.
     */
    void setLastPlayerDamage(Entity entity, Player player);

    /**
     * Transfers the stack from one entity to another.
     * (e.g. slimes split)
     * @param oldEntity The old entity to transfer the stack from.
     * @param newEntity The new entity to transfer the stack to.
     * @return The new stack.
     */
    EntityStack transferStack(LivingEntity oldEntity, LivingEntity newEntity, boolean takeOne);

    /**
     * TODO: Add javadoc
     */
    EntityStack updateStack(LivingEntity oldEntity, LivingEntity newEntity);
}
