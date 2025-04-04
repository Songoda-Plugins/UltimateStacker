package com.songoda.ultimatestacker.stackable.entity.custom.entities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class MythicMobsCustomEntityLegacy extends MythicMobsProvider {


    public MythicMobsCustomEntityLegacy() {
        super(Bukkit.getPluginManager().getPlugin("MythicMobs"));
    }

    @Override
    public String getPluginName() {
        return "MythicMobs";
    }

    @Override
    public boolean isMatchingType(Entity entity) {
        try {
            return (boolean) getMobManager().getClass().getMethod("isActiveMob", UUID.class).invoke(getMobManager(), entity.getUniqueId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getDisplayName(Entity entity) {
        try {
            Object mob = getMob(entity);
            if (mob == null) return null;
            Object type = mob.getClass().getMethod("getType").invoke(mob);
            return type.getClass().getMethod("getDisplayName").invoke(type).toString();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean isSimilar(LivingEntity original, LivingEntity entity) {
        if (!isMatchingType(original) || getMob(entity) == null) return false;
        try {
            Object originalMob = getMob(original);
            Object mob = getMob(entity);
            return originalMob.getClass().getMethod("getType").invoke(originalMob).equals(mob.getClass().getMethod("getType").invoke(mob));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNBTIdentifier(Entity entity) {
        try {
            Object mob = getMob(entity);
            Object type = mob.getClass().getMethod("getType").invoke(mob);
            return getInternalName(type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public LivingEntity spawnFromIdentifier(String string, Location location) {
        return getMobTypes().stream().map(type -> {
            try {
                if (getInternalName(type).equals(string)) {
                    return (LivingEntity) getMobManager().getClass().getMethod("spawnMob", String.class, Location.class).invoke(getMobManager(), getInternalName(type), location);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public boolean isCustomEntity(Entity entity) {
        return getMob(entity) != null;
    }

    private Object getMob(Entity entity) {
        try {
            return getMobManager().getClass().getMethod("getMythicMobInstance", Entity.class).invoke(getMobManager(), entity);
        } catch (Exception e) {
            return null;
        }
    }

    private String getInternalName(Object type) {
        try {
            return type.getClass().getMethod("getInternalName").invoke(type).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Collection<Object> getMobTypes() {
        try {
            Method getMobTypes = getMobManager().getClass().getMethod("getMobTypes");
            return (Collection<Object>) getMobTypes.invoke(getMobManager());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Object getMobManager() {
        try {
            Field mobManager = plugin.getClass().getDeclaredField("mobManager");
            mobManager.setAccessible(true);
            return mobManager.get(plugin);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
