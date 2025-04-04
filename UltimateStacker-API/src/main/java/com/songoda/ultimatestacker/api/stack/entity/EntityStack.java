package com.songoda.ultimatestacker.api.stack.entity;

import com.songoda.ultimatestacker.api.utils.Stackable;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public interface EntityStack extends Stackable {


    EntityType getType();

    UUID getUuid();

    LivingEntity getHostEntity();

    LivingEntity takeOneAndSpawnEntity(Location location);

    void releaseHost();

    void destroy();

    void updateNameTag();
}
