package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseMule extends EntityHorseChestedAbstract {

    public EntityHorseMule(World world) {
        super(world);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityHorseChestedAbstract.b(dataconvertermanager, EntityHorseMule.class);
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.I;
    }

    protected SoundEffect F() {
        super.F();
        return SoundEffects.ej;
    }

    protected SoundEffect cf() {
        super.cf();
        return SoundEffects.el;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.em;
    }

    protected void dp() {
        this.a(SoundEffects.ek, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }
}
