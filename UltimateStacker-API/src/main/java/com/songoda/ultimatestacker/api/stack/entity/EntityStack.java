package com.songoda.ultimatestacker.api.stack.entity;

import com.songoda.ultimatestacker.api.utils.Stackable;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;

public interface EntityStack extends Stackable {


    /**
     * Get the type of entity this stack represents.
     *
     * @return The EntityType of the stacked entity.
     */
    EntityType getType();

    /**
     * Get the UUID of this entity stack. Equals with the host entity's UUID.
     *
     * @return The UUID of the entity stack.
     */
    UUID getUuid();

    /**
     * Get the host entity that represents this stack in the world.
     *
     * @return The LivingEntity that is the host of this stack.
     */
    LivingEntity getHostEntity();

    /**
     * Take one entity from the stack and spawn it at the given location.
     *
     * @param location The location to spawn the entity.
     * @return The LivingEntity that was spawned.
     */
    LivingEntity takeOneAndSpawnEntity(Location location);

    /**
     * Release the host entity from being managed by this stack.
     * After calling this, the host entity will no longer be part of the stack.
     */
    void releaseHost();

    /**
     * Destroy the entire entity stack, removing the host entity from the world.
     */
    void destroy();

    /**
     * Update the name tag of the host entity to reflect the current stack size.
     */
    void updateNameTag();

    /**
     * Calculate the loot drops for a given number of entities from the stack.
     * Note: You might need to split and create multiple ItemStacks if the quantity exceeds max stack size.
     *
     * @param amount The number of entities to calculate loot for.
     * @return A map of ItemStacks to their quantities representing the loot drops.
     */
    Map<ItemStack, BigInteger> calculateLoot(int amount);
}
