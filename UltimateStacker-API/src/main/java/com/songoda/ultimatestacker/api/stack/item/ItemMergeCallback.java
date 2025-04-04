package com.songoda.ultimatestacker.api.stack.item;

import org.bukkit.entity.Item;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemMergeCallback<F extends Item, T extends Item, S extends StackedItem> {

    /**
     * Called when two items are merged
     * @param from The item that was merged into another (null if the merge was successful)
     * @param to The item that was merged into
     * @param stack The item that was created from the merge (null if the merge was unsuccessful)
     */
    void accept(@Nullable F from, @NotNull T to, @Nullable S stack);
}
