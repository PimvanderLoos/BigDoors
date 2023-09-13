package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class AttachedToLeavesDecorator extends WorldGenFeatureTree {

    public static final Codec<AttachedToLeavesDecorator> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((attachedtoleavesdecorator) -> {
            return attachedtoleavesdecorator.probability;
        }), Codec.intRange(0, 16).fieldOf("exclusion_radius_xz").forGetter((attachedtoleavesdecorator) -> {
            return attachedtoleavesdecorator.exclusionRadiusXZ;
        }), Codec.intRange(0, 16).fieldOf("exclusion_radius_y").forGetter((attachedtoleavesdecorator) -> {
            return attachedtoleavesdecorator.exclusionRadiusY;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("block_provider").forGetter((attachedtoleavesdecorator) -> {
            return attachedtoleavesdecorator.blockProvider;
        }), Codec.intRange(1, 16).fieldOf("required_empty_blocks").forGetter((attachedtoleavesdecorator) -> {
            return attachedtoleavesdecorator.requiredEmptyBlocks;
        }), ExtraCodecs.nonEmptyList(EnumDirection.CODEC.listOf()).fieldOf("directions").forGetter((attachedtoleavesdecorator) -> {
            return attachedtoleavesdecorator.directions;
        })).apply(instance, AttachedToLeavesDecorator::new);
    });
    protected final float probability;
    protected final int exclusionRadiusXZ;
    protected final int exclusionRadiusY;
    protected final WorldGenFeatureStateProvider blockProvider;
    protected final int requiredEmptyBlocks;
    protected final List<EnumDirection> directions;

    public AttachedToLeavesDecorator(float f, int i, int j, WorldGenFeatureStateProvider worldgenfeaturestateprovider, int k, List<EnumDirection> list) {
        this.probability = f;
        this.exclusionRadiusXZ = i;
        this.exclusionRadiusY = j;
        this.blockProvider = worldgenfeaturestateprovider;
        this.requiredEmptyBlocks = k;
        this.directions = list;
    }

    @Override
    public void place(WorldGenFeatureTree.a worldgenfeaturetree_a) {
        Set<BlockPosition> set = new HashSet();
        RandomSource randomsource = worldgenfeaturetree_a.random();
        Iterator iterator = SystemUtils.shuffledCopy(worldgenfeaturetree_a.leaves(), randomsource).iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();
            EnumDirection enumdirection = (EnumDirection) SystemUtils.getRandom(this.directions, randomsource);
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            if (!set.contains(blockposition1) && randomsource.nextFloat() < this.probability && this.hasRequiredEmptyBlocks(worldgenfeaturetree_a, blockposition, enumdirection)) {
                BlockPosition blockposition2 = blockposition1.offset(-this.exclusionRadiusXZ, -this.exclusionRadiusY, -this.exclusionRadiusXZ);
                BlockPosition blockposition3 = blockposition1.offset(this.exclusionRadiusXZ, this.exclusionRadiusY, this.exclusionRadiusXZ);
                Iterator iterator1 = BlockPosition.betweenClosed(blockposition2, blockposition3).iterator();

                while (iterator1.hasNext()) {
                    BlockPosition blockposition4 = (BlockPosition) iterator1.next();

                    set.add(blockposition4.immutable());
                }

                worldgenfeaturetree_a.setBlock(blockposition1, this.blockProvider.getState(randomsource, blockposition1));
            }
        }

    }

    private boolean hasRequiredEmptyBlocks(WorldGenFeatureTree.a worldgenfeaturetree_a, BlockPosition blockposition, EnumDirection enumdirection) {
        for (int i = 1; i <= this.requiredEmptyBlocks; ++i) {
            BlockPosition blockposition1 = blockposition.relative(enumdirection, i);

            if (!worldgenfeaturetree_a.isAir(blockposition1)) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected WorldGenFeatureTrees<?> type() {
        return WorldGenFeatureTrees.ATTACHED_TO_LEAVES;
    }
}
