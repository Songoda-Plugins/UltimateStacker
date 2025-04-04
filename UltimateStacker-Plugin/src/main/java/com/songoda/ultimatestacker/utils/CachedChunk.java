package com.songoda.ultimatestacker.utils;

import com.songoda.core.world.SWorld;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Objects;

public class CachedChunk {

    private final SWorld sWorld;
    private final int x;
    private final int z;

    public CachedChunk(SWorld sWorld, Location location) {
        this(sWorld, (int)location.getX() >> 4, (int)location.getZ() >> 4);
    }

    public CachedChunk(SWorld sWorld, int x, int z) {
        this.sWorld = sWorld;
        this.x = x;
        this.z = z;
    }

    public String getWorld() {
        return sWorld.getWorld().getName();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public Chunk getChunk() {
        World world = sWorld.getWorld();
        if (world == null)
            return null;
        return world.getChunkAt(this.x, this.z);
    }

    public Entity[] getEntities() {
        if (!sWorld.getWorld().isChunkLoaded(x, z)) {
            return new Entity[0];
        }
        Chunk chunk = getChunk();
        return chunk == null ? new Entity[0] : chunk.getEntities();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Chunk) {
            Chunk other = (Chunk) o;
            return getWorld().equals(other.getWorld().getName()) && this.x == other.getX() && this.z == other.getZ();
        } else if (o instanceof CachedChunk) {
            CachedChunk other = (CachedChunk) o;
            return getWorld().equals(other.getWorld()) && this.x == other.getX() && this.z == other.getZ();
        } else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorld(), this.x, this.z);
    }

}
