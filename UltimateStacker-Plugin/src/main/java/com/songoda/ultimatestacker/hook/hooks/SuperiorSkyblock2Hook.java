package com.songoda.ultimatestacker.hook.hooks;

import com.bgsoftware.superiorskyblock.api.SuperiorSkyblockAPI;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.songoda.core.hooks.Hook;
import org.bukkit.Location;

public class SuperiorSkyblock2Hook {

    private boolean enabled;

    public SuperiorSkyblock2Hook(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return "SuperiorSkyblock2";
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getDropMultiplier(Location mobDeathLocation) {
        Island island = SuperiorSkyblockAPI.getIslandAt(mobDeathLocation);
        if (island == null) {
            return 1;
        }
        return island.getUpgrades().getOrDefault("mob-drops", 0);
    }
}
