package com.songoda.ultimatestacker.gui;

import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.core.utils.TextUtils;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.ultimatestacker.convert.Convert;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GUIConvertWhat extends Gui {

    private Convert convertFrom;

    private boolean entities = true;
    private boolean spawners = true;

    public GUIConvertWhat(Convert convertFrom, Gui returnTo) {
        super(returnTo);
        this.setRows(1);
        this.setTitle("What Do You Want To Convert?");
        this.convertFrom = convertFrom;

        if (convertFrom.canEntities()) {
            this.setButton(0, GuiUtils.createButtonItem(XMaterial.STONE,
                    ChatColor.GRAY + "Stacked Entities",
                    entities ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"),
                    (event) -> toggleEntities());
        }

        if (convertFrom.canSpawners()) {
            this.setButton(1, GuiUtils.createButtonItem(XMaterial.STONE,
                    ChatColor.GRAY + "Stacked Spawners",
                    spawners ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"),
                    (event) -> toggleSpawners());
        }

        this.setButton(8, GuiUtils.createButtonItem(XMaterial.GREEN_WOOL, ChatColor.GREEN + "Run"),
                (event) -> run(event.player));

    }

    private void toggleEntities() {
        entities = !entities;
        this.updateItem(0, ChatColor.GRAY + "Stacked Entities", entities ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No");
    }

    private void toggleSpawners() {
        spawners = !spawners;
        this.updateItem(1, ChatColor.GRAY + "Stacked Spawners", spawners ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No");
    }

    private void run(Player player) {
        if (entities) {
            convertFrom.convertEntities();
            //UltimateStacker.getInstance().getEntityStackManager().tryAndLoadColdEntities();
        }
        if (spawners) {
            convertFrom.convertSpawners();
        }

        convertFrom.disablePlugin();
        exit();
        player.sendMessage(TextUtils.formatText("&7Data converted successfully. Remove &6" + convertFrom.getName() + " &7and restart your server to continue."));
    }
}
