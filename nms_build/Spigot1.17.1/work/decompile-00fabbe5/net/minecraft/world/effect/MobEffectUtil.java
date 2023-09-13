package net.minecraft.world.effect;

import net.minecraft.util.MathHelper;
import net.minecraft.util.UtilColor;
import net.minecraft.world.entity.EntityLiving;

public final class MobEffectUtil {

    public MobEffectUtil() {}

    public static String a(MobEffect mobeffect, float f) {
        if (mobeffect.h()) {
            return "**:**";
        } else {
            int i = MathHelper.d((float) mobeffect.getDuration() * f);

            return UtilColor.a(i);
        }
    }

    public static boolean a(EntityLiving entityliving) {
        return entityliving.hasEffect(MobEffects.DIG_SPEED) || entityliving.hasEffect(MobEffects.CONDUIT_POWER);
    }

    public static int b(EntityLiving entityliving) {
        int i = 0;
        int j = 0;

        if (entityliving.hasEffect(MobEffects.DIG_SPEED)) {
            i = entityliving.getEffect(MobEffects.DIG_SPEED).getAmplifier();
        }

        if (entityliving.hasEffect(MobEffects.CONDUIT_POWER)) {
            j = entityliving.getEffect(MobEffects.CONDUIT_POWER).getAmplifier();
        }

        return Math.max(i, j);
    }

    public static boolean c(EntityLiving entityliving) {
        return entityliving.hasEffect(MobEffects.WATER_BREATHING) || entityliving.hasEffect(MobEffects.CONDUIT_POWER);
    }
}
