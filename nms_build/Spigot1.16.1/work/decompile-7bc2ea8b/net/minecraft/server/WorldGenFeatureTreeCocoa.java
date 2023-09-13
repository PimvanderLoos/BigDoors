package net.minecraft.server;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WorldGenFeatureTreeCocoa extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeCocoa> a = Codec.FLOAT.fieldOf("probability").xmap(WorldGenFeatureTreeCocoa::new, (worldgenfeaturetreecocoa) -> {
        return worldgenfeaturetreecocoa.b;
    }).codec();
    private final float b;

    public WorldGenFeatureTreeCocoa(float f) {
        this.b = f;
    }

    @Override
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.c;
    }

    @Override
    public void a(GeneratorAccess generatoraccess, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
        if (random.nextFloat() < this.b) {
            int i = ((BlockPosition) list.get(0)).getY();

            list.stream().filter((blockposition) -> {
                return blockposition.getY() - i <= 2;
            }).forEach((blockposition) -> {
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    if (random.nextFloat() <= 0.25F) {
                        EnumDirection enumdirection1 = enumdirection.opposite();
                        BlockPosition blockposition1 = blockposition.b(enumdirection1.getAdjacentX(), 0, enumdirection1.getAdjacentZ());

                        if (WorldGenerator.b(generatoraccess, blockposition1)) {
                            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.COCOA.getBlockData().set(BlockCocoa.AGE, random.nextInt(3))).set(BlockCocoa.FACING, enumdirection);

                            this.a((IWorldWriter) generatoraccess, blockposition1, iblockdata, set, structureboundingbox);
                        }
                    }
                }

            });
        }
    }
}
