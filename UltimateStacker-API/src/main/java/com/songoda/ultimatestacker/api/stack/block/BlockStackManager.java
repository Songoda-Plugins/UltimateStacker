package com.songoda.ultimatestacker.api.stack.block;

import com.songoda.core.database.Data;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public interface BlockStackManager {

    void addBlocks(Map<Location, BlockStack> blocks);

    BlockStack addBlock(BlockStack blockStack);

    BlockStack removeBlock(Location location);

    BlockStack getBlock(Location location);

    BlockStack getBlock(Block block, XMaterial material);

    BlockStack createBlock(Location location, XMaterial material);

    BlockStack createBlock(Block block);

    boolean isBlock(Location location);

    Collection<BlockStack> getStacks();

    Collection<Data> getStacksData();

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param item Item material to check
     * @return true if this material will not stack
     */
    boolean isMaterialBlacklisted(ItemStack item);

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @return true if this material will not stack
     */
    boolean isMaterialBlacklisted(String type);

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @return true if this material will not stack
     */
    boolean isMaterialBlacklisted(Material type);

    /**
     * Check to see if this material is not permitted to stack
     *
     * @param type Material to check
     * @param data data value for this item (for 1.12 and older servers)
     * @return true if this material will not stack
     */
    boolean isMaterialBlacklisted(Material type, byte data);
}
