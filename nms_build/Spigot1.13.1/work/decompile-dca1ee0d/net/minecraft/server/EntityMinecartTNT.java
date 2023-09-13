package net.minecraft.server;

public class EntityMinecartTNT extends EntityMinecartAbstract {

    private int a = -1;

    public EntityMinecartTNT(World world) {
        super(EntityTypes.TNT_MINECART, world);
    }

    public EntityMinecartTNT(World world, double d0, double d1, double d2) {
        super(EntityTypes.TNT_MINECART, world, d0, d1, d2);
    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.TNT;
    }

    public IBlockData z() {
        return Blocks.TNT.getBlockData();
    }

    public void tick() {
        super.tick();
        if (this.a > 0) {
            --this.a;
            this.world.addParticle(Particles.M, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
        } else if (this.a == 0) {
            this.c(this.motX * this.motX + this.motZ * this.motZ);
        }

        if (this.positionChanged) {
            double d0 = this.motX * this.motX + this.motZ * this.motZ;

            if (d0 >= 0.009999999776482582D) {
                this.c(d0);
            }
        }

    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        Entity entity = damagesource.j();

        if (entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;

            if (entityarrow.isBurning()) {
                this.c(entityarrow.motX * entityarrow.motX + entityarrow.motY * entityarrow.motY + entityarrow.motZ * entityarrow.motZ);
            }
        }

        return super.damageEntity(damagesource, f);
    }

    public void a(DamageSource damagesource) {
        double d0 = this.motX * this.motX + this.motZ * this.motZ;

        if (!damagesource.p() && !damagesource.isExplosion() && d0 < 0.009999999776482582D) {
            super.a(damagesource);
            if (!damagesource.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
                this.a((IMaterial) Blocks.TNT);
            }

        } else {
            if (this.a < 0) {
                this.f();
                this.a = this.random.nextInt(20) + this.random.nextInt(20);
            }

        }
    }

    protected void c(double d0) {
        if (!this.world.isClientSide) {
            double d1 = Math.sqrt(d0);

            if (d1 > 5.0D) {
                d1 = 5.0D;
            }

            this.world.explode(this, this.locX, this.locY, this.locZ, (float) (4.0D + this.random.nextDouble() * 1.5D * d1), true);
            this.die();
        }

    }

    public void c(float f, float f1) {
        if (f >= 3.0F) {
            float f2 = f / 10.0F;

            this.c((double) (f2 * f2));
        }

        super.c(f, f1);
    }

    public void a(int i, int j, int k, boolean flag) {
        if (flag && this.a < 0) {
            this.f();
        }

    }

    public void f() {
        this.a = 80;
        if (!this.world.isClientSide) {
            this.world.broadcastEntityEffect(this, (byte) 10);
            if (!this.isSilent()) {
                this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }

    }

    public boolean k() {
        return this.a > -1;
    }

    public float a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return this.k() && (iblockdata.a(TagsBlock.RAILS) || iblockaccess.getType(blockposition.up()).a(TagsBlock.RAILS)) ? 0.0F : super.a(explosion, iblockaccess, blockposition, iblockdata, fluid, f);
    }

    public boolean a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return this.k() && (iblockdata.a(TagsBlock.RAILS) || iblockaccess.getType(blockposition.up()).a(TagsBlock.RAILS)) ? false : super.a(explosion, iblockaccess, blockposition, iblockdata, f);
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("TNTFuse", 99)) {
            this.a = nbttagcompound.getInt("TNTFuse");
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("TNTFuse", this.a);
    }
}
