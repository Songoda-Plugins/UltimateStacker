package com.songoda.ultimatestacker.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CommandGiveSpawner extends AbstractCommand {

    private final UltimateStacker plugin;

    public CommandGiveSpawner(UltimateStacker plugin) {
        super(CommandType.CONSOLE_OK, "givespawner");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        if (args.length < 2) return ReturnType.SYNTAX_ERROR;

        if (Bukkit.getPlayer(args[0]) == null && !args[0].trim().equalsIgnoreCase("all")) {
            sender.sendMessage(args[0] + " is not a player...");
            return ReturnType.SYNTAX_ERROR;
        }

        EntityType type = null;
        String input = args[1].toUpperCase().replace("_", "").replace(" ", "");
        for (EntityType types : EntityType.values()) {
            String compare = types.name().toUpperCase().replace("_", "").replace(" ", "");
            if (input.equals(compare))
                type = types;
        }

        if (type == null) {
            plugin.getLocale().newMessage("&7The entity StackType &6" + args[1] + " &7does not exist. Try one of these:").sendPrefixedMessage(sender);
            StringBuilder list = new StringBuilder();

            for (EntityType types : EntityType.values()) {
                if (types.isSpawnable() && types.isAlive() && !types.toString().contains("ARMOR"))
                    list.append(types.name().toUpperCase().replace(" ", "_")).append("&7, &6");
            }
            sender.sendMessage(TextUtils.formatText("&6" + list));
        } else {

            int amt = args.length == 3 ? Integer.parseInt(args[2]) : 1;
            ItemStack itemStack = Methods.getSpawnerItem(type, amt);
            if (!args[0].trim().equalsIgnoreCase("all")) {
                Player player = Bukkit.getOfflinePlayer(args[0]).getPlayer();
                player.getInventory().addItem(itemStack);
                plugin.getLocale().getMessage("command.give.success")
                        .processPlaceholder("type", Methods.compileSpawnerName(type, amt))
                        .sendPrefixedMessage(player);
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.getInventory().addItem(itemStack);
                    plugin.getLocale().getMessage("command.give.success")
                            .processPlaceholder("type", Methods.compileSpawnerName(type, amt))
                            .sendPrefixedMessage(player);
                }
            }
        }
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        if (args.length == 1) {
            List<String> players = new ArrayList<>();
            players.add("all");
            players.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
            return players;
        } else if (args.length == 2) {
            return Arrays.stream(EntityType.values())
                    .filter(types -> types.isSpawnable() && types.isAlive() && !types.toString().contains("ARMOR"))
                    .map(Enum::name).collect(Collectors.toList());
        } else if (args.length == 3) {
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
        return "givespawner <player/all> <type> [size]";
    }

    @Override
    public String getDescription() {
        return "Gives an operator the ability to spawn a spawner of his or her choice.";
    }
}
