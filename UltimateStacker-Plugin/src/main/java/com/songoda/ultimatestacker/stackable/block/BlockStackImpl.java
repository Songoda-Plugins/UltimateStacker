package com.songoda.ultimatestacker.stackable.block;

import com.songoda.core.database.Data;
import com.songoda.core.database.SerializedLocation;
import com.songoda.third_party.com.cryptomorin.xseries.XMaterial;
import com.songoda.core.utils.TextUtils;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.api.stack.block.BlockStack;
import com.songoda.ultimatestacker.settings.Settings;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BlockStackImpl implements BlockStack {

    // This is the unique identifier for this stack.
    // It is reset on every plugin load.
    // Used for holograms.
    private final UUID uniqueId = UUID.randomUUID();

    // The id that identifies this stack in the database.
    private int id;

    private int amount = 2;
    private XMaterial material;
    private Location location;

    /**
     * Required for deserialization
     */
    public BlockStackImpl() {
    }

    public BlockStackImpl(XMaterial material, Location location) {
        this.material = material;
        this.location = location;
        this.id = UltimateStacker.getInstance().getDataManager().getNextId("blocks");
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("amount", amount);
        map.put("material", material.name());
        map.putAll(SerializedLocation.of(location));
        return map;
    }

    @Override
    public Data deserialize(Map<String, Object> map) {
        id = (int) map.get("id");
        amount = (int) map.get("amount");
        material = XMaterial.valueOf((String) map.get("material"));
        location = SerializedLocation.of(map);
        return this;
    }

    @Override
    public String getTableName() {
        return "blocks";
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public void setAmount(int amount) {
        if (amount == 1) {
            destroy();
            return;
        }
        this.amount = amount;
    }

    @Override
    public boolean isValid() {
        return XMaterial.matchXMaterial(location.getBlock().getType().name()).get() == material;
    }

    @Override
    public void add(int amount) {
        this.amount = this.amount + amount;
    }

    @Override
    public void take(int amount) {
        this.amount = this.amount - amount;
    }

    @Override
    public void destroy() {
        amount = 0;
        UltimateStacker plugin = UltimateStacker.getInstance();
        plugin.getBlockStackManager().removeBlock(location);
        plugin.removeHologram(this);
        plugin.getPluginDataManager().delete(this);
    }

    @Override
    public Location getLocation() {
        return this.location.clone();
    }

    @Override
    public XMaterial getMaterial() {
        return material;
    }

    @Override
    public String getHologramName() {
        String nameFormat = Settings.NAME_FORMAT_BLOCK.getString();
        String displayName = TextUtils.formatText(material.name().toLowerCase().replace("_", " "), true);

        nameFormat = nameFormat.replace("{TYPE}", displayName);
        nameFormat = nameFormat.replace("{AMT}", Integer.toString(amount));

        return TextUtils.formatText(nameFormat).trim();
    }

    @Override
    public boolean areHologramsEnabled() {
        return Settings.BLOCK_HOLOGRAMS.getBoolean();
    }

    @Override
    public String getHologramId() {
        return "UltimateStacker-" + uniqueId;
    }

    @Override
    public String toString() {
        return "BlockStackImpl{" +
                "id=" + id +
                ", amount=" + amount +
                ", material=" + material +
                ", location=" + location +
                '}';
    }
}
