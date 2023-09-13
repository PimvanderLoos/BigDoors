package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VillagePlaceType {

    private static final Predicate<VillagePlaceType> v = (villageplacetype) -> {
        return ((Set) IRegistry.VILLAGER_PROFESSION.d().map(VillagerProfession::b).collect(Collectors.toSet())).contains(villageplacetype);
    };
    public static final Predicate<VillagePlaceType> a = (villageplacetype) -> {
        return true;
    };
    private static final Set<IBlockData> w = (Set) ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((block) -> {
        return block.getStates().a().stream();
    }).filter((iblockdata) -> {
        return iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.HEAD;
    }).collect(ImmutableSet.toImmutableSet());
    private static final Map<IBlockData, VillagePlaceType> x = Maps.newHashMap();
    public static final VillagePlaceType b = a("unemployed", ImmutableSet.of(), 1, VillagePlaceType.v, 1);
    public static final VillagePlaceType c = a("armorer", a(Blocks.BLAST_FURNACE), 1, 1);
    public static final VillagePlaceType d = a("butcher", a(Blocks.SMOKER), 1, 1);
    public static final VillagePlaceType e = a("cartographer", a(Blocks.CARTOGRAPHY_TABLE), 1, 1);
    public static final VillagePlaceType f = a("cleric", a(Blocks.BREWING_STAND), 1, 1);
    public static final VillagePlaceType g = a("farmer", a(Blocks.COMPOSTER), 1, 1);
    public static final VillagePlaceType h = a("fisherman", a(Blocks.BARREL), 1, 1);
    public static final VillagePlaceType i = a("fletcher", a(Blocks.FLETCHING_TABLE), 1, 1);
    public static final VillagePlaceType j = a("leatherworker", a(Blocks.CAULDRON), 1, 1);
    public static final VillagePlaceType k = a("librarian", a(Blocks.LECTERN), 1, 1);
    public static final VillagePlaceType l = a("mason", a(Blocks.STONECUTTER), 1, 1);
    public static final VillagePlaceType m = a("nitwit", ImmutableSet.of(), 1, 1);
    public static final VillagePlaceType n = a("shepherd", a(Blocks.LOOM), 1, 1);
    public static final VillagePlaceType o = a("toolsmith", a(Blocks.SMITHING_TABLE), 1, 1);
    public static final VillagePlaceType p = a("weaponsmith", a(Blocks.GRINDSTONE), 1, 1);
    public static final VillagePlaceType q = a("home", VillagePlaceType.w, 1, 1);
    public static final VillagePlaceType r = a("meeting", a(Blocks.BELL), 32, 6);
    public static final VillagePlaceType s = a("beehive", a(Blocks.BEEHIVE), 0, 1);
    public static final VillagePlaceType t = a("bee_nest", a(Blocks.BEE_NEST), 0, 1);
    public static final VillagePlaceType u = a("nether_portal", a(Blocks.NETHER_PORTAL), 0, 1);
    private final String y;
    private final Set<IBlockData> z;
    private final int A;
    private final Predicate<VillagePlaceType> B;
    private final int C;

    private static Set<IBlockData> a(Block block) {
        return ImmutableSet.copyOf(block.getStates().a());
    }

    private VillagePlaceType(String s, Set<IBlockData> set, int i, Predicate<VillagePlaceType> predicate, int j) {
        this.y = s;
        this.z = ImmutableSet.copyOf(set);
        this.A = i;
        this.B = predicate;
        this.C = j;
    }

    private VillagePlaceType(String s, Set<IBlockData> set, int i, int j) {
        this.y = s;
        this.z = ImmutableSet.copyOf(set);
        this.A = i;
        this.B = (villageplacetype) -> {
            return villageplacetype == this;
        };
        this.C = j;
    }

    public int b() {
        return this.A;
    }

    public Predicate<VillagePlaceType> c() {
        return this.B;
    }

    public int d() {
        return this.C;
    }

    public String toString() {
        return this.y;
    }

    private static VillagePlaceType a(String s, Set<IBlockData> set, int i, int j) {
        return a((VillagePlaceType) IRegistry.POINT_OF_INTEREST_TYPE.a(new MinecraftKey(s), (Object) (new VillagePlaceType(s, set, i, j))));
    }

    private static VillagePlaceType a(String s, Set<IBlockData> set, int i, Predicate<VillagePlaceType> predicate, int j) {
        return a((VillagePlaceType) IRegistry.POINT_OF_INTEREST_TYPE.a(new MinecraftKey(s), (Object) (new VillagePlaceType(s, set, i, predicate, j))));
    }

    private static VillagePlaceType a(VillagePlaceType villageplacetype) {
        villageplacetype.z.forEach((iblockdata) -> {
            VillagePlaceType villageplacetype1 = (VillagePlaceType) VillagePlaceType.x.put(iblockdata, villageplacetype);

            if (villageplacetype1 != null) {
                throw (IllegalStateException) SystemUtils.c(new IllegalStateException(String.format("%s is defined in too many tags", iblockdata)));
            }
        });
        return villageplacetype;
    }

    public static Optional<VillagePlaceType> b(IBlockData iblockdata) {
        return Optional.ofNullable(VillagePlaceType.x.get(iblockdata));
    }

    public static Stream<IBlockData> e() {
        return VillagePlaceType.x.keySet().stream();
    }
}
