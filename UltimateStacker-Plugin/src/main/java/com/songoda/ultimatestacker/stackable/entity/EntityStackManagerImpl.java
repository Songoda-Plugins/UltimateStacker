package com.songoda.ultimatestacker.stackable.entity;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.api.stack.entity.EntityStackManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataType;

import java.util.UUID;

public class EntityStackManagerImpl implements EntityStackManager {

    private final UltimateStacker plugin;
    private Object STACKED_ENTITY_KEY;

    public EntityStackManagerImpl(UltimateStacker plugin) {
        this.plugin = plugin;
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14))
            this.STACKED_ENTITY_KEY = new org.bukkit.NamespacedKey(plugin, "US_AMOUNT");
    }

    @Override
    public EntityStack createStackedEntity(LivingEntity entity, int amount) {
        EntityStackImpl stackedEntity = new EntityStackImpl(entity, amount);
        stackedEntity.updateNameTag();
        return stackedEntity;
    }

    @Override
    public boolean isStackedEntity(Entity entity) {
        if (entity.hasMetadata("US_AMOUNT")) {
            return true;
        }
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
            return entity.getPersistentDataContainer().has((org.bukkit.NamespacedKey) STACKED_ENTITY_KEY, PersistentDataType.INTEGER);
        }
        return false;
    }

    @Override
    public EntityStack getStackedEntity(UUID entityUUID) {
        Entity entity = Bukkit.getEntity(entityUUID);
        if (entity == null) return null;
        if (!isStackedEntity(entity)) return null;
        return new EntityStackImpl((LivingEntity) entity);
    }

    @Override
    public EntityStack getStackedEntity(LivingEntity entity) {
        if (!isStackedEntity(entity)) return null;
        return new EntityStackImpl(entity);
    }

    @Deprecated()
    public boolean isStackedAndLoaded(LivingEntity entity) {
        return isStackedEntity(entity);
    }

    public int getAmount(Entity entity) {
        if (!isStackedEntity(entity)) return 1;
        if (entity.getMetadata("US_AMOUNT").isEmpty()) return 1;
        return entity.getMetadata("US_AMOUNT").get(0).asInt();
    }

    @Override
    public String getLastPlayerDamage(Entity entity) {
        if (!entity.hasMetadata("US_LAST_PLAYER_DAMAGE")) return null;
        if (entity.getMetadata("US_LAST_PLAYER_DAMAGE").isEmpty()) return null;
        return entity.getMetadata("US_LAST_PLAYER_DAMAGE").get(0).asString();
    }

    @Override
    public void setLastPlayerDamage(Entity entity, Player player) {
        if (player == null) return;
        if (entity == null) return;
        if (entity instanceof Player) return;
        entity.setMetadata("US_LAST_PLAYER_DAMAGE", new FixedMetadataValue(plugin, player.getName()));
    }

    @Override
    public EntityStack transferStack(LivingEntity oldEntity, LivingEntity newEntity, boolean takeOne) {
        EntityStack stack = getStackedEntity(oldEntity);
        if (stack == null) return null;
        EntityStack newStack = new EntityStackImpl(newEntity, takeOne ? stack.getAmount() - 1 : stack.getAmount());
        newStack.updateNameTag();
        stack.destroy();
        return newStack;
    }

    @Override
    public EntityStack updateStack(LivingEntity oldEntity, LivingEntity newEntity) {
        EntityStack stack = getStackedEntity(oldEntity);
        if (stack == null) return null;
        int amount = stack.getAmount() - 1;
        stack.destroy();
        if (amount == 0 && newEntity != null) {
            newEntity.remove();
            return null;
        }
        return createStackedEntity(newEntity, amount);
    }
}
