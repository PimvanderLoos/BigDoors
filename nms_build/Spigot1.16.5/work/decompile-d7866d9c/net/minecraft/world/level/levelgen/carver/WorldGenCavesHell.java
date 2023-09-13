package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureConfigurationChance;
import net.minecraft.world.level.material.FluidTypes;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenCavesHell extends WorldGenCaves {

    public WorldGenCavesHell(Codec<WorldGenFeatureConfigurationChance> codec) {
        super(codec, 128);
        this.j = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.SOUL_SOIL, Blocks.CRIMSON_NYLIUM, Blocks.WARPED_NYLIUM, Blocks.NETHER_WART_BLOCK, Blocks.WARPED_WART_BLOCK, Blocks.BASALT, Blocks.BLACKSTONE});
        this.k = ImmutableSet.of(FluidTypes.LAVA, FluidTypes.WATER);
    }

    @Override
    protected int a() {
        return 10;
    }

    @Override
    protected float a(Random random) {
        return (random.nextFloat() * 2.0F + random.nextFloat()) * 2.0F;
    }

    @Override
    protected double b() {
        return 5.0D;
    }

    @Override
    protected int b(Random random) {
        return random.nextInt(this.l);
    }

    @Override
    protected boolean a(IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, BitSet bitset, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition1, BlockPosition.MutableBlockPosition blockposition_mutableblockposition2, int i, int j, int k, int l, int i1, int j1, int k1, int l1, MutableBoolean mutableboolean) {
        int i2 = j1 | l1 << 4 | k1 << 8;

        if (bitset.get(i2)) {
            return false;
        } else {
            bitset.set(i2);
            blockposition_mutableblockposition.d(l, k1, i1);
            if (this.a(ichunkaccess.getType(blockposition_mutableblockposition))) {
                IBlockData iblockdata;

                if (k1 <= 31) {
                    iblockdata = WorldGenCavesHell.i.getBlockData();
                } else {
                    iblockdata = WorldGenCavesHell.g;
                }

                ichunkaccess.setType(blockposition_mutableblockposition, iblockdata, false);
                return true;
            } else {
                return false;
            }
        }
    }
}
