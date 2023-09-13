package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.IRegistry;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.BaseStoneSource;
import net.minecraft.world.level.levelgen.SingleBaseStoneSource;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldGenCarverAbstract<C extends WorldGenCarverConfiguration> {

    public static final WorldGenCarverAbstract<CaveCarverConfiguration> CAVE = a("cave", (WorldGenCarverAbstract) (new WorldGenCaves(CaveCarverConfiguration.CODEC)));
    public static final WorldGenCarverAbstract<CaveCarverConfiguration> NETHER_CAVE = a("nether_cave", (WorldGenCarverAbstract) (new WorldGenCavesHell(CaveCarverConfiguration.CODEC)));
    public static final WorldGenCarverAbstract<CanyonCarverConfiguration> CANYON = a("canyon", (WorldGenCarverAbstract) (new WorldGenCanyon(CanyonCarverConfiguration.CODEC)));
    public static final WorldGenCarverAbstract<CanyonCarverConfiguration> UNDERWATER_CANYON = a("underwater_canyon", (WorldGenCarverAbstract) (new WorldGenCanyonOcean(CanyonCarverConfiguration.CODEC)));
    public static final WorldGenCarverAbstract<CaveCarverConfiguration> UNDERWATER_CAVE = a("underwater_cave", (WorldGenCarverAbstract) (new WorldGenCavesOcean(CaveCarverConfiguration.CODEC)));
    protected static final BaseStoneSource STONE_SOURCE = new SingleBaseStoneSource(Blocks.STONE.getBlockData());
    protected static final IBlockData AIR = Blocks.AIR.getBlockData();
    protected static final IBlockData CAVE_AIR = Blocks.CAVE_AIR.getBlockData();
    protected static final Fluid WATER = FluidTypes.WATER.h();
    protected static final Fluid LAVA = FluidTypes.LAVA.h();
    protected Set<Block> replaceableBlocks;
    protected Set<FluidType> liquids;
    private final Codec<WorldGenCarverWrapper<C>> configuredCodec;

    private static <C extends WorldGenCarverConfiguration, F extends WorldGenCarverAbstract<C>> F a(String s, F f0) {
        return (WorldGenCarverAbstract) IRegistry.a(IRegistry.CARVER, s, (Object) f0);
    }

    public WorldGenCarverAbstract(Codec<C> codec) {
        this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.PACKED_ICE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.GRANITE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.RAW_IRON_BLOCK, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.RAW_COPPER_BLOCK});
        this.liquids = ImmutableSet.of(FluidTypes.WATER);
        this.configuredCodec = codec.fieldOf("config").xmap(this::a, WorldGenCarverWrapper::a).codec();
    }

    public WorldGenCarverWrapper<C> a(C c0) {
        return new WorldGenCarverWrapper<>(this, c0);
    }

    public Codec<WorldGenCarverWrapper<C>> c() {
        return this.configuredCodec;
    }

    public int d() {
        return 4;
    }

    protected boolean a(CarvingContext carvingcontext, C c0, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, long i, Aquifer aquifer, double d0, double d1, double d2, double d3, double d4, BitSet bitset, WorldGenCarverAbstract.a worldgencarverabstract_a) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int j = chunkcoordintpair.x;
        int k = chunkcoordintpair.z;
        Random random = new Random(i + (long) j + (long) k);
        double d5 = (double) chunkcoordintpair.b();
        double d6 = (double) chunkcoordintpair.c();
        double d7 = 16.0D + d3 * 2.0D;

        if (Math.abs(d0 - d5) <= d7 && Math.abs(d2 - d6) <= d7) {
            int l = chunkcoordintpair.d();
            int i1 = chunkcoordintpair.e();
            int j1 = Math.max(MathHelper.floor(d0 - d3) - l - 1, 0);
            int k1 = Math.min(MathHelper.floor(d0 + d3) - l, 15);
            int l1 = Math.max(MathHelper.floor(d1 - d4) - 1, carvingcontext.a() + 1);
            int i2 = Math.min(MathHelper.floor(d1 + d4) + 1, carvingcontext.a() + carvingcontext.b() - 8);
            int j2 = Math.max(MathHelper.floor(d2 - d3) - i1 - 1, 0);
            int k2 = Math.min(MathHelper.floor(d2 + d3) - i1, 15);

            if (!c0.aquifersEnabled && this.a(ichunkaccess, j1, k1, l1, i2, j2, k2)) {
                return false;
            } else {
                boolean flag = false;
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

                for (int l2 = j1; l2 <= k1; ++l2) {
                    int i3 = chunkcoordintpair.a(l2);
                    double d8 = ((double) i3 + 0.5D - d0) / d3;

                    for (int j3 = j2; j3 <= k2; ++j3) {
                        int k3 = chunkcoordintpair.b(j3);
                        double d9 = ((double) k3 + 0.5D - d2) / d3;

                        if (d8 * d8 + d9 * d9 < 1.0D) {
                            MutableBoolean mutableboolean = new MutableBoolean(false);

                            for (int l3 = i2; l3 > l1; --l3) {
                                double d10 = ((double) l3 - 0.5D - d1) / d4;

                                if (!worldgencarverabstract_a.shouldSkip(carvingcontext, d8, d10, d9, l3)) {
                                    int i4 = l3 - carvingcontext.a();
                                    int j4 = l2 | j3 << 4 | i4 << 8;

                                    if (!bitset.get(j4) || b(c0)) {
                                        bitset.set(j4);
                                        blockposition_mutableblockposition.d(i3, l3, k3);
                                        flag |= this.a(carvingcontext, c0, ichunkaccess, function, bitset, random, blockposition_mutableblockposition, blockposition_mutableblockposition1, aquifer, mutableboolean);
                                    }
                                }
                            }
                        }
                    }
                }

                return flag;
            }
        } else {
            return false;
        }
    }

    protected boolean a(CarvingContext carvingcontext, C c0, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, BitSet bitset, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition1, Aquifer aquifer, MutableBoolean mutableboolean) {
        IBlockData iblockdata = ichunkaccess.getType(blockposition_mutableblockposition);
        IBlockData iblockdata1 = ichunkaccess.getType(blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.UP));

        if (iblockdata.a(Blocks.GRASS_BLOCK) || iblockdata.a(Blocks.MYCELIUM)) {
            mutableboolean.setTrue();
        }

        if (!this.a(iblockdata, iblockdata1) && !b(c0)) {
            return false;
        } else {
            IBlockData iblockdata2 = this.a(carvingcontext, c0, blockposition_mutableblockposition, aquifer);

            if (iblockdata2 == null) {
                return false;
            } else {
                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata2, false);
                if (mutableboolean.isTrue()) {
                    blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, EnumDirection.DOWN);
                    if (ichunkaccess.getType(blockposition_mutableblockposition1).a(Blocks.DIRT)) {
                        ichunkaccess.setType(blockposition_mutableblockposition1, ((BiomeBase) function.apply(blockposition_mutableblockposition)).e().e().a(), false);
                    }
                }

                return true;
            }
        }
    }

    @Nullable
    private IBlockData a(CarvingContext carvingcontext, C c0, BlockPosition blockposition, Aquifer aquifer) {
        if (blockposition.getY() <= c0.lavaLevel.a((WorldGenerationContext) carvingcontext)) {
            return WorldGenCarverAbstract.LAVA.getBlockData();
        } else if (!c0.aquifersEnabled) {
            return b(c0) ? a(c0, WorldGenCarverAbstract.AIR) : WorldGenCarverAbstract.AIR;
        } else {
            IBlockData iblockdata = aquifer.a(WorldGenCarverAbstract.STONE_SOURCE, blockposition.getX(), blockposition.getY(), blockposition.getZ(), 0.0D);

            return iblockdata == Blocks.STONE.getBlockData() ? (b(c0) ? c0.debugSettings.e() : null) : (b(c0) ? a(c0, iblockdata) : iblockdata);
        }
    }

    private static IBlockData a(WorldGenCarverConfiguration worldgencarverconfiguration, IBlockData iblockdata) {
        if (iblockdata.a(Blocks.AIR)) {
            return worldgencarverconfiguration.debugSettings.b();
        } else if (iblockdata.a(Blocks.WATER)) {
            IBlockData iblockdata1 = worldgencarverconfiguration.debugSettings.c();

            return iblockdata1.b(BlockProperties.WATERLOGGED) ? (IBlockData) iblockdata1.set(BlockProperties.WATERLOGGED, true) : iblockdata1;
        } else {
            return iblockdata.a(Blocks.LAVA) ? worldgencarverconfiguration.debugSettings.d() : iblockdata;
        }
    }

    public abstract boolean a(CarvingContext carvingcontext, C c0, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, Random random, Aquifer aquifer, ChunkCoordIntPair chunkcoordintpair, BitSet bitset);

    public abstract boolean a(C c0, Random random);

    protected boolean a(IBlockData iblockdata) {
        return this.replaceableBlocks.contains(iblockdata.getBlock());
    }

    protected boolean a(IBlockData iblockdata, IBlockData iblockdata1) {
        return this.a(iblockdata) || (iblockdata.a(Blocks.SAND) || iblockdata.a(Blocks.GRAVEL)) && !iblockdata1.getFluid().a((Tag) TagsFluid.WATER);
    }

    protected boolean a(IChunkAccess ichunkaccess, int i, int j, int k, int l, int i1, int j1) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int k1 = chunkcoordintpair.d();
        int l1 = chunkcoordintpair.e();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int i2 = i; i2 <= j; ++i2) {
            for (int j2 = i1; j2 <= j1; ++j2) {
                for (int k2 = k - 1; k2 <= l + 1; ++k2) {
                    blockposition_mutableblockposition.d(k1 + i2, k2, l1 + j2);
                    if (this.liquids.contains(ichunkaccess.getFluid(blockposition_mutableblockposition).getType())) {
                        return true;
                    }

                    if (k2 != l + 1 && !a(i2, j2, i, j, i1, j1)) {
                        k2 = l;
                    }
                }
            }
        }

        return false;
    }

    private static boolean a(int i, int j, int k, int l, int i1, int j1) {
        return i == k || i == l || j == i1 || j == j1;
    }

    protected static boolean a(ChunkCoordIntPair chunkcoordintpair, double d0, double d1, int i, int j, float f) {
        double d2 = (double) chunkcoordintpair.b();
        double d3 = (double) chunkcoordintpair.c();
        double d4 = d0 - d2;
        double d5 = d1 - d3;
        double d6 = (double) (j - i);
        double d7 = (double) (f + 2.0F + 16.0F);

        return d4 * d4 + d5 * d5 - d6 * d6 <= d7 * d7;
    }

    private static boolean b(WorldGenCarverConfiguration worldgencarverconfiguration) {
        return worldgencarverconfiguration.debugSettings.a();
    }

    public interface a {

        boolean shouldSkip(CarvingContext carvingcontext, double d0, double d1, double d2, int i);
    }
}
