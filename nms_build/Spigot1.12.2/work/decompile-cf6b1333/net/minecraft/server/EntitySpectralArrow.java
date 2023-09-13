package net.minecraft.server;

public class EntitySpectralArrow extends EntityArrow {

    public int duration = 200;

    public EntitySpectralArrow(World world) {
        super(world);
    }

    public EntitySpectralArrow(World world, EntityLiving entityliving) {
        super(world, entityliving);
    }

    public EntitySpectralArrow(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public void B_() {
        super.B_();
        if (this.world.isClientSide && !this.inGround) {
            this.world.addParticle(EnumParticle.SPELL_INSTANT, this.locX, this.locY, this.locZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }

    }

    protected ItemStack j() {
        return new ItemStack(Items.SPECTRAL_ARROW);
    }

    protected void a(EntityLiving entityliving) {
        super.a(entityliving);
        MobEffect mobeffect = new MobEffect(MobEffects.GLOWING, this.duration, 0);

        entityliving.addEffect(mobeffect);
    }

    public static void c(DataConverterManager dataconvertermanager) {
        EntityArrow.a(dataconvertermanager, "SpectralArrow");
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("Duration")) {
            this.duration = nbttagcompound.getInt("Duration");
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Duration", this.duration);
    }
}
