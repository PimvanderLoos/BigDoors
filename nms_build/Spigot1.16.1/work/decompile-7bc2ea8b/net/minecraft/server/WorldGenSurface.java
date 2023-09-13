package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Random;

public abstract class WorldGenSurface<C extends WorldGenSurfaceConfiguration> {

    public static final IBlockData f = Blocks.AIR.getBlockData();
    public static final IBlockData g = Blocks.DIRT.getBlockData();
    public static final IBlockData h = Blocks.GRASS_BLOCK.getBlockData();
    public static final IBlockData i = Blocks.PODZOL.getBlockData();
    public static final IBlockData j = Blocks.GRAVEL.getBlockData();
    public static final IBlockData k = Blocks.STONE.getBlockData();
    public static final IBlockData l = Blocks.COARSE_DIRT.getBlockData();
    public static final IBlockData m = Blocks.SAND.getBlockData();
    public static final IBlockData n = Blocks.RED_SAND.getBlockData();
    public static final IBlockData o = Blocks.WHITE_TERRACOTTA.getBlockData();
    public static final IBlockData p = Blocks.MYCELIUM.getBlockData();
    public static final IBlockData q = Blocks.SOUL_SAND.getBlockData();
    public static final IBlockData r = Blocks.NETHERRACK.getBlockData();
    public static final IBlockData s = Blocks.END_STONE.getBlockData();
    public static final IBlockData t = Blocks.CRIMSON_NYLIUM.getBlockData();
    public static final IBlockData u = Blocks.WARPED_NYLIUM.getBlockData();
    public static final IBlockData v = Blocks.NETHER_WART_BLOCK.getBlockData();
    public static final IBlockData w = Blocks.WARPED_WART_BLOCK.getBlockData();
    public static final IBlockData x = Blocks.BLACKSTONE.getBlockData();
    public static final IBlockData y = Blocks.BASALT.getBlockData();
    public static final IBlockData z = Blocks.MAGMA_BLOCK.getBlockData();
    public static final WorldGenSurfaceConfigurationBase A = new WorldGenSurfaceConfigurationBase(WorldGenSurface.f, WorldGenSurface.f, WorldGenSurface.f);
    public static final WorldGenSurfaceConfigurationBase B = new WorldGenSurfaceConfigurationBase(WorldGenSurface.i, WorldGenSurface.g, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase C = new WorldGenSurfaceConfigurationBase(WorldGenSurface.j, WorldGenSurface.j, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase D = new WorldGenSurfaceConfigurationBase(WorldGenSurface.h, WorldGenSurface.g, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase E = new WorldGenSurfaceConfigurationBase(WorldGenSurface.g, WorldGenSurface.g, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase F = new WorldGenSurfaceConfigurationBase(WorldGenSurface.k, WorldGenSurface.k, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase G = new WorldGenSurfaceConfigurationBase(WorldGenSurface.l, WorldGenSurface.g, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase H = new WorldGenSurfaceConfigurationBase(WorldGenSurface.m, WorldGenSurface.m, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase I = new WorldGenSurfaceConfigurationBase(WorldGenSurface.h, WorldGenSurface.g, WorldGenSurface.m);
    public static final WorldGenSurfaceConfigurationBase J = new WorldGenSurfaceConfigurationBase(WorldGenSurface.m, WorldGenSurface.m, WorldGenSurface.m);
    public static final WorldGenSurfaceConfigurationBase K = new WorldGenSurfaceConfigurationBase(WorldGenSurface.n, WorldGenSurface.o, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase L = new WorldGenSurfaceConfigurationBase(WorldGenSurface.p, WorldGenSurface.g, WorldGenSurface.j);
    public static final WorldGenSurfaceConfigurationBase M = new WorldGenSurfaceConfigurationBase(WorldGenSurface.r, WorldGenSurface.r, WorldGenSurface.r);
    public static final WorldGenSurfaceConfigurationBase N = new WorldGenSurfaceConfigurationBase(WorldGenSurface.q, WorldGenSurface.q, WorldGenSurface.q);
    public static final WorldGenSurfaceConfigurationBase O = new WorldGenSurfaceConfigurationBase(WorldGenSurface.s, WorldGenSurface.s, WorldGenSurface.s);
    public static final WorldGenSurfaceConfigurationBase P = new WorldGenSurfaceConfigurationBase(WorldGenSurface.t, WorldGenSurface.r, WorldGenSurface.v);
    public static final WorldGenSurfaceConfigurationBase Q = new WorldGenSurfaceConfigurationBase(WorldGenSurface.u, WorldGenSurface.r, WorldGenSurface.w);
    public static final WorldGenSurfaceConfigurationBase R = new WorldGenSurfaceConfigurationBase(WorldGenSurface.x, WorldGenSurface.y, WorldGenSurface.z);
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> S = a("default", new WorldGenSurfaceDefaultBlock(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> T = a("mountain", new WorldGenSurfaceExtremeHills(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> U = a("shattered_savanna", new WorldGenSurfaceSavannaMutated(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> V = a("gravelly_mountain", new WorldGenSurfaceExtremeHillMutated(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> W = a("giant_tree_taiga", new WorldGenSurfaceTaigaMega(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> X = a("swamp", new WorldGenSurfaceSwamp(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> Y = a("badlands", new WorldGenSurfaceMesa(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> Z = a("wooded_badlands", new WorldGenSurfaceMesaForest(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> aa = a("eroded_badlands", new WorldGenSurfaceMesaBryce(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> ab = a("frozen_ocean", new WorldGenSurfaceFrozenOcean(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> ac = a("nether", new WorldGenSurfaceNether(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> ad = a("nether_forest", new WorldGenSurfaceNetherForest(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> ae = a("soul_sand_valley", new WorldGenSurfaceSoulSandValley(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> af = a("basalt_deltas", new WorldGenSurfaceBasaltDeltas(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> ag = a("nope", new WorldGenSurfaceEmpty(WorldGenSurfaceConfigurationBase.a));
    private final Codec<WorldGenSurfaceComposite<C>> a;

    private static <C extends WorldGenSurfaceConfiguration, F extends WorldGenSurface<C>> F a(String s, F f0) {
        return (WorldGenSurface) IRegistry.a(IRegistry.SURFACE_BUILDER, s, (Object) f0);
    }

    public WorldGenSurface(Codec<C> codec) {
        this.a = codec.fieldOf("config").xmap((worldgensurfaceconfiguration) -> {
            return new WorldGenSurfaceComposite<>(this, worldgensurfaceconfiguration);
        }, (worldgensurfacecomposite) -> {
            return worldgensurfacecomposite.c;
        }).codec();
    }

    public Codec<WorldGenSurfaceComposite<C>> d() {
        return this.a;
    }

    public abstract void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, C c0);

    public void a(long i) {}
}
