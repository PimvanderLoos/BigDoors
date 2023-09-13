package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;

public class PosRuleTestLinear extends PosRuleTest {

    public static final Codec<PosRuleTestLinear> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((posruletestlinear) -> {
            return posruletestlinear.minChance;
        }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((posruletestlinear) -> {
            return posruletestlinear.maxChance;
        }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((posruletestlinear) -> {
            return posruletestlinear.minDist;
        }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((posruletestlinear) -> {
            return posruletestlinear.maxDist;
        })).apply(instance, PosRuleTestLinear::new);
    });
    private final float minChance;
    private final float maxChance;
    private final int minDist;
    private final int maxDist;

    public PosRuleTestLinear(float f, float f1, int i, int j) {
        if (i >= j) {
            throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
        } else {
            this.minChance = f;
            this.maxChance = f1;
            this.minDist = i;
            this.maxDist = j;
        }
    }

    @Override
    public boolean a(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random) {
        int i = blockposition1.k(blockposition2);
        float f = random.nextFloat();

        return (double) f <= MathHelper.b((double) this.minChance, (double) this.maxChance, MathHelper.c((double) i, (double) this.minDist, (double) this.maxDist));
    }

    @Override
    protected PosRuleTestType<?> a() {
        return PosRuleTestType.LINEAR_POS_TEST;
    }
}
