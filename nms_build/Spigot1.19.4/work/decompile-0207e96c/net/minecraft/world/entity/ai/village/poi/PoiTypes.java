package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.SystemUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockBed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockPropertyBedPart;

public class PoiTypes {

    public static final ResourceKey<VillagePlaceType> ARMORER = createKey("armorer");
    public static final ResourceKey<VillagePlaceType> BUTCHER = createKey("butcher");
    public static final ResourceKey<VillagePlaceType> CARTOGRAPHER = createKey("cartographer");
    public static final ResourceKey<VillagePlaceType> CLERIC = createKey("cleric");
    public static final ResourceKey<VillagePlaceType> FARMER = createKey("farmer");
    public static final ResourceKey<VillagePlaceType> FISHERMAN = createKey("fisherman");
    public static final ResourceKey<VillagePlaceType> FLETCHER = createKey("fletcher");
    public static final ResourceKey<VillagePlaceType> LEATHERWORKER = createKey("leatherworker");
    public static final ResourceKey<VillagePlaceType> LIBRARIAN = createKey("librarian");
    public static final ResourceKey<VillagePlaceType> MASON = createKey("mason");
    public static final ResourceKey<VillagePlaceType> SHEPHERD = createKey("shepherd");
    public static final ResourceKey<VillagePlaceType> TOOLSMITH = createKey("toolsmith");
    public static final ResourceKey<VillagePlaceType> WEAPONSMITH = createKey("weaponsmith");
    public static final ResourceKey<VillagePlaceType> HOME = createKey("home");
    public static final ResourceKey<VillagePlaceType> MEETING = createKey("meeting");
    public static final ResourceKey<VillagePlaceType> BEEHIVE = createKey("beehive");
    public static final ResourceKey<VillagePlaceType> BEE_NEST = createKey("bee_nest");
    public static final ResourceKey<VillagePlaceType> NETHER_PORTAL = createKey("nether_portal");
    public static final ResourceKey<VillagePlaceType> LODESTONE = createKey("lodestone");
    public static final ResourceKey<VillagePlaceType> LIGHTNING_ROD = createKey("lightning_rod");
    private static final Set<IBlockData> BEDS = (Set) ImmutableList.of(Blocks.RED_BED, Blocks.BLACK_BED, Blocks.BLUE_BED, Blocks.BROWN_BED, Blocks.CYAN_BED, Blocks.GRAY_BED, Blocks.GREEN_BED, Blocks.LIGHT_BLUE_BED, Blocks.LIGHT_GRAY_BED, Blocks.LIME_BED, Blocks.MAGENTA_BED, Blocks.ORANGE_BED, new Block[]{Blocks.PINK_BED, Blocks.PURPLE_BED, Blocks.WHITE_BED, Blocks.YELLOW_BED}).stream().flatMap((block) -> {
        return block.getStateDefinition().getPossibleStates().stream();
    }).filter((iblockdata) -> {
        return iblockdata.getValue(BlockBed.PART) == BlockPropertyBedPart.HEAD;
    }).collect(ImmutableSet.toImmutableSet());
    private static final Set<IBlockData> CAULDRONS = (Set) ImmutableList.of(Blocks.CAULDRON, Blocks.LAVA_CAULDRON, Blocks.WATER_CAULDRON, Blocks.POWDER_SNOW_CAULDRON).stream().flatMap((block) -> {
        return block.getStateDefinition().getPossibleStates().stream();
    }).collect(ImmutableSet.toImmutableSet());
    private static final Map<IBlockData, Holder<VillagePlaceType>> TYPE_BY_STATE = Maps.newHashMap();

    public PoiTypes() {}

    private static Set<IBlockData> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    private static ResourceKey<VillagePlaceType> createKey(String s) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, new MinecraftKey(s));
    }

    private static VillagePlaceType register(IRegistry<VillagePlaceType> iregistry, ResourceKey<VillagePlaceType> resourcekey, Set<IBlockData> set, int i, int j) {
        VillagePlaceType villageplacetype = new VillagePlaceType(set, i, j);

        IRegistry.register(iregistry, resourcekey, villageplacetype);
        registerBlockStates(iregistry.getHolderOrThrow(resourcekey), set);
        return villageplacetype;
    }

    private static void registerBlockStates(Holder<VillagePlaceType> holder, Set<IBlockData> set) {
        set.forEach((iblockdata) -> {
            Holder<VillagePlaceType> holder1 = (Holder) PoiTypes.TYPE_BY_STATE.put(iblockdata, holder);

            if (holder1 != null) {
                throw (IllegalStateException) SystemUtils.pauseInIde(new IllegalStateException(String.format(Locale.ROOT, "%s is defined in more than one PoI type", iblockdata)));
            }
        });
    }

    public static Optional<Holder<VillagePlaceType>> forState(IBlockData iblockdata) {
        return Optional.ofNullable((Holder) PoiTypes.TYPE_BY_STATE.get(iblockdata));
    }

    public static boolean hasPoi(IBlockData iblockdata) {
        return PoiTypes.TYPE_BY_STATE.containsKey(iblockdata);
    }

    public static VillagePlaceType bootstrap(IRegistry<VillagePlaceType> iregistry) {
        register(iregistry, PoiTypes.ARMORER, getBlockStates(Blocks.BLAST_FURNACE), 1, 1);
        register(iregistry, PoiTypes.BUTCHER, getBlockStates(Blocks.SMOKER), 1, 1);
        register(iregistry, PoiTypes.CARTOGRAPHER, getBlockStates(Blocks.CARTOGRAPHY_TABLE), 1, 1);
        register(iregistry, PoiTypes.CLERIC, getBlockStates(Blocks.BREWING_STAND), 1, 1);
        register(iregistry, PoiTypes.FARMER, getBlockStates(Blocks.COMPOSTER), 1, 1);
        register(iregistry, PoiTypes.FISHERMAN, getBlockStates(Blocks.BARREL), 1, 1);
        register(iregistry, PoiTypes.FLETCHER, getBlockStates(Blocks.FLETCHING_TABLE), 1, 1);
        register(iregistry, PoiTypes.LEATHERWORKER, PoiTypes.CAULDRONS, 1, 1);
        register(iregistry, PoiTypes.LIBRARIAN, getBlockStates(Blocks.LECTERN), 1, 1);
        register(iregistry, PoiTypes.MASON, getBlockStates(Blocks.STONECUTTER), 1, 1);
        register(iregistry, PoiTypes.SHEPHERD, getBlockStates(Blocks.LOOM), 1, 1);
        register(iregistry, PoiTypes.TOOLSMITH, getBlockStates(Blocks.SMITHING_TABLE), 1, 1);
        register(iregistry, PoiTypes.WEAPONSMITH, getBlockStates(Blocks.GRINDSTONE), 1, 1);
        register(iregistry, PoiTypes.HOME, PoiTypes.BEDS, 1, 1);
        register(iregistry, PoiTypes.MEETING, getBlockStates(Blocks.BELL), 32, 6);
        register(iregistry, PoiTypes.BEEHIVE, getBlockStates(Blocks.BEEHIVE), 0, 1);
        register(iregistry, PoiTypes.BEE_NEST, getBlockStates(Blocks.BEE_NEST), 0, 1);
        register(iregistry, PoiTypes.NETHER_PORTAL, getBlockStates(Blocks.NETHER_PORTAL), 0, 1);
        register(iregistry, PoiTypes.LODESTONE, getBlockStates(Blocks.LODESTONE), 0, 1);
        return register(iregistry, PoiTypes.LIGHTNING_ROD, getBlockStates(Blocks.LIGHTNING_ROD), 0, 1);
    }
}
