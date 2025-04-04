package com.songoda.ultimatestacker.stackable.item;

import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.item.StackedItem;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.utils.Methods;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class StackedItemImpl implements StackedItem {

    private final Item item;

    public StackedItemImpl(Item item) {
        this.item = item;
        if (!item.hasMetadata("US_AMT")) {
            item.setMetadata("US_AMT", new FixedMetadataValue(UltimateStacker.getInstance(), item.getItemStack().getAmount()));
        }
    }

    public StackedItemImpl(Item item, int amount) {
        this.item = item;
        setAmount(amount);
    }

    @Override
    public int getAmount() {
        if (item.hasMetadata("US_AMT")) {
            return item.getMetadata("US_AMT").get(0).asInt();
        } else {
            return item.getItemStack().getAmount();
        }
    }

    @Override
    public void setAmount(int amount) {
        updateItemAmount(item, amount);
    }

    @Override
    public void add(int amount) {
        updateItemAmount(item, getAmount() + amount);
    }

    @Override
    public void take(int amount) {
        //check that amount not go below 0
        if (getAmount() - amount < 0) {
            amount = 0;
        }

        updateItemAmount(item, getAmount() - amount);
    }

    @Override
    public Location getLocation() {
        return item.getLocation();
    }

    @Override
    public boolean isValid() {
        return item.isValid();
    }

    private void updateItemAmount(Item item, int newAmount) {
        updateItemAmount(item, item.getItemStack(), newAmount);
    }

    /**
     * Change the stacked amount for this item
     *
     * @param item      item entity to update
     * @param itemStack StackedItem that will represent this item
     * @param newAmount number of items this item represents
     */
    private void updateItemAmount(Item item, ItemStack itemStack, int newAmount) {
        boolean blacklisted = UltimateStacker.isMaterialBlacklisted(itemStack);

        if (newAmount > (itemStack.getMaxStackSize() / 2) && !blacklisted) {
            itemStack.setAmount(Math.max(1, itemStack.getMaxStackSize() / 2));
        } else {
            itemStack.setAmount(newAmount);
        }

        // If amount is 0, Minecraft change the type to AIR
        if (itemStack.getType() == Material.AIR) {
            return;
        }

        updateItemMeta(item, itemStack, newAmount);
    }

    private void updateItemMeta(Item item, ItemStack itemStack, int newAmount) {
        Material material = itemStack.getType();
        if (material == Material.AIR) return;

        String name = TextUtils.convertToInvisibleString("IS") + Methods.compileItemName(itemStack, newAmount);

        boolean blacklisted = UltimateStacker.isMaterialBlacklisted(itemStack);

        if (newAmount > (itemStack.getMaxStackSize() / 2) && !blacklisted) {
            item.setMetadata("US_AMT", new FixedMetadataValue(UltimateStacker.getInstance(), newAmount));
        } else {
            item.removeMetadata("US_AMT", UltimateStacker.getInstance());
        }
        item.setItemStack(itemStack);

        if ((blacklisted && !Settings.ITEM_HOLOGRAM_BLACKLIST.getBoolean())
                || !UltimateStacker.getInstance().getItemFile().getBoolean("Items." + material + ".Has Hologram")
                || !Settings.ITEM_HOLOGRAMS.getBoolean()
                || newAmount < Settings.ITEM_MIN_HOLOGRAM_SIZE.getInt()) {
            return;
        }

        item.setCustomName(name);
        item.setCustomNameVisible(true);
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public Item destroy() {
        item.removeMetadata("US_AMT", UltimateStacker.getInstance());
        item.setCustomName(null);
        item.setCustomNameVisible(true);
        return item;
    }

    @Override
    public String toString() {
        return "StackedItemImpl{" +
                "ItemStack=" + item.getItemStack() +
                ", us_amount=" + getAmount() +
                '}';
    }
}
