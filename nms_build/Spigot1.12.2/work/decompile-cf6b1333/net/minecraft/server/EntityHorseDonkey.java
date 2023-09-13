package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseDonkey extends EntityHorseChestedAbstract {

    public EntityHorseDonkey(World world) {
        super(world);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityHorseChestedAbstract.b(dataconvertermanager, EntityHorseDonkey.class);
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.H;
    }

    protected SoundEffect F() {
        super.F();
        return SoundEffects.aC;
    }

    protected SoundEffect cf() {
        super.cf();
        return SoundEffects.aF;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.aG;
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.dL() && ((EntityHorseAbstract) entityanimal).dL());
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        Object object = entityageable instanceof EntityHorse ? new EntityHorseMule(this.world) : new EntityHorseDonkey(this.world);

        this.a(entityageable, (EntityHorseAbstract) object);
        return (EntityAgeable) object;
    }
}
