package com.songoda.ultimatestacker.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.entity.EntityStack;
import com.songoda.ultimatestacker.tasks.StackingTask;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: 3/21/2020
 * Time Created: 1:02 PM
 */
public class CommandSpawn extends AbstractCommand {

    private final UltimateStacker plugin;

    public CommandSpawn(UltimateStacker plugin) {
        super(CommandType.PLAYER_ONLY, "spawn");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player player = (Player) sender;

        if (args.length < 2) return ReturnType.SYNTAX_ERROR;

        EntityType type = null;
        for (EntityType types : EntityType.values()) {
            String input = args[0].toUpperCase().replace("_", "").replace(" ", "");
            String compare = types.name().toUpperCase().replace("_", "").replace(" ", "");
            if (input.equals(compare))
                type = types;
        }

        if (type == null) {
            plugin.getLocale().newMessage("&7The entity &6" + args[0] + " &7does not exist. Try one of these:").sendPrefixedMessage(sender);
            StringBuilder list = new StringBuilder();

            for (EntityType types : EntityType.values()) {
                if (types.isSpawnable() && types.isAlive() && !types.toString().contains("ARMOR"))
                    list.append(types.name().toUpperCase().replace(" ", "_")).append("&7, &6");
            }
            sender.sendMessage(TextUtils.formatText("&6" + list));
        } else {
            StackingTask stackingTask = plugin.getStackingTask();
            if (stackingTask != null) {
                LivingEntity entity = (LivingEntity)player.getWorld().spawnEntity(player.getLocation(), type);
                EntityStack stack = plugin.getEntityStackManager().createStackedEntity(entity, Integer.parseInt(args[1]));
                stackingTask.attemptSplit(stack, -1);
            }
        }

        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            return Arrays.stream(EntityType.values())
                    .filter(types -> types.isSpawnable() && types.isAlive() && !types.toString().contains("ARMOR"))
                    .map(Enum::name).collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("1", "2", "3", "4", "5");
        }
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatestacker.admin";
    }

    @Override
    public String getSyntax() {
        return "spawn <entity> <amount>";
    }

    @Override
    public String getDescription() {
        return "Spawns a stack of the specified entity at your location.";
    }
}
