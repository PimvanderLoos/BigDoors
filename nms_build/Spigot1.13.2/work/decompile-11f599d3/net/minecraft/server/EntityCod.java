package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityCod extends EntityFishSchool {

    public EntityCod(World world) {
        super(EntityTypes.COD, world);
        this.setSize(0.5F, 0.3F);
    }

    protected ItemStack l() {
        return new ItemStack(Items.COD_BUCKET);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aK;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_COD_AMBIENT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_COD_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_COD_HURT;
    }

    protected SoundEffect dz() {
        return SoundEffects.ENTITY_COD_FLOP;
    }
}
