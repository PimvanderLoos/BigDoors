package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityHorseMule extends EntityHorseChestedAbstract {

    public EntityHorseMule(World world) {
        super(EntityTypes.MULE, world);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.P;
    }

    protected SoundEffect D() {
        super.D();
        return SoundEffects.ENTITY_MULE_AMBIENT;
    }

    protected SoundEffect cs() {
        super.cs();
        return SoundEffects.ENTITY_MULE_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.ENTITY_MULE_HURT;
    }

    protected void dC() {
        this.a(SoundEffects.ENTITY_MULE_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }
}
