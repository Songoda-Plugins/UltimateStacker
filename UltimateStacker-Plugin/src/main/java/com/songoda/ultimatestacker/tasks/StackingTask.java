package com.songoda.ultimatestacker.tasks;

import com.songoda.core.compatibility.ServerVersion;
import com.songoda.core.hooks.WorldGuardHook;
import com.songoda.core.world.SWorld;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.api.stack.entity.EntityStackManager;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.stackable.entity.Check;
import com.songoda.ultimatestacker.stackable.entity.custom.CustomEntity;
import com.songoda.ultimatestacker.utils.CachedChunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static com.songoda.ultimatestacker.stackable.entity.Check.getChecks;

public class StackingTask extends BukkitRunnable {

    private final UltimateStacker plugin;
    private final EntityStackManager stackManager;
    private final BreedingTask breedingTask;

    private final ConfigurationSection configurationSection = UltimateStacker.getInstance().getMobFile();
    private final List<UUID> processed = new ArrayList<>();

    private final Map<EntityType, Integer> entityStackSizes = new ConcurrentHashMap<>();
    private final int maxEntityStackSize = Settings.MAX_STACK_ENTITIES.getInt(),
            minEntityStackSize = Settings.MIN_STACK_ENTITIES.getInt(),
            searchRadius = Settings.SEARCH_RADIUS.getInt(),
            chunkRadius = Settings.STACK_WHOLE_CHUNK_RADIUS.getInt(),
            maxPerTypeStacksPerChunk = Settings.MAX_PER_TYPE_STACKS_PER_CHUNK.getInt();
    private final List<String> disabledWorlds = Settings.DISABLED_WORLDS.getStringList(),
            stackReasons = Settings.STACK_REASONS.getStringList();
    private final List<Check> checks = getChecks(Settings.STACK_CHECKS.getStringList());
    private final boolean stackFlyingDown = Settings.ONLY_STACK_FLYING_DOWN.getBoolean(),
            stackWholeChunk = Settings.STACK_WHOLE_CHUNK.getBoolean(),
            weaponsArentEquipment = Settings.WEAPONS_ARENT_EQUIPMENT.getBoolean(),
            onlyStackFromSpawners = Settings.ONLY_STACK_FROM_SPAWNERS.getBoolean(),
            onlyStackOnSurface = Settings.ONLY_STACK_ON_SURFACE.getBoolean();

    private final Set<SWorld> loadedWorlds;

    public StackingTask(UltimateStacker plugin) {
        this.plugin = plugin;
        stackManager = plugin.getEntityStackManager();
        breedingTask = plugin.getBreedingTask();

        // Add loaded worlds.
        loadedWorlds = new HashSet<>();
        for (World world : Bukkit.getWorlds()) {
            //Filter disabled worlds to avoid continuous checks in the stacking loop
            if (isWorldDisabled(world)) continue;
            loadedWorlds.add(new SWorld(world));
        }
    }

    public void start() {
        int tickRate = Settings.STACK_SEARCH_TICK_SPEED.getInt();
        runTaskTimerAsynchronously(plugin, tickRate, tickRate);
    }

