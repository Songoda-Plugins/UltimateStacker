package com.songoda.ultimatestacker.convert;

import com.songoda.ultimatestacker.UltimateStacker;
import org.bukkit.Bukkit;
import uk.antiperson.stackmob.StackMob;

public class StackMobConvert implements Convert {

    private final UltimateStacker plugin;

    private final StackMob stackMob;

    public StackMobConvert() {
        this.plugin = UltimateStacker.getInstance();
        stackMob = (StackMob) Bukkit.getPluginManager().getPlugin("StackMob");
    }

    @Override
    public String getName() {
        return "StackMob";
    }

    @Override
    public boolean canEntities() {
        return true;
    }

    @Override
    public boolean canSpawners() {
        return false;
    }

    @Override
    public void convertEntities() {
    }

    @Override
    public void convertSpawners() {
    }

    @Override
    public void disablePlugin() {
        Bukkit.getPluginManager().disablePlugin(stackMob);
    }
}
