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

    protected SoundEffect G() {
        super.G();
        return SoundEffects.dW;
    }

    protected SoundEffect bX() {
        super.bX();
        return SoundEffects.dY;
    }

    protected SoundEffect bW() {
        super.bW();
        return SoundEffects.dZ;
    }

    protected void dk() {
        this.a(SoundEffects.dX, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }
}
