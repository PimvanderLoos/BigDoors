package net.minecraft.server;

import java.util.Random;

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
    public float aQ() {
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

        this.setMot(vec3d.x, (double) (this.dI() + (float) this.getSize() * 0.1F), vec3d.z);
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
