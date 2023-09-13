package net.minecraft.server;

public class EntityMinecartFurnace extends EntityMinecartAbstract {

    private static final DataWatcherObject<Boolean> c = DataWatcher.a(EntityMinecartFurnace.class, DataWatcherRegistry.h);
    private int d;
    public double a;
    public double b;

    public EntityMinecartFurnace(World world) {
        super(world);
    }

    public EntityMinecartFurnace(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityMinecartAbstract.a(dataconvertermanager, EntityMinecartFurnace.class);
    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.FURNACE;
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityMinecartFurnace.c, Boolean.valueOf(false));
    }

    public void B_() {
        super.B_();
        if (this.d > 0) {
            --this.d;
        }

        if (this.d <= 0) {
            this.a = 0.0D;
            this.b = 0.0D;
        }

        this.l(this.d > 0);
        if (this.j() && this.random.nextInt(4) == 0) {
            this.world.addParticle(EnumParticle.SMOKE_LARGE, this.locX, this.locY + 0.8D, this.locZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }

    }

    protected double p() {
        return 0.2D;
    }

    public void a(DamageSource damagesource) {
        super.a(damagesource);
        if (!damagesource.isExplosion() && this.world.getGameRules().getBoolean("doEntityDrops")) {
            this.a(new ItemStack(Blocks.FURNACE, 1), 0.0F);
        }

    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        super.a(blockposition, iblockdata);
        double d0 = this.a * this.a + this.b * this.b;

        if (d0 > 1.0E-4D && this.motX * this.motX + this.motZ * this.motZ > 0.001D) {
            d0 = (double) MathHelper.sqrt(d0);
            this.a /= d0;
            this.b /= d0;
            if (this.a * this.motX + this.b * this.motZ < 0.0D) {
                this.a = 0.0D;
                this.b = 0.0D;
            } else {
                double d1 = d0 / this.p();

                this.a *= d1;
                this.b *= d1;
            }
        }

    }

    protected void r() {
        double d0 = this.a * this.a + this.b * this.b;

        if (d0 > 1.0E-4D) {
            d0 = (double) MathHelper.sqrt(d0);
            this.a /= d0;
            this.b /= d0;
            double d1 = 1.0D;

            this.motX *= 0.800000011920929D;
            this.motY *= 0.0D;
            this.motZ *= 0.800000011920929D;
            this.motX += this.a * 1.0D;
            this.motZ += this.b * 1.0D;
        } else {
            this.motX *= 0.9800000190734863D;
            this.motY *= 0.0D;
            this.motZ *= 0.9800000190734863D;
        }

        super.r();
    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.COAL && this.d + 3600 <= 32000) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            this.d += 3600;
        }

        this.a = this.locX - entityhuman.locX;
        this.b = this.locZ - entityhuman.locZ;
        return true;
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setDouble("PushX", this.a);
        nbttagcompound.setDouble("PushZ", this.b);
        nbttagcompound.setShort("Fuel", (short) this.d);
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.a = nbttagcompound.getDouble("PushX");
        this.b = nbttagcompound.getDouble("PushZ");
        this.d = nbttagcompound.getShort("Fuel");
    }

    protected boolean j() {
        return ((Boolean) this.datawatcher.get(EntityMinecartFurnace.c)).booleanValue();
    }

    protected void l(boolean flag) {
        this.datawatcher.set(EntityMinecartFurnace.c, Boolean.valueOf(flag));
    }

    public IBlockData x() {
        return (this.j() ? Blocks.LIT_FURNACE : Blocks.FURNACE).getBlockData().set(BlockFurnace.FACING, EnumDirection.NORTH);
    }
}
