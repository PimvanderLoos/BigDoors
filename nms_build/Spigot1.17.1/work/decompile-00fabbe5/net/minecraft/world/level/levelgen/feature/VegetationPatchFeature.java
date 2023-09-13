package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.BaseBlockPosition;
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

public class VegetationPatchFeature extends WorldGenerator<VegetationPatchConfiguration> {

    public VegetationPatchFeature(Codec<VegetationPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeaturePlaceContext<VegetationPatchConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        VegetationPatchConfiguration vegetationpatchconfiguration = (VegetationPatchConfiguration) featureplacecontext.e();
        Random random = featureplacecontext.c();
        BlockPosition blockposition = featureplacecontext.d();
        Predicate<IBlockData> predicate = a(vegetationpatchconfiguration);
        int i = vegetationpatchconfiguration.xzRadius.a(random) + 1;
        int j = vegetationpatchconfiguration.xzRadius.a(random) + 1;
        Set<BlockPosition> set = this.a(generatoraccessseed, vegetationpatchconfiguration, random, blockposition, predicate, i, j);

        this.a(featureplacecontext, generatoraccessseed, vegetationpatchconfiguration, random, set, i, j);
        return !set.isEmpty();
    }

    protected Set<BlockPosition> a(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, Random random, BlockPosition blockposition, Predicate<IBlockData> predicate, int i, int j) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition1 = blockposition_mutableblockposition.i();
        EnumDirection enumdirection = vegetationpatchconfiguration.surface.a();
        EnumDirection enumdirection1 = enumdirection.opposite();
        Set<BlockPosition> set = new HashSet();

        for (int k = -i; k <= i; ++k) {
            boolean flag = k == -i || k == i;

            for (int l = -j; l <= j; ++l) {
                boolean flag1 = l == -j || l == j;
                boolean flag2 = flag || flag1;
                boolean flag3 = flag && flag1;
                boolean flag4 = flag2 && !flag3;

                if (!flag3 && (!flag4 || vegetationpatchconfiguration.extraEdgeColumnChance != 0.0F && random.nextFloat() <= vegetationpatchconfiguration.extraEdgeColumnChance)) {
                    blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, k, 0, l);

                    int i1;

                    for (i1 = 0; generatoraccessseed.a((BlockPosition) blockposition_mutableblockposition, BlockBase.BlockData::isAir) && i1 < vegetationpatchconfiguration.verticalRange; ++i1) {
                        blockposition_mutableblockposition.c(enumdirection);
                    }

                    for (i1 = 0; generatoraccessseed.a((BlockPosition) blockposition_mutableblockposition, (iblockdata) -> {
                        return !iblockdata.isAir();
                    }) && i1 < vegetationpatchconfiguration.verticalRange; ++i1) {
                        blockposition_mutableblockposition.c(enumdirection1);
                    }

                    blockposition_mutableblockposition1.a((BaseBlockPosition) blockposition_mutableblockposition, vegetationpatchconfiguration.surface.a());
                    IBlockData iblockdata = generatoraccessseed.getType(blockposition_mutableblockposition1);

                    if (generatoraccessseed.isEmpty(blockposition_mutableblockposition) && iblockdata.d(generatoraccessseed, blockposition_mutableblockposition1, vegetationpatchconfiguration.surface.a().opposite())) {
                        int j1 = vegetationpatchconfiguration.depth.a(random) + (vegetationpatchconfiguration.extraBottomBlockChance > 0.0F && random.nextFloat() < vegetationpatchconfiguration.extraBottomBlockChance ? 1 : 0);
                        BlockPosition blockposition1 = blockposition_mutableblockposition1.immutableCopy();
                        boolean flag5 = this.a(generatoraccessseed, vegetationpatchconfiguration, predicate, random, blockposition_mutableblockposition1, j1);

                        if (flag5) {
                            set.add(blockposition1);
                        }
                    }
                }
            }
        }

        return set;
    }

    protected void a(FeaturePlaceContext<VegetationPatchConfiguration> featureplacecontext, GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, Random random, Set<BlockPosition> set, int i, int j) {
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            if (vegetationpatchconfiguration.vegetationChance > 0.0F && random.nextFloat() < vegetationpatchconfiguration.vegetationChance) {
                this.a(generatoraccessseed, vegetationpatchconfiguration, featureplacecontext.b(), random, blockposition);
            }
        }

    }

    protected boolean a(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, ChunkGenerator chunkgenerator, Random random, BlockPosition blockposition) {
        return ((WorldGenFeatureConfigured) vegetationpatchconfiguration.vegetationFeature.get()).a(generatoraccessseed, chunkgenerator, random, blockposition.shift(vegetationpatchconfiguration.surface.a().opposite()));
    }

    protected boolean a(GeneratorAccessSeed generatoraccessseed, VegetationPatchConfiguration vegetationpatchconfiguration, Predicate<IBlockData> predicate, Random random, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int i) {
        for (int j = 0; j < i; ++j) {
            IBlockData iblockdata = vegetationpatchconfiguration.groundState.a(random, blockposition_mutableblockposition);
            IBlockData iblockdata1 = generatoraccessseed.getType(blockposition_mutableblockposition);

            if (!iblockdata.a(iblockdata1.getBlock())) {
                if (!predicate.test(iblockdata1)) {
                    return j != 0;
                }

                generatoraccessseed.setTypeAndData(blockposition_mutableblockposition, iblockdata, 2);
                blockposition_mutableblockposition.c(vegetationpatchconfiguration.surface.a());
            }
        }

        return true;
    }

    private static Predicate<IBlockData> a(VegetationPatchConfiguration vegetationpatchconfiguration) {
        Tag<Block> tag = TagsBlock.a().a(vegetationpatchconfiguration.replaceable);

        return tag == null ? (iblockdata) -> {
            return true;
        } : (iblockdata) -> {
            return iblockdata.a(tag);
        };
    }
}
