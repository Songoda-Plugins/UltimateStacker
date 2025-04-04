package com.songoda.ultimatestacker.stackable.entity.custom;

import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.stackable.entity.custom.entities.MythicMobsCustomEntity;
import com.songoda.ultimatestacker.stackable.entity.custom.entities.MythicMobsCustomEntityLegacy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomEntityManager {

    public CustomEntityManager() {
        load();
    }

    private final List<CustomEntity> registeredCustomEntities = new ArrayList<>();

    public void load() {
        if (isEnabled("MythicMobs")) {
            Plugin plugin = Bukkit.getPluginManager().getPlugin("MythicMobs");
            if (plugin.getDescription().getVersion().startsWith("4.")) {
                registeredCustomEntities.add(new MythicMobsCustomEntityLegacy());
            } else {
                registeredCustomEntities.add(new MythicMobsCustomEntity());
            }
        }
    }

    public boolean isEnabled(String plugin) {
        return Bukkit.getPluginManager().isPluginEnabled(plugin)
                && Settings.ENABLED_CUSTOM_ENTITY_PLUGINS.getStringList().contains(plugin);
    }

    public CustomEntity getCustomEntity(Entity entity) {
        for (CustomEntity customEntity : registeredCustomEntities) {
            if (customEntity.isMatchingType(entity) && customEntity.isCustomEntity(entity)) {
                if (Settings.BLACKLISTED_CUSTOM_ENTITIES.getStringList()
                        .contains((customEntity.getPluginName() + "_" + customEntity.getNBTIdentifier(entity)).toLowerCase()))
                    continue;
                return customEntity;
            }
        }
        return null;
    }

    public List<CustomEntity> getRegisteredCustomEntities() {
        return Collections.unmodifiableList(registeredCustomEntities);
    }

    public boolean isCustomEntity(Entity entity) {
        return getCustomEntity(entity) != null && getCustomEntity(entity).isCustomEntity(entity);
    }

    public boolean isStackable(Entity entity) {
        CustomEntity customEntity = getCustomEntity(entity);
        if (customEntity == null) return true;
        String key = customEntity.getPluginName().toLowerCase() + "_" + customEntity.getNBTIdentifier(entity).toLowerCase();
        return !Settings.BLACKLISTED_CUSTOM_ENTITIES.getStringList().contains(key);
    }
}
