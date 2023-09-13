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

    private static final Supplier<Set<VillagePlaceType>> y = Suppliers.memoize(() -> {
        return (Set) IRegistry.VILLAGER_PROFESSION.g().map(VillagerProfession::b).collect(Collectors.toSet());
    });
    public static final Predicate<VillagePlaceType> a = (villageplacetype) -> {
        return ((Set) VillagePlaceType.y.get()).contains(villageplacetype);
    };
    public static final Predicate<VillagePlaceType> b = (villageplacetype) -> {
        return true;
    };
    private static final Set<IBlockData> z = (Set) ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((block) -> {
        return block.getStates().a().stream();
    }).filter((iblockdata) -> {
        return iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.HEAD;
    }).collect(ImmutableSet.toImmutableSet());
    private static final Map<IBlockData, VillagePlaceType> A = Maps.newHashMap();
    public static final VillagePlaceType c = a("unemployed", ImmutableSet.of(), 1, VillagePlaceType.a, 1);
    public static final VillagePlaceType d = a("armorer", a(Blocks.BLAST_FURNACE), 1, 1);
    public static final VillagePlaceType e = a("butcher", a(Blocks.SMOKER), 1, 1);
    public static final VillagePlaceType f = a("cartographer", a(Blocks.CARTOGRAPHY_TABLE), 1, 1);
    public static final VillagePlaceType g = a("cleric", a(Blocks.BREWING_STAND), 1, 1);
    public static final VillagePlaceType h = a("farmer", a(Blocks.COMPOSTER), 1, 1);
    public static final VillagePlaceType i = a("fisherman", a(Blocks.BARREL), 1, 1);
    public static final VillagePlaceType j = a("fletcher", a(Blocks.FLETCHING_TABLE), 1, 1);
    public static final VillagePlaceType k = a("leatherworker", a(Blocks.CAULDRON), 1, 1);
    public static final VillagePlaceType l = a("librarian", a(Blocks.LECTERN), 1, 1);
    public static final VillagePlaceType m = a("mason", a(Blocks.STONECUTTER), 1, 1);
    public static final VillagePlaceType n = a("nitwit", ImmutableSet.of(), 1, 1);
    public static final VillagePlaceType o = a("shepherd", a(Blocks.LOOM), 1, 1);
    public static final VillagePlaceType p = a("toolsmith", a(Blocks.SMITHING_TABLE), 1, 1);
    public static final VillagePlaceType q = a("weaponsmith", a(Blocks.GRINDSTONE), 1, 1);
    public static final VillagePlaceType r = a("home", VillagePlaceType.z, 1, 1);
    public static final VillagePlaceType s = a("meeting", a(Blocks.BELL), 32, 6);
    public static final VillagePlaceType t = a("beehive", a(Blocks.BEEHIVE), 0, 1);
    public static final VillagePlaceType u = a("bee_nest", a(Blocks.BEE_NEST), 0, 1);
    public static final VillagePlaceType v = a("nether_portal", a(Blocks.NETHER_PORTAL), 0, 1);
    public static final VillagePlaceType w = a("lodestone", a(Blocks.LODESTONE), 0, 1);
    protected static final Set<IBlockData> x = new ObjectOpenHashSet(VillagePlaceType.A.keySet());
    private final String B;
    private final Set<IBlockData> C;
    private final int D;
    private final Predicate<VillagePlaceType> E;
    private final int F;

    private static Set<IBlockData> a(Block block) {
        return ImmutableSet.copyOf(block.getStates().a());
    }

    private VillagePlaceType(String s, Set<IBlockData> set, int i, Predicate<VillagePlaceType> predicate, int j) {
        this.B = s;
        this.C = ImmutableSet.copyOf(set);
        this.D = i;
        this.E = predicate;
        this.F = j;
    }

    private VillagePlaceType(String s, Set<IBlockData> set, int i, int j) {
        this.B = s;
        this.C = ImmutableSet.copyOf(set);
        this.D = i;
        this.E = (villageplacetype) -> {
            return villageplacetype == this;
        };
        this.F = j;
    }

    public int b() {
        return this.D;
    }

    public Predicate<VillagePlaceType> c() {
        return this.E;
    }

    public int d() {
        return this.F;
    }

    public String toString() {
        return this.B;
    }

    private static VillagePlaceType a(String s, Set<IBlockData> set, int i, int j) {
        return a((VillagePlaceType) IRegistry.a((IRegistry) IRegistry.POINT_OF_INTEREST_TYPE, new MinecraftKey(s), (Object) (new VillagePlaceType(s, set, i, j))));
    }

    private static VillagePlaceType a(String s, Set<IBlockData> set, int i, Predicate<VillagePlaceType> predicate, int j) {
        return a((VillagePlaceType) IRegistry.a((IRegistry) IRegistry.POINT_OF_INTEREST_TYPE, new MinecraftKey(s), (Object) (new VillagePlaceType(s, set, i, predicate, j))));
    }

    private static VillagePlaceType a(VillagePlaceType villageplacetype) {
        villageplacetype.C.forEach((iblockdata) -> {
            VillagePlaceType villageplacetype1 = (VillagePlaceType) VillagePlaceType.A.put(iblockdata, villageplacetype);

            if (villageplacetype1 != null) {
                throw (IllegalStateException) SystemUtils.c((Throwable) (new IllegalStateException(String.format("%s is defined in too many tags", iblockdata))));
            }
        });
        return villageplacetype;
    }

    public static Optional<VillagePlaceType> b(IBlockData iblockdata) {
        return Optional.ofNullable(VillagePlaceType.A.get(iblockdata));
    }
}
