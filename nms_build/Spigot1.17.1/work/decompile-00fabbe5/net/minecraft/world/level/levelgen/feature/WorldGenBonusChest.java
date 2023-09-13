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
import net.minecraft.world.level.IBlockAccess;
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
    public boolean generate(FeaturePlaceContext<WorldGenFeatureEmptyConfiguration> featureplacecontext) {
        Random random = featureplacecontext.c();
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(featureplacecontext.d());
        List<Integer> list = (List) IntStream.rangeClosed(chunkcoordintpair.d(), chunkcoordintpair.f()).boxed().collect(Collectors.toList());

        Collections.shuffle(list, random);
        List<Integer> list1 = (List) IntStream.rangeClosed(chunkcoordintpair.e(), chunkcoordintpair.g()).boxed().collect(Collectors.toList());

        Collections.shuffle(list1, random);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Integer integer = (Integer) iterator.next();
            Iterator iterator1 = list1.iterator();

            while (iterator1.hasNext()) {
                Integer integer1 = (Integer) iterator1.next();

                blockposition_mutableblockposition.d(integer, 0, integer1);
                BlockPosition blockposition = generatoraccessseed.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition);

                if (generatoraccessseed.isEmpty(blockposition) || generatoraccessseed.getType(blockposition).getCollisionShape(generatoraccessseed, blockposition).isEmpty()) {
                    generatoraccessseed.setTypeAndData(blockposition, Blocks.CHEST.getBlockData(), 2);
                    TileEntityLootable.a((IBlockAccess) generatoraccessseed, random, blockposition, LootTables.SPAWN_BONUS_CHEST);
                    IBlockData iblockdata = Blocks.TORCH.getBlockData();
                    Iterator iterator2 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator2.hasNext()) {
                        EnumDirection enumdirection = (EnumDirection) iterator2.next();
                        BlockPosition blockposition1 = blockposition.shift(enumdirection);

                        if (iblockdata.canPlace(generatoraccessseed, blockposition1)) {
                            generatoraccessseed.setTypeAndData(blockposition1, iblockdata, 2);
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
