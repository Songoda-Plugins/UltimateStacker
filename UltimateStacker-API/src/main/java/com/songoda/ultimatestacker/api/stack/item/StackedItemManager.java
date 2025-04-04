package com.songoda.ultimatestacker.api.stack.item;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Future;


public interface StackedItemManager {

    /**
     * Get the StackedItem for the given Item
     * Creates a new StackedItem if it does not exist
     * @param item The Item to get the stack for
     * @return The StackedItem for the given Item
     */
    @NotNull StackedItem getStackedItem(Item item);

    /**
     * Create a new StackedItem for the given ItemStack
     * @param item The ItemStack to create the stack for
     * @param location The location to spawn the stack
     * @param amount The amount of items in the stack
     * @param killed The entity that is dropping the item
     * @return The StackedItem for the given Item or null if it could not be created
     */
    @Nullable StackedItem createStack(ItemStack item, Location location, int amount, LivingEntity killed);

    /**
     * Create a new StackedItem for the given item
     * @param item The item to create the stack for
     * @param amount The amount of items in the stack
     * @return The StackedItem for the given Item or
     *         null if it could not be created or a
     *         plugin cancelled the event
     */
    @Nullable StackedItem createStack(Item item, int amount);

    /**
     * Create a new StackedItem for the given ItemStack
     * @param item The ItemStack to create the stack for
     * @param amount The amount of items in the stack
     * @param location The location to spawn the stack
     * @return The StackedItem for the given Item or null if it could not be created
     */
    @Nullable StackedItem createStack(ItemStack item, Location location, int amount);

    /**
     * Update the stack for the given item
     * @param item The Item to update
     * @param newAmount The new amount of the stack
     * @return The StackedItem for the given Item
     */
    @NotNull StackedItem updateStack(Item item, int newAmount);

    /**
     * Create a new StackedItem for the given item in the main thread
     * @param item The ItemStack to create the stack for
     * @param amount The amount of items in the stack
     * @param location The location to spawn the stack
     * @return The StackedItem for the given Item or null if it could not be created
     */
    @Nullable Future<StackedItem> createStackSync(ItemStack item, Location location, int amount);

    /**
     * Create a new StackedItem for the given item in the main thread
     * @param item The item to create the stack for
     * @param amount The amount of items in the stack
     * @return The StackedItem for the given Item
     */
    @NotNull Future<StackedItem> createStackSync(Item item, int amount);

    /**
     * Update the stack for the given item in the main thread
     * @param item The Item to update
     * @param newAmount The new amount of the stack
     * @return The StackedItem for the given Item
     */
    @NotNull Future<StackedItem> updateStackSync(Item item, int newAmount);

    /**
     * Get the actual amount of the given item
     * @param item item to check
     * @return The amount of items in the stacked item or vanilla stack size if not stacked
     */
    int getActualItemAmount(Item item);

    /**
     * Returns true if the given item is stacked
     *
     * @param item item to check
     * @return true if the given item is stacked
     */
    boolean isStackedItem(Item item);

    /**
     * Merge two items together if they are the same type
     * @param from first item
     * @param to second item
     * @param ignoreRestrictions ignore ignoreRestrictions such as max stack size, or blacklist
     * @return The merged item or null if they merge was unsuccessful
     */
    @Nullable StackedItem merge(Item from, Item to, boolean ignoreRestrictions);

    /**
     * Merge two items together if they are the same type
     * @param from first item
     * @param to second item
     * @param ignoreRestrictions ignore ignoreRestrictions such as max stack size, or blacklist
     * @param callback callback to be called when the merge is successful see {@link  ItemMergeCallback#accept(Item, Item, StackedItem)}
     * @return The merged item or null if they merge was unsuccessful
     */
    @Nullable StackedItem merge(Item from, Item to, boolean ignoreRestrictions, ItemMergeCallback<Item, Item, StackedItem> callback);

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param item Item material to check
     * @return true if this material will not stack
     */
    boolean isMaterialBlacklisted(ItemStack item);

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @return true if this material will not stack
     */
    boolean isMaterialBlacklisted(String type);

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @return true if this material will not stack
     */
    boolean isMaterialBlacklisted(Material type);

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @param data data value for this item (for 1.12 and older servers)
     * @return true if this material will not stack
     */
    boolean isMaterialBlacklisted(Material type, byte data);
}
