package com.songoda.ultimatestacker.stackable.block;

import com.songoda.core.database.Data;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.block.BlockStack;
import com.songoda.ultimatestacker.api.stack.block.BlockStackManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BlockStackManagerImpl implements BlockStackManager {

    private final Map<Location, BlockStack> registeredBlocks = new HashMap<>();

    @Override
    public void addBlocks(Map<Location, BlockStack> blocks) {
        this.registeredBlocks.putAll(blocks);
    }

    @Override
    public BlockStack addBlock(BlockStack blockStack) {
        this.registeredBlocks.put(roundLocation(blockStack.getLocation()), blockStack);
        return blockStack;
    }

    @Override
    public BlockStack removeBlock(Location location) {
        return registeredBlocks.remove(roundLocation(location));
    }

    @Override
    public BlockStack getBlock(Location location) {
        return this.registeredBlocks.get(location);
    }

    @Override
    public BlockStack getBlock(Block block, XMaterial material) {
        return this.getBlock(block.getLocation());
    }

    @Override
    public BlockStack createBlock(Location location, XMaterial material) {
        return this.registeredBlocks.computeIfAbsent(location, b -> new BlockStackImpl(material, location));
    }

    @Override
    public BlockStack createBlock(Block block) {
        return this.createBlock(block.getLocation(), XMaterial.matchXMaterial(block.getType().name()).get());
    }

    @Override
    public boolean isBlock(Location location) {
        return this.registeredBlocks.get(location) != null;
    }

    @Override
    public Collection<BlockStack> getStacks() {
        return Collections.unmodifiableCollection(this.registeredBlocks.values());
    }

    @Override
    public Collection<Data> getStacksData() {
        return Collections.unmodifiableCollection(this.registeredBlocks.values());
    }

    @Override
    public boolean isMaterialBlacklisted(ItemStack item) {
        return UltimateStacker.isMaterialBlacklisted(item);
    }

    @Override
    public boolean isMaterialBlacklisted(String type) {
        return UltimateStacker.isMaterialBlacklisted(type);
    }

    @Override
    public boolean isMaterialBlacklisted(Material type) {
        return UltimateStacker.isMaterialBlacklisted(type);
    }

    @Override
    public boolean isMaterialBlacklisted(Material type, byte data) {
        return UltimateStacker.isMaterialBlacklisted(type, data);
    }


    private Location roundLocation(Location location) {
        location = location.clone();
        location.setX(location.getBlockX());
        location.setY(location.getBlockY());
        location.setZ(location.getBlockZ());
        return location;
    }
}
