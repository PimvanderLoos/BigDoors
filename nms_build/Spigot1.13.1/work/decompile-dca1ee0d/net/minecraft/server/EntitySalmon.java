package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySalmon extends EntityFish {

    public EntitySalmon(World world) {
        super(EntityTypes.SALMON, world);
        this.setSize(0.7F, 0.4F);
    }

    protected void n() {
        super.n();
        this.goalSelector.a(5, new PathfinderGoalFishSchool(this));
    }

    protected int dy() {
        return 5;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aJ;
    }

    protected ItemStack dA() {
        return new ItemStack(Items.SALMON_BUCKET);
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_SALMON_AMBIENT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_SALMON_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_SALMON_HURT;
    }

    protected SoundEffect dC() {
        return SoundEffects.ENTITY_SALMON_FLOP;
    }
}
