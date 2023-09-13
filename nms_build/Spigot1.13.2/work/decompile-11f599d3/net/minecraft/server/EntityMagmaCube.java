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

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public boolean a(IWorldReader iworldreader) {
        return iworldreader.a_(this, this.getBoundingBox()) && iworldreader.getCubes(this, this.getBoundingBox()) && !iworldreader.containsLiquid(this.getBoundingBox());
    }

    public void setSize(int i, boolean flag) {
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
    protected MinecraftKey getDefaultLootTable() {
        return this.dy() ? LootTables.a : LootTables.ap;
    }

    public boolean isBurning() {
        return false;
    }

    protected int dr() {
        return super.dr() * 4;
    }

    protected void ds() {
        this.a *= 0.9F;
    }

    protected void cH() {
        this.motY = (double) (0.42F + (float) this.getSize() * 0.1F);
        this.impulse = true;
    }

    protected void c(Tag<FluidType> tag) {
        if (tag == TagsFluid.LAVA) {
            this.motY = (double) (0.22F + (float) this.getSize() * 0.05F);
            this.impulse = true;
        } else {
            super.c(tag);
        }

    }

    public void c(float f, float f1) {}

    protected boolean dt() {
        return this.cP();
    }

    protected int du() {
        return super.du() + 2;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return this.dy() ? SoundEffects.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_HURT;
    }

    protected SoundEffect cs() {
        return this.dy() ? SoundEffects.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_DEATH;
    }

    protected SoundEffect dv() {
        return this.dy() ? SoundEffects.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEffects.ENTITY_MAGMA_CUBE_SQUISH;
    }

    protected SoundEffect dw() {
        return SoundEffects.ENTITY_MAGMA_CUBE_JUMP;
    }
}
