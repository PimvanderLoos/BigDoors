package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySkeletonStray extends EntitySkeletonAbstract {

    public EntitySkeletonStray(World world) {
        super(world);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntitySkeletonStray.class);
    }

    public boolean cM() {
        return super.cM() && this.world.h(new BlockPosition(this));
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.aq;
    }

    protected SoundEffect G() {
        return SoundEffects.gQ;
    }

    protected SoundEffect bW() {
        return SoundEffects.gS;
    }

    protected SoundEffect bX() {
        return SoundEffects.gR;
    }

    SoundEffect o() {
        return SoundEffects.gT;
    }

    protected EntityArrow a(float f) {
        EntityArrow entityarrow = super.a(f);

        if (entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow) entityarrow).a(new MobEffect(MobEffects.SLOWER_MOVEMENT, 600));
        }

        return entityarrow;
    }
}
