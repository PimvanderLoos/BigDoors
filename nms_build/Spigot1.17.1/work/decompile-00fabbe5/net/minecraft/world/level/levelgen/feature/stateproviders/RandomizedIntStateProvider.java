package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
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
        Collection<Integer> collection = blockstateinteger.getValues();

        for (int i = intprovider.a(); i <= intprovider.b(); ++i) {
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
    protected WorldGenFeatureStateProviders<?> a() {
        return WorldGenFeatureStateProviders.RANDOMIZED_INT_STATE_PROVIDER;
    }

    @Override
    public IBlockData a(Random random, BlockPosition blockposition) {
        IBlockData iblockdata = this.source.a(random, blockposition);

        if (this.property == null || !iblockdata.b(this.property)) {
            this.property = a(iblockdata, this.propertyName);
        }

        return (IBlockData) iblockdata.set(this.property, this.values.a(random));
    }

    private static BlockStateInteger a(IBlockData iblockdata, String s) {
        Collection<IBlockState<?>> collection = iblockdata.s();
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
