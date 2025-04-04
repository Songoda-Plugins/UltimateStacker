package com.songoda.ultimatestacker.convert;

import com.bgsoftware.wildstacker.api.WildStackerAPI;
import com.bgsoftware.wildstacker.api.objects.StackedSpawner;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.UltimateStackerApi;
import com.songoda.ultimatestacker.api.stack.spawner.SpawnerStack;
import org.bukkit.Bukkit;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.plugin.Plugin;

public class WildStackerConvert implements Convert {

    private Plugin wildStacker;
    private final UltimateStacker plugin;

    public WildStackerConvert() {
        this.plugin = UltimateStacker.getInstance();
        this.wildStacker = Bukkit.getPluginManager().getPlugin("WildStacker");

    }

    @Override
    public String getName() {
        return "WildStacker";
    }

    @Override
    public boolean canEntities() {
        return true;
    }

    @Override
    public boolean canSpawners() {
        return true;
    }

    @Override
    public void convertEntities() {
    }

    @Override
    public void convertSpawners() {
        for (StackedSpawner spawner : WildStackerAPI.getWildStacker().getSystemManager().getStackedSpawners()) {
            SpawnerStack stack = UltimateStackerApi.getSpawnerStackManager().getSpawner(spawner.getLocation());

            stack.setAmount(WildStackerAPI
                    .getSpawnersAmount((CreatureSpawner) spawner.getLocation().getBlock().getState()));
            plugin.updateHologram(stack);
        }
    }

    @Override
    public void disablePlugin() {
        Bukkit.getPluginManager().disablePlugin(wildStacker);
    }
}
