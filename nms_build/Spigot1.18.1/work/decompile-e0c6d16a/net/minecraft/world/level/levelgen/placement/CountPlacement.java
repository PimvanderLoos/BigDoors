package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;

public class CountPlacement extends RepeatingPlacement {

    public static final Codec<CountPlacement> CODEC = IntProvider.codec(0, 256).fieldOf("count").xmap(CountPlacement::new, (countplacement) -> {
        return countplacement.count;
    }).codec();
    private final IntProvider count;

    private CountPlacement(IntProvider intprovider) {
        this.count = intprovider;
    }

    public static CountPlacement of(IntProvider intprovider) {
        return new CountPlacement(intprovider);
    }

    public static CountPlacement of(int i) {
        return of(ConstantInt.of(i));
    }

    @Override
    protected int count(Random random, BlockPosition blockposition) {
        return this.count.sample(random);
    }

    @Override
    public PlacementModifierType<?> type() {
        return PlacementModifierType.COUNT;
    }
}
