package com.songoda.ultimatestacker.stackable.entity;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.lootables.loot.Drop;
import com.songoda.core.lootables.loot.DropUtils;
import com.songoda.core.nms.Nms;
import com.songoda.core.utils.EntityUtils;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.events.entity.EntityStackKillEvent;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.utils.Async;
import com.songoda.ultimatestacker.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EntityStackImpl implements EntityStack {

    private final UltimateStacker plugin = UltimateStacker.getInstance();
    private static Object STACKED_ENTITY_KEY;
    private int amount;
    private LivingEntity hostEntity;

    static {
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
            STACKED_ENTITY_KEY = new org.bukkit.NamespacedKey(UltimateStacker.getInstance(), "US_AMOUNT");
        }
    }

    /**
     * Gets an existing stack from an entity or creates a new one if it doesn't exist.
     *
     * @param entity The entity to get the stack from.
     */
    public EntityStackImpl(LivingEntity entity) {
        if (entity == null) return;
        if (!UltimateStacker.getInstance().getEntityStackManager().isStackedEntity(entity)) {
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
                PersistentDataContainer container = entity.getPersistentDataContainer();
                if (container.has((org.bukkit.NamespacedKey) STACKED_ENTITY_KEY, PersistentDataType.INTEGER)) {
                    this.amount = container.get((org.bukkit.NamespacedKey) STACKED_ENTITY_KEY, PersistentDataType.INTEGER);
                    entity.setMetadata("US_AMOUNT", new FixedMetadataValue(UltimateStacker.getInstance(), amount));
                } else {
                    entity.setMetadata("US_AMOUNT", new FixedMetadataValue(UltimateStacker.getInstance(), 1));
                    this.amount = 1;
                }
            } else {
                entity.setMetadata("US_AMOUNT", new FixedMetadataValue(UltimateStacker.getInstance(), 1));
                this.amount = 1;
            }
        } else {
            if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
                PersistentDataContainer container = entity.getPersistentDataContainer();
                if (container.has((org.bukkit.NamespacedKey) STACKED_ENTITY_KEY, PersistentDataType.INTEGER)) {
                    this.amount = container.get((org.bukkit.NamespacedKey) STACKED_ENTITY_KEY, PersistentDataType.INTEGER);
                } else {
                    this.amount = getMetaCount(entity);
                }
            } else {
                this.amount = getMetaCount(entity);
            }
        }
        this.hostEntity = entity;
    }

    private int getMetaCount(LivingEntity entity) {
        if (entity.hasMetadata("US_AMOUNT")) {
            return entity.getMetadata("US_AMOUNT").get(0).asInt();
        } else {
            return 1;
        }
    }

    /**
     * Creates a new stack or overrides an existing stack.
     *
     * @param entity The entity to create the stack for.
     * @param amount The amount of entities in the stack.
     */
    public EntityStackImpl(LivingEntity entity, int amount) {
        if (entity == null) return;
        this.hostEntity = entity;
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0");
        }
        this.amount = amount;
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
            PersistentDataContainer container = entity.getPersistentDataContainer();
            container.set((org.bukkit.NamespacedKey) STACKED_ENTITY_KEY, PersistentDataType.INTEGER, amount);
        } else {
            entity.setMetadata("US_AMOUNT", new FixedMetadataValue(UltimateStacker.getInstance(), amount));
        }
        updateNameTag();
    }

    @Override
    public EntityType getType() {
        return hostEntity.getType();
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
            PersistentDataContainer container = hostEntity.getPersistentDataContainer();
            container.set((org.bukkit.NamespacedKey) STACKED_ENTITY_KEY, PersistentDataType.INTEGER, amount);
        } else {
            hostEntity.setMetadata("US_AMOUNT", new FixedMetadataValue(UltimateStacker.getInstance(), amount));
        }
        updateNameTag();
    }

    @Override
    public void add(int amount) {
        this.amount += amount;
        setAmount(this.amount);
    }

    @Override
    public void take(int amount) {
        this.amount -= amount;
        setAmount(this.amount);
    }

    @Override
    public Location getLocation() {
        return hostEntity.getLocation();
    }

    @Override
    public boolean isValid() {
        return hostEntity.isValid();
    }

    @Override
    public UUID getUuid() {
        return hostEntity.getUniqueId();
    }

    @Override
    public LivingEntity getHostEntity() {
        return hostEntity;
    }

    private void handleWholeStackDeath(LivingEntity killed, List<Drop> drops, boolean custom, int droppedExp, EntityDeathEvent event) {

        EntityStack stack = plugin.getEntityStackManager().getStackedEntity(killed);
        // In versions 1.14 and below experience is not dropping. Because of this we are doing this ourselves.
        if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_14)) {
            Location killedLocation = killed.getLocation();
            if (droppedExp > 0)
                killedLocation.getWorld().spawn(killedLocation, ExperienceOrb.class).setExperience(droppedExp * getAmount());
        } else {
            event.setDroppedExp(droppedExp * getAmount());
        }


        if (plugin.getCustomEntityManager().getCustomEntity(killed) == null) {
            Async.run(() -> {
                drops.removeIf(it -> it.getItemStack() != null
                        && it.getItemStack().isSimilar(killed.getEquipment().getItemInHand()));
                for (ItemStack item : killed.getEquipment().getArmorContents()) {
                    drops.removeIf(it -> it.getItemStack() != null && it.getItemStack().isSimilar(item));
                }
                DropUtils.processStackedDrop(killed, plugin.getLootablesManager().getDrops(killed, getAmount()), event);
            });
        }

        event.getDrops().clear();
        destroy();
        if (killed.getKiller() == null) return;
        plugin.addExp(killed.getKiller(), this);
    }

    private void handleSingleStackDeath(LivingEntity killed, List<Drop> drops, int droppedExp, EntityDeathEvent event) {
        Bukkit.getPluginManager().callEvent(new EntityStackKillEvent(this, false));

        Vector velocity = killed.getVelocity().clone();
        killed.remove();
        LivingEntity newEntity = takeOneAndSpawnEntity(killed.getLocation());
        if (newEntity == null) {
            return;
        }

        // In versions 1.14 and below experience is not dropping. Because of this we are doing this ourselves.
        if (ServerVersion.isServerVersionAtOrBelow(ServerVersion.V1_14)) {
            Location killedLocation = killed.getLocation();
            if (droppedExp > 0)
                killedLocation.getWorld().spawn(killedLocation, ExperienceOrb.class).setExperience(droppedExp);
        }
        if (plugin.getCustomEntityManager().getCustomEntity(killed) == null) {
            DropUtils.processStackedDrop(killed, drops, event);
        }

        newEntity.setVelocity(velocity);
        plugin.getEntityStackManager().updateStack(killed, newEntity);
    }

    public void onDeath(LivingEntity killed, List<Drop> drops, boolean custom, int droppedExp, EntityDeathEvent event) {
        killed.setCustomName(null);
        killed.setCustomNameVisible(false);

        boolean killWholeStack = Settings.KILL_WHOLE_STACK_ON_DEATH.getBoolean()
                || plugin.getMobFile().getBoolean("Mobs." + killed.getType().name() + ".Kill Whole Stack");

        if (killWholeStack && getAmount() > 1) {
            handleWholeStackDeath(killed, drops, custom, droppedExp, event);
        } else if (getAmount() > 1) {
            List<String> reasons = Settings.INSTANT_KILL.getStringList();
            EntityDamageEvent lastDamageCause = killed.getLastDamageCause();

            if (lastDamageCause != null) {
                EntityDamageEvent.DamageCause cause = lastDamageCause.getCause();
                for (String s : reasons) {
                    if (!cause.name().equalsIgnoreCase(s)) continue;
                    handleWholeStackDeath(killed, drops, custom, Settings.NO_EXP_INSTANT_KILL.getBoolean() ? 0 : droppedExp, event);
                    return;
                }
            }
            handleSingleStackDeath(killed, drops, droppedExp, event);
        }
    }

    @Override
    public synchronized LivingEntity takeOneAndSpawnEntity(Location location) {
        if (amount <= 0) return null;

        LivingEntity entity = (LivingEntity) Objects.requireNonNull(location.getWorld()).spawnEntity(location, hostEntity.getType());
        if (Settings.NO_AI.getBoolean()) {
            Nms.getImplementations().getEntity().setMobAware(entity, false);
        }
        this.hostEntity = entity;
        setAmount(amount--);
        updateNameTag();
        return entity;
    }

    @Override
    public synchronized void releaseHost() {
        wipeData();

        //Summon a new entity, update the stack and remove the metadata from the old entity
        this.hostEntity = takeOneAndSpawnEntity(hostEntity.getLocation());
        if (amount == 2) {
            wipeData();
        } else {
            setAmount(amount - 1);
            updateNameTag();
        }
    }

    private synchronized void wipeData() {
        hostEntity.setCustomName(null);
        hostEntity.setCustomNameVisible(false);

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)) {
            PersistentDataContainer container = hostEntity.getPersistentDataContainer();
            container.remove((org.bukkit.NamespacedKey) STACKED_ENTITY_KEY);
        } else {
            hostEntity.removeMetadata("US_AMOUNT", plugin);
        }
    }

    @Override
    public synchronized void destroy() {
        if (hostEntity == null) return;
        Bukkit.getScheduler().runTask(plugin, hostEntity::remove);
        hostEntity = null;
    }

    public void updateNameTag() {
        if (hostEntity == null)
            return;

        hostEntity.setCustomNameVisible(!Settings.HOLOGRAMS_ON_LOOK_ENTITY.getBoolean());
        hostEntity.setCustomName(Methods.compileEntityName(hostEntity, getAmount()));
    }
}
