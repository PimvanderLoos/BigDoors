package net.minecraft.data.worldgen;

import net.minecraft.data.RegistryGeneration;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurface;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceComposite;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceConfiguration;
import net.minecraft.world.level.levelgen.surfacebuilders.WorldGenSurfaceConfigurationBase;

public class WorldGenSurfaceComposites {

    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> a = a("badlands", WorldGenSurface.B.a(WorldGenSurface.n));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> b = a("basalt_deltas", WorldGenSurface.I.a(WorldGenSurface.u));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> c = a("crimson_forest", WorldGenSurface.G.a(WorldGenSurface.s));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> d = a("desert", WorldGenSurface.v.a(WorldGenSurface.k));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> e = a("end", WorldGenSurface.v.a(WorldGenSurface.r));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> f = a("eroded_badlands", WorldGenSurface.D.a(WorldGenSurface.n));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> g = a("frozen_ocean", WorldGenSurface.E.a(WorldGenSurface.h));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> h = a("full_sand", WorldGenSurface.v.a(WorldGenSurface.m));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> i = a("giant_tree_taiga", WorldGenSurface.z.a(WorldGenSurface.h));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> j = a("grass", WorldGenSurface.v.a(WorldGenSurface.h));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> k = a("gravelly_mountain", WorldGenSurface.y.a(WorldGenSurface.h));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> l = a("ice_spikes", WorldGenSurface.v.a(new WorldGenSurfaceConfigurationBase(Blocks.SNOW_BLOCK.getBlockData(), Blocks.DIRT.getBlockData(), Blocks.GRAVEL.getBlockData())));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> m = a("mountain", WorldGenSurface.w.a(WorldGenSurface.h));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> n = a("mycelium", WorldGenSurface.v.a(WorldGenSurface.o));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> o = a("nether", WorldGenSurface.F.a(WorldGenSurface.p));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> p = a("nope", WorldGenSurface.J.a(WorldGenSurface.i));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> q = a("ocean_sand", WorldGenSurface.v.a(WorldGenSurface.l));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> r = a("shattered_savanna", WorldGenSurface.x.a(WorldGenSurface.h));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> s = a("soul_sand_valley", WorldGenSurface.H.a(WorldGenSurface.q));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> t = a("stone", WorldGenSurface.v.a(WorldGenSurface.i));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> u = a("swamp", WorldGenSurface.A.a(WorldGenSurface.h));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> v = a("warped_forest", WorldGenSurface.G.a(WorldGenSurface.t));
    public static final WorldGenSurfaceComposite<WorldGenSurfaceConfigurationBase> w = a("wooded_badlands", WorldGenSurface.C.a(WorldGenSurface.n));

    private static <SC extends WorldGenSurfaceConfiguration> WorldGenSurfaceComposite<SC> a(String s, WorldGenSurfaceComposite<SC> worldgensurfacecomposite) {
        return (WorldGenSurfaceComposite) RegistryGeneration.a(RegistryGeneration.c, s, (Object) worldgensurfacecomposite);
    }
}
