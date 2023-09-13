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
import net.minecraft.world.level.chunk.IChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenCanyonOcean extends WorldGenCanyon {

    public WorldGenCanyonOcean(Codec<CanyonCarverConfiguration> codec) {
        super(codec);
        this.replaceableBlocks = ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DIRT, Blocks.COARSE_DIRT, new Block[]{Blocks.PODZOL, Blocks.GRASS_BLOCK, Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA, Blocks.SANDSTONE, Blocks.RED_SANDSTONE, Blocks.MYCELIUM, Blocks.SNOW, Blocks.SAND, Blocks.GRAVEL, Blocks.WATER, Blocks.LAVA, Blocks.OBSIDIAN, Blocks.AIR, Blocks.CAVE_AIR});
    }

    @Override
    protected boolean a(IChunkAccess ichunkaccess, int i, int j, int k, int l, int i1, int j1) {
        return false;
    }

    protected boolean a(CarvingContext carvingcontext, CanyonCarverConfiguration canyoncarverconfiguration, IChunkAccess ichunkaccess, Function<BlockPosition, BiomeBase> function, BitSet bitset, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition1, Aquifer aquifer, MutableBoolean mutableboolean) {
        return WorldGenCavesOcean.a(this, ichunkaccess, random, blockposition_mutableblockposition, blockposition_mutableblockposition1, aquifer);
    }
}
