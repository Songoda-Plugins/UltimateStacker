package com.songoda.ultimatestacker.stackable.entity.custom;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

public abstract class CustomEntity {

    protected final Plugin plugin;

    protected CustomEntity(Plugin plugin) {
        this.plugin = plugin;
    }

    public abstract String getPluginName();

    public abstract boolean isMatchingType(Entity entity);

    public abstract String getDisplayName(Entity entity);

    public abstract boolean isSimilar(LivingEntity original, LivingEntity entity);

    public abstract String getNBTIdentifier(Entity entity);

    public abstract LivingEntity spawnFromIdentifier(String string, Location location);

    public abstract boolean isCustomEntity(Entity entity);
}