    @Override
    public void run() {
        //Make sure to continue the task if any exception occurs
        try {
            // Loop through each world.
            for (SWorld sWorld : loadedWorlds) {
                List<LivingEntity> entities;
                // Get the loaded entities from the current world and reverse them.
                try {
                    entities = getLivingEntitiesSync(sWorld).get();
                } catch (ExecutionException | InterruptedException ex) {
                    ex.printStackTrace();
                    continue;
                }

                //Filter non-stackable entities to improve performance on main thread
                entities.removeIf(this::isEntityNotStackable);

                List<LivingEntity> remove = new ArrayList<>();
                for (LivingEntity entity : entities) { //Q: What can cause current modification exception here?
                    // Check our WorldGuard flag.
                    Boolean flag = WorldGuardHook.isEnabled() ? WorldGuardHook.getBooleanFlag(entity.getLocation(), "mob-stacking") : null; //Does this work async?
                    if (flag != null && !flag) {
                        remove.add(entity);
                    }
                }
                entities.removeAll(remove);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    // Loop through the entities.
                    for (LivingEntity entity : entities) {
                        // Make sure our entity has not already been processed.
                        // Skip it if it has been.
                        if (processed.contains(entity.getUniqueId())) continue;

                        // Get entity location to pass around as its faster this way.
                        Location location = entity.getLocation();

                        // Process the entity.
                        processEntity(entity, location, entities);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Make sure we clear the processed list.
            this.processed.clear();
        }
    }

    private Future<List<LivingEntity>> getLivingEntitiesSync(SWorld sWorld) {
        CompletableFuture<List<LivingEntity>> future = new CompletableFuture<>();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> future.complete(sWorld.getLivingEntities()));
        return future;
    }

    public boolean isWorldDisabled(World world) {
        return disabledWorlds.stream().anyMatch(worldStr -> world.getName().equalsIgnoreCase(worldStr));
    }

    //Returns true if the entity is not stackable, and it will be removed from the list
    private boolean isEntityNotStackable(LivingEntity entity) {
        if (!configurationSection.getBoolean("Mobs." + entity.getType().name() + ".Enabled")) return true;

        if (isMaxStack(entity)) return true;

        // Make sure we have the correct entity type and that it is valid.
        if (!entity.isValid()
                || entity instanceof HumanEntity
                || entity instanceof ArmorStand

                // Make sure the entity is not in love or in the breeding queue.
                || breedingTask.isInQueue(entity.getUniqueId()))
            return true;

        //Check nametag or custom entity
        if ((!stackManager.isStackedEntity(entity) && entity.getCustomName() != null) || plugin.getCustomEntityManager().getCustomEntity(entity) != null)
            return true;

        // Allow spawn if stack reasons are set and match, or if from a spawner
        final String spawnReason = entity.hasMetadata("US_REASON") && !entity.getMetadata("US_REASON").isEmpty()
                ? entity.getMetadata("US_REASON").get(0).asString() : null;
        List<String> stackReasons;
        if (onlyStackFromSpawners) {
            // If only stack from spawners is enabled, make sure the entity spawned from a spawner.
            if (!"SPAWNER".equals(spawnReason))
                return true;
        } else if (!(stackReasons = this.stackReasons).isEmpty() && !stackReasons.contains(spawnReason)) {
            // Only stack if on the list of events to stack
            return true;
        }

        // If only stack on surface is enabled make sure the entity is on a surface then entity is stackable.
        //return !onlyStackOnSurface || canFly(entity) || entity.getType().name().equals("SHULKER") || ((entity).isOnGround() || (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) && (entity).isSwimming()));
        return onlyStackOnSurface && canFly(entity) && !entity.getType().name().equals("SHULKER") && !entity.isOnGround() && !(ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) && (entity).isSwimming());
    }

    private void processEntity(LivingEntity baseEntity, Location location, List<LivingEntity> entities) {
        if (baseEntity instanceof EnderDragon) {
            // Ender dragons are not stackable.
            return;
        }

        // Get the stack from the entity. It should be noted that this value will
        // be null if our entity is not a stack.
        EntityStack baseStack = plugin.getEntityStackManager().getStackedEntity(baseEntity);

        // Get the maximum stack size for this entity.
        int maxEntityStackSize = getEntityMaxStackSize(baseEntity);

        // Is this entity stacked?
        boolean isStack = baseStack != null;

        // The amount that is stackable.
        int baseSize = isStack ? baseStack.getAmount() : 1;

        // Attempt to split overstacked entities.
        // If this is successful, we can return because the entity was processed
        if (isStack && attemptSplit(baseStack, maxEntityStackSize)) {
            return;
        }

        // Get similar entities around our entity and make sure those entities are both compatible and stackable.
        List<LivingEntity> stackableFriends = getSimilarEntitiesAroundEntity(baseEntity, location);

        //Total entities that can be stacked into the base entity
        int maxStackable = maxEntityStackSize - baseSize;
        int toStack = 0;
        List<LivingEntity> remove = new ArrayList<>();

        // Loop through our similar stackable entities.
        for (LivingEntity friendlyEntity : stackableFriends) {

            if (!entities.contains(friendlyEntity))
                continue;

            // Process similar entities.
            EntityStack friendStack = stackManager.getStackedEntity(friendlyEntity);
            int amount = friendStack != null ? friendStack.getAmount() : 1;
            if (toStack + amount <= maxStackable) {
                toStack += amount;
                remove.add(friendlyEntity);
                continue;
            }
            break; //We max, exit loop
        }

        //Nothing to stack
        if (toStack == 0) {
            return;
        }

        //Add to base stack and remove stacked friends
        stackManager.createStackedEntity(baseEntity, baseSize + toStack);
        processed.add(baseEntity.getUniqueId());

        //Remove merged entities
        //We in sync, so we can remove entities
        for (LivingEntity entity : remove) {
            processed.add(entity.getUniqueId());
            entity.remove();
        }
    }

