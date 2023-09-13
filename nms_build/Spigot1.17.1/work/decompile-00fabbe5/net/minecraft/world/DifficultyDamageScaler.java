package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.MathHelper;

@Immutable
public class DifficultyDamageScaler {

    private static final float DIFFICULTY_TIME_GLOBAL_OFFSET = -72000.0F;
    private static final float MAX_DIFFICULTY_TIME_GLOBAL = 1440000.0F;
    private static final float MAX_DIFFICULTY_TIME_LOCAL = 3600000.0F;
    private final EnumDifficulty base;
    private final float effectiveDifficulty;

    public DifficultyDamageScaler(EnumDifficulty enumdifficulty, long i, long j, float f) {
        this.base = enumdifficulty;
        this.effectiveDifficulty = this.a(enumdifficulty, i, j, f);
    }

    public EnumDifficulty a() {
        return this.base;
    }

    public float b() {
        return this.effectiveDifficulty;
    }

    public boolean c() {
        return this.effectiveDifficulty >= (float) EnumDifficulty.HARD.ordinal();
    }

    public boolean a(float f) {
        return this.effectiveDifficulty > f;
    }

    public float d() {
        return this.effectiveDifficulty < 2.0F ? 0.0F : (this.effectiveDifficulty > 4.0F ? 1.0F : (this.effectiveDifficulty - 2.0F) / 2.0F);
    }

    private float a(EnumDifficulty enumdifficulty, long i, long j, float f) {
        if (enumdifficulty == EnumDifficulty.PEACEFUL) {
            return 0.0F;
        } else {
            boolean flag = enumdifficulty == EnumDifficulty.HARD;
            float f1 = 0.75F;
            float f2 = MathHelper.a(((float) i + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;

            f1 += f2;
            float f3 = 0.0F;

            f3 += MathHelper.a((float) j / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
            f3 += MathHelper.a(f * 0.25F, 0.0F, f2);
            if (enumdifficulty == EnumDifficulty.EASY) {
                f3 *= 0.5F;
            }

            f1 += f3;
            return (float) enumdifficulty.a() * f1;
        }
    }
}
