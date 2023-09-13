package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public record VillagerProfession(String name, Predicate<Holder<VillagePlaceType>> heldJobSite, Predicate<Holder<VillagePlaceType>> acquirableJobSite, ImmutableSet<Item> requestedItems, ImmutableSet<Block> secondaryPoi, @Nullable SoundEffect workSound) {

    public static final Predicate<Holder<VillagePlaceType>> ALL_ACQUIRABLE_JOBS = (holder) -> {
        return holder.is(PoiTypeTags.ACQUIRABLE_JOB_SITE);
    };
    public static final VillagerProfession NONE = register("none", VillagePlaceType.NONE, VillagerProfession.ALL_ACQUIRABLE_JOBS, (SoundEffect) null);
    public static final VillagerProfession ARMORER = register("armorer", PoiTypes.ARMORER, SoundEffects.VILLAGER_WORK_ARMORER);
    public static final VillagerProfession BUTCHER = register("butcher", PoiTypes.BUTCHER, SoundEffects.VILLAGER_WORK_BUTCHER);
    public static final VillagerProfession CARTOGRAPHER = register("cartographer", PoiTypes.CARTOGRAPHER, SoundEffects.VILLAGER_WORK_CARTOGRAPHER);
    public static final VillagerProfession CLERIC = register("cleric", PoiTypes.CLERIC, SoundEffects.VILLAGER_WORK_CLERIC);
    public static final VillagerProfession FARMER = register("farmer", PoiTypes.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEffects.VILLAGER_WORK_FARMER);
    public static final VillagerProfession FISHERMAN = register("fisherman", PoiTypes.FISHERMAN, SoundEffects.VILLAGER_WORK_FISHERMAN);
    public static final VillagerProfession FLETCHER = register("fletcher", PoiTypes.FLETCHER, SoundEffects.VILLAGER_WORK_FLETCHER);
    public static final VillagerProfession LEATHERWORKER = register("leatherworker", PoiTypes.LEATHERWORKER, SoundEffects.VILLAGER_WORK_LEATHERWORKER);
    public static final VillagerProfession LIBRARIAN = register("librarian", PoiTypes.LIBRARIAN, SoundEffects.VILLAGER_WORK_LIBRARIAN);
    public static final VillagerProfession MASON = register("mason", PoiTypes.MASON, SoundEffects.VILLAGER_WORK_MASON);
    public static final VillagerProfession NITWIT = register("nitwit", VillagePlaceType.NONE, VillagePlaceType.NONE, (SoundEffect) null);
    public static final VillagerProfession SHEPHERD = register("shepherd", PoiTypes.SHEPHERD, SoundEffects.VILLAGER_WORK_SHEPHERD);
    public static final VillagerProfession TOOLSMITH = register("toolsmith", PoiTypes.TOOLSMITH, SoundEffects.VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession WEAPONSMITH = register("weaponsmith", PoiTypes.WEAPONSMITH, SoundEffects.VILLAGER_WORK_WEAPONSMITH);

    public String toString() {
        return this.name;
    }

    private static VillagerProfession register(String s, ResourceKey<VillagePlaceType> resourcekey, @Nullable SoundEffect soundeffect) {
        return register(s, (holder) -> {
            return holder.is(resourcekey);
        }, (holder) -> {
            return holder.is(resourcekey);
        }, soundeffect);
    }

    private static VillagerProfession register(String s, Predicate<Holder<VillagePlaceType>> predicate, Predicate<Holder<VillagePlaceType>> predicate1, @Nullable SoundEffect soundeffect) {
        return register(s, predicate, predicate1, ImmutableSet.of(), ImmutableSet.of(), soundeffect);
    }

    private static VillagerProfession register(String s, ResourceKey<VillagePlaceType> resourcekey, ImmutableSet<Item> immutableset, ImmutableSet<Block> immutableset1, @Nullable SoundEffect soundeffect) {
        return register(s, (holder) -> {
            return holder.is(resourcekey);
        }, (holder) -> {
            return holder.is(resourcekey);
        }, immutableset, immutableset1, soundeffect);
    }

    private static VillagerProfession register(String s, Predicate<Holder<VillagePlaceType>> predicate, Predicate<Holder<VillagePlaceType>> predicate1, ImmutableSet<Item> immutableset, ImmutableSet<Block> immutableset1, @Nullable SoundEffect soundeffect) {
        return (VillagerProfession) IRegistry.register(BuiltInRegistries.VILLAGER_PROFESSION, new MinecraftKey(s), new VillagerProfession(s, predicate, predicate1, immutableset, immutableset1, soundeffect));
    }
}
