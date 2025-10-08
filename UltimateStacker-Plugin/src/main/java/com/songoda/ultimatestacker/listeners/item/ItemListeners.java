package com.songoda.ultimatestacker.listeners.item;

import com.songoda.core.nms.Nms;
import com.songoda.core.nms.world.WorldCore;
import com.songoda.core.world.SWorld;
import com.songoda.third_party.org.apache.commons.lang3.StringUtils;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.UltimateStackerApi;
import com.songoda.ultimatestacker.api.stack.item.StackedItem;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.utils.Methods;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemListeners implements Listener {

    private final UltimateStacker plugin;

    public ItemListeners(UltimateStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMerge(ItemMergeEvent event) {
        if (!Settings.STACK_ITEMS.getBoolean()) {
            return;
        }

        org.bukkit.entity.Item from = event.getEntity();
        org.bukkit.entity.Item to = event.getTarget();

        if (Settings.STACK_WITH_NEWEST.getBoolean()) {
            from = event.getTarget();
            to = event.getEntity();
        }

        StackedItem stackedFrom = UltimateStacker.getInstance().getStackedItemManager().getStackedItem(from);
        StackedItem stackedTo = UltimateStacker.getInstance().getStackedItemManager().getStackedItem(to);

        if (stackedFrom == null || stackedTo == null) {
            return;
        }

        int totalAmount = stackedFrom.getAmount() + stackedTo.getAmount();
        int maxStack = Settings.MAX_STACK_ITEMS.getInt();

        if (totalAmount > maxStack) {
            stackedTo.setAmount(maxStack);
            stackedFrom.setAmount(totalAmount - maxStack);
            event.setCancelled(true);
        } else {
            stackedTo.setAmount(totalAmount);
            stackedFrom.destroy();
            event.setCancelled(true);
            from.remove();
        }
    }

    @EventHandler
    public void onInvPickup(InventoryPickupItemEvent event) {
        if (!Settings.STACK_ITEMS.getBoolean() || !UltimateStacker.getInstance().getStackedItemManager().isStackedItem(event.getItem())) {
            return;
        }

        event.setCancelled(true);

        Methods.updateInventory(event.getItem(), event.getInventory());
        if (event.getInventory().getHolder() instanceof BlockState) {
            Block invHolder = ((BlockState) event.getInventory().getHolder()).getBlock();
            Nms.getImplementations().getWorld().updateAdjacentComparators(invHolder);
        }
    }

    @EventHandler
    public void onExist(ItemSpawnEvent event) {
        if (!Settings.STACK_ITEMS.getBoolean()) return;

        List<String> disabledWorlds = Settings.DISABLED_WORLDS.getStringList();
        if (disabledWorlds.stream().anyMatch(worldStr -> event.getEntity().getWorld().getName().equalsIgnoreCase(worldStr)))
            return;

        if (UltimateStackerApi.getStackedItemManager().isStackedItem(event.getEntity())) return;

        ItemStack itemStack = event.getEntity().getItemStack();

        if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() &&
                StringUtils.substring(itemStack.getItemMeta().getDisplayName(), 0, 3).equals("***")) {
            return; //Compatibility with Shop instance: https://www.spigotmc.org/resources/shop-a-simple-intuitive-shop-instance.9628/
        }

        UltimateStackerApi.getStackedItemManager().createStack(event.getEntity(), itemStack.getAmount());
    }
}
