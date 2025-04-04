package com.songoda.ultimatestacker.api;

public interface Settings {

    /**
     * @return The maximum size of a StackedItem
     */
    int getMaxItemStackSize();

    boolean killWholeStackOnDeath();


}
