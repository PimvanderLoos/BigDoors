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

    public static final VillagerProfession NONE = register("none", VillagePlaceType.UNEMPLOYED, (SoundEffect) null);
    public static final VillagerProfession ARMORER = register("armorer", VillagePlaceType.ARMORER, SoundEffects.VILLAGER_WORK_ARMORER);
    public static final VillagerProfession BUTCHER = register("butcher", VillagePlaceType.BUTCHER, SoundEffects.VILLAGER_WORK_BUTCHER);
    public static final VillagerProfession CARTOGRAPHER = register("cartographer", VillagePlaceType.CARTOGRAPHER, SoundEffects.VILLAGER_WORK_CARTOGRAPHER);
    public static final VillagerProfession CLERIC = register("cleric", VillagePlaceType.CLERIC, SoundEffects.VILLAGER_WORK_CLERIC);
    public static final VillagerProfession FARMER = register("farmer", VillagePlaceType.FARMER, ImmutableSet.of(Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.BONE_MEAL), ImmutableSet.of(Blocks.FARMLAND), SoundEffects.VILLAGER_WORK_FARMER);
    public static final VillagerProfession FISHERMAN = register("fisherman", VillagePlaceType.FISHERMAN, SoundEffects.VILLAGER_WORK_FISHERMAN);
    public static final VillagerProfession FLETCHER = register("fletcher", VillagePlaceType.FLETCHER, SoundEffects.VILLAGER_WORK_FLETCHER);
    public static final VillagerProfession LEATHERWORKER = register("leatherworker", VillagePlaceType.LEATHERWORKER, SoundEffects.VILLAGER_WORK_LEATHERWORKER);
    public static final VillagerProfession LIBRARIAN = register("librarian", VillagePlaceType.LIBRARIAN, SoundEffects.VILLAGER_WORK_LIBRARIAN);
    public static final VillagerProfession MASON = register("mason", VillagePlaceType.MASON, SoundEffects.VILLAGER_WORK_MASON);
    public static final VillagerProfession NITWIT = register("nitwit", VillagePlaceType.NITWIT, (SoundEffect) null);
    public static final VillagerProfession SHEPHERD = register("shepherd", VillagePlaceType.SHEPHERD, SoundEffects.VILLAGER_WORK_SHEPHERD);
    public static final VillagerProfession TOOLSMITH = register("toolsmith", VillagePlaceType.TOOLSMITH, SoundEffects.VILLAGER_WORK_TOOLSMITH);
    public static final VillagerProfession WEAPONSMITH = register("weaponsmith", VillagePlaceType.WEAPONSMITH, SoundEffects.VILLAGER_WORK_WEAPONSMITH);
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

    public String getName() {
        return this.name;
    }

    public VillagePlaceType getJobPoiType() {
        return this.jobPoiType;
    }

    public ImmutableSet<Item> getRequestedItems() {
        return this.requestedItems;
    }

    public ImmutableSet<Block> getSecondaryPoi() {
        return this.secondaryPoi;
    }

    @Nullable
    public SoundEffect getWorkSound() {
        return this.workSound;
    }

    public String toString() {
        return this.name;
    }

    static VillagerProfession register(String s, VillagePlaceType villageplacetype, @Nullable SoundEffect soundeffect) {
        return register(s, villageplacetype, ImmutableSet.of(), ImmutableSet.of(), soundeffect);
    }

    static VillagerProfession register(String s, VillagePlaceType villageplacetype, ImmutableSet<Item> immutableset, ImmutableSet<Block> immutableset1, @Nullable SoundEffect soundeffect) {
        return (VillagerProfession) IRegistry.register(IRegistry.VILLAGER_PROFESSION, new MinecraftKey(s), new VillagerProfession(s, villageplacetype, immutableset, immutableset1, soundeffect));
    }
}
