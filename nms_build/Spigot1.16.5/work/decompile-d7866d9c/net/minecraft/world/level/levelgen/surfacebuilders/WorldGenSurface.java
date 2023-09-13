package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public abstract class WorldGenSurface<C extends WorldGenSurfaceConfiguration> {

    private static final IBlockData a = Blocks.DIRT.getBlockData();
    private static final IBlockData b = Blocks.GRASS_BLOCK.getBlockData();
    private static final IBlockData c = Blocks.PODZOL.getBlockData();
    private static final IBlockData d = Blocks.GRAVEL.getBlockData();
    private static final IBlockData e = Blocks.STONE.getBlockData();
    private static final IBlockData K = Blocks.COARSE_DIRT.getBlockData();
    private static final IBlockData L = Blocks.SAND.getBlockData();
    private static final IBlockData M = Blocks.RED_SAND.getBlockData();
    private static final IBlockData N = Blocks.WHITE_TERRACOTTA.getBlockData();
    private static final IBlockData O = Blocks.MYCELIUM.getBlockData();
    private static final IBlockData P = Blocks.SOUL_SAND.getBlockData();
    private static final IBlockData Q = Blocks.NETHERRACK.getBlockData();
    private static final IBlockData R = Blocks.END_STONE.getBlockData();
    private static final IBlockData S = Blocks.CRIMSON_NYLIUM.getBlockData();
    private static final IBlockData T = Blocks.WARPED_NYLIUM.getBlockData();
    private static final IBlockData U = Blocks.NETHER_WART_BLOCK.getBlockData();
    private static final IBlockData V = Blocks.WARPED_WART_BLOCK.getBlockData();
    private static final IBlockData W = Blocks.BLACKSTONE.getBlockData();
    private static final IBlockData X = Blocks.BASALT.getBlockData();
    private static final IBlockData Y = Blocks.MAGMA_BLOCK.getBlockData();
    public static final WorldGenSurfaceConfigurationBase f = new WorldGenSurfaceConfigurationBase(WorldGenSurface.c, WorldGenSurface.a, WorldGenSurface.d);
    public static final WorldGenSurfaceConfigurationBase g = new WorldGenSurfaceConfigurationBase(WorldGenSurface.d, WorldGenSurface.d, WorldGenSurface.d);
    public static final WorldGenSurfaceConfigurationBase h = new WorldGenSurfaceConfigurationBase(WorldGenSurface.b, WorldGenSurface.a, WorldGenSurface.d);
    public static final WorldGenSurfaceConfigurationBase i = new WorldGenSurfaceConfigurationBase(WorldGenSurface.e, WorldGenSurface.e, WorldGenSurface.d);
    public static final WorldGenSurfaceConfigurationBase j = new WorldGenSurfaceConfigurationBase(WorldGenSurface.K, WorldGenSurface.a, WorldGenSurface.d);
    public static final WorldGenSurfaceConfigurationBase k = new WorldGenSurfaceConfigurationBase(WorldGenSurface.L, WorldGenSurface.L, WorldGenSurface.d);
    public static final WorldGenSurfaceConfigurationBase l = new WorldGenSurfaceConfigurationBase(WorldGenSurface.b, WorldGenSurface.a, WorldGenSurface.L);
    public static final WorldGenSurfaceConfigurationBase m = new WorldGenSurfaceConfigurationBase(WorldGenSurface.L, WorldGenSurface.L, WorldGenSurface.L);
    public static final WorldGenSurfaceConfigurationBase n = new WorldGenSurfaceConfigurationBase(WorldGenSurface.M, WorldGenSurface.N, WorldGenSurface.d);
    public static final WorldGenSurfaceConfigurationBase o = new WorldGenSurfaceConfigurationBase(WorldGenSurface.O, WorldGenSurface.a, WorldGenSurface.d);
    public static final WorldGenSurfaceConfigurationBase p = new WorldGenSurfaceConfigurationBase(WorldGenSurface.Q, WorldGenSurface.Q, WorldGenSurface.Q);
    public static final WorldGenSurfaceConfigurationBase q = new WorldGenSurfaceConfigurationBase(WorldGenSurface.P, WorldGenSurface.P, WorldGenSurface.P);
    public static final WorldGenSurfaceConfigurationBase r = new WorldGenSurfaceConfigurationBase(WorldGenSurface.R, WorldGenSurface.R, WorldGenSurface.R);
    public static final WorldGenSurfaceConfigurationBase s = new WorldGenSurfaceConfigurationBase(WorldGenSurface.S, WorldGenSurface.Q, WorldGenSurface.U);
    public static final WorldGenSurfaceConfigurationBase t = new WorldGenSurfaceConfigurationBase(WorldGenSurface.T, WorldGenSurface.Q, WorldGenSurface.V);
    public static final WorldGenSurfaceConfigurationBase u = new WorldGenSurfaceConfigurationBase(WorldGenSurface.W, WorldGenSurface.X, WorldGenSurface.Y);
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> v = a("default", new WorldGenSurfaceDefaultBlock(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> w = a("mountain", new WorldGenSurfaceExtremeHills(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> x = a("shattered_savanna", new WorldGenSurfaceSavannaMutated(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> y = a("gravelly_mountain", new WorldGenSurfaceExtremeHillMutated(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> z = a("giant_tree_taiga", new WorldGenSurfaceTaigaMega(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> A = a("swamp", new WorldGenSurfaceSwamp(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> B = a("badlands", new WorldGenSurfaceMesa(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> C = a("wooded_badlands", new WorldGenSurfaceMesaForest(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> D = a("eroded_badlands", new WorldGenSurfaceMesaBryce(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> E = a("frozen_ocean", new WorldGenSurfaceFrozenOcean(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> F = a("nether", new WorldGenSurfaceNether(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> G = a("nether_forest", new WorldGenSurfaceNetherForest(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> H = a("soul_sand_valley", new WorldGenSurfaceSoulSandValley(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> I = a("basalt_deltas", new WorldGenSurfaceBasaltDeltas(WorldGenSurfaceConfigurationBase.a));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> J = a("nope", new WorldGenSurfaceEmpty(WorldGenSurfaceConfigurationBase.a));
    private final Codec<WorldGenSurfaceComposite<C>> Z;

    private static <C extends WorldGenSurfaceConfiguration, F extends WorldGenSurface<C>> F a(String s, F f0) {
        return (WorldGenSurface) IRegistry.a(IRegistry.SURFACE_BUILDER, s, (Object) f0);
    }

    public WorldGenSurface(Codec<C> codec) {
        this.Z = codec.fieldOf("config").xmap(this::a, WorldGenSurfaceComposite::a).codec();
    }

    public Codec<WorldGenSurfaceComposite<C>> d() {
        return this.Z;
    }

    public WorldGenSurfaceComposite<C> a(C c0) {
        return new WorldGenSurfaceComposite<>(this, c0);
    }

    public abstract void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1, C c0);

    public void a(long i) {}
}
