package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;

public class PosRuleTestAxisAlignedLinear extends PosRuleTest {

    public static final Codec<PosRuleTestAxisAlignedLinear> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.minChance;
        }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.maxChance;
        }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.minDist;
        }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.maxDist;
        }), EnumDirection.EnumAxis.CODEC.fieldOf("axis").orElse(EnumDirection.EnumAxis.Y).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.axis;
        })).apply(instance, PosRuleTestAxisAlignedLinear::new);
    });
    private final float minChance;
    private final float maxChance;
    private final int minDist;
    private final int maxDist;
    private final EnumDirection.EnumAxis axis;

    public PosRuleTestAxisAlignedLinear(float f, float f1, int i, int j, EnumDirection.EnumAxis enumdirection_enumaxis) {
        if (i >= j) {
            throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
        } else {
            this.minChance = f;
            this.maxChance = f1;
            this.minDist = i;
            this.maxDist = j;
            this.axis = enumdirection_enumaxis;
        }
    }

    @Override
    public boolean test(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, RandomSource randomsource) {
        EnumDirection enumdirection = EnumDirection.get(EnumDirection.EnumAxisDirection.POSITIVE, this.axis);
        float f = (float) Math.abs((blockposition1.getX() - blockposition2.getX()) * enumdirection.getStepX());
        float f1 = (float) Math.abs((blockposition1.getY() - blockposition2.getY()) * enumdirection.getStepY());
        float f2 = (float) Math.abs((blockposition1.getZ() - blockposition2.getZ()) * enumdirection.getStepZ());
        int i = (int) (f + f1 + f2);
        float f3 = randomsource.nextFloat();

        return f3 <= MathHelper.clampedLerp(this.minChance, this.maxChance, MathHelper.inverseLerp((float) i, (float) this.minDist, (float) this.maxDist));
    }

    @Override
    protected PosRuleTestType<?> getType() {
        return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS_TEST;
    }
}
