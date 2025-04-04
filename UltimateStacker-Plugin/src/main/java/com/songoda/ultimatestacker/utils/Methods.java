package com.songoda.ultimatestacker.utils;

import com.songoda.core.third_party.de.tr7zw.nbtapi.NBT;
import com.songoda.core.utils.TextUtils;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.UltimateStackerApi;
import com.songoda.ultimatestacker.api.stack.item.StackedItem;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.stackable.entity.custom.CustomEntity;
import org.bukkit.ChatColor;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class Methods {

    public static void updateInventory(Item item, Inventory inventory) {
        StackedItem stackedItem = UltimateStackerApi.getStackedItemManager().getStackedItem(item);
        int amount = stackedItem.getAmount();
        ItemStack itemStack = item.getItemStack();
        final int maxStack = itemStack.getMaxStackSize();

        while (amount > 0) {
            int subtract = Math.min(amount, maxStack);
            amount -= subtract;
            ItemStack newItem = itemStack.clone();
            newItem.setAmount(subtract);
            Map<Integer, ItemStack> result = inventory.addItem(newItem);
            if (result.get(0) != null) {
                amount += result.get(0).getAmount();
                break;
            }
        }

        if (amount <= 0) {
            item.remove();
        } else {
            stackedItem.setAmount(amount);
        }
    }

    public static String compileItemName(ItemStack item, int amount) {
        String nameFormat = Settings.NAME_FORMAT_ITEM.getString();
        String displayName = TextUtils.formatText(UltimateStacker.getInstance().getItemFile()
                .getString("Items." + item.getType().name() + ".Display Name"));

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName())
            displayName = Settings.NAME_FORMAT_RESET.getBoolean() ?
                    ChatColor.stripColor(item.getItemMeta().getDisplayName()) : item.getItemMeta().getDisplayName();

        nameFormat = nameFormat.replace("{TYPE}", displayName);
        nameFormat = nameFormat.replace("{AMT}", Integer.toString(amount));

        if (amount == 1 && !Settings.SHOW_STACK_SIZE_SINGLE.getBoolean()) {
            nameFormat = nameFormat.replaceAll("\\[.*?]", "");
        } else {
            nameFormat = nameFormat.replace("[", "").replace("]", "");
        }

        String info = TextUtils.convertToInvisibleString(Methods.insertSemicolon(String.valueOf(amount)) + ":");

        return info + TextUtils.formatText(nameFormat).trim();
    }

    public static String compileSpawnerName(EntityType entityType, int amount) {
        String nameFormat = UltimateStacker.getInstance().getConfig().getString("Spawners.Name Format");
        String displayName = TextUtils.formatText(UltimateStacker.getInstance().getSpawnerFile().getString("Spawners." + entityType.name() + ".Display Name"));

        nameFormat = nameFormat.replace("{TYPE}", displayName);
        nameFormat = nameFormat.replace("{AMT}", Integer.toString(amount));

        return TextUtils.formatText(nameFormat).trim();
    }

    public static String compileEntityName(Entity entity, int amount) {
        String nameFormat = Settings.NAME_FORMAT_ENTITY.getString();
        String displayName = TextUtils.formatText(UltimateStacker.getInstance().getMobFile().getString("Mobs." + entity.getType().name() + ".Display Name"));

        CustomEntity customEntity = UltimateStacker.getInstance().getCustomEntityManager().getCustomEntity(entity);
        if (customEntity != null) {
            displayName = customEntity.getDisplayName(entity);
        }

        nameFormat = nameFormat.replace("{TYPE}", displayName);
        nameFormat = nameFormat.replace("{AMT}", Integer.toString(amount));

        return TextUtils.formatText(nameFormat).trim();
    }

    public static ItemStack getSpawnerItem(EntityType entityType, int amount) {
        ItemStack item = XMaterial.SPAWNER.parseItem();
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Methods.compileSpawnerName(entityType, amount));
        CreatureSpawner cs = (CreatureSpawner) ((BlockStateMeta) meta).getBlockState();
        cs.setSpawnedType(entityType);
        ((BlockStateMeta) meta).setBlockState(cs);
        item.setItemMeta(meta);

        NBT.modify(item, readableItemNBT -> {
            readableItemNBT.setInteger("spawner_stack_size", amount);
        });
        return item;
    }

    public static boolean isInt(String number) {
        if (number == null || number.equals(""))
            return false;
        try {
            Integer.parseInt(number);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static String insertSemicolon(String s) {
        if (s == null || s.equals(""))
            return "";
        StringBuilder hidden = new StringBuilder();
        for (char c : s.toCharArray()) hidden.append(";").append(c);
        return hidden.toString();
    }
}
