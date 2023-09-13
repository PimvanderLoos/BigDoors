package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.ItemCrossbow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3D;
import org.joml.Vector3f;

public interface ICrossbow extends IRangedEntity {

    void setChargingCrossbow(boolean flag);

    void shootCrossbowProjectile(EntityLiving entityliving, ItemStack itemstack, IProjectile iprojectile, float f);

    @Nullable
    EntityLiving getTarget();

    void onCrossbowAttackPerformed();

    default void performCrossbowAttack(EntityLiving entityliving, float f) {
        EnumHand enumhand = ProjectileHelper.getWeaponHoldingHand(entityliving, Items.CROSSBOW);
        ItemStack itemstack = entityliving.getItemInHand(enumhand);

        if (entityliving.isHolding(Items.CROSSBOW)) {
            ItemCrossbow.performShooting(entityliving.level, entityliving, enumhand, itemstack, f, (float) (14 - entityliving.level.getDifficulty().getId() * 4));
        }

        this.onCrossbowAttackPerformed();
    }

    default void shootCrossbowProjectile(EntityLiving entityliving, EntityLiving entityliving1, IProjectile iprojectile, float f, float f1) {
        double d0 = entityliving1.getX() - entityliving.getX();
        double d1 = entityliving1.getZ() - entityliving.getZ();
        double d2 = Math.sqrt(d0 * d0 + d1 * d1);
        double d3 = entityliving1.getY(0.3333333333333333D) - iprojectile.getY() + d2 * 0.20000000298023224D;
        Vector3f vector3f = this.getProjectileShotVector(entityliving, new Vec3D(d0, d3, d1), f);

        iprojectile.shoot((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), f1, (float) (14 - entityliving.level.getDifficulty().getId() * 4));
        entityliving.playSound(SoundEffects.CROSSBOW_SHOOT, 1.0F, 1.0F / (entityliving.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    default Vector3f getProjectileShotVector(EntityLiving entityliving, Vec3D vec3d, float f) {
        Vector3f vector3f = vec3d.toVector3f().normalize();
        Vector3f vector3f1 = (new Vector3f(vector3f)).cross(new Vector3f(0.0F, 1.0F, 0.0F));

        if ((double) vector3f1.lengthSquared() <= 1.0E-7D) {
            Vec3D vec3d1 = entityliving.getUpVector(1.0F);

            vector3f1 = (new Vector3f(vector3f)).cross(vec3d1.toVector3f());
        }

        Vector3f vector3f2 = (new Vector3f(vector3f)).rotateAxis(1.5707964F, vector3f1.x, vector3f1.y, vector3f1.z);

        return (new Vector3f(vector3f)).rotateAxis(f * 0.017453292F, vector3f2.x, vector3f2.y, vector3f2.z);
    }
}