    /**
     * This method splitting overstacked entities into new stacks.
     * Must be called synchronously.
     *
     * @param baseStack          The base stack to check for splitting.
     * @param maxEntityStackSize The maximum stack size for the entity. -1 if we need to calculate it.
     * @return True if the split was successful, false otherwise.
     */
    public boolean attemptSplit(EntityStack baseStack, int maxEntityStackSize) {
        LivingEntity hostEntity = baseStack.getHostEntity();
        int stackSize = baseStack.getAmount();
        int maxEntityStackAmount = maxEntityStackSize == -1 ? getEntityMaxStackSize(hostEntity) : maxEntityStackSize;

        if (stackSize <= maxEntityStackAmount) return false;

        baseStack.setAmount(maxEntityStackAmount);

        int finalStackSize = stackSize - maxEntityStackAmount;
        do {
            // Create a new stack, summon entity and add to stack.
            LivingEntity newEntity = (LivingEntity) hostEntity.getWorld().spawnEntity(hostEntity.getLocation(), hostEntity.getType());
            int toAdd = Math.min(finalStackSize, maxEntityStackAmount);
            EntityStack newStack = stackManager.createStackedEntity(newEntity, toAdd);
            processed.add(newEntity.getUniqueId());
            finalStackSize -= maxEntityStackAmount;
        } while (finalStackSize >= 0);

        //Mark it as processed.
        processed.add(hostEntity.getUniqueId());
        return true;
    }

    private Set<CachedChunk> getNearbyChunks(SWorld sWorld, Location location) {
        //Only stack entities in the same chunk
        if (stackWholeChunk && chunkRadius == 0) {
            return Collections.singleton(new CachedChunk(sWorld, location.getChunk().getX(), location.getChunk().getZ()));
        }
        World world = location.getWorld();
        if (world == null) return new HashSet<>();

        CachedChunk firstChunk = new CachedChunk(sWorld, location);
        Set<CachedChunk> chunks = new TreeSet<>(Comparator.comparingInt(CachedChunk::getX).thenComparingInt(CachedChunk::getZ));
        chunks.add(firstChunk);

        //Calculate chunk coordinates we need to check
        int minX = (int) Math.floor((location.getX() - chunkRadius) / 16.0D);
        int maxX = (int) Math.floor((location.getX() + chunkRadius) / 16.0D);
        int minZ = (int) Math.floor((location.getZ() - chunkRadius) / 16.0D);
        int maxZ = (int) Math.floor((location.getZ() + chunkRadius) / 16.0D);

        for (int x = minX; x <= maxX; ++x) {
            for (int z = minZ; z <= maxZ; ++z) {
                if (x == minX || x == maxX || z == minZ || z == maxZ) {
                    chunks.add(new CachedChunk(sWorld, x, z));
                }
            }
        }
        return chunks;
    }

