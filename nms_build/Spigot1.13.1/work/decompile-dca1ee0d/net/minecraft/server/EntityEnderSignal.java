package net.minecraft.server;

public class EntityEnderSignal extends Entity {

    public double targetX;
    public double targetY;
    public double targetZ;
    public int despawnTimer;
    public boolean shouldDropItem;

    public EntityEnderSignal(World world) {
        super(EntityTypes.EYE_OF_ENDER, world);
        this.setSize(0.25F, 0.25F);
    }

    public EntityEnderSignal(World world, double d0, double d1, double d2) {
        this(world);
        this.despawnTimer = 0;
        this.setPosition(d0, d1, d2);
    }

    protected void x_() {}

    public void a(BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        int i = blockposition.getY();
        double d1 = (double) blockposition.getZ();
        double d2 = d0 - this.locX;
        double d3 = d1 - this.locZ;
        float f = MathHelper.sqrt(d2 * d2 + d3 * d3);

        if (f > 12.0F) {
            this.targetX = this.locX + d2 / (double) f * 12.0D;
            this.targetZ = this.locZ + d3 / (double) f * 12.0D;
            this.targetY = this.locY + 8.0D;
        } else {
            this.targetX = d0;
            this.targetY = (double) i;
            this.targetZ = d1;
        }

        this.despawnTimer = 0;
        this.shouldDropItem = this.random.nextInt(5) > 0;
    }

    public void tick() {
        this.N = this.locX;
        this.O = this.locY;
        this.P = this.locZ;
        super.tick();
        this.locX += this.motX;
        this.locY += this.motY;
        this.locZ += this.motZ;
        float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        this.yaw = (float) (MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);

        for (this.pitch = (float) (MathHelper.c(this.motY, (double) f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
            ;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
        this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
        if (!this.world.isClientSide) {
            double d0 = this.targetX - this.locX;
            double d1 = this.targetZ - this.locZ;
            float f1 = (float) Math.sqrt(d0 * d0 + d1 * d1);
            float f2 = (float) MathHelper.c(d1, d0);
            double d2 = (double) f + (double) (f1 - f) * 0.0025D;

            if (f1 < 1.0F) {
                d2 *= 0.8D;
                this.motY *= 0.8D;
            }

            this.motX = Math.cos((double) f2) * d2;
            this.motZ = Math.sin((double) f2) * d2;
            if (this.locY < this.targetY) {
                this.motY += (1.0D - this.motY) * 0.014999999664723873D;
            } else {
                this.motY += (-1.0D - this.motY) * 0.014999999664723873D;
            }
        }

        float f3 = 0.25F;

        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                this.world.addParticle(Particles.e, this.locX - this.motX * 0.25D, this.locY - this.motY * 0.25D, this.locZ - this.motZ * 0.25D, this.motX, this.motY, this.motZ);
            }
        } else {
            this.world.addParticle(Particles.K, this.locX - this.motX * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, this.locY - this.motY * 0.25D - 0.5D, this.locZ - this.motZ * 0.25D + this.random.nextDouble() * 0.6D - 0.3D, this.motX, this.motY, this.motZ);
        }

        if (!this.world.isClientSide) {
            this.setPosition(this.locX, this.locY, this.locZ);
            ++this.despawnTimer;
            if (this.despawnTimer > 80 && !this.world.isClientSide) {
                this.a(SoundEffects.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
                this.die();
                if (this.shouldDropItem) {
                    this.world.addEntity(new EntityItem(this.world, this.locX, this.locY, this.locZ, new ItemStack(Items.ENDER_EYE)));
                } else {
                    this.world.triggerEffect(2003, new BlockPosition(this), 0);
                }
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {}

    public void a(NBTTagCompound nbttagcompound) {}

    public float az() {
        return 1.0F;
    }

    public boolean bk() {
        return false;
    }
}
