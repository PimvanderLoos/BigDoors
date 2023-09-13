package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityMagmaCube extends EntitySlime {

    public EntityMagmaCube(World world) {
        super(world);
        this.fireProof = true;
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityMagmaCube.class);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    public boolean P() {
        return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public boolean canSpawn() {
        return this.world.a(this.getBoundingBox(), (Entity) this) && this.world.getCubes(this, this.getBoundingBox()).isEmpty() && !this.world.containsLiquid(this.getBoundingBox());
    }

    protected void setSize(int i, boolean flag) {
        super.setSize(i, flag);
        this.getAttributeInstance(GenericAttributes.h).setValue((double) (i * 3));
    }

    public float aw() {
        return 1.0F;
    }

    protected EnumParticle p() {
        return EnumParticle.FLAME;
    }

    protected EntitySlime de() {
        return new EntityMagmaCube(this.world);
    }

    @Nullable
    protected MinecraftKey J() {
        return this.dm() ? LootTables.a : LootTables.ai;
    }

    public boolean isBurning() {
        return false;
    }

    protected int df() {
        return super.df() * 4;
    }

    protected void dg() {
        this.a *= 0.9F;
    }

    protected void cu() {
        this.motY = (double) (0.42F + (float) this.getSize() * 0.1F);
        this.impulse = true;
    }

    protected void cw() {
        this.motY = (double) (0.22F + (float) this.getSize() * 0.05F);
        this.impulse = true;
    }

    public void e(float f, float f1) {}

    protected boolean dh() {
        return true;
    }

    protected int di() {
        return super.di() + 2;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return this.dm() ? SoundEffects.hj : SoundEffects.dW;
    }

    protected SoundEffect cf() {
        return this.dm() ? SoundEffects.hi : SoundEffects.dV;
    }

    protected SoundEffect dj() {
        return this.dm() ? SoundEffects.hk : SoundEffects.dY;
    }

    protected SoundEffect dk() {
        return SoundEffects.dX;
    }
}
