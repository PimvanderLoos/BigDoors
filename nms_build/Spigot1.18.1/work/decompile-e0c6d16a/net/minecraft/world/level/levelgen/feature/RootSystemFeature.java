package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.RootSystemConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class RootSystemFeature extends WorldGenerator<RootSystemConfiguration> {

    public RootSystemFeature(Codec<RootSystemConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RootSystemConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();

        if (!generatoraccessseed.getBlockState(blockposition).isAir()) {
            return false;
        } else {
            Random random = featureplacecontext.random();
            BlockPosition blockposition1 = featureplacecontext.origin();
            RootSystemConfiguration rootsystemconfiguration = (RootSystemConfiguration) featureplacecontext.config();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition1.mutable();

            if (placeDirtAndTree(generatoraccessseed, featureplacecontext.chunkGenerator(), rootsystemconfiguration, random, blockposition_mutableblockposition, blockposition1)) {
                placeRoots(generatoraccessseed, rootsystemconfiguration, random, blockposition1, blockposition_mutableblockposition);
            }

            return true;
        }
    }

    private static boolean spaceForTree(GeneratorAccessSeed generatoraccessseed, RootSystemConfiguration rootsystemconfiguration, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int i = 1; i <= rootsystemconfiguration.requiredVerticalSpaceForTree; ++i) {
            blockposition_mutableblockposition.move(EnumDirection.UP);
            IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition_mutableblockposition);

            if (!isAllowedTreeSpace(iblockdata, i, rootsystemconfiguration.allowedVerticalWaterForTree)) {
                return false;
            }
        }

        return true;
    }

    private static boolean isAllowedTreeSpace(IBlockData iblockdata, int i, int j) {
        if (iblockdata.isAir()) {
            return true;
        } else {
            int k = i + 1;

            return k <= j && iblockdata.getFluidState().is((Tag) TagsFluid.WATER);
        }
    }

    private static boolean placeDirtAndTree(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RootSystemConfiguration rootsystemconfiguration, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition blockposition) {
        for (int i = 0; i < rootsystemconfiguration.rootColumnMaxHeight; ++i) {
            blockposition_mutableblockposition.move(EnumDirection.UP);
            if (rootsystemconfiguration.allowedTreePosition.test(generatoraccessseed, blockposition_mutableblockposition) && spaceForTree(generatoraccessseed, rootsystemconfiguration, blockposition_mutableblockposition)) {
                BlockPosition blockposition1 = blockposition_mutableblockposition.below();

                if (generatoraccessseed.getFluidState(blockposition1).is((Tag) TagsFluid.LAVA) || !generatoraccessseed.getBlockState(blockposition1).getMaterial().isSolid()) {
                    return false;
                }

                if (((PlacedFeature) rootsystemconfiguration.treeFeature.get()).place(generatoraccessseed, chunkgenerator, random, blockposition_mutableblockposition)) {
                    placeDirt(blockposition, blockposition.getY() + i, generatoraccessseed, rootsystemconfiguration, random);
                    return true;
                }
            }
        }

        return false;
    }

    private static void placeDirt(BlockPosition blockposition, int i, GeneratorAccessSeed generatoraccessseed, RootSystemConfiguration rootsystemconfiguration, Random random) {
        int j = blockposition.getX();
        int k = blockposition.getZ();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int l = blockposition.getY(); l < i; ++l) {
            placeRootedDirt(generatoraccessseed, rootsystemconfiguration, random, j, k, blockposition_mutableblockposition.set(j, l, k));
        }

    }

    private static void placeRootedDirt(GeneratorAccessSeed generatoraccessseed, RootSystemConfiguration rootsystemconfiguration, Random random, int i, int j, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        int k = rootsystemconfiguration.rootRadius;
        Tag<Block> tag = TagsBlock.getAllTags().getTag(rootsystemconfiguration.rootReplaceable);
        Predicate<IBlockData> predicate = tag == null ? (iblockdata) -> {
            return true;
        } : (iblockdata) -> {
            return iblockdata.is(tag);
        };

        for (int l = 0; l < rootsystemconfiguration.rootPlacementAttempts; ++l) {
            blockposition_mutableblockposition.setWithOffset(blockposition_mutableblockposition, random.nextInt(k) - random.nextInt(k), 0, random.nextInt(k) - random.nextInt(k));
            if (predicate.test(generatoraccessseed.getBlockState(blockposition_mutableblockposition))) {
                generatoraccessseed.setBlock(blockposition_mutableblockposition, rootsystemconfiguration.rootStateProvider.getState(random, blockposition_mutableblockposition), 2);
            }

            blockposition_mutableblockposition.setX(i);
            blockposition_mutableblockposition.setZ(j);
        }

    }

    private static void placeRoots(GeneratorAccessSeed generatoraccessseed, RootSystemConfiguration rootsystemconfiguration, Random random, BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        int i = rootsystemconfiguration.hangingRootRadius;
        int j = rootsystemconfiguration.hangingRootsVerticalSpan;

        for (int k = 0; k < rootsystemconfiguration.hangingRootPlacementAttempts; ++k) {
            blockposition_mutableblockposition.setWithOffset(blockposition, random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
            if (generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition)) {
                IBlockData iblockdata = rootsystemconfiguration.hangingRootStateProvider.getState(random, blockposition_mutableblockposition);

                if (iblockdata.canSurvive(generatoraccessseed, blockposition_mutableblockposition) && generatoraccessseed.getBlockState(blockposition_mutableblockposition.above()).isFaceSturdy(generatoraccessseed, blockposition_mutableblockposition, EnumDirection.DOWN)) {
                    generatoraccessseed.setBlock(blockposition_mutableblockposition, iblockdata, 2);
                }
            }
        }

    }
}
