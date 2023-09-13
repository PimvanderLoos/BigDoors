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

    protected SoundEffect G() {
        super.G();
        return SoundEffects.az;
    }

    protected SoundEffect bX() {
        super.bX();
        return SoundEffects.aC;
    }

    protected SoundEffect bW() {
        super.bW();
        return SoundEffects.aD;
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.dG() && ((EntityHorseAbstract) entityanimal).dG());
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        Object object = entityageable instanceof EntityHorse ? new EntityHorseMule(this.world) : new EntityHorseDonkey(this.world);

        this.a(entityageable, (EntityHorseAbstract) object);
        return (EntityAgeable) object;
    }
}
