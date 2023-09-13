package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public class MangroveRootPlacer extends RootPlacer {

    public static final int ROOT_WIDTH_LIMIT = 8;
    public static final int ROOT_LENGTH_LIMIT = 15;
    public static final Codec<MangroveRootPlacer> CODEC = RecordCodecBuilder.create((instance) -> {
        return rootPlacerParts(instance).and(MangroveRootPlacement.CODEC.fieldOf("mangrove_root_placement").forGetter((mangroverootplacer) -> {
            return mangroverootplacer.mangroveRootPlacement;
        })).apply(instance, MangroveRootPlacer::new);
    });
    private final MangroveRootPlacement mangroveRootPlacement;

    public MangroveRootPlacer(IntProvider intprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider, Optional<AboveRootPlacement> optional, MangroveRootPlacement mangroverootplacement) {
        super(intprovider, worldgenfeaturestateprovider, optional);
        this.mangroveRootPlacement = mangroverootplacement;
    }

    @Override
    public boolean placeRoots(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition blockposition, BlockPosition blockposition1, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        List<BlockPosition> list = Lists.newArrayList();
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        while (blockposition_mutableblockposition.getY() < blockposition1.getY()) {
            if (!this.canPlaceRoot(virtuallevelreadable, blockposition_mutableblockposition)) {
                return false;
            }

            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

        list.add(blockposition1.below());
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition2 = blockposition1.relative(enumdirection);
            List<BlockPosition> list1 = Lists.newArrayList();

            if (!this.simulateRoots(virtuallevelreadable, randomsource, blockposition2, enumdirection, blockposition1, list1, 0)) {
                return false;
            }

            list.addAll(list1);
            list.add(blockposition1.relative(enumdirection));
        }

        iterator = list.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition3 = (BlockPosition) iterator.next();

            this.placeRoot(virtuallevelreadable, biconsumer, randomsource, blockposition3, worldgenfeaturetreeconfiguration);
        }

        return true;
    }

    private boolean simulateRoots(VirtualLevelReadable virtuallevelreadable, RandomSource randomsource, BlockPosition blockposition, EnumDirection enumdirection, BlockPosition blockposition1, List<BlockPosition> list, int i) {
        int j = this.mangroveRootPlacement.maxRootLength();

        if (i != j && list.size() <= j) {
            List<BlockPosition> list1 = this.potentialRootPositions(blockposition, enumdirection, randomsource, blockposition1);
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition2 = (BlockPosition) iterator.next();

                if (this.canPlaceRoot(virtuallevelreadable, blockposition2)) {
                    list.add(blockposition2);
                    if (!this.simulateRoots(virtuallevelreadable, randomsource, blockposition2, enumdirection, blockposition1, list, i + 1)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected List<BlockPosition> potentialRootPositions(BlockPosition blockposition, EnumDirection enumdirection, RandomSource randomsource, BlockPosition blockposition1) {
        BlockPosition blockposition2 = blockposition.below();
        BlockPosition blockposition3 = blockposition.relative(enumdirection);
        int i = blockposition.distManhattan(blockposition1);
        int j = this.mangroveRootPlacement.maxRootWidth();
        float f = this.mangroveRootPlacement.randomSkewChance();

        return i > j - 3 && i <= j ? (randomsource.nextFloat() < f ? List.of(blockposition2, blockposition3.below()) : List.of(blockposition2)) : (i > j ? List.of(blockposition2) : (randomsource.nextFloat() < f ? List.of(blockposition2) : (randomsource.nextBoolean() ? List.of(blockposition3) : List.of(blockposition2))));
    }

    @Override
    protected boolean canPlaceRoot(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return super.canPlaceRoot(virtuallevelreadable, blockposition) || virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return iblockdata.is(this.mangroveRootPlacement.canGrowThrough());
        });
    }

    @Override
    protected void placeRoot(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (virtuallevelreadable.isStateAtPosition(blockposition, (iblockdata) -> {
            return iblockdata.is(this.mangroveRootPlacement.muddyRootsIn());
        })) {
            IBlockData iblockdata = this.mangroveRootPlacement.muddyRootsProvider().getState(randomsource, blockposition);

            biconsumer.accept(blockposition, this.getPotentiallyWaterloggedState(virtuallevelreadable, blockposition, iblockdata));
        } else {
            super.placeRoot(virtuallevelreadable, biconsumer, randomsource, blockposition, worldgenfeaturetreeconfiguration);
        }

    }

    @Override
    protected RootPlacerType<?> type() {
        return RootPlacerType.MANGROVE_ROOT_PLACER;
    }
}
