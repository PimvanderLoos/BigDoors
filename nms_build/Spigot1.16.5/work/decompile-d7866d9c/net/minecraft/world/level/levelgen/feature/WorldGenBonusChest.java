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
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureEmptyConfiguration;
import net.minecraft.world.level.storage.loot.LootTables;

public class WorldGenBonusChest extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenBonusChest(Codec<WorldGenFeatureEmptyConfiguration> codec) {
        super(codec);
    }

    public boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
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
                BlockPosition blockposition1 = generatoraccessseed.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition);

                if (generatoraccessseed.isEmpty(blockposition1) || generatoraccessseed.getType(blockposition1).getCollisionShape(generatoraccessseed, blockposition1).isEmpty()) {
                    generatoraccessseed.setTypeAndData(blockposition1, Blocks.CHEST.getBlockData(), 2);
                    TileEntityLootable.a((IBlockAccess) generatoraccessseed, random, blockposition1, LootTables.b);
                    IBlockData iblockdata = Blocks.TORCH.getBlockData();
                    Iterator iterator2 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator2.hasNext()) {
                        EnumDirection enumdirection = (EnumDirection) iterator2.next();
                        BlockPosition blockposition2 = blockposition1.shift(enumdirection);

                        if (iblockdata.canPlace(generatoraccessseed, blockposition2)) {
                            generatoraccessseed.setTypeAndData(blockposition2, iblockdata, 2);
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
