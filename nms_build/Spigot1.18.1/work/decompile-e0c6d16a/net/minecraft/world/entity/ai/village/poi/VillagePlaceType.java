package net.minecraft.world.entity.ai.village.poi;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyBedPart;

public class VillagePlaceType {

    private static final Supplier<Set<VillagePlaceType>> ALL_JOB_POI_TYPES = Suppliers.memoize(() -> {
        return (Set) IRegistry.VILLAGER_PROFESSION.stream().map(VillagerProfession::getJobPoiType).collect(Collectors.toSet());
    });
    public static final Predicate<VillagePlaceType> ALL_JOBS = (villageplacetype) -> {
        return ((Set) VillagePlaceType.ALL_JOB_POI_TYPES.get()).contains(villageplacetype);
    };
    public static final Predicate<VillagePlaceType> ALL = (villageplacetype) -> {
        return true;
    };
    private static final Set<IBlockData> BEDS = (Set) ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((block) -> {
        return block.getStateDefinition().getPossibleStates().stream();
    }).filter((iblockdata) -> {
        return iblockdata.getValue(BlockBed.PART) == BlockPropertyBedPart.HEAD;
    }).collect(ImmutableSet.toImmutableSet());
    private static final Set<IBlockData> CAULDRONS = (Set) ImmutableList.of(Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON).stream().flatMap((block) -> {
        return block.getStateDefinition().getPossibleStates().stream();
    }).collect(ImmutableSet.toImmutableSet());
    private static final Map<IBlockData, VillagePlaceType> TYPE_BY_STATE = Maps.newHashMap();
    public static final VillagePlaceType UNEMPLOYED = register("unemployed", ImmutableSet.of(), 1, VillagePlaceType.ALL_JOBS, 1);
    public static final VillagePlaceType ARMORER = register("armorer", getBlockStates(Blocks.BLAST_FURNACE), 1, 1);
    public static final VillagePlaceType BUTCHER = register("butcher", getBlockStates(Blocks.SMOKER), 1, 1);
    public static final VillagePlaceType CARTOGRAPHER = register("cartographer", getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, 1);
    public static final VillagePlaceType CLERIC = register("cleric", getBlockStates(Blocks.BREWING_STAND), 1, 1);
    public static final VillagePlaceType FARMER = register("farmer", getBlockStates(Blocks.COMPOSTER), 1, 1);
    public static final VillagePlaceType FISHERMAN = register("fisherman", getBlockStates(Blocks.BARREL), 1, 1);
    public static final VillagePlaceType FLETCHER = register("fletcher", getBlockStates(Blocks.FLETCHING_TABLE), 1, 1);
    public static final VillagePlaceType LEATHERWORKER = register("leatherworker", VillagePlaceType.CAULDRONS, 1, 1);
    public static final VillagePlaceType LIBRARIAN = register("librarian", getBlockStates(Blocks.LECTERN), 1, 1);
    public static final VillagePlaceType MASON = register("mason", getBlockStates(Blocks.STONECUTTER), 1, 1);
    public static final VillagePlaceType NITWIT = register("nitwit", ImmutableSet.of(), 1, 1);
    public static final VillagePlaceType SHEPHERD = register("shepherd", getBlockStates(Blocks.LOOM), 1, 1);
    public static final VillagePlaceType TOOLSMITH = register("toolsmith", getBlockStates(Blocks.SMITHING_TABLE), 1, 1);
    public static final VillagePlaceType WEAPONSMITH = register("weaponsmith", getBlockStates(Blocks.GRINDSTONE), 1, 1);
    public static final VillagePlaceType HOME = register("home", VillagePlaceType.BEDS, 1, 1);
    public static final VillagePlaceType MEETING = register("meeting", getBlockStates(Blocks.BELL), 32, 6);
    public static final VillagePlaceType BEEHIVE = register("beehive", getBlockStates(Blocks.BEEHIVE), 0, 1);
    public static final VillagePlaceType BEE_NEST = register("bee_nest", getBlockStates(Blocks.BEE_NEST), 0, 1);
    public static final VillagePlaceType NETHER_PORTAL = register("nether_portal", getBlockStates(Blocks.NETHER_PORTAL), 0, 1);
    public static final VillagePlaceType LODESTONE = register("lodestone", getBlockStates(Blocks.LODESTONE), 0, 1);
    public static final VillagePlaceType LIGHTNING_ROD = register("lightning_rod", getBlockStates(Blocks.LIGHTNING_ROD), 0, 1);
    protected static final Set<IBlockData> ALL_STATES = new ObjectOpenHashSet(VillagePlaceType.TYPE_BY_STATE.keySet());
    private final String name;
    private final Set<IBlockData> matchingStates;
    private final int maxTickets;
    private final Predicate<VillagePlaceType> predicate;
    private final int validRange;

    private static Set<IBlockData> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    private VillagePlaceType(String s, Set<IBlockData> set, int i, Predicate<VillagePlaceType> predicate, int j) {
        this.name = s;
        this.matchingStates = ImmutableSet.copyOf(set);
        this.maxTickets = i;
        this.predicate = predicate;
        this.validRange = j;
    }

    private VillagePlaceType(String s, Set<IBlockData> set, int i, int j) {
        this.name = s;
        this.matchingStates = ImmutableSet.copyOf(set);
        this.maxTickets = i;
        this.predicate = (villageplacetype) -> {
            return villageplacetype == this;
        };
        this.validRange = j;
    }

    public String getName() {
        return this.name;
    }

    public int getMaxTickets() {
        return this.maxTickets;
    }

    public Predicate<VillagePlaceType> getPredicate() {
        return this.predicate;
    }

    public boolean is(IBlockData iblockdata) {
        return this.matchingStates.contains(iblockdata);
    }

    public int getValidRange() {
        return this.validRange;
    }

    public String toString() {
        return this.name;
    }

    private static VillagePlaceType register(String s, Set<IBlockData> set, int i, int j) {
        return registerBlockStates((VillagePlaceType) IRegistry.register(IRegistry.POINT_OF_INTEREST_TYPE, new MinecraftKey(s), new VillagePlaceType(s, set, i, j)));
    }

    private static VillagePlaceType register(String s, Set<IBlockData> set, int i, Predicate<VillagePlaceType> predicate, int j) {
        return registerBlockStates((VillagePlaceType) IRegistry.register(IRegistry.POINT_OF_INTEREST_TYPE, new MinecraftKey(s), new VillagePlaceType(s, set, i, predicate, j)));
    }

    private static VillagePlaceType registerBlockStates(VillagePlaceType villageplacetype) {
        villageplacetype.matchingStates.forEach((iblockdata) -> {
            VillagePlaceType villageplacetype1 = (VillagePlaceType) VillagePlaceType.TYPE_BY_STATE.put(iblockdata, villageplacetype);

            if (villageplacetype1 != null) {
                throw (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException(String.format("%s is defined in too many tags", iblockdata)));
            }
        });
        return villageplacetype;
    }

    public static Optional<VillagePlaceType> forState(IBlockData iblockdata) {
        return Optional.ofNullable((VillagePlaceType) VillagePlaceType.TYPE_BY_STATE.get(iblockdata));
    }
}
