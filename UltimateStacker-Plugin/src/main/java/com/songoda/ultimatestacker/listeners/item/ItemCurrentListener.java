package com.songoda.ultimatestacker.listeners.item;

import com.songoda.third_party.com.cryptomorin.xseries.XSound;
import com.songoda.ultimatestacker.api.UltimateStackerApi;
import com.songoda.ultimatestacker.api.stack.item.StackedItem;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.utils.Methods;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemCurrentListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPickup(EntityPickupItemEvent event) {
        if (!Settings.STACK_ITEMS.getBoolean() || event.getItem() instanceof Arrow) return;
        // Amount here is not the total amount of item (32 if more than 32) but the amount of item the player can retrieve
        // ie there is x64 diamonds blocks (so 32), the player pick 8 items so the amount is 8 and not 32

        StackedItem stackedItem = UltimateStackerApi.getStackedItemManager().getStackedItem(event.getItem());
        ItemStack stack = stackedItem.getItem().getItemStack();
        int amount = stackedItem.getAmount();

        if (event.getEntity() instanceof Player) {
            if (amount < (stack.getMaxStackSize() / 2)) return;
            event.setCancelled(true);
            Player player = (Player) event.getEntity();
            XSound.ENTITY_ITEM_PICKUP.play(player, .2f, (float) (1 + Math.random()));
            Methods.updateInventory(event.getItem(), player.getInventory());
        } else {
            stackedItem.setAmount(amount - 1);
        }
    }
}
