package net.minecraft.world.entity.monster.hoglin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.phys.Vec3D;

public interface IOglin {

    int ATTACK_ANIMATION_DURATION = 10;

    int getAttackAnimationRemainingTicks();

    static boolean hurtAndThrowTarget(EntityLiving entityliving, EntityLiving entityliving1) {
        float f = (float) entityliving.getAttributeValue(GenericAttributes.ATTACK_DAMAGE);
        float f1;

        if (!entityliving.isBaby() && (int) f > 0) {
            f1 = f / 2.0F + (float) entityliving.level.random.nextInt((int) f);
        } else {
            f1 = f;
        }

        boolean flag = entityliving1.hurt(DamageSource.mobAttack(entityliving), f1);

        if (flag) {
            entityliving.doEnchantDamageEffects(entityliving, entityliving1);
            if (!entityliving.isBaby()) {
                throwTarget(entityliving, entityliving1);
            }
        }

        return flag;
    }

    static void throwTarget(EntityLiving entityliving, EntityLiving entityliving1) {
        double d0 = entityliving.getAttributeValue(GenericAttributes.ATTACK_KNOCKBACK);
        double d1 = entityliving1.getAttributeValue(GenericAttributes.KNOCKBACK_RESISTANCE);
        double d2 = d0 - d1;

        if (d2 > 0.0D) {
            double d3 = entityliving1.getX() - entityliving.getX();
            double d4 = entityliving1.getZ() - entityliving.getZ();
            float f = (float) (entityliving.level.random.nextInt(21) - 10);
            double d5 = d2 * (double) (entityliving.level.random.nextFloat() * 0.5F + 0.2F);
            Vec3D vec3d = (new Vec3D(d3, 0.0D, d4)).normalize().scale(d5).yRot(f);
            double d6 = d2 * (double) entityliving.level.random.nextFloat() * 0.5D;

            entityliving1.push(vec3d.x, d6, vec3d.z);
            entityliving1.hurtMarked = true;
        }
    }
}
