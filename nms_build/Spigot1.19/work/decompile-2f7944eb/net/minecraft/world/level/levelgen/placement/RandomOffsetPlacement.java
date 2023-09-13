package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class RandomOffsetPlacement extends PlacementModifier {

    public static final Codec<RandomOffsetPlacement> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(IntProvider.codec(-16, 16).fieldOf("xz_spread").forGetter((randomoffsetplacement) -> {
            return randomoffsetplacement.xzSpread;
        }), IntProvider.codec(-16, 16).fieldOf("y_spread").forGetter((randomoffsetplacement) -> {
            return randomoffsetplacement.ySpread;
        })).apply(instance, RandomOffsetPlacement::new);
    });
    private final IntProvider xzSpread;
    private final IntProvider ySpread;

    public static RandomOffsetPlacement of(IntProvider intprovider, IntProvider intprovider1) {
        return new RandomOffsetPlacement(intprovider, intprovider1);
    }

    public static RandomOffsetPlacement vertical(IntProvider intprovider) {
        return new RandomOffsetPlacement(ConstantInt.of(0), intprovider);
    }

    public static RandomOffsetPlacement horizontal(IntProvider intprovider) {
        return new RandomOffsetPlacement(intprovider, ConstantInt.of(0));
    }

    private RandomOffsetPlacement(IntProvider intprovider, IntProvider intprovider1) {
        this.xzSpread = intprovider;
        this.ySpread = intprovider1;
    }

    @Override
    public Stream<BlockPosition> getPositions(PlacementContext placementcontext, RandomSource randomsource, BlockPosition blockposition) {
        int i = blockposition.getX() + this.xzSpread.sample(randomsource);
        int j = blockposition.getY() + this.ySpread.sample(randomsource);
        int k = blockposition.getZ() + this.xzSpread.sample(randomsource);

        return Stream.of(new BlockPosition(i, j, k));
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.RANDOM_OFFSET;
    }
}
