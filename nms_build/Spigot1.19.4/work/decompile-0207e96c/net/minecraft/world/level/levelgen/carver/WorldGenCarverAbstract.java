package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.Holder;
import net.minecraft.core.IRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.commons.lang3.mutable.MutableBoolean;

public abstract class WorldGenCarverAbstract<C extends WorldGenCarverConfiguration> {

    public static final WorldGenCarverAbstract<CaveCarverConfiguration> CAVE = register("cave", new WorldGenCaves(CaveCarverConfiguration.CODEC));
    public static final WorldGenCarverAbstract<CaveCarverConfiguration> NETHER_CAVE = register("nether_cave", new WorldGenCavesHell(CaveCarverConfiguration.CODEC));
    public static final WorldGenCarverAbstract<CanyonCarverConfiguration> CANYON = register("canyon", new WorldGenCanyon(CanyonCarverConfiguration.CODEC));
    protected static final IBlockData AIR = Blocks.AIR.defaultBlockState();
    protected static final IBlockData CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
    protected static final Fluid WATER = FluidTypes.WATER.defaultFluidState();
    protected static final Fluid LAVA = FluidTypes.LAVA.defaultFluidState();
    protected Set<FluidType> liquids;
    private final Codec<WorldGenCarverWrapper<C>> configuredCodec;

    private static <C extends WorldGenCarverConfiguration, F extends WorldGenCarverAbstract<C>> F register(String s, F f0) {
        return (WorldGenCarverAbstract) IRegistry.register(BuiltInRegistries.CARVER, s, f0);
    }

    public WorldGenCarverAbstract(Codec<C> codec) {
        this.liquids = ImmutableSet.of(FluidTypes.WATER);
        this.configuredCodec = codec.fieldOf("config").xmap(this::configured, WorldGenCarverWrapper::config).codec();
    }

    public WorldGenCarverWrapper<C> configured(C c0) {
        return new WorldGenCarverWrapper<>(this, c0);
    }

    public Codec<WorldGenCarverWrapper<C>> configuredCodec() {
        return this.configuredCodec;
    }

    public int getRange() {
        return 4;
    }

    protected boolean carveEllipsoid(CarvingContext carvingcontext, C c0, IChunkAccess ichunkaccess, Function<BlockPosition, Holder<BiomeBase>> function, Aquifer aquifer, double d0, double d1, double d2, double d3, double d4, CarvingMask carvingmask, WorldGenCarverAbstract.a worldgencarverabstract_a) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        double d5 = (double) chunkcoordintpair.getMiddleBlockX();
        double d6 = (double) chunkcoordintpair.getMiddleBlockZ();
        double d7 = 16.0D + d3 * 2.0D;

