package com.songoda.ultimatestacker.api.utils;

import org.bukkit.Location;

/**
 * Represents an object which can have a hologram
 */
public interface Hologramable {

    /**
     * Gets the name of the hologram
     * @return The name of the hologram
     */
    String getHologramName();

    /**
     * Checks if the hologram is enabled
     * @return True if the hologram is enabled, otherwise false
     */
    boolean areHologramsEnabled();

    /**
     * Checks if the hologram holder is valid
     * @return True if the hologram holder is valid, otherwise false
     */
    boolean isValid();

    /**
     * Gets the id of the hologram
     * @return The id of the hologram
     */
    String getHologramId();

    /**
     * Gets the location of the stack which has the hologram
     * @return The location
     */
    Location getLocation();
}
