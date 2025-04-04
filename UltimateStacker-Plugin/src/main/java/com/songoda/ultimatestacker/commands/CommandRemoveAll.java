package com.songoda.ultimatestacker.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.api.stack.entity.EntityStackManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CommandRemoveAll extends AbstractCommand {

    private final UltimateStacker plugin;

    public CommandRemoveAll(UltimateStacker plugin) {
        super(CommandType.CONSOLE_OK, "removeall");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 1) return ReturnType.SYNTAX_ERROR;

        String type = args[0];

        boolean all = args.length == 2;

        if (!type.equalsIgnoreCase("entities")
                && !type.equalsIgnoreCase("items")) {
            return ReturnType.SYNTAX_ERROR;
        }

        int amountRemoved = 0;
        EntityStackManager stackManager = plugin.getEntityStackManager();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entityO : world.getEntities()) {
                if (entityO instanceof Player) continue;

                if (entityO instanceof LivingEntity && (stackManager.isStackedEntity(entityO) || all)
                        && type.equalsIgnoreCase("entities")) {
                    EntityStack stack = plugin.getEntityStackManager().getStackedEntity((LivingEntity) entityO);
                    if (stack == null) continue;
                    stack.destroy();
                    amountRemoved++;
                } else if (entityO.getType() == EntityType.DROPPED_ITEM && type.equalsIgnoreCase("items")) {
                    if (!UltimateStacker.getInstance().getStackedItemManager().isStackedItem((Item)entityO) && !all)
                        continue;
                    entityO.remove();
                    amountRemoved++;
                }
            }
        }

        if (type.equalsIgnoreCase("entities") && amountRemoved == 1) type = "Entity";
        if (type.equalsIgnoreCase("items") && amountRemoved == 1) type = "Item";

        if (amountRemoved == 0) {
            plugin.getLocale().newMessage("&7No" + (all ? " " : " stacked ")
                    + type + " exist that could be removed.").sendPrefixedMessage(sender);
        } else {
            plugin.getLocale().newMessage("&7Removed &6" + amountRemoved + (all ? " " : " stacked ")
                    + TextUtils.formatText(type.toLowerCase(), true) + " &7Successfully.").sendPrefixedMessage(sender);
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1)
            return Arrays.asList("entities", "items");
        else if (args.length == 2)
            return Collections.singletonList("all");
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatestacker.admin";
    }

    @Override
    public String getSyntax() {
        return "removeall <entities/items> [all]";
    }

    @Override
    public String getDescription() {
        return "Remove all stacked entites or items from the world.";
    }

}
