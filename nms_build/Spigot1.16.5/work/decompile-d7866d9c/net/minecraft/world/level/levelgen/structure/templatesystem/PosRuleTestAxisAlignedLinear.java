package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;

public class PosRuleTestAxisAlignedLinear extends PosRuleTest {

    public static final Codec<PosRuleTestAxisAlignedLinear> a = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.b;
        }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.d;
        }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.e;
        }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.f;
        }), EnumDirection.EnumAxis.d.fieldOf("axis").orElse(EnumDirection.EnumAxis.Y).forGetter((posruletestaxisalignedlinear) -> {
            return posruletestaxisalignedlinear.g;
        })).apply(instance, PosRuleTestAxisAlignedLinear::new);
    });
    private final float b;
    private final float d;
    private final int e;
    private final int f;
    private final EnumDirection.EnumAxis g;

    public PosRuleTestAxisAlignedLinear(float f, float f1, int i, int j, EnumDirection.EnumAxis enumdirection_enumaxis) {
        if (i >= j) {
            throw new IllegalArgumentException("Invalid range: [" + i + "," + j + "]");
        } else {
            this.b = f;
            this.d = f1;
            this.e = i;
            this.f = j;
            this.g = enumdirection_enumaxis;
        }
    }

    @Override
    public boolean a(BlockPosition blockposition, BlockPosition blockposition1, BlockPosition blockposition2, Random random) {
        EnumDirection enumdirection = EnumDirection.a(EnumDirection.EnumAxisDirection.POSITIVE, this.g);
        float f = (float) Math.abs((blockposition1.getX() - blockposition2.getX()) * enumdirection.getAdjacentX());
        float f1 = (float) Math.abs((blockposition1.getY() - blockposition2.getY()) * enumdirection.getAdjacentY());
        float f2 = (float) Math.abs((blockposition1.getZ() - blockposition2.getZ()) * enumdirection.getAdjacentZ());
        int i = (int) (f + f1 + f2);
        float f3 = random.nextFloat();

        return (double) f3 <= MathHelper.b((double) this.b, (double) this.d, MathHelper.c((double) i, (double) this.e, (double) this.f));
    }

    @Override
    protected PosRuleTestType<?> a() {
        return PosRuleTestType.c;
    }
}
