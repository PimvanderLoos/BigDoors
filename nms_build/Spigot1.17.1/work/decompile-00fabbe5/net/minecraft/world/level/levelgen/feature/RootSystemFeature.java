package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.core.BaseBlockPosition;
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

public class RootSystemFeature extends WorldGenerator<RootSystemConfiguration> {

    public RootSystemFeature(Codec<RootSystemConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<RootSystemConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();

        if (!generatoraccessseed.getType(blockposition).isAir()) {
            return false;
        } else {
            Random random = featureplacecontext.c();
            BlockPosition blockposition1 = featureplacecontext.d();
            RootSystemConfiguration rootsystemconfiguration = (RootSystemConfiguration) featureplacecontext.e();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition1.i();

            if (this.a(generatoraccessseed, featureplacecontext.b(), rootsystemconfiguration, random, blockposition_mutableblockposition, blockposition1)) {
                this.a(generatoraccessseed, rootsystemconfiguration, random, blockposition1, blockposition_mutableblockposition);
            }

            return true;
        }
    }

    private boolean a(GeneratorAccessSeed generatoraccessseed, RootSystemConfiguration rootsystemconfiguration, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        for (int i = 1; i <= rootsystemconfiguration.requiredVerticalSpaceForTree; ++i) {
            blockposition_mutableblockposition.c(EnumDirection.UP);
            IBlockData iblockdata = generatoraccessseed.getType(blockposition_mutableblockposition);

            if (!a(iblockdata, i, rootsystemconfiguration.allowedVerticalWaterForTree)) {
                return false;
            }
        }

        return true;
    }

    private static boolean a(IBlockData iblockdata, int i, int j) {
        return iblockdata.isAir() || i <= j && iblockdata.getFluid().a((Tag) TagsFluid.WATER);
    }

    private boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RootSystemConfiguration rootsystemconfiguration, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, BlockPosition blockposition) {
        int i = blockposition.getX();
        int j = blockposition.getZ();

        for (int k = 0; k < rootsystemconfiguration.rootColumnMaxHeight; ++k) {
            blockposition_mutableblockposition.c(EnumDirection.UP);
            if (WorldGenTrees.e(generatoraccessseed, blockposition_mutableblockposition)) {
                if (this.a(generatoraccessseed, rootsystemconfiguration, (BlockPosition) blockposition_mutableblockposition)) {
                    BlockPosition blockposition1 = blockposition_mutableblockposition.down();

                    if (generatoraccessseed.getFluid(blockposition1).a((Tag) TagsFluid.LAVA) || !generatoraccessseed.getType(blockposition1).getMaterial().isBuildable()) {
                        return false;
                    }

                    if (this.a(generatoraccessseed, chunkgenerator, rootsystemconfiguration, random, (BlockPosition) blockposition_mutableblockposition)) {
                        return true;
                    }
                }
            } else {
                this.a(generatoraccessseed, rootsystemconfiguration, random, i, j, blockposition_mutableblockposition);
            }
        }

        return false;
    }

    private boolean a(GeneratorAccessSeed generatoraccessseed, ChunkGenerator chunkgenerator, RootSystemConfiguration rootsystemconfiguration, Random random, BlockPosition blockposition) {
        return ((WorldGenFeatureConfigured) rootsystemconfiguration.treeFeature.get()).a(generatoraccessseed, chunkgenerator, random, blockposition);
    }

    private void a(GeneratorAccessSeed generatoraccessseed, RootSystemConfiguration rootsystemconfiguration, Random random, int i, int j, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        int k = rootsystemconfiguration.rootRadius;
        Tag<Block> tag = TagsBlock.a().a(rootsystemconfiguration.rootReplaceable);
        Predicate<IBlockData> predicate = tag == null ? (iblockdata) -> {
            return true;
        } : (iblockdata) -> {
            return iblockdata.a(tag);
        };

        for (int l = 0; l < rootsystemconfiguration.rootPlacementAttempts; ++l) {
            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition_mutableblockposition, random.nextInt(k) - random.nextInt(k), 0, random.nextInt(k) - random.nextInt(k));
            if (predicate.test(generatoraccessseed.getType(blockposition_mutableblockposition))) {
                generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, rootsystemconfiguration.rootStateProvider.a(random, blockposition_mutableblockposition), 2);
            }

            blockposition_mutableblockposition.u(i);
            blockposition_mutableblockposition.s(j);
        }

    }

    private void a(GeneratorAccessSeed generatoraccessseed, RootSystemConfiguration rootsystemconfiguration, Random random, BlockPosition blockposition, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        int i = rootsystemconfiguration.hangingRootRadius;
        int j = rootsystemconfiguration.hangingRootsVerticalSpan;

        for (int k = 0; k < rootsystemconfiguration.hangingRootPlacementAttempts; ++k) {
            blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, random.nextInt(i) - random.nextInt(i), random.nextInt(j) - random.nextInt(j), random.nextInt(i) - random.nextInt(i));
            if (generatoraccessseed.isEmpty(blockposition_mutableblockposition)) {
                IBlockData iblockdata = rootsystemconfiguration.hangingRootStateProvider.a(random, blockposition_mutableblockposition);

                if (iblockdata.canPlace(generatoraccessseed, blockposition_mutableblockposition) && generatoraccessseed.getType(blockposition_mutableblockposition.up()).d(generatoraccessseed, blockposition_mutableblockposition, EnumDirection.DOWN)) {
                    generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
                }
            }
        }

    }
}
