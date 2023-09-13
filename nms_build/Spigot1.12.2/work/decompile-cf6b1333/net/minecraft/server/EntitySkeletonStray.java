package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySkeletonStray extends EntitySkeletonAbstract {

    public EntitySkeletonStray(World world) {
        super(world);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntitySkeletonStray.class);
    }

    public boolean P() {
        return super.P() && this.world.h(new BlockPosition(this));
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.aq;
    }

    protected SoundEffect F() {
        return SoundEffects.hR;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.hT;
    }

    protected SoundEffect cf() {
        return SoundEffects.hS;
    }

    SoundEffect p() {
        return SoundEffects.hU;
    }

    protected EntityArrow a(float f) {
        EntityArrow entityarrow = super.a(f);

        if (entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).a(new MobEffect(MobEffects.SLOWER_MOVEMENT, 600));
        }

        return entityarrow;
    }
}
