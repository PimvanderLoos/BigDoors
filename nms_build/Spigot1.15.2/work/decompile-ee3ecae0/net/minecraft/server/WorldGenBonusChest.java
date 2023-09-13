package net.minecraft.server;

import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WorldGenBonusChest extends WorldGenerator<WorldGenFeatureEmptyConfiguration> {

    public WorldGenBonusChest(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function) {
        super(function);
    }

    public boolean a(GeneratorAccess generatoraccess, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, Random random, BlockPosition blockposition, WorldGenFeatureEmptyConfiguration worldgenfeatureemptyconfiguration) {
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
                BlockPosition blockposition1 = generatoraccess.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition);

                if (generatoraccess.isEmpty(blockposition1) || generatoraccess.getType(blockposition1).getCollisionShape(generatoraccess, blockposition1).isEmpty()) {
                    generatoraccess.setTypeAndData(blockposition1, Blocks.CHEST.getBlockData(), 2);
                    TileEntityLootable.a(generatoraccess, random, blockposition1, LootTables.b);
                    IBlockData iblockdata = Blocks.TORCH.getBlockData();
                    Iterator iterator2 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator2.hasNext()) {
                        EnumDirection enumdirection = (EnumDirection) iterator2.next();
                        BlockPosition blockposition2 = blockposition1.shift(enumdirection);

                        if (iblockdata.canPlace(generatoraccess, blockposition2)) {
                            generatoraccess.setTypeAndData(blockposition2, iblockdata, 2);
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }
}
