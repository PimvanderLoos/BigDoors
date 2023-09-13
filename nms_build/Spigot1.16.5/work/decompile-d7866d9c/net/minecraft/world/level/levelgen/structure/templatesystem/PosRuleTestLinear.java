package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.util.MathHelper;

public class PosRuleTestLinear extends PosRuleTest {

    public static final Codec<PosRuleTestLinear> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((posruletestlinear) -> {
            return posruletestlinear.b;
        }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((posruletestlinear) -> {
            return posruletestlinear.d;
        }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((posruletestlinear) -> {
            return posruletestlinear.e;
        }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((posruletestlinear) -> {
            return posruletestlinear.f;
        })).apply(instance, PosRuleTestLinear::new);
    });
    private final float b;
    private final float d;
    private final int e;
    private final int f;

    public PosRuleTestLinear(float f, float f1, int i, int j) {
        if (i >= j) {
            throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
        } else {
            this.b = f;
            this.d = f1;
            this.e = i;
            this.f = j;
        }
    }

    @Override
    public boolean a(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random) {
        int i = blockposition1.k(blockposition2);
        float f = random.nextFloat();

        return (double) f <= MathHelper.b((double) this.b, (double) this.d, MathHelper.c((double) i, (double) this.e, (double) this.f));
    }

    @Override
    protected PosRuleTestType<?> a() {
        return PosRuleTestType.b;
    }
}
