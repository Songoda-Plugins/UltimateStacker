package com.songoda.ultimatestacker.tasks;

import com.songoda.core.thread.TaskScheduler;
import com.songoda.ultimatestacker.UltimateStacker;
import org.bukkit.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BreedingTask extends TaskScheduler {

    private final UltimateStacker plugin;

    private final Set<UUID> breedingEntities = new HashSet<>();
    private final Set<UUID> inLoveEntities = new HashSet<>();

    public BreedingTask(UltimateStacker plugin) {
        super(plugin);
        this.plugin = plugin;

    }

    public void addBreedingTicket(LivingEntity mother, LivingEntity father) {
        UUID motherUUID = mother.getUniqueId();
        UUID fatherUUID = father.getUniqueId();

        addTask(() -> {
            breedingEntities.remove(motherUUID);
            breedingEntities.remove(fatherUUID);
        }, 20L * 1000L);

        breedingEntities.add(motherUUID);
        inLoveEntities.remove(motherUUID);
        breedingEntities.add(fatherUUID);
        inLoveEntities.remove(fatherUUID);
    }

    public void addInLoveTicket(LivingEntity entity) {
        UUID entityUUID = entity.getUniqueId();
        addTask(() -> inLoveEntities.remove(entityUUID), 5L * 60L * 1000L);
        inLoveEntities.add(entityUUID);
    }

    public boolean isInQueue(UUID uniqueId) {
        return breedingEntities.contains(uniqueId) || inLoveEntities.contains(uniqueId);
    }
}
