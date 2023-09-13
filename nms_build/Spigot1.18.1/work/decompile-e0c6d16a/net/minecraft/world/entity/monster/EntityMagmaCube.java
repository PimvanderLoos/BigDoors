package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.phys.Vec3D;

public class EntityMagmaCube extends EntitySlime {

    public EntityMagmaCube(EntityTypes<? extends EntityMagmaCube> entitytypes, World world) {
        super(entitytypes, world);
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
    }

    public static boolean checkMagmaCubeSpawnRules(EntityTypes<EntityMagmaCube> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        return iworldreader.isUnobstructed(this) && !iworldreader.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    public void setSize(int i, boolean flag) {
        super.setSize(i, flag);
        this.getAttribute(GenericAttributes.ARMOR).setBaseValue((double) (i * 3));
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Override
    protected ParticleParam getParticleType() {
        return Particles.FLAME;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return this.isTiny() ? LootTables.EMPTY : this.getType().getDefaultLootTable();
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    protected int getJumpDelay() {
        return super.getJumpDelay() * 4;
    }

    @Override
    protected void decreaseSquish() {
        this.targetSquish *= 0.9F;
    }

    @Override
    protected void jumpFromGround() {
        Vec3D vec3d = this.getDeltaMovement();

        this.setDeltaMovement(vec3d.x, (double) (this.getJumpPower() + (float) this.getSize() * 0.1F), vec3d.z);
        this.hasImpulse = true;
    }

    @Override
    protected void jumpInLiquid(Tag<FluidType> tag) {
        if (tag == TagsFluid.LAVA) {
            Vec3D vec3d = this.getDeltaMovement();

            this.setDeltaMovement(vec3d.x, (double) (0.22F + (float) this.getSize() * 0.05F), vec3d.z);
            this.hasImpulse = true;
        } else {
            super.jumpInLiquid(tag);
        }

    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    protected boolean isDealsDamage() {
        return this.isEffectiveAi();
    }

    @Override
    protected float getAttackDamage() {
        return super.getAttackDamage() + 2.0F;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return this.isTiny() ? SoundEffects.MAGMA_CUBE_HURT_SMALL : SoundEffects.MAGMA_CUBE_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return this.isTiny() ? SoundEffects.MAGMA_CUBE_DEATH_SMALL : SoundEffects.MAGMA_CUBE_DEATH;
    }

    @Override
    protected SoundEffect getSquishSound() {
        return this.isTiny() ? SoundEffects.MAGMA_CUBE_SQUISH_SMALL : SoundEffects.MAGMA_CUBE_SQUISH;
    }

    @Override
    protected SoundEffect getJumpSound() {
        return SoundEffects.MAGMA_CUBE_JUMP;
    }
}