        if (Math.abs(d0 - d5) <= d7 && Math.abs(d2 - d6) <= d7) {
            int i = chunkcoordintpair.getMinBlockX();
            int j = chunkcoordintpair.getMinBlockZ();
            int k = Math.max(MathHelper.floor(d0 - d3) - i - 1, 0);
            int l = Math.min(MathHelper.floor(d0 + d3) - i, 15);
            int i1 = Math.max(MathHelper.floor(d1 - d4) - 1, carvingcontext.getMinGenY() + 1);
            int j1 = ichunkaccess.isUpgrading() ? 0 : 7;
            int k1 = Math.min(MathHelper.floor(d1 + d4) + 1, carvingcontext.getMinGenY() + carvingcontext.getGenDepth() - 1 - j1);
            int l1 = Math.max(MathHelper.floor(d2 - d3) - j - 1, 0);
            int i2 = Math.min(MathHelper.floor(d2 + d3) - j, 15);
            boolean flag = false;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = new BlockPosition.MutableBlockPosition();

            for (int j2 = k; j2 <= l; ++j2) {
                int k2 = chunkcoordintpair.getBlockX(j2);
                double d8 = ((double) k2 + 0.5D - d0) / d3;

                for (int l2 = l1; l2 <= i2; ++l2) {
                    int i3 = chunkcoordintpair.getBlockZ(l2);
                    double d9 = ((double) i3 + 0.5D - d2) / d3;

                    if (d8 * d8 + d9 * d9 < 1.0D) {
                        MutableBoolean mutableboolean = new MutableBoolean(false);

                        for (int j3 = k1; j3 > i1; --j3) {
                            double d10 = ((double) j3 - 0.5D - d1) / d4;

                            if (!worldgencarverabstract_a.shouldSkip(carvingcontext, d8, d10, d9, j3) && (!carvingmask.get(j2, j3, l2) || isDebugEnabled(c0))) {
                                carvingmask.set(j2, j3, l2);
                                blockposition_mutableblockposition.set(k2, j3, i3);
                                flag |= this.carveBlock(carvingcontext, c0, ichunkaccess, function, carvingmask, blockposition_mutableblockposition, blockposition_mutableblockposition1, aquifer, mutableboolean);
                            }
                        }
                    }
                }
            }

            return flag;
        } else {
            return false;
        }
    }

    protected boolean carveBlock(CarvingContext carvingcontext, C c0, IChunkAccess ichunkaccess, Function<BlockPosition, Holder<BiomeBase>> function, CarvingMask carvingmask, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition1, Aquifer aquifer, MutableBoolean mutableboolean) {
        IBlockData iblockdata = ichunkaccess.getBlockState(blockposition_mutableblockposition);

        if (iblockdata.is(Blocks.GRASS_BLOCK) || iblockdata.is(Blocks.MYCELIUM)) {
            mutableboolean.setTrue();
        }

        if (!this.canReplaceBlock(c0, iblockdata) && !isDebugEnabled(c0)) {
            return false;
        } else {
            IBlockData iblockdata1 = this.getCarveState(carvingcontext, c0, blockposition_mutableblockposition, aquifer);

            if (iblockdata1 == null) {
                return false;
            } else {
                ichunkaccess.setBlockState(blockposition_mutableblockposition, iblockdata1, false);
                if (aquifer.shouldScheduleFluidUpdate() && !iblockdata1.getFluidState().isEmpty()) {
                    ichunkaccess.markPosForPostprocessing(blockposition_mutableblockposition);
                }

                if (mutableboolean.isTrue()) {
                    blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, EnumDirection.DOWN);
                    if (ichunkaccess.getBlockState(blockposition_mutableblockposition1).is(Blocks.DIRT)) {
                        carvingcontext.topMaterial(function, ichunkaccess, blockposition_mutableblockposition1, !iblockdata1.getFluidState().isEmpty()).ifPresent((iblockdata2) -> {
                            ichunkaccess.setBlockState(blockposition_mutableblockposition1, iblockdata2, false);
                            if (!iblockdata2.getFluidState().isEmpty()) {
                                ichunkaccess.markPosForPostprocessing(blockposition_mutableblockposition1);
                            }

                        });
                    }
                }

                return true;
            }
        }
    }

    @Nullable
    private IBlockData getCarveState(CarvingContext carvingcontext, C c0, BlockPosition blockposition, Aquifer aquifer) {
        if (blockposition.getY() <= c0.lavaLevel.resolveY(carvingcontext)) {
            return WorldGenCarverAbstract.LAVA.createLegacyBlock();
        } else {
            IBlockData iblockdata = aquifer.computeSubstance(new DensityFunction.e(blockposition.getX(), blockposition.getY(), blockposition.getZ()), 0.0D);

            return iblockdata == null ? (isDebugEnabled(c0) ? c0.debugSettings.getBarrierState() : null) : (isDebugEnabled(c0) ? getDebugState(c0, iblockdata) : iblockdata);
        }
    }

    private static IBlockData getDebugState(WorldGenCarverConfiguration worldgencarverconfiguration, IBlockData iblockdata) {
        if (iblockdata.is(Blocks.AIR)) {
            return worldgencarverconfiguration.debugSettings.getAirState();
        } else if (iblockdata.is(Blocks.WATER)) {
            IBlockData iblockdata1 = worldgencarverconfiguration.debugSettings.getWaterState();

            return iblockdata1.hasProperty(BlockProperties.WATERLOGGED) ? (IBlockData) iblockdata1.setValue(BlockProperties.WATERLOGGED, true) : iblockdata1;
        } else {
            return iblockdata.is(Blocks.LAVA) ? worldgencarverconfiguration.debugSettings.getLavaState() : iblockdata;
        }
    }

    public abstract boolean carve(CarvingContext carvingcontext, C c0, IChunkAccess ichunkaccess, Function<BlockPosition, Holder<BiomeBase>> function, RandomSource randomsource, Aquifer aquifer, ChunkCoordIntPair chunkcoordintpair, CarvingMask carvingmask);

    public abstract boolean isStartChunk(C c0, RandomSource randomsource);

    protected boolean canReplaceBlock(C c0, IBlockData iblockdata) {
        return iblockdata.is(c0.replaceable);
    }

    protected static boolean canReach(ChunkCoordIntPair chunkcoordintpair, double d0, double d1, int i, int j, float f) {
        double d2 = (double) chunkcoordintpair.getMiddleBlockX();
        double d3 = (double) chunkcoordintpair.getMiddleBlockZ();
        double d4 = d0 - d2;
        double d5 = d1 - d3;
        double d6 = (double) (j - i);
        double d7 = (double) (f + 2.0F + 16.0F);

        return d4 * d4 + d5 * d5 - d6 * d6 <= d7 * d7;
    }

    private static boolean isDebugEnabled(WorldGenCarverConfiguration worldgencarverconfiguration) {
        return worldgencarverconfiguration.debugSettings.isDebugMode();
    }

    public interface a {

        boolean shouldSkip(CarvingContext carvingcontext, double d0, double d1, double d2, int i);
    }
}
