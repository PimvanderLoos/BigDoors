package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySkeletonStray extends EntitySkeletonAbstract {

    public EntitySkeletonStray(World world) {
        super(EntityTypes.STRAY, world);
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return super.a(generatoraccess, flag) && (flag || generatoraccess.e(new BlockPosition(this)));
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.ax;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_STRAY_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_STRAY_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_STRAY_DEATH;
    }

    SoundEffect l() {
        return SoundEffects.ENTITY_STRAY_STEP;
    }

    protected EntityArrow a(float f) {
        EntityArrow entityarrow = super.a(f);

        if (entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).a(new MobEffect(MobEffects.SLOWER_MOVEMENT, 600));
        }

        return entityarrow;
    }
}
