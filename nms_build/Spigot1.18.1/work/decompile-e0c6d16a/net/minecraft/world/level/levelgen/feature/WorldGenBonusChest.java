package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
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
        Random random = featureplacecontext.random();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(featureplacecontext.origin());
        List<Integer> list = (List) IntStream.rangeClosed(chunkcoordintpair.getMinBlockX(), chunkcoordintpair.getMaxBlockX()).boxed().collect(Collectors.toList());

        Collections.shuffle(list, random);
        List<Integer> list1 = (List) IntStream.rangeClosed(chunkcoordintpair.getMinBlockZ(), chunkcoordintpair.getMaxBlockZ()).boxed().collect(Collectors.toList());

        Collections.shuffle(list1, random);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                Integer integer1 = (Integer) iterator1.next();

                blockposition_mutableblockposition.set(integer, 0, integer1);
                BlockPosition blockposition = generatoraccessseed.getHeightmapPos(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition);

                if (generatoraccessseed.isEmptyBlock(blockposition) || generatoraccessseed.getBlockState(blockposition).getCollisionShape(generatoraccessseed, blockposition).isEmpty()) {
                    generatoraccessseed.setBlock(blockposition, Blocks.CHEST.defaultBlockState(), 2);
                    TileEntityLootable.setLootTable(generatoraccessseed, random, blockposition, LootTables.SPAWN_BONUS_CHEST);
                    IBlockData iblockdata = Blocks.TORCH.defaultBlockState();
                    Iterator iterator2 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator2.hasNext()) {
                        EnumDirection enumdirection = (EnumDirection) iterator2.next();
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
