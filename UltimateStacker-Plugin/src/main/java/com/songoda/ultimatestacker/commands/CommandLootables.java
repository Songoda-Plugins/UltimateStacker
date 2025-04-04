package com.songoda.ultimatestacker.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.lootables.gui.GuiEditor;
import com.songoda.ultimatestacker.UltimateStacker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandLootables extends AbstractCommand {

    private final UltimateStacker plugin;

    public CommandLootables(UltimateStacker plugin) {
        super(CommandType.PLAYER_ONLY, "lootables");
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        Player p = (Player) sender;
        plugin.getGuiManager().showGUI(p, new GuiEditor(plugin.getLootablesManager().getLootManager()));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return null;
    }

    @Override
    public String getPermissionNode() {
        return "ultimatestacker.admin";
    }

    @Override
    public String getSyntax() {
        return "lootables";
    }

    @Override
    public String getDescription() {
        return "Modify the drop tables.";
    }
}
