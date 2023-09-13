package net.minecraft.server;

import java.util.Random;

public class EntitySkeletonStray extends EntitySkeletonAbstract {

    public EntitySkeletonStray(EntityTypes<? extends EntitySkeletonStray> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static boolean a(EntityTypes<EntitySkeletonStray> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return b(entitytypes, worldaccess, enummobspawn, blockposition, random) && (enummobspawn == EnumMobSpawn.SPAWNER || worldaccess.e(blockposition));
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_STRAY_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_STRAY_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_STRAY_DEATH;
    }

    @Override
    SoundEffect eK() {
        return SoundEffects.ENTITY_STRAY_STEP;
    }

    @Override
    protected EntityArrow b(ItemStack itemstack, float f) {
        EntityArrow entityarrow = super.b(itemstack, f);

        if (entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).addEffect(new MobEffect(MobEffects.SLOWER_MOVEMENT, 600));
        }

        return entityarrow;
    }
}
