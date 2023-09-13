package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityMagmaCube extends EntitySlime {

    public EntityMagmaCube(World world) {
        super(EntityTypes.MAGMA_CUBE, world);
        this.fireProof = true;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    public boolean a(GeneratorAccess generatoraccess) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public boolean a(IWorldReader iworldreader) {
        return iworldreader.b(this, this.getBoundingBox()) && iworldreader.getCubes(this, this.getBoundingBox()) && !iworldreader.containsLiquid(this.getBoundingBox());
    }

    protected void setSize(int i, boolean flag) {
        super.setSize(i, flag);
        this.getAttributeInstance(GenericAttributes.h).setValue((double) (i * 3));
    }

    public float az() {
        return 1.0F;
    }

    protected ParticleParam l() {
        return Particles.y;
    }

    @Nullable
    protected MinecraftKey G() {
        return this.dz() ? LootTables.a : LootTables.ap;
    }

    public boolean isBurning() {
        return false;
    }

    protected int ds() {
        return super.ds() * 4;
    }

    protected void dt() {
        this.a *= 0.9F;
    }

    protected void cH() {
        this.motY = (double) (0.42F + (float) this.getSize() * 0.1F);
        this.impulse = true;
    }

    protected void c(Tag<FluidType> tag) {
        if (tag == TagsFluid.b) {
            this.motY = (double) (0.22F + (float) this.getSize() * 0.05F);
            this.impulse = true;
        } else {
            super.c(tag);
        }

    }

    public void c(float f, float f1) {}

    protected boolean du() {
        return true;
    }

    protected int dv() {
        return super.dv() + 2;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return this.dz() ? SoundEffects.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_HURT;
    }

    protected SoundEffect cs() {
        return this.dz() ? SoundEffects.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_DEATH;
    }

    protected SoundEffect dw() {
        return this.dz() ? SoundEffects.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_SQUISH;
    }

    protected SoundEffect dx() {
        return SoundEffects.ENTITY_MAGMA_CUBE_JUMP;
    }
}
