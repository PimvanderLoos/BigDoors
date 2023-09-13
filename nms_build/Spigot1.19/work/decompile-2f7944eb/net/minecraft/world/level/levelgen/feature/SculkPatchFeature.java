package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SculkBehaviour;
import net.minecraft.world.level.block.SculkShriekerBlock;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.SculkPatchConfiguration;

public class SculkPatchFeature extends WorldGenerator<SculkPatchConfiguration> {

    public SculkPatchFeature(Codec<SculkPatchConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SculkPatchConfiguration> featureplacecontext) {
        GeneratorAccessSeed generatoraccessseed = featureplacecontext.level();
        BlockPosition blockposition = featureplacecontext.origin();

        if (!this.canSpreadFrom(generatoraccessseed, blockposition)) {
            return false;
        } else {
            SculkPatchConfiguration sculkpatchconfiguration = (SculkPatchConfiguration) featureplacecontext.config();
            RandomSource randomsource = featureplacecontext.random();
            SculkSpreader sculkspreader = SculkSpreader.createWorldGenSpreader();
            int i = sculkpatchconfiguration.spreadRounds() + sculkpatchconfiguration.growthRounds();

            int j;
            int k;

            for (int l = 0; l < i; ++l) {
                for (j = 0; j < sculkpatchconfiguration.chargeCount(); ++j) {
                    sculkspreader.addCursors(blockposition, sculkpatchconfiguration.amountPerCharge());
                }

                boolean flag = l < sculkpatchconfiguration.spreadRounds();

                for (k = 0; k < sculkpatchconfiguration.spreadAttempts(); ++k) {
                    sculkspreader.updateCursors(generatoraccessseed, blockposition, randomsource, flag);
                }

                sculkspreader.clear();
            }

            BlockPosition blockposition1 = blockposition.below();

            if (randomsource.nextFloat() <= sculkpatchconfiguration.catalystChance() && generatoraccessseed.getBlockState(blockposition1).isCollisionShapeFullBlock(generatoraccessseed, blockposition1)) {
                generatoraccessseed.setBlock(blockposition, Blocks.SCULK_CATALYST.defaultBlockState(), 3);
            }

            j = sculkpatchconfiguration.extraRareGrowths().sample(randomsource);

            for (k = 0; k < j; ++k) {
                BlockPosition blockposition2 = blockposition.offset(randomsource.nextInt(5) - 2, 0, randomsource.nextInt(5) - 2);

                if (generatoraccessseed.getBlockState(blockposition2).isAir() && generatoraccessseed.getBlockState(blockposition2.below()).isFaceSturdy(generatoraccessseed, blockposition2.below(), EnumDirection.UP)) {
                    generatoraccessseed.setBlock(blockposition2, (IBlockData) Blocks.SCULK_SHRIEKER.defaultBlockState().setValue(SculkShriekerBlock.CAN_SUMMON, true), 3);
                }
            }

            return true;
        }
    }

    private boolean canSpreadFrom(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);

        if (iblockdata.getBlock() instanceof SculkBehaviour) {
            return true;
        } else if (!iblockdata.isAir() && (!iblockdata.is(Blocks.WATER) || !iblockdata.getFluidState().isSource())) {
            return false;
        } else {
            Stream stream = EnumDirection.stream();

            Objects.requireNonNull(blockposition);
            return stream.map(blockposition::relative).anyMatch((blockposition1) -> {
                return generatoraccess.getBlockState(blockposition1).isCollisionShapeFullBlock(generatoraccess, blockposition1);
            });
        }
    }
}
