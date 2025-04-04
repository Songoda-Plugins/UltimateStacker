package com.songoda.ultimatestacker.listeners;

import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.settings.Settings;
import com.songoda.ultimatestacker.stackable.entity.Split;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class InteractListeners implements Listener {

    private final UltimateStacker plugin;

    public InteractListeners(UltimateStacker plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof LivingEntity)) return;
        Player player = event.getPlayer();
        LivingEntity entity = (LivingEntity)event.getRightClicked();

        ItemStack item = player.getInventory().getItemInHand();

        if (!plugin.getEntityStackManager().isStackedEntity(entity)) return;

        if (item.getType() != Material.NAME_TAG && !correctFood(item, entity)) return;

        EntityStack stack = plugin.getEntityStackManager().getStackedEntity(entity);

        if (stack.getAmount() <= 1
                || item.getType() == Material.NAME_TAG
                && Settings.SPLIT_CHECKS.getStringList().stream().noneMatch(line -> Split.valueOf(line) == Split.NAME_TAG)
                || item.getType() != Material.NAME_TAG
                && Settings.SPLIT_CHECKS.getStringList().stream().noneMatch(line -> Split.valueOf(line) == Split.ENTITY_BREED))
            return;

        if (item.getType() == Material.NAME_TAG)
            event.setCancelled(true);
        else if (entity instanceof Ageable && !((Ageable) entity).isAdult())
            return;

        stack.releaseHost();

        if (item.getType() == Material.NAME_TAG) {
            entity.setCustomName(item.getItemMeta().getDisplayName());
        } else {
            if (entity instanceof Ageable
                    && !((Ageable) entity).isAdult()) {
                return;
            }

            plugin.getBreedingTask().addInLoveTicket(entity);
        }
    }

    private boolean correctFood(ItemStack is, Entity entity) {
        Material type = is.getType();

        switch (entity.getType().name()) {
            case "COW":
            case "MUSHROOM_COW":
            case "SHEEP":
                return type == Material.WHEAT;
            case "PIG":
                return type == Material.CARROT || type == Material.BEETROOT || type == Material.POTATO;
            case "CHICKEN":
                return type == Material.WHEAT_SEEDS
                        || type == Material.MELON_SEEDS
                        || type == Material.PUMPKIN_SEEDS
                        || type == Material.BEETROOT_SEEDS;
            case "HORSE":
            case "DONKEY":
            case "MULE":
                return (type == Material.GOLDEN_APPLE || type == Material.GOLDEN_CARROT) && ((AbstractHorse) entity).isTamed();
            case "WOLF":
                return type == Material.BEEF
                        || type == Material.CHICKEN
                        || type == Material.COD
                        || type == Material.MUTTON
                        || type == Material.PORKCHOP
                        || type == Material.RABBIT
                        || type == Material.SALMON
                        || type == Material.COOKED_BEEF
                        || type == Material.COOKED_CHICKEN
                        || type == Material.COOKED_COD
                        || type == Material.COOKED_MUTTON
                        || type == Material.COOKED_PORKCHOP
                        || type == Material.COOKED_RABBIT
                        || type == Material.COOKED_SALMON
                        || type == Material.ROTTEN_FLESH
                        && ((Wolf) entity).isTamed();
            case "OCELOT":
            case "CAT":
                return (type == Material.COD || type == Material.SALMON) && ((Tameable) entity).isTamed();
            case "PANDA":
                return type == Material.BAMBOO;
            case "FOX":
                return type == Material.SWEET_BERRIES || type == Material.GLOW_BERRIES;
            case "RABBIT":
                return type == Material.CARROT || type == Material.GOLDEN_CARROT || type == Material.DANDELION;
            case "LLAMA":
            case "TRADER_LLAMA":
                return type == Material.HAY_BLOCK;
            case "TURTLE":
                return type == Material.SEAGRASS;
            case "HOGLIN":
                return type == Material.CRIMSON_FUNGUS;
            case "STRIDER":
                return type == Material.WARPED_FUNGUS;
            case "BEE":
                return type == Material.HONEYCOMB || type == Material.HONEY_BOTTLE;
            case "AXOLOTL":
                return type == Material.TROPICAL_FISH_BUCKET;
            case "GOAT":
                return type == Material.WHEAT;
            case "GLOW_SQUID":
                return type == Material.GLOW_INK_SAC;
            case "CAMEL":
                return type == Material.CACTUS;
            default:
                return false;
        }
    }

}
