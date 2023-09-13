package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.BlockCocoa;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.levelgen.structure.StructureBoundingBox;

public class WorldGenFeatureTreeCocoa extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeCocoa> a = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(WorldGenFeatureTreeCocoa::new, (worldgenfeaturetreecocoa) -> {
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
    public void a(GeneratorAccessSeed generatoraccessseed, Random random, List<BlockPosition> list, List<BlockPosition> list1, Set<BlockPosition> set, StructureBoundingBox structureboundingbox) {
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

                        if (WorldGenerator.b(generatoraccessseed, blockposition1)) {
                            IBlockData iblockdata = (IBlockData) ((IBlockData) Blocks.COCOA.getBlockData().set(BlockCocoa.AGE, random.nextInt(3))).set(BlockCocoa.FACING, enumdirection);

                            this.a((IWorldWriter) generatoraccessseed, blockposition1, iblockdata, set, structureboundingbox);
                        }
                    }
                }

            });
        }
    }
}
