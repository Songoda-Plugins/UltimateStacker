package com.songoda.ultimatestacker.stackable.spawner;

import com.songoda.core.database.Data;
import com.songoda.ultimatestacker.api.stack.spawner.SpawnerStack;
import com.songoda.ultimatestacker.api.stack.spawner.SpawnerStackManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnerStackManagerImpl implements SpawnerStackManager {

    private final Map<Location, SpawnerStack> registeredSpawners = new HashMap<>();

    @Override
    public void addSpawners(List<SpawnerStack> spawners) {
        for (SpawnerStack spawner : spawners) {
            this.registeredSpawners.put(roundLocation(spawner.getLocation()), spawner);
        }
    }

    @Override
    public SpawnerStack addSpawner(SpawnerStack spawnerStack) {
        this.registeredSpawners.put(roundLocation(spawnerStack.getLocation()), spawnerStack);
        return spawnerStack;
    }

    @Override
    public @Nullable SpawnerStack removeSpawner(Location location) {
        return registeredSpawners.remove(roundLocation(location));
    }

    @Override
    public SpawnerStack getSpawner(Location location) {
        return this.registeredSpawners.get(roundLocation(location));
    }

    @Override
    public boolean isSpawner(Block block) {
        return isSpawner(roundLocation(block.getLocation()));
    }

    @Override
    public SpawnerStack getSpawner(Block block) {
        return this.getSpawner(roundLocation(block.getLocation()));
    }

    @Override
    public boolean isSpawner(Location location) {
        return this.registeredSpawners.get(roundLocation(location)) != null;
    }

    @Override
    public @NotNull Collection<SpawnerStack> getStacks() {
        return Collections.unmodifiableCollection(this.registeredSpawners.values());
    }

    @Override
    public @NotNull Collection<Data> getStacksData() {
        return Collections.unmodifiableCollection(this.registeredSpawners.values());
    }

    private Location roundLocation(Location location) {
        location = location.clone();
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());
        return location;
    }
}
