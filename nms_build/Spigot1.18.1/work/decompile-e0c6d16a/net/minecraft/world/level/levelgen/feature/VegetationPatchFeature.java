package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.configurations.VegetationPatchConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class VegetationPatchFeature extends WorldGenerator<VegetationPatchConfiguration> {

    public VegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<VegetationPatchConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        VegetationPatchConfiguration vegetationpatchconfiguration = (VegetationPatchConfiguration) featureplacecontext.config();
        Random random = featureplacecontext.random();
        BlockPosition blockposition = featureplacecontext.origin();
        Predicate<IBlockData> predicate = getReplaceableTag(vegetationpatchconfiguration);
        int i = vegetationpatchconfiguration.xzRadius.sample(random) + 1;
        int j = vegetationpatchconfiguration.xzRadius.sample(random) + 1;
        Set<BlockPosition> set = this.placeGroundPatch(generatoraccessseed, vegetationpatchconfiguration, random, blockposition, predicate, i, j);

        this.distributeVegetation(featureplacecontext, generatoraccessseed, vegetationpatchconfiguration, random, set, i, j);
        return !set.isEmpty();
    }

    protected Set<BlockPosition> placeGroundPatch(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, Random random, BlockPosition blockposition, Predicate<IBlockData> predicate, int i, int j) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition_mutableblockposition.mutable();
        EnumDirection enumdirection = vegetationpatchconfiguration.surface.getDirection();
        EnumDirection enumdirection1 = enumdirection.getOpposite();
        Set<BlockPosition> set = new HashSet();

        for (int k = -i; k <= i; ++k) {
            boolean flag = k == -i || k == i;

            for (int l = -j; l <= j; ++l) {
                boolean flag1 = l == -j || l == j;
                boolean flag2 = flag || flag1;
                boolean flag3 = flag && flag1;
                boolean flag4 = flag2 && !flag3;

                if (!flag3 && (!flag4 || vegetationpatchconfiguration.extraEdgeColumnChance != 0.0F && random.nextFloat() <= vegetationpatchconfiguration.extraEdgeColumnChance)) {
                    blockposition_mutableblockposition.setWithOffset(blockposition, k, 0, l);

                    int i1;

                    for (i1 = 0; generatoraccessseed.isStateAtPosition(blockposition_mutableblockposition, BlockBase.BlockData::isAir) && i1 < vegetationpatchconfiguration.verticalRange; ++i1) {
                        blockposition_mutableblockposition.move(enumdirection);
                    }

                    for (i1 = 0; generatoraccessseed.isStateAtPosition(blockposition_mutableblockposition, (iblockdata) -> {
                        return !iblockdata.isAir();
                    }) && i1 < vegetationpatchconfiguration.verticalRange; ++i1) {
                        blockposition_mutableblockposition.move(enumdirection1);
                    }

                    blockposition_mutableblockposition1.setWithOffset(blockposition_mutableblockposition, vegetationpatchconfiguration.surface.getDirection());
                    IBlockData iblockdata = generatoraccessseed.getBlockState(blockposition_mutableblockposition1);

                    if (generatoraccessseed.isEmptyBlock(blockposition_mutableblockposition) && iblockdata.isFaceSturdy(generatoraccessseed, blockposition_mutableblockposition1, vegetationpatchconfiguration.surface.getDirection().getOpposite())) {
                        int j1 = vegetationpatchconfiguration.depth.sample(random) + (vegetationpatchconfiguration.extraBottomBlockChance > 0.0F && random.nextFloat() < vegetationpatchconfiguration.extraBottomBlockChance ? 1 : 0);
                        BlockPosition blockposition1 = blockposition_mutableblockposition1.immutable();
                        boolean flag5 = this.placeGround(generatoraccessseed, vegetationpatchconfiguration, predicate, random, blockposition_mutableblockposition1, j1);

                        if (flag5) {
                            set.add(blockposition1);
                        }
                    }
                }
            }
        }

        return set;
    }

    protected void distributeVegetation(FeaturePlaceContext<VegetationPatchConfiguration> featureplacecontext, GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, Random random, Set<BlockPosition> set, int i, int j) {
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            if (vegetationpatchconfiguration.vegetationChance > 0.0F && random.nextFloat() < vegetationpatchconfiguration.vegetationChance) {
                this.placeVegetation(generatoraccessseed, vegetationpatchconfiguration, featureplacecontext.chunkGenerator(), random, blockposition);
            }
        }

    }

    protected boolean placeVegetation(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return ((PlacedFeature) vegetationpatchconfiguration.vegetationFeature.get()).place(generatoraccessseed, chunkgenerator, random, blockposition.relative(vegetationpatchconfiguration.surface.getDirection().getOpposite()));
    }

    protected boolean placeGround(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, Predicate<IBlockData> predicate, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i) {
        for (int j = 0; j < i; ++j) {
            IBlockData iblockdata = vegetationpatchconfiguration.groundState.getState(random, blockposition_mutableblockposition);
            IBlockData iblockdata1 = generatoraccessseed.getBlockState(blockposition_mutableblockposition);

            if (!iblockdata.is(iblockdata1.getBlock())) {
                if (!predicate.test(iblockdata1)) {
                    return j != 0;
                }

                generatoraccessseed.setBlock(blockposition_mutableblockposition, iblockdata, 2);
                blockposition_mutableblockposition.move(vegetationpatchconfiguration.surface.getDirection());
            }
        }

        return true;
    }

    private static Predicate<IBlockData> getReplaceableTag(VegetationPatchConfiguration vegetationpatchconfiguration) {
        Tag<Block> tag = TagsBlock.getAllTags().getTag(vegetationpatchconfiguration.replaceable);

        return tag == null ? (iblockdata) -> {
            return true;
        } : (iblockdata) -> {
            return iblockdata.is(tag);
        };
    }
}
