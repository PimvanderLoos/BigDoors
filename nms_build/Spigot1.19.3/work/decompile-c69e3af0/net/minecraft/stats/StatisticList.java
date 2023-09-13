package net.minecraft.stats;

import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class StatisticList {

    public static final StatisticWrapper<Block> BLOCK_MINED = makeRegistryStatType("mined", BuiltInRegistries.BLOCK);
    public static final StatisticWrapper<Item> ITEM_CRAFTED = makeRegistryStatType("crafted", BuiltInRegistries.ITEM);
    public static final StatisticWrapper<Item> ITEM_USED = makeRegistryStatType("used", BuiltInRegistries.ITEM);
    public static final StatisticWrapper<Item> ITEM_BROKEN = makeRegistryStatType("broken", BuiltInRegistries.ITEM);
    public static final StatisticWrapper<Item> ITEM_PICKED_UP = makeRegistryStatType("picked_up", BuiltInRegistries.ITEM);
    public static final StatisticWrapper<Item> ITEM_DROPPED = makeRegistryStatType("dropped", BuiltInRegistries.ITEM);
    public static final StatisticWrapper<EntityTypes<?>> ENTITY_KILLED = makeRegistryStatType("killed", BuiltInRegistries.ENTITY_TYPE);
    public static final StatisticWrapper<EntityTypes<?>> ENTITY_KILLED_BY = makeRegistryStatType("killed_by", BuiltInRegistries.ENTITY_TYPE);
    public static final StatisticWrapper<MinecraftKey> CUSTOM = makeRegistryStatType("custom", BuiltInRegistries.CUSTOM_STAT);
    public static final MinecraftKey LEAVE_GAME = makeCustomStat("leave_game", Counter.DEFAULT);
    public static final MinecraftKey PLAY_TIME = makeCustomStat("play_time", Counter.TIME);
    public static final MinecraftKey TOTAL_WORLD_TIME = makeCustomStat("total_world_time", Counter.TIME);
    public static final MinecraftKey TIME_SINCE_DEATH = makeCustomStat("time_since_death", Counter.TIME);
    public static final MinecraftKey TIME_SINCE_REST = makeCustomStat("time_since_rest", Counter.TIME);
    public static final MinecraftKey CROUCH_TIME = makeCustomStat("sneak_time", Counter.TIME);
    public static final MinecraftKey WALK_ONE_CM = makeCustomStat("walk_one_cm", Counter.DISTANCE);
    public static final MinecraftKey CROUCH_ONE_CM = makeCustomStat("crouch_one_cm", Counter.DISTANCE);
    public static final MinecraftKey SPRINT_ONE_CM = makeCustomStat("sprint_one_cm", Counter.DISTANCE);
    public static final MinecraftKey WALK_ON_WATER_ONE_CM = makeCustomStat("walk_on_water_one_cm", Counter.DISTANCE);
    public static final MinecraftKey FALL_ONE_CM = makeCustomStat("fall_one_cm", Counter.DISTANCE);
    public static final MinecraftKey CLIMB_ONE_CM = makeCustomStat("climb_one_cm", Counter.DISTANCE);
    public static final MinecraftKey FLY_ONE_CM = makeCustomStat("fly_one_cm", Counter.DISTANCE);
    public static final MinecraftKey WALK_UNDER_WATER_ONE_CM = makeCustomStat("walk_under_water_one_cm", Counter.DISTANCE);
    public static final MinecraftKey MINECART_ONE_CM = makeCustomStat("minecart_one_cm", Counter.DISTANCE);
    public static final MinecraftKey BOAT_ONE_CM = makeCustomStat("boat_one_cm", Counter.DISTANCE);
    public static final MinecraftKey PIG_ONE_CM = makeCustomStat("pig_one_cm", Counter.DISTANCE);
    public static final MinecraftKey HORSE_ONE_CM = makeCustomStat("horse_one_cm", Counter.DISTANCE);
    public static final MinecraftKey AVIATE_ONE_CM = makeCustomStat("aviate_one_cm", Counter.DISTANCE);
    public static final MinecraftKey SWIM_ONE_CM = makeCustomStat("swim_one_cm", Counter.DISTANCE);
    public static final MinecraftKey STRIDER_ONE_CM = makeCustomStat("strider_one_cm", Counter.DISTANCE);
    public static final MinecraftKey JUMP = makeCustomStat("jump", Counter.DEFAULT);
    public static final MinecraftKey DROP = makeCustomStat("drop", Counter.DEFAULT);
    public static final MinecraftKey DAMAGE_DEALT = makeCustomStat("damage_dealt", Counter.DIVIDE_BY_TEN);
    public static final MinecraftKey DAMAGE_DEALT_ABSORBED = makeCustomStat("damage_dealt_absorbed", Counter.DIVIDE_BY_TEN);
    public static final MinecraftKey DAMAGE_DEALT_RESISTED = makeCustomStat("damage_dealt_resisted", Counter.DIVIDE_BY_TEN);
    public static final MinecraftKey DAMAGE_TAKEN = makeCustomStat("damage_taken", Counter.DIVIDE_BY_TEN);
    public static final MinecraftKey DAMAGE_BLOCKED_BY_SHIELD = makeCustomStat("damage_blocked_by_shield", Counter.DIVIDE_BY_TEN);
    public static final MinecraftKey DAMAGE_ABSORBED = makeCustomStat("damage_absorbed", Counter.DIVIDE_BY_TEN);
    public static final MinecraftKey DAMAGE_RESISTED = makeCustomStat("damage_resisted", Counter.DIVIDE_BY_TEN);
    public static final MinecraftKey DEATHS = makeCustomStat("deaths", Counter.DEFAULT);
    public static final MinecraftKey MOB_KILLS = makeCustomStat("mob_kills", Counter.DEFAULT);
    public static final MinecraftKey ANIMALS_BRED = makeCustomStat("animals_bred", Counter.DEFAULT);
    public static final MinecraftKey PLAYER_KILLS = makeCustomStat("player_kills", Counter.DEFAULT);
    public static final MinecraftKey FISH_CAUGHT = makeCustomStat("fish_caught", Counter.DEFAULT);
    public static final MinecraftKey TALKED_TO_VILLAGER = makeCustomStat("talked_to_villager", Counter.DEFAULT);
    public static final MinecraftKey TRADED_WITH_VILLAGER = makeCustomStat("traded_with_villager", Counter.DEFAULT);
    public static final MinecraftKey EAT_CAKE_SLICE = makeCustomStat("eat_cake_slice", Counter.DEFAULT);
    public static final MinecraftKey FILL_CAULDRON = makeCustomStat("fill_cauldron", Counter.DEFAULT);
    public static final MinecraftKey USE_CAULDRON = makeCustomStat("use_cauldron", Counter.DEFAULT);
    public static final MinecraftKey CLEAN_ARMOR = makeCustomStat("clean_armor", Counter.DEFAULT);
    public static final MinecraftKey CLEAN_BANNER = makeCustomStat("clean_banner", Counter.DEFAULT);
    public static final MinecraftKey CLEAN_SHULKER_BOX = makeCustomStat("clean_shulker_box", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_BREWINGSTAND = makeCustomStat("interact_with_brewingstand", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_BEACON = makeCustomStat("interact_with_beacon", Counter.DEFAULT);
    public static final MinecraftKey INSPECT_DROPPER = makeCustomStat("inspect_dropper", Counter.DEFAULT);
    public static final MinecraftKey INSPECT_HOPPER = makeCustomStat("inspect_hopper", Counter.DEFAULT);
    public static final MinecraftKey INSPECT_DISPENSER = makeCustomStat("inspect_dispenser", Counter.DEFAULT);
    public static final MinecraftKey PLAY_NOTEBLOCK = makeCustomStat("play_noteblock", Counter.DEFAULT);
    public static final MinecraftKey TUNE_NOTEBLOCK = makeCustomStat("tune_noteblock", Counter.DEFAULT);
    public static final MinecraftKey POT_FLOWER = makeCustomStat("pot_flower", Counter.DEFAULT);
    public static final MinecraftKey TRIGGER_TRAPPED_CHEST = makeCustomStat("trigger_trapped_chest", Counter.DEFAULT);
    public static final MinecraftKey OPEN_ENDERCHEST = makeCustomStat("open_enderchest", Counter.DEFAULT);
    public static final MinecraftKey ENCHANT_ITEM = makeCustomStat("enchant_item", Counter.DEFAULT);
    public static final MinecraftKey PLAY_RECORD = makeCustomStat("play_record", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_FURNACE = makeCustomStat("interact_with_furnace", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_CRAFTING_TABLE = makeCustomStat("interact_with_crafting_table", Counter.DEFAULT);
    public static final MinecraftKey OPEN_CHEST = makeCustomStat("open_chest", Counter.DEFAULT);
    public static final MinecraftKey SLEEP_IN_BED = makeCustomStat("sleep_in_bed", Counter.DEFAULT);
    public static final MinecraftKey OPEN_SHULKER_BOX = makeCustomStat("open_shulker_box", Counter.DEFAULT);
    public static final MinecraftKey OPEN_BARREL = makeCustomStat("open_barrel", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_BLAST_FURNACE = makeCustomStat("interact_with_blast_furnace", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_SMOKER = makeCustomStat("interact_with_smoker", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_LECTERN = makeCustomStat("interact_with_lectern", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_CAMPFIRE = makeCustomStat("interact_with_campfire", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_CARTOGRAPHY_TABLE = makeCustomStat("interact_with_cartography_table", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_LOOM = makeCustomStat("interact_with_loom", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_STONECUTTER = makeCustomStat("interact_with_stonecutter", Counter.DEFAULT);
    public static final MinecraftKey BELL_RING = makeCustomStat("bell_ring", Counter.DEFAULT);
    public static final MinecraftKey RAID_TRIGGER = makeCustomStat("raid_trigger", Counter.DEFAULT);
    public static final MinecraftKey RAID_WIN = makeCustomStat("raid_win", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_ANVIL = makeCustomStat("interact_with_anvil", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_GRINDSTONE = makeCustomStat("interact_with_grindstone", Counter.DEFAULT);
    public static final MinecraftKey TARGET_HIT = makeCustomStat("target_hit", Counter.DEFAULT);
    public static final MinecraftKey INTERACT_WITH_SMITHING_TABLE = makeCustomStat("interact_with_smithing_table", Counter.DEFAULT);

    public StatisticList() {}

    private static MinecraftKey makeCustomStat(String s, Counter counter) {
        MinecraftKey minecraftkey = new MinecraftKey(s);

        IRegistry.register(BuiltInRegistries.CUSTOM_STAT, s, minecraftkey);
        StatisticList.CUSTOM.get(minecraftkey, counter);
        return minecraftkey;
    }

    private static <T> StatisticWrapper<T> makeRegistryStatType(String s, IRegistry<T> iregistry) {
        return (StatisticWrapper) IRegistry.register(BuiltInRegistries.STAT_TYPE, s, new StatisticWrapper<>(iregistry));
    }
}
