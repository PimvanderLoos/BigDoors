package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityZombieHusk extends EntityZombie {

    public EntityZombieHusk(World world) {
        super(world);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityZombieHusk.class);
    }

    public boolean P() {
        return super.P() && this.world.h(new BlockPosition(this));
    }

    protected boolean p() {
        return false;
    }

    protected SoundEffect F() {
        return SoundEffects.cY;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.da;
    }

    protected SoundEffect cf() {
        return SoundEffects.cZ;
    }

    protected SoundEffect dm() {
        return SoundEffects.db;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.ar;
    }

    public boolean B(Entity entity) {
        boolean flag = super.B(entity);

        if (flag && this.getItemInMainHand().isEmpty() && entity instanceof EntityLiving) {
            float f = this.world.D(new BlockPosition(this)).b();

            ((EntityLiving) entity).addEffect(new MobEffect(MobEffects.HUNGER, 140 * (int) f));
        }

        return flag;
    }

    protected ItemStack dn() {
        return ItemStack.a;
    }
}
