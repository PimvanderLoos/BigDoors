package net.minecraft.world.entity.monster;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3fa;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.item.ItemCrossbow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3D;

public interface ICrossbow extends IRangedEntity {

    void b(boolean flag);

    void a(EntityLiving entityliving, ItemStack itemstack, IProjectile iprojectile, float f);

    @Nullable
    EntityLiving getGoalTarget();

    void U_();

    default void b(EntityLiving entityliving, float f) {
        EnumHand enumhand = ProjectileHelper.a(entityliving, Items.CROSSBOW);
        ItemStack itemstack = entityliving.b(enumhand);

        if (entityliving.a(Items.CROSSBOW)) {
            ItemCrossbow.a(entityliving.world, entityliving, enumhand, itemstack, f, (float) (14 - entityliving.world.getDifficulty().a() * 4));
        }

        this.U_();
    }

    default void a(EntityLiving entityliving, EntityLiving entityliving1, IProjectile iprojectile, float f, float f1) {
        double d0 = entityliving1.locX() - entityliving.locX();
        double d1 = entityliving1.locZ() - entityliving.locZ();
        double d2 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1);
        double d3 = entityliving1.e(0.3333333333333333D) - iprojectile.locY() + d2 * 0.20000000298023224D;
        Vector3fa vector3fa = this.a(entityliving, new Vec3D(d0, d3, d1), f);

        iprojectile.shoot((double) vector3fa.a(), (double) vector3fa.b(), (double) vector3fa.c(), f1, (float) (14 - entityliving.world.getDifficulty().a() * 4));
        entityliving.playSound(SoundEffects.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (entityliving.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    default Vector3fa a(EntityLiving entityliving, Vec3D vec3d, float f) {
        Vec3D vec3d1 = vec3d.d();
        Vec3D vec3d2 = vec3d1.c(new Vec3D(0.0D, 1.0D, 0.0D));

        if (vec3d2.g() <= 1.0E-7D) {
            vec3d2 = vec3d1.c(entityliving.i(1.0F));
        }

        Quaternion quaternion = new Quaternion(new Vector3fa(vec3d2), 90.0F, true);
        Vector3fa vector3fa = new Vector3fa(vec3d1);

        vector3fa.a(quaternion);
        Quaternion quaternion1 = new Quaternion(vector3fa, f, true);
        Vector3fa vector3fa1 = new Vector3fa(vec3d1);

        vector3fa1.a(quaternion1);
        return vector3fa1;
    }
}
