package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;

public class RandomizedIntStateProvider extends WorldGenFeatureStateProvider {

    public static final Codec<RandomizedIntStateProvider> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(WorldGenFeatureStateProvider.CODEC.fieldOf("source").forGetter((randomizedintstateprovider) -> {
            return randomizedintstateprovider.source;
        }), Codec.STRING.fieldOf("property").forGetter((randomizedintstateprovider) -> {
            return randomizedintstateprovider.propertyName;
        }), IntProvider.CODEC.fieldOf("values").forGetter((randomizedintstateprovider) -> {
            return randomizedintstateprovider.values;
        })).apply(instance, RandomizedIntStateProvider::new);
    });
    private final WorldGenFeatureStateProvider source;
    private final String propertyName;
    @Nullable
    private BlockStateInteger property;
    private final IntProvider values;

    public RandomizedIntStateProvider(WorldGenFeatureStateProvider worldgenfeaturestateprovider, BlockStateInteger blockstateinteger, IntProvider intprovider) {
        this.source = worldgenfeaturestateprovider;
        this.property = blockstateinteger;
        this.propertyName = blockstateinteger.getName();
        this.values = intprovider;
        Collection<Integer> collection = blockstateinteger.getPossibleValues();

        for (int i = intprovider.getMinValue(); i <= intprovider.getMaxValue(); ++i) {
            if (!collection.contains(i)) {
                String s = blockstateinteger.getName();

                throw new IllegalArgumentException("Property value out of range: " + s + ": " + i);
            }
        }

    }

    public RandomizedIntStateProvider(WorldGenFeatureStateProvider worldgenfeaturestateprovider, String s, IntProvider intprovider) {
        this.source = worldgenfeaturestateprovider;
        this.propertyName = s;
        this.values = intprovider;
    }

    @Override
    protected WorldGenFeatureStateProviders<?> type() {
        return WorldGenFeatureStateProviders.RANDOMIZED_INT_STATE_PROVIDER;
    }

    @Override
    public IBlockData getState(RandomSource randomsource, BlockPosition blockposition) {
        IBlockData iblockdata = this.source.getState(randomsource, blockposition);

        if (this.property == null || !iblockdata.hasProperty(this.property)) {
            this.property = findProperty(iblockdata, this.propertyName);
        }

        return (IBlockData) iblockdata.setValue(this.property, this.values.sample(randomsource));
    }

    private static BlockStateInteger findProperty(IBlockData iblockdata, String s) {
        Collection<IBlockState<?>> collection = iblockdata.getProperties();
        Optional<BlockStateInteger> optional = collection.stream().filter((iblockstate) -> {
            return iblockstate.getName().equals(s);
        }).filter((iblockstate) -> {
            return iblockstate instanceof BlockStateInteger;
        }).map((iblockstate) -> {
            return (BlockStateInteger) iblockstate;
        }).findAny();

        return (BlockStateInteger) optional.orElseThrow(() -> {
            return new IllegalArgumentException("Illegal property: " + s);
        });
    }
}
