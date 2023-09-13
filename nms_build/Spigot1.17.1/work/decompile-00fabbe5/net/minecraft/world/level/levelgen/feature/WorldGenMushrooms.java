package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.IWorldWriter;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureMushroomConfiguration;

public abstract class WorldGenMushrooms extends WorldGenerator<WorldGenFeatureMushroomConfiguration> {

    public WorldGenMushrooms(Codec<WorldGenFeatureMushroomConfiguration> codec) {
        super(codec);
    }

    protected void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        for (int j = 0; j < i; ++j) {
            blockposition_mutableblockposition.g(blockposition).c(EnumDirection.UP, j);
            if (!generatoraccess.getType(blockposition_mutableblockposition).i(generatoraccess, blockposition_mutableblockposition)) {
                this.a((IWorldWriter) generatoraccess, blockposition_mutableblockposition, worldgenfeaturemushroomconfiguration.stemProvider.a(random, blockposition));
            }
        }

    }

    protected int a(Random random) {
        int i = random.nextInt(3) + 4;

        if (random.nextInt(12) == 0) {
            i *= 2;
        }

        return i;
    }

    protected boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration) {
        int j = blockposition.getY();

        if (j >= generatoraccess.getMinBuildHeight() + 1 && j + i + 1 < generatoraccess.getMaxBuildHeight()) {
            IBlockData iblockdata = generatoraccess.getType(blockposition.down());

            if (!b(iblockdata) && !iblockdata.a((Tag) TagsBlock.MUSHROOM_GROW_BLOCK)) {
                return false;
            } else {
                for (int k = 0; k <= i; ++k) {
                    int l = this.a(-1, -1, worldgenfeaturemushroomconfiguration.foliageRadius, k);

                    for (int i1 = -l; i1 <= l; ++i1) {
                        for (int j1 = -l; j1 <= l; ++j1) {
                            IBlockData iblockdata1 = generatoraccess.getType(blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, i1, k, j1));

                            if (!iblockdata1.isAir() && !iblockdata1.a((Tag) TagsBlock.LEAVES)) {
                                return false;
                            }
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean generate(FeaturePlaceContext<WorldGenFeatureMushroomConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.a();
        BlockPosition blockposition = featureplacecontext.d();
        Random random = featureplacecontext.c();
        WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration = (WorldGenFeatureMushroomConfiguration) featureplacecontext.e();
        int i = this.a(random);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        if (!this.a(generatoraccessseed, blockposition, i, blockposition_mutableblockposition, worldgenfeaturemushroomconfiguration)) {
            return false;
        } else {
            this.a(generatoraccessseed, random, blockposition, i, blockposition_mutableblockposition, worldgenfeaturemushroomconfiguration);
            this.a(generatoraccessseed, random, blockposition, worldgenfeaturemushroomconfiguration, i, blockposition_mutableblockposition);
            return true;
        }
    }

    protected abstract int a(int i, int j, int k, int l);

    protected abstract void a(GeneratorAccess generatoraccess, Random random, BlockPosition blockposition, int i, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, WorldGenFeatureMushroomConfiguration worldgenfeaturemushroomconfiguration);
}