    /**
     * Get all entities around an entity within a radius which are similar to the entity.
     *
     * @param entity The entity to get similar entities around.
     * @return A list of similar entities around the entity.
     */
    public List<LivingEntity> getFriendlyStacksNearby(LivingEntity entity) {
        if (!stackWholeChunk) {
            return entity.getNearbyEntities(searchRadius / 2.0, searchRadius / 2.0, searchRadius / 2.0)
                    .stream().filter(e -> e.getType() == entity.getType() && !isMaxStack((LivingEntity) e))
                    .map(e -> (LivingEntity) e).collect(Collectors.toList());
        }
        List<LivingEntity> entities = new ArrayList<>();
        try {
            Set<CachedChunk> chunks = getNearbyChunks(new SWorld(entity.getWorld()), entity.getLocation());
            for (CachedChunk chunk : chunks) {
                Entity[] entityList = chunk.getEntities();
                for (Entity e : entityList) {
                    if (e.getType() == entity.getType() && !isMaxStack((LivingEntity) e)) {
                        entities.add((LivingEntity) e);
                    }
                }
            }
            entities.removeIf(entity1 -> entity1.equals(entity) || !UltimateStacker.getInstance().getCustomEntityManager().isStackable(entity1));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return entities;
    }

    public List<LivingEntity> getSimilarEntitiesAroundEntity(LivingEntity initialEntity, Location location) {
        try {
            // Create a list of all entities around the initial entity of the same type.
            List<LivingEntity> entityList = new ArrayList<>(getFriendlyStacksNearby(initialEntity));

            CustomEntity customEntity = plugin.getCustomEntityManager().getCustomEntity(initialEntity);
            if (customEntity != null)
                entityList.removeIf(entity -> !customEntity.isSimilar(initialEntity, entity));

            if (stackFlyingDown && canFly(initialEntity))
                entityList.removeIf(entity -> entity.getLocation().getY() > initialEntity.getLocation().getY());

            for (Check check : checks) {
                if (check == null) continue;
                switch (check) {
                    case SPAWN_REASON: {
                        if (initialEntity.hasMetadata("US_REASON"))
                            entityList.removeIf(entity -> entity.hasMetadata("US_REASON") && !entity.getMetadata("US_REASON").get(0).asString().equals("US_REASON"));
                    }
                    case AGE: {
                        if (!(initialEntity instanceof Ageable)) break;

                        if (((Ageable) initialEntity).isAdult()) {
                            entityList.removeIf(entity -> !((Ageable) entity).isAdult());
                        } else {
                            entityList.removeIf(entity -> ((Ageable) entity).isAdult());
                        }
                        break;
                    }
                    case NERFED: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)) break;
                        entityList.removeIf(entity -> entity.hasAI() != initialEntity.hasAI());
                    }
                    case IS_TAMED: {
                        if (!(initialEntity instanceof Tameable)) break;
                        if (((Tameable) initialEntity).isTamed()) {
                            entityList.removeIf(entity -> !((Tameable) entity).isTamed());
                        } else {
                            entityList.removeIf(entity -> ((Tameable) entity).isTamed());
                        }
                    }
                    case ANIMAL_OWNER: {
                        if (!(initialEntity instanceof Tameable)) break;

                        Tameable tameable = ((Tameable) initialEntity);
                        entityList.removeIf(entity -> ((Tameable) entity).getOwner() != tameable.getOwner());
                    }
                    case PIG_SADDLE: {
                        if (!(initialEntity instanceof Pig)) break;
                        entityList.removeIf(entity -> ((Pig) entity).hasSaddle());
                        break;
                    }
                    case SKELETON_TYPE: {
                        if (!(initialEntity instanceof Skeleton)) break;

                        Skeleton skeleton = (Skeleton) initialEntity;
                        entityList.removeIf(entity -> ((Skeleton) entity).getSkeletonType() != skeleton.getSkeletonType());
                        break;
                    }
                    case SHEEP_COLOR: {
                        if (!(initialEntity instanceof Sheep)) break;

                        Sheep sheep = ((Sheep) initialEntity);
                        entityList.removeIf(entity -> ((Sheep) entity).getColor() != sheep.getColor());
                        break;
                    }
                    case SHEEP_SHEARED: {
                        if (!(initialEntity instanceof Sheep)) break;

                        Sheep sheep = ((Sheep) initialEntity);
                        if (sheep.isSheared()) {
                            entityList.removeIf(entity -> !((Sheep) entity).isSheared());
                        } else {
                            entityList.removeIf(entity -> ((Sheep) entity).isSheared());
                        }
                        break;
                    }
                    case SNOWMAN_DERPED: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_9)
                                || !(initialEntity instanceof Snowman)) break;

