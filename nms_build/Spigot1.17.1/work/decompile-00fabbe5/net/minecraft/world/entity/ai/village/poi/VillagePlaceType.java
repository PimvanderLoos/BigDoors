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
        return (Set) IRegistry.VILLAGER_PROFESSION.g().map(VillagerProfession::b).collect(Collectors.toSet());
    });
    public static final Predicate<VillagePlaceType> ALL_JOBS = (villageplacetype) -> {
        return ((Set) VillagePlaceType.ALL_JOB_POI_TYPES.get()).contains(villageplacetype);
    };
    public static final Predicate<VillagePlaceType> ALL = (villageplacetype) -> {
        return true;
    };
    private static final Set<IBlockData> BEDS = (Set) ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((block) -> {
        return block.getStates().a().stream();
    }).filter((iblockdata) -> {
        return iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.HEAD;
    }).collect(ImmutableSet.toImmutableSet());
    private static final Set<IBlockData> CAULDRONS = (Set) ImmutableList.of(Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON).stream().flatMap((block) -> {
        return block.getStates().a().stream();
    }).collect(ImmutableSet.toImmutableSet());
    private static final Map<IBlockData, VillagePlaceType> TYPE_BY_STATE = Maps.newHashMap();
    public static final VillagePlaceType UNEMPLOYED = a("unemployed", ImmutableSet.of(), 1, VillagePlaceType.ALL_JOBS, 1);
    public static final VillagePlaceType ARMORER = a("armorer", a(Blocks.BLAST_FURNACE), 1, 1);
    public static final VillagePlaceType BUTCHER = a("butcher", a(Blocks.SMOKER), 1, 1);
    public static final VillagePlaceType CARTOGRAPHER = a("cartographer", a(Blocks.CARTOGRAPHY_TABLE), 1, 1);
    public static final VillagePlaceType CLERIC = a("cleric", a(Blocks.BREWING_STAND), 1, 1);
    public static final VillagePlaceType FARMER = a("farmer", a(Blocks.COMPOSTER), 1, 1);
    public static final VillagePlaceType FISHERMAN = a("fisherman", a(Blocks.BARREL), 1, 1);
    public static final VillagePlaceType FLETCHER = a("fletcher", a(Blocks.FLETCHING_TABLE), 1, 1);
    public static final VillagePlaceType LEATHERWORKER = a("leatherworker", VillagePlaceType.CAULDRONS, 1, 1);
    public static final VillagePlaceType LIBRARIAN = a("librarian", a(Blocks.LECTERN), 1, 1);
    public static final VillagePlaceType MASON = a("mason", a(Blocks.STONECUTTER), 1, 1);
    public static final VillagePlaceType NITWIT = a("nitwit", ImmutableSet.of(), 1, 1);
    public static final VillagePlaceType SHEPHERD = a("shepherd", a(Blocks.LOOM), 1, 1);
    public static final VillagePlaceType TOOLSMITH = a("toolsmith", a(Blocks.SMITHING_TABLE), 1, 1);
    public static final VillagePlaceType WEAPONSMITH = a("weaponsmith", a(Blocks.GRINDSTONE), 1, 1);
    public static final VillagePlaceType HOME = a("home", VillagePlaceType.BEDS, 1, 1);
    public static final VillagePlaceType MEETING = a("meeting", a(Blocks.BELL), 32, 6);
    public static final VillagePlaceType BEEHIVE = a("beehive", a(Blocks.BEEHIVE), 0, 1);
    public static final VillagePlaceType BEE_NEST = a("bee_nest", a(Blocks.BEE_NEST), 0, 1);
    public static final VillagePlaceType NETHER_PORTAL = a("nether_portal", a(Blocks.NETHER_PORTAL), 0, 1);
    public static final VillagePlaceType LODESTONE = a("lodestone", a(Blocks.LODESTONE), 0, 1);
    public static final VillagePlaceType LIGHTNING_ROD = a("lightning_rod", a(Blocks.LIGHTNING_ROD), 0, 1);
    protected static final Set<IBlockData> ALL_STATES = new ObjectOpenHashSet(VillagePlaceType.TYPE_BY_STATE.keySet());
    private final String name;
    private final Set<IBlockData> matchingStates;
    private final int maxTickets;
    private final Predicate<VillagePlaceType> predicate;
    private final int validRange;

    private static Set<IBlockData> a(Block block) {
        return ImmutableSet.copyOf(block.getStates().a());
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

    public String a() {
        return this.name;
    }

    public int b() {
        return this.maxTickets;
    }

    public Predicate<VillagePlaceType> c() {
        return this.predicate;
    }

    public boolean a(IBlockData iblockdata) {
        return this.matchingStates.contains(iblockdata);
    }

    public int d() {
        return this.validRange;
    }

    public String toString() {
        return this.name;
    }

    private static VillagePlaceType a(String s, Set<IBlockData> set, int i, int j) {
        return a((VillagePlaceType) IRegistry.a((IRegistry) IRegistry.POINT_OF_INTEREST_TYPE, new MinecraftKey(s), (Object) (new VillagePlaceType(s, set, i, j))));
    }

    private static VillagePlaceType a(String s, Set<IBlockData> set, int i, Predicate<VillagePlaceType> predicate, int j) {
        return a((VillagePlaceType) IRegistry.a((IRegistry) IRegistry.POINT_OF_INTEREST_TYPE, new MinecraftKey(s), (Object) (new VillagePlaceType(s, set, i, predicate, j))));
    }

    private static VillagePlaceType a(VillagePlaceType villageplacetype) {
        villageplacetype.matchingStates.forEach((iblockdata) -> {
            VillagePlaceType villageplacetype1 = (VillagePlaceType) VillagePlaceType.TYPE_BY_STATE.put(iblockdata, villageplacetype);

            if (villageplacetype1 != null) {
                throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException(String.format("%s is defined in too many tags", iblockdata))));
            }
        });
        return villageplacetype;
    }

    public static Optional<VillagePlaceType> b(IBlockData iblockdata) {
        return Optional.ofNullable((VillagePlaceType) VillagePlaceType.TYPE_BY_STATE.get(iblockdata));
    }
}
