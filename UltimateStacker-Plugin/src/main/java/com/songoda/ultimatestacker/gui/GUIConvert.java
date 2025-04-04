package com.songoda.ultimatestacker.gui;

import com.songoda.core.gui.Gui;
import com.songoda.core.gui.GuiUtils;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.ultimatestacker.convert.StackMobConvert;
import com.songoda.ultimatestacker.convert.WildStackerConvert;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class GUIConvert extends Gui {

    public GUIConvert() {
        setTitle("Convert");
        setRows(1);
        int current = 0;
        if (Bukkit.getPluginManager().isPluginEnabled("WildStacker")) {
            this.setButton(current++, GuiUtils.createButtonItem(XMaterial.STONE, ChatColor.GRAY + "WildStacker"),
                    (event) -> event.manager.showGUI(event.player, new GUIConvertWhat(new WildStackerConvert(), this)));
        }

        if (Bukkit.getPluginManager().isPluginEnabled("StackMob")) {
            this.setButton(current++, GuiUtils.createButtonItem(XMaterial.STONE, ChatColor.GRAY + "StackMob"),
                    (event) -> event.manager.showGUI(event.player, new GUIConvertWhat(new StackMobConvert(), this)));
        }

    }

}
