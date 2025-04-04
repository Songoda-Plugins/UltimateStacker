package com.songoda.ultimatestacker.stackable.entity.custom.entities;

import io.lumine.mythic.api.mobs.MobManager;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class MythicMobsCustomEntity extends MythicMobsProvider {

    public  MythicMobsCustomEntity() {
        super(Bukkit.getPluginManager().getPlugin("MythicMobs"));
    }

    @Override
    public String getPluginName() {
        return "MythicMobs";
    }

    @Override
    public boolean isMatchingType(Entity entity) {
        return getMobManager().getActiveMobs().stream().anyMatch(activeMob -> activeMob.getEntity().getBukkitEntity().getType().equals(entity.getType()));
    }

    @Override
    public String getDisplayName(Entity entity) {
        return getMobManager().getActiveMobs().stream()
                .filter(activeMob -> activeMob.getEntity().getBukkitEntity().getUniqueId().equals(entity.getUniqueId()))
                .findFirst()
                .map(ActiveMob::getMobType)
                .orElse(null);
    }

    @Override
    public boolean isSimilar(LivingEntity original, LivingEntity entity) {
        if (!isMatchingType(original) || getMob(entity) == null) return false;
        return getMob(original).getType().equals(getMob(entity).getType());
    }

    @Override
    public String getNBTIdentifier(Entity entity) {
        return getMob(entity).getType().getInternalName();
    }

    @Override
    public LivingEntity spawnFromIdentifier(String string, Location location) {
        if (getMobManager().getMythicMob(string).isPresent()) {
            return null;
        }
        return (LivingEntity)getMobManager().getMythicMob(string).get().spawn(BukkitAdapter.adapt(location), 1).getEntity().getBukkitEntity();
    }

    @Override
    public boolean isCustomEntity(Entity entity) {
        return getMob(entity) != null;
    }

    private ActiveMob getMob(Entity entity) {
        return MythicBukkit.inst().getMobManager().getMythicMobInstance(entity);
    }

    private MobManager getMobManager() {
        return ((MythicBukkit) plugin).getMobManager();
    }
}
