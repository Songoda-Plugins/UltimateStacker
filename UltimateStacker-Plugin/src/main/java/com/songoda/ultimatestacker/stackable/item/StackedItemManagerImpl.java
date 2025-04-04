package com.songoda.ultimatestacker.stackable.item;

import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.events.entity.StackedItemSpawnEvent;
import com.songoda.ultimatestacker.api.stack.item.ItemMergeCallback;
import com.songoda.ultimatestacker.api.stack.item.StackedItem;
import com.songoda.ultimatestacker.api.stack.item.StackedItemManager;
import com.songoda.ultimatestacker.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.Future;

public class StackedItemManagerImpl implements StackedItemManager {

    @Override
    public @NotNull StackedItem getStackedItem(Item item) {
        return new StackedItemImpl(item);
    }

    @Override
    public @Nullable StackedItem createStack(ItemStack item, Location location, int amount) {
        if (item.getType() == Material.AIR) return null;
        World world = location.getWorld();
        if (world == null) return null;
        StackedItemSpawnEvent event = new StackedItemSpawnEvent(null, item, amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;

        Item dropped = world.dropItem(location, item);
        if (dropped.getItemStack().getType() == Material.AIR) return null;
        return new StackedItemImpl(dropped, amount);
    }

    @Override
    public @Nullable StackedItem createStack(ItemStack item, Location location, int amount, LivingEntity killed) {
        if (item.getType() == Material.AIR) return null;
        if (killed == null) return createStack(item, location, amount);
        World world = location.getWorld();
        if (world == null) return null;
        StackedItemSpawnEvent event = new StackedItemSpawnEvent(null, item, amount);
        if (killed.hasMetadata("EFA-TAGGED")) {
            event.addExtraData("EFA-TAGGED", killed.getMetadata("EFA-TAGGED").get(0).value());
        }
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;
        Item dropped = world.dropItem(location, item);
        return new StackedItemImpl(dropped, amount);
    }

    @Override
    public StackedItem createStack(Item item, int amount) {
        StackedItemSpawnEvent event = new StackedItemSpawnEvent(item, item.getItemStack(), amount);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) return null;
        return new StackedItemImpl(item, amount);
    }

    @Override
    public @NotNull StackedItem updateStack(Item item, int newAmount) {
        StackedItem stackedItem = getStackedItem(item);
        stackedItem.setAmount(newAmount);
        return stackedItem;
    }

    @Override
    public @Nullable Future<StackedItem> createStackSync(ItemStack item, Location location, int amount) {
        return Bukkit.getScheduler().callSyncMethod(UltimateStacker.getInstance(), () -> createStack(item, location, amount));
    }

    @Override
    public @NotNull Future<StackedItem> createStackSync(Item item, int amount) {
        return Bukkit.getScheduler().callSyncMethod(UltimateStacker.getInstance(), () -> createStack(item, amount));
    }

    @Override
    public @NotNull Future<StackedItem> updateStackSync(Item item, int newAmount) {
        return Bukkit.getScheduler().callSyncMethod(UltimateStacker.getInstance(), () -> updateStack(item, newAmount));
    }

    @Override
    public int getActualItemAmount(Item item) {
        if (isStackedItem(item)) {
            return item.getMetadata("US_AMT").get(0).asInt();
        } else {
            return item.getItemStack().getAmount();
        }
    }

    @Override
    public boolean isStackedItem(Item item) {
        if (item.hasMetadata("US_AMT")) {
            return item.getItemStack().getAmount() != item.getMetadata("US_AMT").get(0).asInt();
        }
        return false;
    }

    @Override
    public @Nullable StackedItem merge(Item from, Item to, boolean ignoreRestrictions) {
        return merge(from, to, ignoreRestrictions, null);
    }

    @Override
    public @Nullable StackedItem merge(Item from, Item to, boolean ignoreRestrictions, ItemMergeCallback<Item, Item, StackedItem> callback) {

        if (!ignoreRestrictions) {
            if (!Settings.STACK_ITEMS.getBoolean()) return null;
            List<String> disabledWorlds = Settings.DISABLED_WORLDS.getStringList();
            if (disabledWorlds.stream().anyMatch(worldStr -> from.getWorld().getName().equalsIgnoreCase(worldStr))) {
                return null;
            }
        }

        long maxItemStackSize = Settings.MAX_STACK_ITEMS.getLong();
        if (maxItemStackSize > Integer.MAX_VALUE) maxItemStackSize = Integer.MAX_VALUE;

        ItemStack fromItemStack = from.getItemStack();
        ItemStack toItemStack = to.getItemStack();

        if (fromItemStack.getType() != toItemStack.getType()) return null;
        if (!ignoreRestrictions && UltimateStacker.isMaterialBlacklisted(fromItemStack)) return null;

        long maxSize = UltimateStacker.getInstance().getItemFile().getInt("Items." + fromItemStack.getType().name() + ".Max Stack Size");

        if (maxSize <= 0) {
            maxSize = maxItemStackSize;
        } else {
            maxSize = Math.min(maxSize, maxItemStackSize);
        }

        long fromAmount = getActualItemAmount(from);
        long toAmount = getActualItemAmount(to);

        if (fromAmount + toAmount > maxSize) {
            if (callback != null) callback.accept(from, to, null);

            //merge was unsuccessful
            return null;
        } else {
            StackedItem merged = new StackedItemImpl(to, (int) (fromAmount + toAmount));
            if (callback != null) callback.accept(null, to, merged);
            return merged;
        }
    }

    @Override
    public boolean isMaterialBlacklisted(ItemStack item) {
        return UltimateStacker.isMaterialBlacklisted(item);
    }

    @Override
    public boolean isMaterialBlacklisted(String type) {
        return UltimateStacker.isMaterialBlacklisted(type);
    }

    @Override
    public boolean isMaterialBlacklisted(Material type) {
        return UltimateStacker.isMaterialBlacklisted(type);
    }

    @Override
    public boolean isMaterialBlacklisted(Material type, byte data) {
        return UltimateStacker.isMaterialBlacklisted(type, data);
    }
}
