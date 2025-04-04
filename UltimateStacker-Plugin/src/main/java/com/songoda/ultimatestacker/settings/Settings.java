package com.songoda.ultimatestacker.settings;

import com.songoda.core.configuration.Config;
import com.songoda.core.configuration.ConfigSetting;
import com.songoda.ultimatestacker.UltimateStacker;
import com.songoda.ultimatestacker.stackable.entity.Check;
import com.songoda.ultimatestacker.stackable.entity.Split;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Settings implements com.songoda.ultimatestacker.api.Settings {

    static final Config config = UltimateStacker.getInstance().getCoreConfig();

    public static final ConfigSetting STACK_SEARCH_TICK_SPEED = new ConfigSetting(config, "Main.Stack Search Tick Speed", 5,
            "The speed in which a new stacks will be created.",
            "It is advised to keep this number low.");

    public static final ConfigSetting INSTANT_STACKING = new ConfigSetting(config, "Main.Instant Stacking", false,
            "Should entities stacked into existing stacks before they spawned?");

    public static final ConfigSetting DISABLED_WORLDS = new ConfigSetting(config, "Main.Disabled Worlds", Arrays.asList("World1", "World2", "World3"),
            "Worlds that stacking doesn't happen in.");

    // Note: this setting is also referenced in EpicSpawners
    public static final ConfigSetting STACK_ENTITIES = new ConfigSetting(config, "Entities.Enabled", true,
            "Should entities be stacked?");

    public static final ConfigSetting NAME_FORMAT_ENTITY = new ConfigSetting(config, "Entities.Name Format", "&f{TYPE} &6{AMT}x",
            "The text displayed above an entities head where {TYPE} refers to",
            "The entities type and {AMT} is the amount currently stacked.");

    public static final ConfigSetting SEARCH_RADIUS = new ConfigSetting(config, "Entities.Search Radius", 5,
            "The distance entities must be to each other in order to stack.");

    public static final ConfigSetting MAX_STACK_ENTITIES = new ConfigSetting(config, "Entities.Max Stack Size", 15,
            "The max amount of entities in a single stack. Max value is 2000000000");

    // Note: this setting is also referenced in EpicSpawners
    public static final ConfigSetting MIN_STACK_ENTITIES = new ConfigSetting(config, "Entities.Min Stack Amount", 5,
            "The minimum amount required before a stack can be formed.",
            "Do not set this to lower than 2.");

    public static final ConfigSetting MAX_PER_TYPE_STACKS_PER_CHUNK = new ConfigSetting(config, "Entities.Max Per Type Stacks Per Chunk", -1,
            "The maximum amount of each entity type stack allowed in a chunk.");

    public static final ConfigSetting STACK_WHOLE_CHUNK = new ConfigSetting(config, "Entities.Stack Whole Chunk", false,
            "Should all qualifying entities in each chunk be stacked?");

    public static final ConfigSetting STACK_WHOLE_CHUNK_RADIUS = new ConfigSetting(config, "Entities.Stack Whole Chunk Radius", 1,
            "Radius in chunks every direction around the entity that will be stacked.", "0 means only the chunk the entity is in.");

    public static final ConfigSetting ENTITY_NAMETAGS = new ConfigSetting(config, "Entities.Holograms Enabled", true,
            "Should holograms be displayed above stacked entities?");

    public static final ConfigSetting HOLOGRAMS_ON_LOOK_ENTITY = new ConfigSetting(config, "Entities.Only Show Holograms On Look", false,
            "Only show nametags above an entities head when looking directly at them.");

    public static final ConfigSetting CUSTOM_DROPS = new ConfigSetting(config, "Entities.Custom Drops.Enabled", true,
            "Should custom drops be enabled?");

    public static final ConfigSetting REROLL = new ConfigSetting(config, "Entities.Custom Drops.Reroll", true,
            "Increases chance of uncommon drops by making a second attempt to",
            "drop if the original attempt failed (Requires the looting enchantment).",
            "This is a default Minecraft mechanic.");

    public static final ConfigSetting KILL_WHOLE_STACK_ON_DEATH = new ConfigSetting(config, "Entities.Kill Whole Stack On Death", false,
            "Should killing a stack of entities kill the whole stack or",
            "just one out of the stack? If you want only certain entities to be",
            "effected by this you can configure it in the entities.yml");

    public static final ConfigSetting CLEAR_LAG = new ConfigSetting(config, "Entities.Clear Lag", false,
            "When enabled, the plugin will hook into ClearLag and extend the",
            "clear task to include stacked entities from this plugin. If this is enabled",
            "the built in task will not run.");

    public static final ConfigSetting INSTANT_KILL = new ConfigSetting(config, "Entities.Instant Kill", Arrays.asList("FALL", "DROWNING", "LAVA", "VOID"),
            "Events that will trigger an entire stack to be killed.",
            "It should be noted that this is useless if the above setting is true.",
            "Any of the following can be added to the list:",
            "\"CONTACT\", \"ENTITY_ATTACK\", \"ENTITY_SWEEP_ATTACK\", \"PROJECTILE\",",
            "\"SUFFOCATION\", \"FALL\", \"FIRE\", \"FIRE_TICK\",",
            "\"MELTING\", \"LAVA\", \"DROWNING\", \"BLOCK_EXPLOSION\",",
            "\"ENTITY_EXPLOSION\", \"VOID\", \"LIGHTNING\", \"SUICIDE\",",
            "\"STARVATION\", \"POISON\", \"MAGIC\", \"WITHER\",",
            "\"FALLING_BLOCK\", \"THORNS\", \"DRAGON_BREATH\", \"CUSTOM\",",
            "\"FLY_INTO_WALL\", \"HOT_FLOOR\", \"CRAMMING\", \"DRYOUT\".");


    public static final ConfigSetting NO_EXP_INSTANT_KILL = new ConfigSetting(config, "Entities.No Exp For Instant Kills", false,
            "Should no experience be dropped when an instant kill is performed?");

    public static final ConfigSetting DONT_DROP_ARMOR = new ConfigSetting(config, "Entities.Dont Drop Armor", false,
            "Should entities not drop their armor when custom drops are enabled?");

    public static final ConfigSetting STACK_CHECKS = new ConfigSetting(config, "Entities.Stack Checks", Arrays.asList(Check.values()).stream()
            .filter(Check::isEnabledByDefault).map(Check::name).collect(Collectors.toList()),
            "These are checks that are processed before an entity is stacked.",
            "You can add and remove from the list at will.",
            "The acceptable check options are:",
            "\"SPAWN_REASON\", \"NERFED\", \"AGE\", \"TICK_AGE\",",
            "\"IS_TAMED\", \"ANIMAL_OWNER\", \"SKELETON_TYPE\", \"ZOMBIE_BABY\",",
            "\"SLIME_SIZE\", \"PIG_SADDLE\", \"SHEEP_SHEARED\", \"SHEEP_COLOR\",",
            "\"SNOWMAN_DERPED\", \"WOLF_COLLAR_COLOR\", \"OCELOT_TYPE\", \"HORSE_COLOR\",",
            "\"HORSE_STYLE\", \"HORSE_CARRYING_CHEST\", \"HORSE_HAS_ARMOR\", \"HORSE_HAS_SADDLE\",",
            "\"HORSE_JUMP\", \"RABBIT_TYPE\", \"VILLAGER_PROFESSION\", \"LLAMA_COLOR\",",
            "\"LLAMA_STRENGTH\", \"PARROT_TYPE\", \"PUFFERFISH_STATE\", \"TROPICALFISH_PATTERN\",",
            "\"TROPICALFISH_BODY_COLOR\", \"TROPICALFISH_PATTERN_COLOR\", \"PHANTOM_SIZE\", \"CAT_TYPE\"",
            "\"AXOLOTL_VARIANT\", \"AXOLOTL_PLAYING_DEAD\", \"GLOW_SQUID_DARK_TICKS\", \"GOAT_HAS_HORNS\",",
            "\"FROG_VARIANT\", \"TADPOLE_AGE\", \"WARDEN_ANGER_LEVEL\", \"SNIFFER_HAS_SEEDS\",",
            "\"FOX_TYPE\", \"HOGLIN_IMMUNE\".");


    public static final ConfigSetting SPLIT_CHECKS = new ConfigSetting(config, "Entities.Split Checks", Arrays.asList(Split.values()).stream()
            .map(Split::name).collect(Collectors.toList()),
            "These are checks that when achieved will break separate a single entity",
            "from a stack.",
            "The following reasons can be added to the list:",
            "\"NAME_TAG\", \"MUSHROOM_SHEAR\", \"SHEEP_SHEAR\", \"SNOWMAN_DERP\",",
            "\"SHEEP_DYE\", \"ENTITY_BREED\".");

    public static final ConfigSetting ONLY_STACK_FROM_SPAWNERS = new ConfigSetting(config, "Entities.Only Stack From Spawners", false,
            "Should entities only be stacked if they originate from a spawner?",
            "It should be noted that the identifier that tells the plugin",
            "if the entity originated from a spawner or not is wiped on",
            "server restart.");

    public static final ConfigSetting STACK_REASONS = new ConfigSetting(config, "Entities.Stack Reasons", Arrays.asList(),
            "This will limit mob stacking to mobs who spawned via the listed reasons.",
            "This list is ignored if Only Stack From Spawners = true.",
            "The following reasons can be added to the list:",
            "\"NATURAL\", \"JOCKEY\", \"CHUNK_GEN\", \"SPAWNER\",",
            "\"EGG\", \"SPAWNER_EGG\", \"LIGHTNING\", \"BUILD_SNOWMAN\", \"HAS_EQUIPMENT\",",
            "\"BUILD_IRONGOLEM\", \"BUILD_WITHER\", \"VILLAGE_DEFENSE\", \"VILLAGE_INVASION\",",
            "\"BREEDING\", \"SLIME_SPLIT\", \"REINFORCEMENTS\", \"NETHER_PORTAL\",",
            "\"DISPENSE_EGG\", \"INFECTION\", \"CURED\", \"OCELOT_BABY\",",
            "\"SILVERFISH_BLOCK\", \"MOUNT\", \"TRAP\", \"ENDER_PEARL\",",
            "\"SHOULDER_ENTITY\", \"DROWNED\", \"SHEARED\", \"EXPLOSION\",",
            "\"CUSTOM\", \"DEFAULT\".");

    public static final ConfigSetting WEAPONS_ARENT_EQUIPMENT = new ConfigSetting(config, "Entities.Weapons Arent Equipment", false,
            "This allows entities holding weapons to stack. Enchanted weapons are excluded.",
            "If you would like to disable the stacked entity check you can do that by removing",
            "\"HAS_EQUIPMENT\", from the list above.");

    public static final ConfigSetting ONLY_STACK_ON_SURFACE = new ConfigSetting(config, "Entities.Only Stack On Surface", true,
            "Should entities only be stacked if they are touching the ground",
            "or swimming? This does not effect flying entities.");

    public static final ConfigSetting ONLY_STACK_FLYING_DOWN = new ConfigSetting(config, "Entities.Only Stack Flying Down", true,
            "Should entities that fly only stack with entities that are lower on the",
            "Y axis. This is important for grinders so that flying entities don't continuously",
            "stack upwards to a higher up entity.");

    public static final ConfigSetting REALISTIC_DAMAGE = new ConfigSetting(config, "Entities.Use Realistic Weapon Damage", true,
            "Should weapons take damage based on the amount of entites in the stack?");

    public static final ConfigSetting DISABLE_KNOCKBACK = new ConfigSetting(config, "Entities.Disable Knockback", false,
            "Should knockback be disabled on unstacked mobs?");

    public static final ConfigSetting SHEAR_IN_ONE_CLICK = new ConfigSetting(config, "Entities.Shear In One Click", false,
            "Should entities be sheared in a single click?");

    public static final ConfigSetting ENABLED_CUSTOM_ENTITY_PLUGINS = new ConfigSetting(config, "Entities.Enabled Custom Entity Plugins", Collections.singletonList("MythicMobs"),
            "Which custom entity plugins should be used?",
            "Remove a plugin from this list to disable the stacking of their entities.");

    public static final ConfigSetting BLACKLISTED_CUSTOM_ENTITIES = new ConfigSetting(config, "Entities.Blacklisted Custom Entities", Collections.singletonList("mythicmobs_test"),
            "Which custom entities should not be stacked?",
            "List the entities using their plugin name as a prefix in all lowercase.",
            "Example: mythicmobs_test");

    public static final ConfigSetting DATABASE_PURGE = new ConfigSetting(config, "Entities.Database purge", 14,
            "How many days must an entity be dormant before it is removed",
            "from the database? This setting will prevent dead entities killed incorrectly",
            "by other plugins from building up inside of your database and creating lag.");

    public static final ConfigSetting STACK_ITEMS = new ConfigSetting(config, "Items.Enabled", true,
            "Should items be stacked?");

    public static final ConfigSetting ITEM_HOLOGRAMS = new ConfigSetting(config, "Items.Holograms Enabled", true,
            "Should holograms be displayed above stacked items?");

    public static final ConfigSetting ITEM_MIN_HOLOGRAM_SIZE = new ConfigSetting(config, "Items.Minimum Hologram Stack Size", 0,
            "What should the minimum item stack size be that will show",
            "holograms?");

    public static final ConfigSetting ITEM_HOLOGRAM_BLACKLIST = new ConfigSetting(config, "Items.Show Holograms For Blacklisted Items", true,
            "Should items that are blacklisted display holograms?");

    public static final ConfigSetting MAX_STACK_ITEMS = new ConfigSetting(config, "Items.Max Stack Size", 512,
            "The max stack size for items. Max value is 1500000000");

    public static final ConfigSetting NAME_FORMAT_ITEM = new ConfigSetting(config, "Items.Name Format", "&f{TYPE} &r[&6{AMT}x]",
            "The text displayed above a dropped item.");

    public static final ConfigSetting NAME_FORMAT_RESET = new ConfigSetting(config, "Items.Name Format Reset", true,
            "Should color codes in dropped item names be removed?",
            "This is added only because it looks smoother in game. This is only visual and",
            "doesn't actually effect the item.");

    public static final ConfigSetting ITEM_BLACKLIST = new ConfigSetting(config, "Items.Blacklist", Collections.singletonList("BARRIER"),
            "Items included in this list will stack to default Minecraft amounts.",
            "Material list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html",
            "Leave this empty by using \"blacklist: []\" if you do not wish to disable",
            "stacking for any items.");

    public static final ConfigSetting ITEM_WHITELIST = new ConfigSetting(config, "Items.Whitelist", Collections.EMPTY_LIST,
            "Items included in this whitelist will be stacked.",
            "Material list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html",
            "Leave this empty by using \"whitelist: []\" if you want everything to be stacked.",
            "Items not in this list will act as if they are blacklisted.");

    public static final ConfigSetting SHOW_STACK_SIZE_SINGLE = new ConfigSetting(config, "Items.Show Stack Size For Single", false,
            "When enabled stack sizes for a stack with a single item will",
            "not display the stack size. The stack size will be added",
            "for stacks containing two or more items.");

    public static final ConfigSetting SPAWNERS_ENABLED = new ConfigSetting(config, "Spawners.Enabled", true,
            "Should spawners be stacked?");

    public static final ConfigSetting SPAWNER_HOLOGRAMS = new ConfigSetting(config, "Spawners.Holograms Enabled", true,
            "Should holograms be displayed above stacked spawners?");

    public static final ConfigSetting EGGS_CONVERT_SPAWNERS = new ConfigSetting(config, "Spawners.Eggs Convert Spawners", true,
            "Should eggs convert spawners? If enabled you will",
            "still need to give perms for it to work.");

    public static final ConfigSetting SPAWNERS_TO_INVENTORY = new ConfigSetting(config, "Spawners.Add Spawners To Inventory On Drop", false,
            "Should broken spawners be added directly to the players inventory?",
            "Alternatively they will drop to the ground?");

    public static final ConfigSetting MAX_STACK_SPAWNERS = new ConfigSetting(config, "Spawners.Max Stack Size", 5,
            "What should the max a spawner can stack to be?");

    public static final ConfigSetting SNEAK_FOR_STACK = new ConfigSetting(config, "Spawners.Sneak To Receive A Stacked Spawner", true,
            "Toggle ability to receive a stacked spawner when breaking a spawner while sneaking.");

    public static final ConfigSetting SNEAK_TO_ADD_ALL = new ConfigSetting(config, "Spawners.Sneak To Add All", true,
            "Should the player be able to add all spawners to the stack if they are sneaking?");

    public static final ConfigSetting SPAWNERS_DONT_EXPLODE = new ConfigSetting(config, "Spawners.Prevent Spawners From Exploding", false,
            "Should spawners not break when blown up?");

    public static final ConfigSetting EXPLOSION_DROP_CHANCE_TNT = new ConfigSetting(config, "Spawners.Chance On TNT Explosion", "100%",
            "Chance of a TNT explosion dropping a spawner.");

    public static final ConfigSetting EXPLOSION_DROP_CHANCE_CREEPER = new ConfigSetting(config, "Spawners.Chance On Creeper Explosion", "100%",
            "Chance of a creeper explosion dropping a spawner.");

    public static final ConfigSetting NO_AI = new ConfigSetting(config, "Spawners.Nerf Spawner Mobs", false,
            "If enabled mobs spawned by spawners will not move or attack.");

    public static final ConfigSetting NAME_FORMAT_SPAWNER = new ConfigSetting(config, "Spawners.Name Format", "&f{TYPE} Spawner &6{AMT}x",
            "The text displayed above a stacked spawner where {TYPE} refers to",
            "The entities type and {AMT} is the amount currently stacked.");

    public static final ConfigSetting STACK_BLOCKS = new ConfigSetting(config, "Blocks.Enabled", true,
            "Should blocks be stacked?");

    public static final ConfigSetting BLOCK_HOLOGRAMS = new ConfigSetting(config, "Blocks.Holograms Enabled", true,
            "Should holograms be displayed above stacked blocks?");

    public static final ConfigSetting STACKABLE_BLOCKS = new ConfigSetting(config, "Blocks.Stackable Blocks", Collections.singletonList("DIAMOND_BLOCK"),
            "What blocks should be stackable?");

    public static final ConfigSetting ALWAYS_ADD_ALL = new ConfigSetting(config, "Blocks.Always Add All", false,
            "Should the whole stack the player is holding always",
            "be added to the stack regardless of if they are sneaking or not?");

    public static final ConfigSetting MAX_REMOVEABLE = new ConfigSetting(config, "Blocks.Max Removeable", 64,
            "What should be the max amount that can be removed with",
            "a single click? Keep in mind high numbers could cause lag.");

    public static final ConfigSetting ADD_TO_INVENTORY = new ConfigSetting(config, "Blocks.Add To Inventory", false,
            "Should blocks be added directly to the inventory when removed?");

    public static final ConfigSetting NAME_FORMAT_BLOCK = new ConfigSetting(config, "Blocks.Name Format", "&6{AMT}x &f{TYPE}",
            "The text displayed above a stacked block where {TYPE} refers to",
            "The entities type and {AMT} is the amount currently stacked.");


    public static final ConfigSetting LANGUGE_MODE = new ConfigSetting(config, "System.Language Mode", "en_US",
            "The enabled language file.",
            "More language files (if available) can be found in the plugins data folder.");

    public static void setupConfig() {
        config.load();
        config.setAutoremove(true).setAutosave(true);

        if (config.getStringList("Entities.Stack Checks").contains("SHEEP_SHEERED")) {
            List<String> stackChecks = config.getStringList("Entities.Stack Checks");
            stackChecks.remove("SHEEP_SHEERED");
            stackChecks.addAll(Arrays.asList("SHEEP_SHEARED", "SNOWMAN_DERPED"));
            config.set("Entities.Stack Checks", stackChecks);

            List<String> splitChecks = config.getStringList("Entities.Split Checks");
            splitChecks.add("SNOWMAN_DERP");
            config.set("Entities.Split Checks", splitChecks);
        }

        config.saveChanges();
    }

    @Override
    public int getMaxItemStackSize() {
        return MAX_STACK_ITEMS.getInt();
    }

    @Override
    public boolean killWholeStackOnDeath() {
        return KILL_WHOLE_STACK_ON_DEATH.getBoolean();
    }
}