                        Snowman snowman = ((Snowman) initialEntity);
                        if (snowman.isDerp()) {
                            entityList.removeIf(entity -> !((Snowman) entity).isDerp());
                        } else {
                            entityList.removeIf(entity -> ((Snowman) entity).isDerp());
                        }
                        break;
                    }
                    case LLAMA_COLOR: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)
                                || !(initialEntity instanceof Llama)) break;
                        Llama llama = ((Llama) initialEntity);
                        entityList.removeIf(entity -> ((Llama) entity).getColor() != llama.getColor());
                        break;
                    }
                    case LLAMA_STRENGTH: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)
                                || !(initialEntity instanceof Llama)) break;
                        Llama llama = ((Llama) initialEntity);
                        entityList.removeIf(entity -> ((Llama) entity).getStrength() != llama.getStrength());
                        break;
                    }
                    case VILLAGER_PROFESSION: {
                        if (!(initialEntity instanceof Villager)) break;
                        Villager villager = ((Villager) initialEntity);
                        entityList.removeIf(entity -> ((Villager) entity).getProfession() != villager.getProfession());
                        break;
                    }
                    case SLIME_SIZE: {
                        if (!(initialEntity instanceof Slime)) break;
                        Slime slime = ((Slime) initialEntity);
                        entityList.removeIf(entity -> ((Slime) entity).getSize() != slime.getSize());
                        break;
                    }
                    case HORSE_CARRYING_CHEST: {
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                            if (!(initialEntity instanceof ChestedHorse)) break;
                            entityList.removeIf(entity -> ((ChestedHorse) entity).isCarryingChest());
                        } else {
                            if (!(initialEntity instanceof Horse)) break;
                            entityList.removeIf(entity -> ((Horse) entity).isCarryingChest());
                        }
                        break;
                    }
                    case HORSE_HAS_ARMOR: {
                        if (!(initialEntity instanceof Horse)) break;
                        entityList.removeIf(entity -> ((Horse) entity).getInventory().getArmor() != null);
                        break;
                    }
                    case HORSE_HAS_SADDLE: {
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                && initialEntity instanceof AbstractHorse) {
                            entityList.removeIf(entity -> ((AbstractHorse) entity).getInventory().getSaddle() != null);
                            break;
                        }
                        if (!(initialEntity instanceof Horse)) break;
                        entityList.removeIf(entity -> ((Horse) entity).getInventory().getSaddle() != null);
                        break;
                    }
                    case HORSE_JUMP: {
                        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_11)) {
                            if (!(initialEntity instanceof AbstractHorse)) break;
                            AbstractHorse horse = ((AbstractHorse) initialEntity);
                            entityList.removeIf(entity -> ((AbstractHorse) entity).getJumpStrength() != horse.getJumpStrength());
                        } else {
                            if (!(initialEntity instanceof Horse)) break;
                            Horse horse = ((Horse) initialEntity);
                            entityList.removeIf(entity -> ((Horse) entity).getJumpStrength() != horse.getJumpStrength());

                        }
                        break;
                    }
                    case HORSE_COLOR: {
                        if (!(initialEntity instanceof Horse)) break;
                        Horse horse = ((Horse) initialEntity);
                        entityList.removeIf(entity -> ((Horse) entity).getColor() != horse.getColor());
                        break;
                    }
                    case HORSE_STYLE: {
                        if (!(initialEntity instanceof Horse)) break;
                        Horse horse = ((Horse) initialEntity);
                        entityList.removeIf(entity -> ((Horse) entity).getStyle() != horse.getStyle());
                        break;
                    }
                    case ZOMBIE_BABY: {
                        if (!(initialEntity instanceof Zombie)) break;
                        Zombie zombie = (Zombie) initialEntity;
                        entityList.removeIf(entity -> ((Zombie) entity).isBaby() != zombie.isBaby());
                        break;
                    }
                    case WOLF_COLLAR_COLOR: {
                        if (!(initialEntity instanceof Wolf)) break;
                        Wolf wolf = (Wolf) initialEntity;
                        entityList.removeIf(entity -> ((Wolf) entity).getCollarColor() != wolf.getCollarColor());
                        break;
                    }
                    case OCELOT_TYPE: {
                        if (!(initialEntity instanceof Ocelot)) break;
                        Ocelot ocelot = (Ocelot) initialEntity;
                        entityList.removeIf(entity -> ((Ocelot) entity).getCatType() != ocelot.getCatType());
                    }
                    case CAT_TYPE: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)
                                || !(initialEntity instanceof Cat)) break;
                        Cat cat = (Cat) initialEntity;
                        entityList.removeIf(entity -> ((Cat) entity).getCatType() != cat.getCatType());
                        break;
                    }
                    case HAS_EQUIPMENT: {
                        if (initialEntity.getEquipment() == null) break;
                        boolean imEquipped = isEquipped(initialEntity);
                        if (imEquipped)
                            entityList = new ArrayList<>();
                        else
                            entityList.removeIf(this::isEquipped);
                        break;
                    }
                    case RABBIT_TYPE: {
                        if (!(initialEntity instanceof Rabbit)) break;
                        Rabbit rabbit = (Rabbit) initialEntity;
                        entityList.removeIf(entity -> ((Rabbit) entity).getRabbitType() != rabbit.getRabbitType());
                        break;
                    }
                    case PARROT_TYPE: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)
                                || !(initialEntity instanceof Parrot)) break;
                        Parrot parrot = (Parrot) initialEntity;
                        entityList.removeIf(entity -> ((Parrot) entity).getVariant() != parrot.getVariant());
                        break;
                    }
                    case PUFFERFISH_STATE: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                || !(initialEntity instanceof PufferFish)) break;
                        PufferFish pufferFish = (PufferFish) initialEntity;
                        entityList.removeIf(entity -> ((PufferFish) entity).getPuffState() != pufferFish.getPuffState());
                        break;
                    }
                    case TROPICALFISH_PATTERN: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                || !(initialEntity instanceof TropicalFish)) break;
                        TropicalFish tropicalFish = (TropicalFish) initialEntity;
                        entityList.removeIf(entity -> ((TropicalFish) entity).getPattern() != tropicalFish.getPattern());
                        break;
                    }
                    case TROPICALFISH_PATTERN_COLOR: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                || !(initialEntity instanceof TropicalFish)) break;
                        TropicalFish tropicalFish = (TropicalFish) initialEntity;
                        entityList.removeIf(entity -> ((TropicalFish) entity).getPatternColor() != tropicalFish.getPatternColor());
                        break;
                    }
                    case TROPICALFISH_BODY_COLOR: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                || !(initialEntity instanceof TropicalFish)) break;
                        TropicalFish tropicalFish = (TropicalFish) initialEntity;
                        entityList.removeIf(entity -> ((TropicalFish) entity).getBodyColor() != tropicalFish.getBodyColor());
                        break;
                    }
                    case PHANTOM_SIZE: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13)
                                || !(initialEntity instanceof Phantom)) break;
                        Phantom phantom = (Phantom) initialEntity;
                        entityList.removeIf(entity -> ((Phantom) entity).getSize() != phantom.getSize());
                        break;
                    }
                    case AXOLOTL_VARIANT: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_17)
                                || !(initialEntity instanceof Axolotl)) break;
                        Axolotl axolotl = (Axolotl) initialEntity;
                        entityList.removeIf(entity -> ((Axolotl) entity).getVariant() != axolotl.getVariant());
                        break;
                    }
                    case GOAT_HAS_HORNS: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_17)
                                || !(initialEntity instanceof Goat)) break;
                        Goat goat = (Goat) initialEntity;
                        boolean hasLeftHorn = goat.hasLeftHorn();
                        boolean hasRightHorn = goat.hasRightHorn();
                        entityList.removeIf(entity -> {
                            Goat otherGoat = (Goat) entity;
                            return otherGoat.hasLeftHorn() != hasLeftHorn || otherGoat.hasRightHorn() != hasRightHorn;
                        });
                        break;
                    }
                    case FROG_VARIANT: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_19)
                                || !(initialEntity instanceof Frog)) break;
                        Frog frog = (Frog) initialEntity;
                        entityList.removeIf(entity -> ((Frog) entity).getVariant() != frog.getVariant());
                        break;
                    }
                    case TADPOLE_AGE: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_19)
                                || !(initialEntity instanceof Tadpole)) break;
                        Tadpole tadpole = (Tadpole) initialEntity;
                        entityList.removeIf(entity -> ((Tadpole) entity).getAge() != tadpole.getAge());
                        break;
                    }
                    case WARDEN_ANGER_LEVEL: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_19)
                                || !(initialEntity instanceof Warden)) break;
                        Warden warden = (Warden) initialEntity;
                        entityList.removeIf(entity -> ((Warden) entity).getAnger() != warden.getAnger());
                        break;
                    }
                    case FOX_TYPE: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_14)
                                || !(initialEntity instanceof Fox)) break;
                        Fox fox = (Fox) initialEntity;
                        entityList.removeIf(entity -> ((Fox) entity).getFoxType() != fox.getFoxType());
                        break;
                    }
                    case HOGLIN_IMMUNE: {
                        if (!ServerVersion.isServerVersionAtLeast(ServerVersion.V1_16)
                                || !(initialEntity instanceof Hoglin)) break;
                        Hoglin hoglin = (Hoglin) initialEntity;
                        if (hoglin.isImmuneToZombification()) {
                            entityList.removeIf(entity -> !((Hoglin) entity).isImmuneToZombification());
                        } else {
                            entityList.removeIf(entity -> ((Hoglin) entity).isImmuneToZombification());
                        }
                        break;
                    }
                }
            }

            if (initialEntity.hasMetadata("breedCooldown")) {
                entityList.removeIf(entity -> !entity.hasMetadata("breedCooldown"));
            }
            return entityList;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean isEquipped(LivingEntity initialEntity) {
        if (initialEntity.getEquipment() == null) return false;
        EntityEquipment equipment = initialEntity.getEquipment();

        return (equipment.getItemInHand().getType() != Material.AIR
                && !weaponsArentEquipment && !equipment.getItemInHand().getEnchantments().isEmpty()
                || (equipment.getHelmet() != null && equipment.getHelmet().getType() != Material.AIR)
                || (equipment.getChestplate() != null && equipment.getChestplate().getType() != Material.AIR)
                || (equipment.getLeggings() != null && equipment.getLeggings().getType() != Material.AIR)
                || (equipment.getBoots() != null && equipment.getBoots().getType() != Material.AIR));
    }

    private int getEntityMaxStackSize(LivingEntity initialEntity) {
        return entityStackSizes.computeIfAbsent(initialEntity.getType(), type -> {
            int maxStackSize = configurationSection.getInt("Mobs." + initialEntity.getType().name() + ".Max Stack Size");
            if (maxStackSize == -1) {
                maxStackSize = maxEntityStackSize;
            }
            return maxStackSize;
        });
    }

    private boolean isMaxStack(LivingEntity livingEntity) {
        EntityStack stack = stackManager.getStackedEntity(livingEntity);
        if (stack == null) return false;
        return stack.getAmount() >= getEntityMaxStackSize(livingEntity);
    }

    public boolean canFly(LivingEntity entity) {
        switch (entity.getType()) {
            case GHAST:
            case BLAZE:
            case PHANTOM:
            case BAT:
            case BEE:
                return true;
            default:
                return false;
        }
    }
}
