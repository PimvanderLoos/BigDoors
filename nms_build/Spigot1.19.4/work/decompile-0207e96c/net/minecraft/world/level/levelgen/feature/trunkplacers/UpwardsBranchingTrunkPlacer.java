package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class UpwardsBranchingTrunkPlacer extends TrunkPlacer {

    public static final Codec<UpwardsBranchingTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return trunkPlacerParts(instance).and(instance.group(IntProvider.POSITIVE_CODEC.fieldOf("extra_branch_steps").forGetter((upwardsbranchingtrunkplacer) -> {
            return upwardsbranchingtrunkplacer.extraBranchSteps;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("place_branch_per_log_probability").forGetter((upwardsbranchingtrunkplacer) -> {
            return upwardsbranchingtrunkplacer.placeBranchPerLogProbability;
        }), IntProvider.NON_NEGATIVE_CODEC.fieldOf("extra_branch_length").forGetter((upwardsbranchingtrunkplacer) -> {
            return upwardsbranchingtrunkplacer.extraBranchLength;
        }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_grow_through").forGetter((upwardsbranchingtrunkplacer) -> {
            return upwardsbranchingtrunkplacer.canGrowThrough;
        }))).apply(instance, UpwardsBranchingTrunkPlacer::new);
    });
    private final IntProvider extraBranchSteps;
    private final float placeBranchPerLogProbability;
    private final IntProvider extraBranchLength;
    private final HolderSet<Block> canGrowThrough;

    public UpwardsBranchingTrunkPlacer(int i, int j, int k, IntProvider intprovider, float f, IntProvider intprovider1, HolderSet<Block> holderset) {
        super(i, j, k);
        this.extraBranchSteps = intprovider;
        this.placeBranchPerLogProbability = f;
        this.extraBranchLength = intprovider1;
        this.canGrowThrough = holderset;
    }

    @Override
    protected TrunkPlacers<?> type() {
        return TrunkPlacers.UPWARDS_BRANCHING_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        List<WorldGenFoilagePlacer.a> list = Lists.newArrayList();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int j = 0; j < i; ++j) {
            int k = blockposition.getY() + j;

            if (this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition.set(blockposition.getX(), k, blockposition.getZ()), worldgenfeaturetreeconfiguration) && j < i - 1 && randomsource.nextFloat() < this.placeBranchPerLogProbability) {
                EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(randomsource);
                int l = this.extraBranchLength.sample(randomsource);
                int i1 = Math.max(0, l - this.extraBranchLength.sample(randomsource) - 1);
                int j1 = this.extraBranchSteps.sample(randomsource);

                this.placeBranch(virtuallevelreadable, biconsumer, randomsource, i, worldgenfeaturetreeconfiguration, list, blockposition_mutableblockposition, k, enumdirection, i1, j1);
            }

            if (j == i - 1) {
                list.add(new WorldGenFoilagePlacer.a(blockposition_mutableblockposition.set(blockposition.getX(), k + 1, blockposition.getZ()), 0, false));
            }
        }

        return list;
    }

    private void placeBranch(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, List<WorldGenFoilagePlacer.a> list, BlockPosition.MutableBlockPosition blockposition_mutableblockposition, int j, EnumDirection enumdirection, int k, int l) {
        int i1 = j + k;
        int j1 = blockposition_mutableblockposition.getX();
        int k1 = blockposition_mutableblockposition.getZ();

        for (int l1 = k; l1 < i && l > 0; --l) {
            if (l1 >= 1) {
                int i2 = j + l1;

                j1 += enumdirection.getStepX();
                k1 += enumdirection.getStepZ();
                i1 = i2;
                if (this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition.set(j1, i2, k1), worldgenfeaturetreeconfiguration)) {
                    i1 = i2 + 1;
                }

                list.add(new WorldGenFoilagePlacer.a(blockposition_mutableblockposition.immutable(), 0, false));
            }

            ++l1;
        }

        if (i1 - j > 1) {
            BlockPosition blockposition = new BlockPosition(j1, i1, k1);

            list.add(new WorldGenFoilagePlacer.a(blockposition, 0, false));
            list.add(new WorldGenFoilagePlacer.a(blockposition.below(2), 0, false));
        }

    }

    @Override
    protected boolean validTreePos(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return super.validTreePos(virtuallevelreadable, blockposition) || virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return iblockdata.is(this.canGrowThrough);
        });
    }
}
