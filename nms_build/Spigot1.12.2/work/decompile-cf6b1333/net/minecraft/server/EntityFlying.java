package net.minecraft.server;

public abstract class EntityFlying extends EntityInsentient {

    public EntityFlying(World world) {
        super(world);
    }

    public void e(float f, float f1) {}

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    public void a(float f, float f1, float f2) {
        if (this.isInWater()) {
            this.b(f, f1, f2, 0.02F);
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.motX *= 0.800000011920929D;
            this.motY *= 0.800000011920929D;
            this.motZ *= 0.800000011920929D;
        } else if (this.au()) {
            this.b(f, f1, f2, 0.02F);
            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.motX *= 0.5D;
            this.motY *= 0.5D;
            this.motZ *= 0.5D;
        } else {
            float f3 = 0.91F;

            if (this.onGround) {
                f3 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
            }

            float f4 = 0.16277136F / (f3 * f3 * f3);

            this.b(f, f1, f2, this.onGround ? 0.1F * f4 : 0.02F);
            f3 = 0.91F;
            if (this.onGround) {
                f3 = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().b) - 1, MathHelper.floor(this.locZ))).getBlock().frictionFactor * 0.91F;
            }

            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.motX *= (double) f3;
            this.motY *= (double) f3;
            this.motZ *= (double) f3;
        }

        this.aF = this.aG;
        double d0 = this.locX - this.lastX;
        double d1 = this.locZ - this.lastZ;
        float f5 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

        if (f5 > 1.0F) {
            f5 = 1.0F;
        }

        this.aG += (f5 - this.aG) * 0.4F;
        this.aH += this.aG;
    }

    public boolean m_() {
        return false;
    }
}
