package net.minecraft.data.worldgen;

import net.minecraft.data.RegistryGeneration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurface;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceComposite;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceConfigurationBase;

public class WorldGenSurfaceComposites {

    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> BADLANDS = a("badlands", WorldGenSurface.BADLANDS.a(WorldGenSurface.CONFIG_BADLANDS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> BASALT_DELTAS = a("basalt_deltas", WorldGenSurface.BASALT_DELTAS.a(WorldGenSurface.CONFIG_BASALT_DELTAS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> CRIMSON_FOREST = a("crimson_forest", WorldGenSurface.NETHER_FOREST.a(WorldGenSurface.CONFIG_CRIMSON_FOREST));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> DESERT = a("desert", WorldGenSurface.DEFAULT.a(WorldGenSurface.CONFIG_DESERT));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> END = a("end", WorldGenSurface.DEFAULT.a(WorldGenSurface.CONFIG_THEEND));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> ERODED_BADLANDS = a("eroded_badlands", WorldGenSurface.ERODED_BADLANDS.a(WorldGenSurface.CONFIG_BADLANDS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> FROZEN_OCEAN = a("frozen_ocean", WorldGenSurface.FROZEN_OCEAN.a(WorldGenSurface.CONFIG_GRASS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> FULL_SAND = a("full_sand", WorldGenSurface.DEFAULT.a(WorldGenSurface.CONFIG_FULL_SAND));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> GIANT_TREE_TAIGA = a("giant_tree_taiga", WorldGenSurface.GIANT_TREE_TAIGA.a(WorldGenSurface.CONFIG_GRASS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> GRASS = a("grass", WorldGenSurface.DEFAULT.a(WorldGenSurface.CONFIG_GRASS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> GRAVELLY_MOUNTAIN = a("gravelly_mountain", WorldGenSurface.GRAVELLY_MOUNTAIN.a(WorldGenSurface.CONFIG_GRASS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> ICE_SPIKES = a("ice_spikes", WorldGenSurface.DEFAULT.a(new WorldGenSurfaceConfigurationBase(Blocks.SNOW_BLOCK.getBlockData(), Blocks.DIRT.getBlockData(), Blocks.GRAVEL.getBlockData())));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> MOUNTAIN = a("mountain", WorldGenSurface.MOUNTAIN.a(WorldGenSurface.CONFIG_GRASS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> MYCELIUM = a("mycelium", WorldGenSurface.DEFAULT.a(WorldGenSurface.CONFIG_MYCELIUM));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> NETHER = a("nether", WorldGenSurface.NETHER.a(WorldGenSurface.CONFIG_HELL));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> NOPE = a("nope", WorldGenSurface.NOPE.a(WorldGenSurface.CONFIG_STONE));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> OCEAN_SAND = a("ocean_sand", WorldGenSurface.DEFAULT.a(WorldGenSurface.CONFIG_OCEAN_SAND));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> SHATTERED_SAVANNA = a("shattered_savanna", WorldGenSurface.SHATTERED_SAVANNA.a(WorldGenSurface.CONFIG_GRASS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> SOUL_SAND_VALLEY = a("soul_sand_valley", WorldGenSurface.SOUL_SAND_VALLEY.a(WorldGenSurface.CONFIG_SOUL_SAND_VALLEY));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> STONE = a("stone", WorldGenSurface.DEFAULT.a(WorldGenSurface.CONFIG_STONE));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> SWAMP = a("swamp", WorldGenSurface.SWAMP.a(WorldGenSurface.CONFIG_GRASS));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> WARPED_FOREST = a("warped_forest", WorldGenSurface.NETHER_FOREST.a(WorldGenSurface.CONFIG_WARPED_FOREST));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> WOODED_BADLANDS = a("wooded_badlands", WorldGenSurface.WOODED_BADLANDS.a(WorldGenSurface.CONFIG_BADLANDS));

    public WorldGenSurfaceComposites() {}

    private static <SC extends WorldGenSurfaceConfiguration> WorldGenSurfaceComposite<SC> a(String s, WorldGenSurfaceComposite<SC> worldgensurfacecomposite) {
        return (WorldGenSurfaceComposite) RegistryGeneration.a(RegistryGeneration.CONFIGURED_SURFACE_BUILDER, s, (Object) worldgensurfacecomposite);
    }
}
