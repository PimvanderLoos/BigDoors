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

    public boolean cM() {
        return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    public boolean canSpawn() {
        return this.world.a(this.getBoundingBox(), (Entity) this) && this.world.getCubes(this, this.getBoundingBox()).isEmpty() && !this.world.containsLiquid(this.getBoundingBox());
    }

    protected void setSize(int i, boolean flag) {
        super.setSize(i, flag);
        this.getAttributeInstance(GenericAttributes.g).setValue((double) (i * 3));
    }

    public float e(float f) {
        return 1.0F;
    }

    protected EnumParticle o() {
        return EnumParticle.FLAME;
    }

    protected EntitySlime da() {
        return new EntityMagmaCube(this.world);
    }

    @Nullable
    protected MinecraftKey J() {
        return this.di() ? LootTables.a : LootTables.ai;
    }

    public boolean isBurning() {
        return false;
    }

    protected int db() {
        return super.db() * 4;
    }

    protected void dc() {
        this.a *= 0.9F;
    }

    protected void cm() {
        this.motY = (double) (0.42F + (float) this.getSize() * 0.1F);
        this.impulse = true;
    }

    protected void co() {
        this.motY = (double) (0.22F + (float) this.getSize() * 0.05F);
        this.impulse = true;
    }

    public void e(float f, float f1) {}

    protected boolean dd() {
        return true;
    }

    protected int de() {
        return super.de() + 2;
    }

    protected SoundEffect bW() {
        return this.di() ? SoundEffects.gi : SoundEffects.dJ;
    }

    protected SoundEffect bX() {
        return this.di() ? SoundEffects.gh : SoundEffects.dI;
    }

    protected SoundEffect df() {
        return this.di() ? SoundEffects.gj : SoundEffects.dL;
    }

    protected SoundEffect dg() {
        return SoundEffects.dK;
    }
}
