package net.minecraft.world.entity.monster.hoglin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.phys.Vec3D;

public interface IOglin {

    static boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        float f = (float) entityliving.b(GenericAttributes.ATTACK_DAMAGE);
        float f1;

        if (!entityliving.isBaby() && (int) f > 0) {
            f1 = f / 2.0F + (float) entityliving.world.random.nextInt((int) f);
        } else {
            f1 = f;
        }

        boolean flag = entityliving1.damageEntity(DamageSource.mobAttack(entityliving), f1);

        if (flag) {
            entityliving.a(entityliving, (Entity) entityliving1);
            if (!entityliving.isBaby()) {
                b(entityliving, entityliving1);
            }
        }

        return flag;
    }

    static void b(EntityLiving entityliving, EntityLiving entityliving1) {
        double d0 = entityliving.b(GenericAttributes.ATTACK_KNOCKBACK);
        double d1 = entityliving1.b(GenericAttributes.KNOCKBACK_RESISTANCE);
        double d2 = d0 - d1;

        if (d2 > 0.0D) {
            double d3 = entityliving1.locX() - entityliving.locX();
            double d4 = entityliving1.locZ() - entityliving.locZ();
            float f = (float) (entityliving.world.random.nextInt(21) - 10);
            double d5 = d2 * (double) (entityliving.world.random.nextFloat() * 0.5F + 0.2F);
            Vec3D vec3d = (new Vec3D(d3, 0.0D, d4)).d().a(d5).b(f);
            double d6 = d2 * (double) entityliving.world.random.nextFloat() * 0.5D;

            entityliving1.i(vec3d.x, d6, vec3d.z);
            entityliving1.velocityChanged = true;
        }
    }
}
