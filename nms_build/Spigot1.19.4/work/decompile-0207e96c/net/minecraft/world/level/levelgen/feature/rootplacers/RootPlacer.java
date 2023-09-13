package net.minecraft.world.level.levelgen.feature.rootplacers;

import com.mojang.datafixers.Products.P3;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.Optional;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.VirtualLevelReadable;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.levelgen.feature.WorldGenTrees;
import net.minecraft.world.level.levelgen.feature.configurations.WorldGenFeatureTreeConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.WorldGenFeatureStateProvider;

public abstract class RootPlacer {

    public static final Codec<RootPlacer> CODEC = BuiltInRegistries.ROOT_PLACER_TYPE.byNameCodec().dispatch(RootPlacer::type, RootPlacerType::codec);
    protected final IntProvider trunkOffsetY;
    protected final WorldGenFeatureStateProvider rootProvider;
    protected final Optional<AboveRootPlacement> aboveRootPlacement;

    protected static <P extends RootPlacer> P3<Mu<P>, IntProvider, WorldGenFeatureStateProvider, Optional<AboveRootPlacement>> rootPlacerParts(Instance<P> instance) {
        return instance.group(IntProvider.CODEC.fieldOf("trunk_offset_y").forGetter((rootplacer) -> {
            return rootplacer.trunkOffsetY;
        }), WorldGenFeatureStateProvider.CODEC.fieldOf("root_provider").forGetter((rootplacer) -> {
            return rootplacer.rootProvider;
        }), AboveRootPlacement.CODEC.optionalFieldOf("above_root_placement").forGetter((rootplacer) -> {
            return rootplacer.aboveRootPlacement;
        }));
    }

    public RootPlacer(IntProvider intprovider, WorldGenFeatureStateProvider worldgenfeaturestateprovider, Optional<AboveRootPlacement> optional) {
        this.trunkOffsetY = intprovider;
        this.rootProvider = worldgenfeaturestateprovider;
        this.aboveRootPlacement = optional;
    }

    protected abstract RootPlacerType<?> type();

    public abstract boolean placeRoots(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition blockposition, BlockPosition blockposition1, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration);

    protected boolean canPlaceRoot(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition) {
        return WorldGenTrees.validTreePos(virtuallevelreadable, blockposition);
    }

    protected void placeRoot(VirtualLevelReadable virtuallevelreadable, BiConsumer<BlockPosition, IBlockData> biconsumer, RandomSource randomsource, BlockPosition blockposition, WorldGenFeatureTreeConfiguration worldgenfeaturetreeconfiguration) {
        if (this.canPlaceRoot(virtuallevelreadable, blockposition)) {
            biconsumer.accept(blockposition, this.getPotentiallyWaterloggedState(virtuallevelreadable, blockposition, this.rootProvider.getState(randomsource, blockposition)));
            if (this.aboveRootPlacement.isPresent()) {
                AboveRootPlacement aboverootplacement = (AboveRootPlacement) this.aboveRootPlacement.get();
                BlockPosition blockposition1 = blockposition.above();

                if (randomsource.nextFloat() < aboverootplacement.aboveRootPlacementChance() && virtuallevelreadable.isStateAtPosition(blockposition1, BlockBase.BlockData::isAir)) {
                    biconsumer.accept(blockposition1, this.getPotentiallyWaterloggedState(virtuallevelreadable, blockposition1, aboverootplacement.aboveRootProvider().getState(randomsource, blockposition1)));
                }
            }

        }
    }

    protected IBlockData getPotentiallyWaterloggedState(VirtualLevelReadable virtuallevelreadable, BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.hasProperty(BlockProperties.WATERLOGGED)) {
            boolean flag = virtuallevelreadable.isFluidAtPosition(blockposition, (fluid) -> {
                return fluid.is(TagsFluid.WATER);
            });

            return (IBlockData) iblockdata.setValue(BlockProperties.WATERLOGGED, flag);
        } else {
            return iblockdata;
        }
    }

    public BlockPosition getTrunkOrigin(BlockPosition blockposition, RandomSource randomsource) {
        return blockposition.above(this.trunkOffsetY.sample(randomsource));
    }
}
