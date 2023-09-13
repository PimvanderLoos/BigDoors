package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.BlockCocoa;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;

public class WorldGenFeatureTreeCocoa extends WorldGenFeatureTree {

    public static final Codec<WorldGenFeatureTreeCocoa> CODEC = Codec.floatRange(0.0F, 1.0F).fieldOf("probability").xmap(WorldGenFeatureTreeCocoa::new, (worldgenfeaturetreecocoa) -> {
        return worldgenfeaturetreecocoa.probability;
    }).codec();
    private final float probability;

    public WorldGenFeatureTreeCocoa(float f) {
        this.probability = f;
    }

    @Override
    protected WorldGenFeatureTrees<?> a() {
        return WorldGenFeatureTrees.COCOA;
    }

    @Override
    public void a(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, Random random, List<BlockPosition> list, List<BlockPosition> list1) {
        if (random.nextFloat() < this.probability) {
            int i = ((BlockPosition) list.get(0)).getY();

            list.stream().filter((blockposition) -> {
                return blockposition.getY() - i <= 2;
            }).forEach((blockposition) -> {
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    if (random.nextFloat() <= 0.25F) {
                        EnumDirection enumdirection1 = enumdirection.opposite();
                        BlockPosition blockposition1 = blockposition.c(enumdirection1.getAdjacentX(), 0, enumdirection1.getAdjacentZ());

                        if (WorldGenerator.b(virtuallevelreadable, blockposition1)) {
                            biconsumer.accept(blockposition1, (IBlockData) ((IBlockData) Blocks.COCOA.getBlockData().set(BlockCocoa.AGE, random.nextInt(3))).set(BlockCocoa.FACING, enumdirection));
                        }
                    }
                }

            });
        }
    }
}
