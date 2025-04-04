package com.songoda.ultimatestacker.commands;

import com.songoda.core.commands.AbstractCommand;
import com.songoda.core.configuration.editor.PluginConfigGui;
import com.songoda.core.gui.GuiManager;
import com.songoda.ultimatestacker.UltimateStacker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandSettings extends AbstractCommand {

    private final UltimateStacker plugin;
    private final GuiManager guiManager;

    public CommandSettings(UltimateStacker plugin, GuiManager guiManager) {
        super(CommandType.PLAYER_ONLY, "Settings");
        this.guiManager = guiManager;
        this.plugin = plugin;
    }

    @Override
    protected ReturnType runCommand(CommandSender sender, String... args) {
        guiManager.showGUI((Player) sender, new PluginConfigGui(plugin));
        return ReturnType.SUCCESS;
    }

    @Override
    protected List<String> onTab(CommandSender sender, String... args) {
        return Collections.emptyList();
    }

    @Override
    public String getPermissionNode() {
        return "ultimatestacker.admin";
    }

    @Override
    public String getSyntax() {
        return "settings";
    }

    @Override
    public String getDescription() {
        return "Edit the UltimateStacker Settings.";
    }
}
