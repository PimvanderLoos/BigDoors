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
import net.minecraft.world.entity.Entity;
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

    public static AttributeProvider.Builder m() {
        return EntityMonster.eR().a(GenericAttributes.MOVEMENT_SPEED, 0.20000000298023224D);
    }

    public static boolean b(EntityTypes<EntityMagmaCube> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.j((Entity) this) && !iworldreader.containsLiquid(this.getBoundingBox());
    }

    @Override
    public void setSize(int i, boolean flag) {
        super.setSize(i, flag);
        this.getAttributeInstance(GenericAttributes.ARMOR).setValue((double) (i * 3));
    }

    @Override
    public float aR() {
        return 1.0F;
    }

    @Override
    protected ParticleParam eI() {
        return Particles.FLAME;
    }

    @Override
    protected MinecraftKey getDefaultLootTable() {
        return this.eQ() ? LootTables.a : this.getEntityType().i();
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    protected int eJ() {
        return super.eJ() * 4;
    }

    @Override
    protected void eK() {
        this.b *= 0.9F;
    }

    @Override
    protected void jump() {
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x, (double) (this.dJ() + (float) this.getSize() * 0.1F), vec3d.z);
        this.impulse = true;
    }

    @Override
    protected void c(Tag<FluidType> tag) {
        if (tag == TagsFluid.LAVA) {
            Vec3D vec3d = this.getMot();

            this.setMot(vec3d.x, (double) (0.22F + (float) this.getSize() * 0.05F), vec3d.z);
            this.impulse = true;
        } else {
            super.c(tag);
        }

    }

    @Override
    public boolean b(float f, float f1) {
        return false;
    }

    @Override
    protected boolean eL() {
        return this.doAITick();
    }

    @Override
    protected float eM() {
        return super.eM() + 2.0F;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.eQ() ? SoundEffects.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.eQ() ? SoundEffects.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_DEATH;
    }

    @Override
    protected SoundEffect getSoundSquish() {
        return this.eQ() ? SoundEffects.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_SQUISH;
    }

    @Override
    protected SoundEffect getSoundJump() {
        return SoundEffects.ENTITY_MAGMA_CUBE_JUMP;
    }
}
