package com.songoda.ultimatestacker.api;

import com.songoda.ultimatestacker.api.stack.block.BlockStackManager;
import com.songoda.ultimatestacker.api.stack.entity.EntityStackManager;
import com.songoda.ultimatestacker.api.stack.item.StackedItemManager;
import com.songoda.ultimatestacker.api.stack.spawner.SpawnerStackManager;
import org.bukkit.plugin.Plugin;

/**
 * The main class of the API
 * <p>
 * <b>!! {@link UltimateStackerApi#getVersion()} value is automatically replaced by maven don't change it !!</b>
 */
public final class UltimateStackerApi {

    private static Plugin plugin;
    private static EntityStackManager entityStackManager;
    private static StackedItemManager stackedItemManager;
    private static SpawnerStackManager spawnerStackManager;
    private static BlockStackManager blockStackManager;
    private static Settings settings;
    private static UltimateStackerApi instance;

    public UltimateStackerApi(Plugin plugin, EntityStackManager entityStackManager, StackedItemManager itemStackManager, SpawnerStackManager spawnerStackManager, BlockStackManager blockStackManager, Settings settings) {
        if (UltimateStackerApi.plugin != null || UltimateStackerApi.entityStackManager != null || UltimateStackerApi.stackedItemManager != null || UltimateStackerApi.spawnerStackManager != null || UltimateStackerApi.blockStackManager != null || UltimateStackerApi.settings != null) {
            throw new IllegalStateException("UltimateStackerAPI has already been initialized!");
        }
        UltimateStackerApi.plugin = plugin;
        UltimateStackerApi.entityStackManager = entityStackManager;
        UltimateStackerApi.stackedItemManager = itemStackManager;
        UltimateStackerApi.spawnerStackManager = spawnerStackManager;
        UltimateStackerApi.blockStackManager = blockStackManager;
        UltimateStackerApi.settings = settings;
        instance = this;
    }

    public static UltimateStackerApi getInstance() {
        return instance;
    }

    /**
     * Used to interact with the plugin
     * @return The plugin
     */
    public static Plugin getPlugin() {
        return plugin;
    }

    /**
     * Used to interact with EntityStacks
     * @return The EntityStackManager
     */
    public static EntityStackManager getEntityStackManager() {
        return entityStackManager;
    }

    /**
     * Used to interact with ItemStacks
     * @return The StackedItemManager
     */
    public static StackedItemManager getStackedItemManager() {
        return stackedItemManager;
    }

    /**
     * Used to interact with SpawnerStacks
     * @return The SpawnerStackManager
     */
    public static SpawnerStackManager getSpawnerStackManager() {
        return spawnerStackManager;
    }

    /**
     * Used to interact with BlockStacks
     * @return The BlockStackManager
     */
    public static BlockStackManager getBlockStackManager() {
        return blockStackManager;
    }

    /**
     * Used to interact with the plugin's settings
     * @return The Settings
     */
    public static Settings getSettings() {
        return settings;
    }

    /**
     * Used to get the version of the plugin
     * @return The version of the plugin
     */
    public static String getVersion() {
        return "UKNOWN_VERSION";
    }
}
