package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenCavesHell extends WorldGenCaves {

    public WorldGenCavesHell(Codec<CaveCarverConfiguration> codec) {
        super(codec);
        this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.BASALT, Blocks.BLACKSTONE});
        this.liquids = ImmutableSet.of(FluidTypes.LAVA, FluidTypes.WATER);
    }

    @Override
    protected int getCaveBound() {
        return 10;
    }

    @Override
    protected float getThickness(Random random) {
        return (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
    }

    @Override
    protected double getYScale() {
        return 5.0D;
    }

    protected boolean carveBlock(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, CarvingMask carvingmask, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition1, Aquifer aquifer, MutableBoolean mutableboolean) {
        if (this.canReplaceBlock(ichunkaccess.getBlockState(blockposition_mutableblockposition))) {
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
