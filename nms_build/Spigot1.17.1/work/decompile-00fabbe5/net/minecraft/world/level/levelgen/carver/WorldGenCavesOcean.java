package net.minecraft.world.level.levelgen.carver;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.SectionPosition;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenCavesOcean extends WorldGenCaves {

    public WorldGenCavesOcean(Codec<CaveCarverConfiguration> codec) {
        super(codec);
        this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.PACKED_ICE});
    }

    @Override
    protected boolean a(IChunkAccess ichunkaccess, int i, int j, int k, int l, int i1, int j1) {
        return false;
    }

    protected boolean a(CarvingContext carvingcontext, CaveCarverConfiguration cavecarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, BitSet bitset, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition1, Aquifer aquifer, MutableBoolean mutableboolean) {
        return a(this, ichunkaccess, random, blockposition_mutableblockposition, blockposition_mutableblockposition1, aquifer);
    }

    protected static boolean a(WorldGenCarverAbstract<?> worldgencarverabstract, IChunkAccess ichunkaccess, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition1, Aquifer aquifer) {
        if (aquifer.a(WorldGenCarverAbstract.STONE_SOURCE, blockposition_mutableblockposition.getX(), blockposition_mutableblockposition.getY(), blockposition_mutableblockposition.getZ(), Double.NEGATIVE_INFINITY).isAir()) {
            return false;
        } else {
            IBlockData iblockdata = ichunkaccess.getType(blockposition_mutableblockposition);

            if (!worldgencarverabstract.a(iblockdata)) {
                return false;
            } else if (blockposition_mutableblockposition.getY() == 10) {
                float f = random.nextFloat();

                if ((double) f < 0.25D) {
                    ichunkaccess.setType(blockposition_mutableblockposition, Blocks.MAGMA_BLOCK.getBlockData(), false);
                    ichunkaccess.o().a(blockposition_mutableblockposition, Blocks.MAGMA_BLOCK, 0);
                } else {
                    ichunkaccess.setType(blockposition_mutableblockposition, Blocks.OBSIDIAN.getBlockData(), false);
                }

                return true;
            } else if (blockposition_mutableblockposition.getY() < 10) {
                ichunkaccess.setType(blockposition_mutableblockposition, Blocks.LAVA.getBlockData(), false);
                return false;
            } else {
                ichunkaccess.setType(blockposition_mutableblockposition, WorldGenCavesOcean.WATER.getBlockData(), false);
                int i = ichunkaccess.getPos().x;
                int j = ichunkaccess.getPos().z;
                UnmodifiableIterator unmodifiableiterator = BlockFluids.POSSIBLE_FLOW_DIRECTIONS.iterator();

                while (unmodifiableiterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) unmodifiableiterator.next();

                    blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, enumdirection);
                    if (SectionPosition.a(blockposition_mutableblockposition1.getX()) != i || SectionPosition.a(blockposition_mutableblockposition1.getZ()) != j || ichunkaccess.getType(blockposition_mutableblockposition1).isAir()) {
                        ichunkaccess.p().a(blockposition_mutableblockposition, WorldGenCavesOcean.WATER.getType(), 0);
                        break;
                    }
                }

                return true;
            }
        }
    }
}
