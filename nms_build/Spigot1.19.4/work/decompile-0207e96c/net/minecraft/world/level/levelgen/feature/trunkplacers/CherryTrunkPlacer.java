package net.minecraft.world.level.levelgen.feature.trunkplacers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.BlockRotatable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.foliageplacers.WorldGenFoilagePlacer;

public class CherryTrunkPlacer extends TrunkPlacer {

    private static final Codec<UniformInt> BRANCH_START_CODEC = ExtraCodecs.validate(UniformInt.CODEC, (uniformint) -> {
        return uniformint.getMaxValue() - uniformint.getMinValue() < 1 ? DataResult.error(() -> {
            return "Need at least 2 blocks variation for the branch starts to fit both branches";
        }) : DataResult.success(uniformint);
    });
    public static final Codec<CherryTrunkPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return trunkPlacerParts(instance).and(instance.group(IntProvider.codec(1, 3).fieldOf("branch_count").forGetter((cherrytrunkplacer) -> {
            return cherrytrunkplacer.branchCount;
        }), IntProvider.codec(2, 16).fieldOf("branch_horizontal_length").forGetter((cherrytrunkplacer) -> {
            return cherrytrunkplacer.branchHorizontalLength;
        }), IntProvider.codec(-16, 0, CherryTrunkPlacer.BRANCH_START_CODEC).fieldOf("branch_start_offset_from_top").forGetter((cherrytrunkplacer) -> {
            return cherrytrunkplacer.branchStartOffsetFromTop;
        }), IntProvider.codec(-16, 16).fieldOf("branch_end_offset_from_top").forGetter((cherrytrunkplacer) -> {
            return cherrytrunkplacer.branchEndOffsetFromTop;
        }))).apply(instance, CherryTrunkPlacer::new);
    });
    private final IntProvider branchCount;
    private final IntProvider branchHorizontalLength;
    private final UniformInt branchStartOffsetFromTop;
    private final UniformInt secondBranchStartOffsetFromTop;
    private final IntProvider branchEndOffsetFromTop;

    public CherryTrunkPlacer(int i, int j, int k, IntProvider intprovider, IntProvider intprovider1, UniformInt uniformint, IntProvider intprovider2) {
        super(i, j, k);
        this.branchCount = intprovider;
        this.branchHorizontalLength = intprovider1;
        this.branchStartOffsetFromTop = uniformint;
        this.secondBranchStartOffsetFromTop = UniformInt.of(uniformint.getMinValue(), uniformint.getMaxValue() - 1);
        this.branchEndOffsetFromTop = intprovider2;
    }

    @Override
    protected TrunkPlacers<?> type() {
        return TrunkPlacers.CHERRY_TRUNK_PLACER;
    }

    @Override
    public List<WorldGenFoilagePlacer.a> placeTrunk(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        setDirtAt(virtuallevelreadable, biconsumer, randomsource, blockposition.below(), worldgenfeaturetreeconfiguration);
        int j = Math.max(0, i - 1 + this.branchStartOffsetFromTop.sample(randomsource));
        int k = Math.max(0, i - 1 + this.secondBranchStartOffsetFromTop.sample(randomsource));

        if (k >= j) {
            ++k;
        }

        int l = this.branchCount.sample(randomsource);
        boolean flag = l == 3;
        boolean flag1 = l >= 2;
        int i1;

        if (flag) {
            i1 = i;
        } else if (flag1) {
            i1 = Math.max(j, k) + 1;
        } else {
            i1 = j + 1;
        }

        for (int j1 = 0; j1 < i1; ++j1) {
            this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition.above(j1), worldgenfeaturetreeconfiguration);
        }

        List<WorldGenFoilagePlacer.a> list = new ArrayList();

        if (flag) {
            list.add(new WorldGenFoilagePlacer.a(blockposition.above(i1), 0, false));
        }

        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.getRandomDirection(randomsource);
        Function<IBlockData, IBlockData> function = (iblockdata) -> {
            return (IBlockData) iblockdata.trySetValue(BlockRotatable.AXIS, enumdirection.getAxis());
        };

        list.add(this.generateBranch(virtuallevelreadable, biconsumer, randomsource, i, blockposition, worldgenfeaturetreeconfiguration, function, enumdirection, j, j < i1 - 1, blockposition_mutableblockposition));
        if (flag1) {
            list.add(this.generateBranch(virtuallevelreadable, biconsumer, randomsource, i, blockposition, worldgenfeaturetreeconfiguration, function, enumdirection.getOpposite(), k, k < i1 - 1, blockposition_mutableblockposition));
        }

        return list;
    }

    private WorldGenFoilagePlacer.a generateBranch(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, int i, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration, Function<IBlockData, IBlockData> function, EnumDirection enumdirection, int j, boolean flag, BlockPosition.MutableBlockPosition blockposition_mutableblockposition) {
        blockposition_mutableblockposition.set(blockposition).move(EnumDirection.UP, j);
        int k = i - 1 + this.branchEndOffsetFromTop.sample(randomsource);
        boolean flag1 = flag || k < j;
        int l = this.branchHorizontalLength.sample(randomsource) + (flag1 ? 1 : 0);
        BlockPosition blockposition1 = blockposition.relative(enumdirection, l).above(k);
        int i1 = flag1 ? 2 : 1;

        for (int j1 = 0; j1 < i1; ++j1) {
            this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition.move(enumdirection), worldgenfeaturetreeconfiguration, function);
        }

        EnumDirection enumdirection1 = blockposition1.getY() > blockposition_mutableblockposition.getY() ? EnumDirection.UP : EnumDirection.DOWN;

        while (true) {
            int k1 = blockposition_mutableblockposition.distManhattan(blockposition1);

            if (k1 == 0) {
                return new WorldGenFoilagePlacer.a(blockposition1.above(), 0, false);
            }

            float f = (float) Math.abs(blockposition1.getY() - blockposition_mutableblockposition.getY()) / (float) k1;
            boolean flag2 = randomsource.nextFloat() < f;

            blockposition_mutableblockposition.move(flag2 ? enumdirection1 : enumdirection);
            this.placeLog(virtuallevelreadable, biconsumer, randomsource, blockposition_mutableblockposition, worldgenfeaturetreeconfiguration, flag2 ? Function.identity() : function);
        }
    }
}
