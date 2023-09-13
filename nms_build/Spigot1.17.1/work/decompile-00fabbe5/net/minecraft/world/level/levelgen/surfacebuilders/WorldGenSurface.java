package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.IRegistry;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;

public abstract class WorldGenSurface<C extends WorldGenSurfaceConfiguration> {

    private static final IBlockData DIRT = Blocks.DIRT.getBlockData();
    private static final IBlockData GRASS_BLOCK = Blocks.GRASS_BLOCK.getBlockData();
    private static final IBlockData PODZOL = Blocks.PODZOL.getBlockData();
    private static final IBlockData GRAVEL = Blocks.GRAVEL.getBlockData();
    private static final IBlockData STONE = Blocks.STONE.getBlockData();
    private static final IBlockData COARSE_DIRT = Blocks.COARSE_DIRT.getBlockData();
    private static final IBlockData SAND = Blocks.SAND.getBlockData();
    private static final IBlockData RED_SAND = Blocks.RED_SAND.getBlockData();
    private static final IBlockData WHITE_TERRACOTTA = Blocks.WHITE_TERRACOTTA.getBlockData();
    private static final IBlockData MYCELIUM = Blocks.MYCELIUM.getBlockData();
    private static final IBlockData SOUL_SAND = Blocks.SOUL_SAND.getBlockData();
    private static final IBlockData NETHERRACK = Blocks.NETHERRACK.getBlockData();
    private static final IBlockData ENDSTONE = Blocks.END_STONE.getBlockData();
    private static final IBlockData CRIMSON_NYLIUM = Blocks.CRIMSON_NYLIUM.getBlockData();
    private static final IBlockData WARPED_NYLIUM = Blocks.WARPED_NYLIUM.getBlockData();
    private static final IBlockData NETHER_WART_BLOCK = Blocks.NETHER_WART_BLOCK.getBlockData();
    private static final IBlockData WARPED_WART_BLOCK = Blocks.WARPED_WART_BLOCK.getBlockData();
    private static final IBlockData BLACKSTONE = Blocks.BLACKSTONE.getBlockData();
    private static final IBlockData BASALT = Blocks.BASALT.getBlockData();
    private static final IBlockData MAGMA = Blocks.MAGMA_BLOCK.getBlockData();
    public static final WorldGenSurfaceConfigurationBase CONFIG_PODZOL = new WorldGenSurfaceConfigurationBase(WorldGenSurface.PODZOL, WorldGenSurface.DIRT, WorldGenSurface.GRAVEL);
    public static final WorldGenSurfaceConfigurationBase CONFIG_GRAVEL = new WorldGenSurfaceConfigurationBase(WorldGenSurface.GRAVEL, WorldGenSurface.GRAVEL, WorldGenSurface.GRAVEL);
    public static final WorldGenSurfaceConfigurationBase CONFIG_GRASS = new WorldGenSurfaceConfigurationBase(WorldGenSurface.GRASS_BLOCK, WorldGenSurface.DIRT, WorldGenSurface.GRAVEL);
    public static final WorldGenSurfaceConfigurationBase CONFIG_STONE = new WorldGenSurfaceConfigurationBase(WorldGenSurface.STONE, WorldGenSurface.STONE, WorldGenSurface.GRAVEL);
    public static final WorldGenSurfaceConfigurationBase CONFIG_COARSE_DIRT = new WorldGenSurfaceConfigurationBase(WorldGenSurface.COARSE_DIRT, WorldGenSurface.DIRT, WorldGenSurface.GRAVEL);
    public static final WorldGenSurfaceConfigurationBase CONFIG_DESERT = new WorldGenSurfaceConfigurationBase(WorldGenSurface.SAND, WorldGenSurface.SAND, WorldGenSurface.GRAVEL);
    public static final WorldGenSurfaceConfigurationBase CONFIG_OCEAN_SAND = new WorldGenSurfaceConfigurationBase(WorldGenSurface.GRASS_BLOCK, WorldGenSurface.DIRT, WorldGenSurface.SAND);
    public static final WorldGenSurfaceConfigurationBase CONFIG_FULL_SAND = new WorldGenSurfaceConfigurationBase(WorldGenSurface.SAND, WorldGenSurface.SAND, WorldGenSurface.SAND);
    public static final WorldGenSurfaceConfigurationBase CONFIG_BADLANDS = new WorldGenSurfaceConfigurationBase(WorldGenSurface.RED_SAND, WorldGenSurface.WHITE_TERRACOTTA, WorldGenSurface.GRAVEL);
    public static final WorldGenSurfaceConfigurationBase CONFIG_MYCELIUM = new WorldGenSurfaceConfigurationBase(WorldGenSurface.MYCELIUM, WorldGenSurface.DIRT, WorldGenSurface.GRAVEL);
    public static final WorldGenSurfaceConfigurationBase CONFIG_HELL = new WorldGenSurfaceConfigurationBase(WorldGenSurface.NETHERRACK, WorldGenSurface.NETHERRACK, WorldGenSurface.NETHERRACK);
    public static final WorldGenSurfaceConfigurationBase CONFIG_SOUL_SAND_VALLEY = new WorldGenSurfaceConfigurationBase(WorldGenSurface.SOUL_SAND, WorldGenSurface.SOUL_SAND, WorldGenSurface.SOUL_SAND);
    public static final WorldGenSurfaceConfigurationBase CONFIG_THEEND = new WorldGenSurfaceConfigurationBase(WorldGenSurface.ENDSTONE, WorldGenSurface.ENDSTONE, WorldGenSurface.ENDSTONE);
    public static final WorldGenSurfaceConfigurationBase CONFIG_CRIMSON_FOREST = new WorldGenSurfaceConfigurationBase(WorldGenSurface.CRIMSON_NYLIUM, WorldGenSurface.NETHERRACK, WorldGenSurface.NETHER_WART_BLOCK);
    public static final WorldGenSurfaceConfigurationBase CONFIG_WARPED_FOREST = new WorldGenSurfaceConfigurationBase(WorldGenSurface.WARPED_NYLIUM, WorldGenSurface.NETHERRACK, WorldGenSurface.WARPED_WART_BLOCK);
    public static final WorldGenSurfaceConfigurationBase CONFIG_BASALT_DELTAS = new WorldGenSurfaceConfigurationBase(WorldGenSurface.BLACKSTONE, WorldGenSurface.BASALT, WorldGenSurface.MAGMA);
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> DEFAULT = a("default", new WorldGenSurfaceDefaultBlock(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> MOUNTAIN = a("mountain", new WorldGenSurfaceExtremeHills(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> SHATTERED_SAVANNA = a("shattered_savanna", new WorldGenSurfaceSavannaMutated(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> GRAVELLY_MOUNTAIN = a("gravelly_mountain", new WorldGenSurfaceExtremeHillMutated(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> GIANT_TREE_TAIGA = a("giant_tree_taiga", new WorldGenSurfaceTaigaMega(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> SWAMP = a("swamp", new WorldGenSurfaceSwamp(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> BADLANDS = a("badlands", new WorldGenSurfaceMesa(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> WOODED_BADLANDS = a("wooded_badlands", new WorldGenSurfaceMesaForest(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> ERODED_BADLANDS = a("eroded_badlands", new WorldGenSurfaceMesaBryce(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> FROZEN_OCEAN = a("frozen_ocean", new WorldGenSurfaceFrozenOcean(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> NETHER = a("nether", new WorldGenSurfaceNether(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> NETHER_FOREST = a("nether_forest", new WorldGenSurfaceNetherForest(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> SOUL_SAND_VALLEY = a("soul_sand_valley", new WorldGenSurfaceSoulSandValley(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> BASALT_DELTAS = a("basalt_deltas", new WorldGenSurfaceBasaltDeltas(WorldGenSurfaceConfigurationBase.CODEC));
    public static final WorldGenSurface<WorldGenSurfaceConfigurationBase> NOPE = a("nope", new WorldGenSurfaceEmpty(WorldGenSurfaceConfigurationBase.CODEC));
    private final Codec<WorldGenSurfaceComposite<C>> configuredCodec;

    private static <C extends WorldGenSurfaceConfiguration, F extends WorldGenSurface<C>> F a(String s, F f0) {
        return (WorldGenSurface) IRegistry.a(IRegistry.SURFACE_BUILDER, s, (Object) f0);
    }

    public WorldGenSurface(Codec<C> codec) {
        this.configuredCodec = codec.fieldOf("config").xmap(this::a, WorldGenSurfaceComposite::a).codec();
    }

    public Codec<WorldGenSurfaceComposite<C>> d() {
        return this.configuredCodec;
    }

    public WorldGenSurfaceComposite<C> a(C c0) {
        return new WorldGenSurfaceComposite<>(this, c0);
    }

    public abstract void a(Random random, IChunkAccess ichunkaccess, BiomeBase biomebase, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, int i1, long j1, C c0);

    public void a(long i) {}
}
