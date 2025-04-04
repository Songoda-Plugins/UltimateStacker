package com.songoda.ultimatestacker.api.utils;

import org.bukkit.Location;

/**
 * Represents an object that can be stacked
 */
public interface Stackable {

    /**
     * Get the amount of the stack
     * @return The amount of the stack
     */
    int getAmount();

    /**
     * Set the amount of the stack
     * @param amount The new amount of the stack
     */
    void setAmount(int amount);

    /**
     * Add to the amount of the stack
     * @param amount The amount to add to the stack
     */
    void add(int amount);

    /**
     * Take from the amount of the stack
     * @param amount The amount to take from the stack
     */
    void take(int amount);

    /**
     * Get the location of the stack
     * @return The location of the stack
     */
    Location getLocation();

    /**
     * Check if the stack is valid
     * @return True if the stack is valid
     */
    boolean isValid();
}
