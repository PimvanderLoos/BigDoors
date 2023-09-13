package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenCavesHell extends WorldGenCaves {

    public WorldGenCavesHell(Codec<CaveCarverConfiguration> codec) {
        super(codec);
        this.liquids = ImmutableSet.of(FluidTypes.LAVA, FluidTypes.WATER);
    }

    @Override
    protected int getCaveBound() {
        return 10;
    }

    @Override
    protected float getThickness(RandomSource randomsource) {
        return (randomsource.nextFloat() * 2.0F + randomsource.nextFloat()) * 2.0F;
    }

    @Override
    protected double getYScale() {
        return 5.0D;
    }

    protected boolean carveBlock(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, Holder<BiomeBase>> function, CarvingMask carvingmask, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition1, Aquifer aquifer, MutableBoolean mutableboolean) {
        if (this.canReplaceBlock(cavecarverconfiguration, ichunkaccess.getBlockState(blockposition_mutableblockposition))) {
            IBlockData iblockdata;

            if (blockposition_mutableblockposition.getY() <= carvingcontext.getMinGenY() + 31) {
                iblockdata = WorldGenCavesHell.LAVA.createLegacyBlock();
            } else {
                iblockdata = WorldGenCavesHell.CAVE_AIR;
            }

            ichunkaccess.setBlockState(blockposition_mutableblockposition, iblockdata, false);
            return true;
        } else {
            return false;
        }
    }
}
