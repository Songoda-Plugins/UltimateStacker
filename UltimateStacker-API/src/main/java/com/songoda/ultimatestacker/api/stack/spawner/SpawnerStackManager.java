package com.songoda.ultimatestacker.api.stack.spawner;

import com.songoda.core.database.Data;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Represents a manager for SpawnerStacks
 */
public interface SpawnerStackManager {

    /**
     * Add a list of spawners to the manager
     * @param spawners The list of spawners to add
     */
    void addSpawners(List<SpawnerStack> spawners);

    /**
     * Add a spawner to the manager
     * @param spawnerStack The spawner to add
     * @return The added spawner
     */
    SpawnerStack addSpawner(SpawnerStack spawnerStack);

    /**
     * Get the spawner for the given block
     * @param block The block to get the spawner for
     * @return The spawner for the given block
     */
    @Nullable SpawnerStack getSpawner(Block block);

    /**
     * Get the spawner for the given location
     * @param location The location to get the spawner for
     * @return The spawner for the given location
     */
    @Nullable SpawnerStack getSpawner(Location location);

    /**
     * Check if the given block is a SpawnerStack
     * @param block The block to check
     * @return True if the given block is a SpawnerStack
     */
    boolean isSpawner(Block block);

    /**
     * Check if the given block is a SpawnerStack
     * @param location The block to check
     * @return True if the given block is a SpawnerStack
     */
    boolean isSpawner(Location location);

    /**
     * Remove a spawner from the manager
     * @param location The location of the spawner to remove
     * @return The removed spawner
     */
    @Nullable SpawnerStack removeSpawner(Location location);

    /**
     * Get all the SpawnerStack in the manager
     * @return All SpawnerStack in the manager
     */
    @NotNull Collection<SpawnerStack> getStacks();

    /**
     * Get all the SpawnerStack as Data in the manager
     * @return All SpawnerStack as Data in the manager
     */
    @NotNull Collection<Data> getStacksData();
}
