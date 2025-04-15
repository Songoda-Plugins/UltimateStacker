package com.songoda.ultimatestacker.stackable.spawner;

import com.songoda.core.database.Data;
import com.songoda.core.database.SerializedLocation;
import com.songoda.core.nms.Nms;
import com.songoda.core.nms.world.SpawnedEntity;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.core.utils.EntityUtils;
import com.songoda.core.world.SSpawner;
import com.songoda.third_party.org.jooq.impl.DSL;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.UltimateStackerApi;
import com.songoda.ultimatestacker.api.stack.spawner.SpawnerStack;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.utils.Methods;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class SpawnerStackImpl implements SpawnerStack {

    private final UUID uniqueHologramId = UUID.randomUUID();
    private int id;
    private Location location;
    private int amount;

    private SSpawner sSpawner;

    public SpawnerStackImpl() {
    }

    public SpawnerStackImpl(Location location, int amount) {
        this.id = UltimateStacker.getInstance().getDataManager().getNextId("spawners");
        this.location = location;
        this.amount = amount;

        this.sSpawner = new SSpawner(this.location);
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public boolean isValid() {
        return XMaterial.matchXMaterial(this.location.getBlock().getType().name()).filter(material -> material == XMaterial.SPAWNER).isPresent();
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public void add(int amount) {
        this.amount += amount;
    }

    @Override
    public void take(int amount) {
        this.amount -= amount;
    }

    @Override
    public int calculateSpawnCount(EntityType type, boolean ignoreRestrictions) {
        if (!UltimateStacker.getInstance().getMobFile().getBoolean("Mobs." + type.name() + ".Enabled") && !ignoreRestrictions) {
            return 0;
        }

        Random random = new Random();
        int count = 0;
        for (int i = 0; i < getAmount(); ++i) {
            count += random.nextInt(3) + 1;
        }
        return count;
    }

    @Override
    public int spawn() {
        return spawn(-1, Settings.NO_AI.getBoolean());
    }

    @Override
    public int spawn(boolean noAI) {
        return spawn(-1, noAI);
    }

    @Override
    public int spawn(int toSpawn, boolean noAI) {
        CreatureSpawner creatureSpawner = (CreatureSpawner) this.location.getBlock().getState();
        if (toSpawn <= 0) toSpawn = calculateSpawnCount(creatureSpawner.getSpawnedType(), false);
        int finalToSpawn = toSpawn;
        return spawn(toSpawn, "EXPLOSION_NORMAL", null, (e) -> {
            if (noAI) {
                Nms.getImplementations().getEntity().setMobAware(e, false);
            }

            UltimateStacker.getInstance().getEntityStackManager().createStackedEntity(e, finalToSpawn);
            return true;
        }, creatureSpawner.getSpawnedType());
    }

    @Override
    public int spawn(int amountToSpawn, EntityType... types) {
        return this.sSpawner.spawn(amountToSpawn, types);
    }

    @Override
    public int spawn(int amountToSpawn, String particle, Set<XMaterial> canSpawnOn, SpawnedEntity spawned, EntityType... types) {
        return this.sSpawner.spawn(amountToSpawn, particle, canSpawnOn, spawned, types);
    }

    public int getId() {
        return this.id;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("amount", this.amount);
        map.putAll(new SerializedLocation(this.location).asMap());
        return map;
    }

    @Override
    public Data deserialize(Map<String, Object> map) {
        this.id = (int) map.get("id");
        this.amount = (int) map.get("amount");
        this.location = SerializedLocation.of(map);
        this.sSpawner = new SSpawner(this.location);
        return this;
    }

    @Override
    public String getTableName() {
        return "spawners";
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    @Override
    public String getHologramName() {
        if (!(this.location.getBlock().getState() instanceof CreatureSpawner)) {
            UltimateStackerApi.getSpawnerStackManager().removeSpawner(this.location);
            return null;
        }
        CreatureSpawner creatureSpawner = (CreatureSpawner) this.location.getBlock().getState();
        if (creatureSpawner.getSpawnedType() == null) {
            return null;
        }
        return Methods.compileSpawnerName(creatureSpawner.getSpawnedType(), this.amount);
    }

    @Override
    public boolean areHologramsEnabled() {
        return Settings.SPAWNER_HOLOGRAMS.getBoolean();
    }

    public int getX() {
        return this.location.getBlockX();
    }

    public int getY() {
        return this.location.getBlockY();
    }

    public int getZ() {
        return this.location.getBlockZ();
    }

    public World getWorld() {
        return this.location.getWorld();
    }

    @Override
    public String getHologramId() {
        return "UltimateStacker-" + this.uniqueHologramId;
    }

    @Override
    public String toString() {
        return "SpawnerStackImpl{" +
                "uniqueHologramId=" + this.uniqueHologramId +
                ", id=" + this.id +
                ", location=" + this.location +
                ", amount=" + this.amount +
                '}';
    }
}
