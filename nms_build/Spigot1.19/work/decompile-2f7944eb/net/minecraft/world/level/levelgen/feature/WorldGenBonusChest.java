package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import java.util.Iterator;
import java.util.stream.IntStream;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntityLootable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenBonusChest extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenBonusChest(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        RandomSource randomsource = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(featureplacecontext.origin());
        IntArrayList intarraylist = SystemUtils.toShuffledList(IntStream.rangeClosed(chunkcoordintpair.getMinBlockX(), chunkcoordintpair.getMaxBlockX()), randomsource);
        IntArrayList intarraylist1 = SystemUtils.toShuffledList(IntStream.rangeClosed(chunkcoordintpair.getMinBlockZ(), chunkcoordintpair.getMaxBlockZ()), randomsource);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        IntListIterator intlistiterator = intarraylist.iterator();

        while (intlistiterator.hasNext()) {
            Integer integer = (Integer) intlistiterator.next();
            IntListIterator intlistiterator1 = intarraylist1.iterator();

            while (intlistiterator1.hasNext()) {
                Integer integer1 = (Integer) intlistiterator1.next();

                blockposition_mutableblockposition.set(integer, 0, integer1);
                BlockPosition blockposition = generatoraccessseed.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition);

                if (generatoraccessseed.isEmptyBlock(blockposition) || generatoraccessseed.getBlockState(blockposition).getCollisionShape(generatoraccessseed, blockposition).isEmpty()) {
                    generatoraccessseed.setBlock(blockposition, Blocks.CHEST.defaultBlockState(), 2);
                    TileEntityLootable.setLootTable(generatoraccessseed, randomsource, blockposition, LootTables.SPAWN_BONUS_CHEST);
                    IBlockData iblockdata = Blocks.TORCH.defaultBlockState();
                    Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator.hasNext()) {
                        EnumDirection enumdirection = (EnumDirection) iterator.next();
                        BlockPosition blockposition1 = blockposition.relative(enumdirection);

                        if (iblockdata.canSurvive(generatoraccessseed, blockposition1)) {
                            generatoraccessseed.setBlock(blockposition1, iblockdata, 2);
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
