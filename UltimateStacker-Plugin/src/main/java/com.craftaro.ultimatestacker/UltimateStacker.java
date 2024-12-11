package com.craftaro.ultimatestacker;

import com.craftaro.core.SongodaCore;
import com.craftaro.core.SongodaPlugin;
import com.craftaro.core.commands.CommandManager;
import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.core.configuration.Config;
import com.craftaro.core.database.DataManager;
import com.craftaro.core.dependency.Dependency;
import com.craftaro.core.gui.GuiManager;
import com.craftaro.core.hooks.EntityStackerManager;
import com.craftaro.core.hooks.HologramManager;
import com.craftaro.core.hooks.ProtectionManager;
import com.craftaro.core.hooks.WorldGuardHook;
import com.craftaro.core.utils.TextUtils;
import com.craftaro.third_party.com.cryptomorin.xseries.XMaterial;
import com.craftaro.ultimatestacker.api.UltimateStackerApi;
import com.craftaro.ultimatestacker.api.stack.block.BlockStack;
import com.craftaro.ultimatestacker.api.stack.block.BlockStackManager;
import com.craftaro.ultimatestacker.api.stack.entity.EntityStack;
import com.craftaro.ultimatestacker.api.stack.entity.EntityStackManager;
import com.craftaro.ultimatestacker.api.stack.item.StackedItemManager;
import com.craftaro.ultimatestacker.api.stack.spawner.SpawnerStack;
import com.craftaro.ultimatestacker.api.stack.spawner.SpawnerStackManager;
import com.craftaro.ultimatestacker.api.utils.Hologramable;
import com.craftaro.ultimatestacker.commands.*;
import com.craftaro.ultimatestacker.database.migrations._1_InitialMigration;
import com.craftaro.ultimatestacker.database.migrations._2_EntityStacks;
import com.craftaro.ultimatestacker.database.migrations._3_BlockStacks;
import com.craftaro.ultimatestacker.database.migrations._6_RemoveStackedEntityTable;
import com.craftaro.ultimatestacker.hook.StackerHook;
import com.craftaro.ultimatestacker.hook.hooks.JobsHook;
import com.craftaro.ultimatestacker.hook.hooks.SuperiorSkyblock2Hook;
import com.craftaro.ultimatestacker.listeners.*;
import com.craftaro.ultimatestacker.listeners.entity.EntityCurrentListener;
import com.craftaro.ultimatestacker.listeners.entity.EntityListeners;
import com.craftaro.ultimatestacker.listeners.item.ItemCurrentListener;
import com.craftaro.ultimatestacker.listeners.item.ItemLegacyListener;
import com.craftaro.ultimatestacker.listeners.item.ItemListeners;
import com.craftaro.ultimatestacker.lootables.LootablesManager;
import com.craftaro.ultimatestacker.settings.Settings;
import com.craftaro.ultimatestacker.stackable.block.BlockStackImpl;
import com.craftaro.ultimatestacker.stackable.block.BlockStackManagerImpl;
import com.craftaro.ultimatestacker.stackable.entity.EntityStackManagerImpl;
import com.craftaro.ultimatestacker.stackable.entity.custom.CustomEntityManager;
import com.craftaro.ultimatestacker.stackable.item.StackedItemManagerImpl;
import com.craftaro.ultimatestacker.stackable.spawner.SpawnerStackImpl;
import com.craftaro.ultimatestacker.stackable.spawner.SpawnerStackManagerImpl;
import com.craftaro.ultimatestacker.tasks.BreedingTask;
import com.craftaro.ultimatestacker.tasks.StackingTask;
import com.craftaro.ultimatestacker.utils.Async;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

import java.util.*;

public class UltimateStacker extends SongodaPlugin {

    private static UltimateStacker INSTANCE;
    private final static Set<String> whitelist = new HashSet();
    private final static Set<String> blacklist = new HashSet();

    private final Config mobFile = new Config(this, "mobs.yml");
    private final Config itemFile = new Config(this, "items.yml");
    private final Config spawnerFile = new Config(this, "spawners.yml");

    private final GuiManager guiManager = new GuiManager(this);
    private final List<StackerHook> stackerHooks = new ArrayList<>();
    private EntityStackManager entityStackManager;
    private SpawnerStackManager spawnerStackManager;
    private BlockStackManager blockStackManager;
    private StackedItemManager stackedItemManager;
    private LootablesManager lootablesManager;
    private CommandManager commandManager;
    private CustomEntityManager customEntityManager;
    private StackingTask stackingTask;
    private BreedingTask breedingTask;
    private UltimateStackerApi API;
    private SuperiorSkyblock2Hook superiorSkyblock2Hook;
    private boolean instantStacking;

