package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityZombieHusk extends EntityZombie {

    public EntityZombieHusk(World world) {
        super(world);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityZombieHusk.class);
    }

    public boolean cM() {
        return super.cM() && this.world.h(new BlockPosition(this));
    }

    protected boolean o() {
        return false;
    }

    protected SoundEffect G() {
        return SoundEffects.cS;
    }

    protected SoundEffect bW() {
        return SoundEffects.cU;
    }

    protected SoundEffect bX() {
        return SoundEffects.cT;
    }

    protected SoundEffect di() {
        return SoundEffects.cV;
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

    protected ItemStack dj() {
        return ItemStack.a;
    }
}
