package com.songoda.ultimatestacker.stackable.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Check {

    SPAWN_REASON(false),
    NERFED(true),
    AGE(true),
    TICK_AGE(false),
    IS_TAMED(true),
    ANIMAL_OWNER(true),
    SKELETON_TYPE(true),
    ZOMBIE_BABY(true),
    HAS_EQUIPMENT(false),
    SLIME_SIZE(true),
    PIG_SADDLE(true),
    SHEEP_SHEARED(true),
    SHEEP_COLOR(true),
    SNOWMAN_DERPED(true),
    WOLF_COLLAR_COLOR(true),
    OCELOT_TYPE(false),
    HORSE_COLOR(false),
    HORSE_STYLE(true),
    HORSE_CARRYING_CHEST(true),
    HORSE_HAS_ARMOR(true),
    HORSE_HAS_SADDLE(true),
    HORSE_JUMP(true),
    RABBIT_TYPE(false),
    VILLAGER_PROFESSION(true),
    LLAMA_COLOR(false),
    LLAMA_STRENGTH(true),
    PARROT_TYPE(false),
    PUFFERFISH_STATE(true),
    TROPICALFISH_PATTERN(true),
    TROPICALFISH_BODY_COLOR(true),
    TROPICALFISH_PATTERN_COLOR(true),
    PHANTOM_SIZE(true),
    CAT_TYPE(false),
    AXOLOTL_VARIANT(false),
    AXOLOTL_PLAYING_DEAD(true),
    GLOW_SQUID_DARK_TICKS(true),
    GOAT_HAS_HORNS(false),
    FROG_VARIANT(true),
    TADPOLE_AGE(false),
    WARDEN_ANGER_LEVEL(false),
    SNIFFER_HAS_SEEDS(true),
    FOX_TYPE(false),
    HOGLIN_IMMUNE(true);
    private final boolean isEnabledByDefault;
    private final static Map<String, Check> checks = new HashMap();

    static {
        for (Check c : values()) {
            checks.put(c.name(), c);
        }
    }

    Check(boolean isEnabledByDefault) {
        this.isEnabledByDefault = isEnabledByDefault;
    }

    public static List<Check> getChecks(List<String> strChecks) {
        List<Check> checks = new ArrayList<>();
        for (String checkStr : strChecks)
            checks.add(getCheck(checkStr));
        return checks;
    }

    public boolean isEnabledByDefault() {
        return isEnabledByDefault;
    }

    public static Check getCheck(String name) {
        return name != null ? checks.get(name.toUpperCase()) : null;
    }
}