    public static UltimateStacker getInstance() {
        return INSTANCE;
    }

    @Override
    protected Set<Dependency> getDependencies() {
        return new HashSet<>();
    }

    @Override
    public void onPluginLoad() {
        INSTANCE = this;

        // Register WorldGuard
        WorldGuardHook.addHook("mob-stacking", true);
    }

    @Override
    public void onPluginDisable() {
        if (this.stackingTask != null)
            this.stackingTask.cancel();
        this.dataManager.saveBatchSync(this.spawnerStackManager.getStacksData());
        this.dataManager.saveBatchSync(this.blockStackManager.getStacksData());
        this.dataManager.shutdownNow();
        HologramManager.removeAllHolograms();
        Async.shutdown();
    }

    @Override
    public void onPluginEnable() {
        // Run Songoda Updater
        Async.start();
        SongodaCore.registerPlugin(this, 16, XMaterial.IRON_INGOT);
        // Setup Config
        Settings.setupConfig();
        this.setLocale(Settings.LANGUGE_MODE.getString(), false);
        blacklist.clear();
        whitelist.clear();
        whitelist.addAll(Settings.ITEM_WHITELIST.getStringList());
        blacklist.addAll(Settings.ITEM_BLACKLIST.getStringList());

        // Setup plugin commands
        this.commandManager = new CommandManager(this);
        this.commandManager.addMainCommand("us")
                .addSubCommands(new CommandSettings(this, guiManager),
                        new CommandRemoveAll(this),
                        new CommandReload(this),
                        new CommandGiveSpawner(this),
                        new CommandSpawn(this),
                        new CommandLootables(this),
                        new CommandConvert(guiManager)
                );

        PluginManager pluginManager = Bukkit.getPluginManager();
        this.superiorSkyblock2Hook = new SuperiorSkyblock2Hook(pluginManager.isPluginEnabled("SuperiorSkyblock2"));

        this.lootablesManager = new LootablesManager(superiorSkyblock2Hook);
        this.lootablesManager.createDefaultLootables();
        this.getLootablesManager().getLootManager().loadLootables();

        for (EntityType value : EntityType.values()) {
            if (value.isSpawnable() && value.isAlive() && !value.toString().contains("ARMOR")) {
                mobFile.addDefault("Mobs." + value.name() + ".Enabled", true);
                mobFile.addDefault("Mobs." + value.name() + ".Display Name", TextUtils.formatText(value.name().toLowerCase().replace("_", " "), true));
                mobFile.addDefault("Mobs." + value.name() + ".Max Stack Size", -1);
                mobFile.addDefault("Mobs." + value.name() + ".Kill Whole Stack", false);
            }
        }
        mobFile.load();
        mobFile.saveChanges();

        for (Material value : Material.values()) {
            itemFile.addDefault("Items." + value.name() + ".Has Hologram", true);
            itemFile.addDefault("Items." + value.name() + ".Max Stack Size", -1);
            itemFile.addDefault("Items." + value.name() + ".Display Name", WordUtils.capitalizeFully(value.name().toLowerCase().replace("_", " ")));
        }
        itemFile.load();
        itemFile.saveChanges();

        for (EntityType value : EntityType.values()) {
            if (value.isSpawnable() && value.isAlive() && !value.toString().contains("ARMOR")) {
                spawnerFile.addDefault("Spawners." + value.name() + ".Max Stack Size", -1);
                spawnerFile.addDefault("Spawners." + value.name() + ".Display Name", TextUtils.formatText(value.name().toLowerCase().replace("_", " "), true));
            }
        }
        spawnerFile.load();
        spawnerFile.saveChanges();

        if (Bukkit.getPluginManager().isPluginEnabled("BentoBox")) {
            ProtectionManager.load(Bukkit.getPluginManager().getPlugin("BentoBox"));
        }

        this.spawnerStackManager = new SpawnerStackManagerImpl();
        this.entityStackManager = new EntityStackManagerImpl(this);
        this.blockStackManager = new BlockStackManagerImpl();
        this.stackedItemManager = new StackedItemManagerImpl();
        this.customEntityManager = new CustomEntityManager();

        guiManager.init();
        if (Settings.STACK_ENTITIES.getBoolean() && ServerVersion.isServerVersionAtLeast(ServerVersion.V1_10)) {
            pluginManager.registerEvents(new BreedListeners(this), this);
        }
        pluginManager.registerEvents(new BlockListeners(this), this);
        pluginManager.registerEvents(new DeathListeners(this), this);
        pluginManager.registerEvents(new ShearListeners(this), this);
        pluginManager.registerEvents(new InteractListeners(this), this);
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13))
            pluginManager.registerEvents(new EntityCurrentListener(this), this);

        this.instantStacking = Settings.STACK_ENTITIES.getBoolean() && Settings.INSTANT_STACKING.getBoolean();
        pluginManager.registerEvents(new EntityListeners(this), this);
        pluginManager.registerEvents(new ItemListeners(this), this);

        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_12)) {
            pluginManager.registerEvents(new ItemCurrentListener(), this);
        } else {
            pluginManager.registerEvents(new ItemLegacyListener(), this);
        }

        pluginManager.registerEvents(new TameListeners(this), this);
        pluginManager.registerEvents(new SpawnerListeners(this), this);
        pluginManager.registerEvents(new SheepDyeListeners(this), this);

        if (Settings.CLEAR_LAG.getBoolean() && pluginManager.isPluginEnabled("ClearLag")) {
            pluginManager.registerEvents(new ClearLagListeners(this), this);
        }

        // Register Hooks
        if (pluginManager.isPluginEnabled("Jobs")) {
            stackerHooks.add(new JobsHook());
        }

        HologramManager.load(this);
        EntityStackerManager.load();

        initDatabase(Arrays.asList(new _1_InitialMigration(), new _2_EntityStacks(), new _3_BlockStacks(), new _6_RemoveStackedEntityTable()));

        API = new UltimateStackerApi(this, entityStackManager, stackedItemManager, spawnerStackManager, blockStackManager, new Settings());
    }

    @Override
    public void onDataLoad() {
        if (HologramManager.isEnabled())
            // Set the offset so that the holograms don't end up inside the blocks.
            HologramManager.getHolograms().setPositionOffset(.5, .65, .5);

        // Load current data.
        final boolean useSpawnerHolo = Settings.SPAWNER_HOLOGRAMS.getBoolean();
        this.dataManager.loadBatch(SpawnerStackImpl.class, "spawners").forEach((data) -> {
            SpawnerStack spawner = (SpawnerStack) data;
            this.spawnerStackManager.addSpawner(spawner);
            if (useSpawnerHolo) {
                if (spawner == null) return;
                if (spawner.getLocation() == null) return;
                if (spawner.getLocation().getWorld() != null) {
                    updateHologram(spawner);
                }
            }
        });
        this.stackingTask = new StackingTask(this);
        this.breedingTask = new BreedingTask(this);

        //Start stacking task
        if (Settings.STACK_ENTITIES.getBoolean()) {
            stackingTask.start();
        }

        this.instantStacking = Settings.STACK_ENTITIES.getBoolean() && Settings.INSTANT_STACKING.getBoolean();
        final boolean useBlockHolo = Settings.BLOCK_HOLOGRAMS.getBoolean();
        this.dataManager.loadBatch(BlockStackImpl.class, "blocks").forEach((data) -> {
            BlockStack blockStack = (BlockStack) data;
            this.blockStackManager.addBlock(blockStack);
            if (useBlockHolo) {
                if (blockStack == null) return;
                if (blockStack.getLocation().getWorld() != null)
                    updateHologram(blockStack);
            }
        });
    }

    public UltimateStackerApi getAPI() {
        return API;
    }

    public void addExp(Player player, EntityStack stack) {
        for (StackerHook stackerHook : stackerHooks) {
            stackerHook.applyExperience(player, stack);
        }
    }

    @Override
    public List<Config> getExtraConfig() {
        return Arrays.asList(mobFile, itemFile, spawnerFile);
    }

    @Override
    public void onConfigReload() {
        blacklist.clear();
        whitelist.clear();
        whitelist.addAll(Settings.ITEM_WHITELIST.getStringList());
        blacklist.addAll(Settings.ITEM_BLACKLIST.getStringList());

        this.setLocale(getConfig().getString("System.Language Mode"), true);
        this.locale.reloadMessages();

        if (stackingTask != null)
            this.stackingTask.cancel();

        this.stackingTask = new StackingTask(this);
        if (Settings.STACK_ENTITIES.getBoolean()) {
            stackingTask.start();
        }

        this.mobFile.load();
        this.itemFile.load();
        this.spawnerFile.load();
        this.getLootablesManager().getLootManager().loadLootables();
    }

    public boolean spawnersEnabled() {
        return !this.getServer().getPluginManager().isPluginEnabled("EpicSpawners")
                && Settings.SPAWNERS_ENABLED.getBoolean();
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public LootablesManager getLootablesManager() {
        return lootablesManager;
    }

    public StackingTask getStackingTask() {
        return stackingTask;
    }

    public Config getMobFile() {
        return mobFile;
    }

    public Config getItemFile() {
        return itemFile;
    }

    public Config getSpawnerFile() {
        return spawnerFile;
    }

    public DataManager getPluginDataManager() {
        return dataManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public BlockStackManager getBlockStackManager() {
        return blockStackManager;
    }

    public EntityStackManager getEntityStackManager() {
        return entityStackManager;
    }

    public StackedItemManager getStackedItemManager() {
        return stackedItemManager;
    }

    public SpawnerStackManager getSpawnerStackManager() {
        return spawnerStackManager;
    }

    public CustomEntityManager getCustomEntityManager() {
        return customEntityManager;
    }

    public void updateHologram(Hologramable stack) {
        // Is this stack invalid?
        if (!stack.isValid()) {
            if (stack instanceof BlockStackImpl) {
                blockStackManager.removeBlock(stack.getLocation());
                BlockStackImpl blockStack = (BlockStackImpl) stack;
                dataManager.delete(blockStack);
            } else if (stack instanceof SpawnerStackImpl) {
                spawnerStackManager.removeSpawner(stack.getLocation());
                SpawnerStackImpl spawnerStack = (SpawnerStackImpl) stack;
                dataManager.delete(spawnerStack);
            }
        } else {
            // are holograms enabled?
            if (!stack.areHologramsEnabled() && !HologramManager.getManager().isEnabled()) return;
            // update the hologram
            if (stack.getHologramName() == null) {
                if (stack instanceof BlockStackImpl) {
                    BlockStackImpl blockStack = (BlockStackImpl) stack;
                    getLogger().warning("Hologram name is null for BlocStack at " + blockStack.getLocation());
                } else {
                    SpawnerStackImpl spawnerStack = (SpawnerStackImpl) stack;
                    getLogger().warning("Hologram name is null for SpawnerStack at " + spawnerStack.getLocation());
                }
                return;
            }
            if (!HologramManager.isHologramLoaded(stack.getHologramId())) {
                HologramManager.createHologram(stack.getHologramId(), stack.getLocation(), stack.getHologramName());
                return;
            }
            HologramManager.updateHologram(stack.getHologramId(), stack.getHologramName());
        }
    }

    public void removeHologram(Hologramable stack) {
        HologramManager.removeHologram(stack.getHologramId());
    }

    public SuperiorSkyblock2Hook getSuperiorSkyblock2Hook() {
        return superiorSkyblock2Hook;
    }

    public boolean isInstantStacking() {
        return instantStacking;
    }

    //////// Convenient API //////////

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param item Item material to check
     * @return true if this material will not stack
     */
    public static boolean isMaterialBlacklisted(ItemStack item) {
        Optional<XMaterial> mat = XMaterial.matchXMaterial(item.getType().name());
        // this shouldn't happen, but just in case?
        return mat.map(xMaterial -> isMaterialBlacklisted(xMaterial.name()) || isMaterialBlacklisted(xMaterial.parseMaterial()))
                .orElseGet(() -> ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) ?
                        isMaterialBlacklisted(item.getType()) : isMaterialBlacklisted(item.getType(), item.getData().getData()));
    }

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @return true if this material will not stack
     */
    public static boolean isMaterialBlacklisted(String type) {
        return !whitelist.isEmpty() && !whitelist.contains(type)
                || !blacklist.isEmpty() && blacklist.contains(type);
    }

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @return true if this material will not stack
     */
    public static boolean isMaterialBlacklisted(Material type) {
        return !whitelist.isEmpty() && !whitelist.contains(type.name())
                || !blacklist.isEmpty() && blacklist.contains(type.name());
    }

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @param data data value for this item (for 1.12 and older servers)
     * @return true if this material will not stack
     */
    public static boolean isMaterialBlacklisted(Material type, byte data) {
        String combined = type.toString() + ":" + data;

        return !whitelist.isEmpty() && !whitelist.contains(combined)
                || !blacklist.isEmpty() && blacklist.contains(combined);
    }

    public BreedingTask getBreedingTask() {
        return breedingTask;
    }
}
