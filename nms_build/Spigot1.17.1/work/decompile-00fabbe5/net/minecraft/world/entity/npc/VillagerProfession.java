package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableSet;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.ai.village.poi.VillagePlaceType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class VillagerProfession {

    public static final VillagerProfession NONE = a("none", VillagePlaceType.UNEMPLOYED, (SoundEffect) null);
    public static final VillagerProfession ARMORER = a("armorer", VillagePlaceType.ARMORER, SoundEffects.VILLAGER_WORK_ARMORER);
    public static final VillagerProfession BUTCHER = a("butcher", VillagePlaceType.BUTCHER, SoundEffects.VILLAGER_WORK_BUTCHER);
    public static final VillagerProfession CARTOGRAPHER = a("cartographer", VillagePlaceType.CARTOGRAPHER, SoundEffects.VILLAGER_WORK_CARTOGRAPHER);
    public static final VillagerProfession CLERIC = a("cleric", VillagePlaceType.CLERIC, SoundEffects.VILLAGER_WORK_CLERIC);
    public static final VillagerProfession FARMER = a("farmer", VillagePlaceType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEffects.VILLAGER_WORK_FARMER);
    public static final VillagerProfession FISHERMAN = a("fisherman", VillagePlaceType.FISHERMAN, SoundEffects.VILLAGER_WORK_FISHERMAN);
    public static final VillagerProfession FLETCHER = a("fletcher", VillagePlaceType.FLETCHER, SoundEffects.VILLAGER_WORK_FLETCHER);
    public static final VillagerProfession LEATHERWORKER = a("leatherworker", VillagePlaceType.LEATHERWORKER, SoundEffects.VILLAGER_WORK_LEATHERWORKER);
    public static final VillagerProfession LIBRARIAN = a("librarian", VillagePlaceType.LIBRARIAN, SoundEffects.VILLAGER_WORK_LIBRARIAN);
    public static final VillagerProfession MASON = a("mason", VillagePlaceType.MASON, SoundEffects.VILLAGER_WORK_MASON);
    public static final VillagerProfession NITWIT = a("nitwit", VillagePlaceType.NITWIT, (SoundEffect) null);
    public static final VillagerProfession SHEPHERD = a("shepherd", VillagePlaceType.SHEPHERD, SoundEffects.VILLAGER_WORK_SHEPHERD);
    public static final VillagerProfession TOOLSMITH = a("toolsmith", VillagePlaceType.TOOLSMITH, SoundEffects.VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession WEAPONSMITH = a("weaponsmith", VillagePlaceType.WEAPONSMITH, SoundEffects.VILLAGER_WORK_WEAPONSMITH);
    private final String name;
    private final VillagePlaceType jobPoiType;
    private final ImmutableSet<Item> requestedItems;
    private final ImmutableSet<Block> secondaryPoi;
    @Nullable
    private final SoundEffect workSound;

    private VillagerProfession(String s, VillagePlaceType villageplacetype, ImmutableSet<Item> immutableset, ImmutableSet<Block> immutableset1, @Nullable SoundEffect soundeffect) {
        this.name = s;
        this.jobPoiType = villageplacetype;
        this.requestedItems = immutableset;
        this.secondaryPoi = immutableset1;
        this.workSound = soundeffect;
    }

    public String a() {
        return this.name;
    }

    public VillagePlaceType b() {
        return this.jobPoiType;
    }

    public ImmutableSet<Item> c() {
        return this.requestedItems;
    }

    public ImmutableSet<Block> d() {
        return this.secondaryPoi;
    }

    @Nullable
    public SoundEffect e() {
        return this.workSound;
    }

    public String toString() {
        return this.name;
    }

    static VillagerProfession a(String s, VillagePlaceType villageplacetype, @Nullable SoundEffect soundeffect) {
        return a(s, villageplacetype, ImmutableSet.of(), ImmutableSet.of(), soundeffect);
    }

    static VillagerProfession a(String s, VillagePlaceType villageplacetype, ImmutableSet<Item> immutableset, ImmutableSet<Block> immutableset1, @Nullable SoundEffect soundeffect) {
        return (VillagerProfession) IRegistry.a((IRegistry) IRegistry.VILLAGER_PROFESSION, new MinecraftKey(s), (Object) (new VillagerProfession(s, villageplacetype, immutableset, immutableset1, soundeffect)));
    }
}
