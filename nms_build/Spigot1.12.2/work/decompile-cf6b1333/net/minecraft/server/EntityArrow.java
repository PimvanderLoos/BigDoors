package net.minecraft.server;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import javax.annotation.Nullable;

public abstract class EntityArrow extends Entity implements IProjectile {

    private static final Predicate<Entity> f = Predicates.and(new Predicate[] { IEntitySelector.e, IEntitySelector.a, new Predicate() {
        public boolean a(@Nullable Entity entity) {
            return entity.isInteractable();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((Entity) object);
        }
    }});
    private static final DataWatcherObject<Byte> g = DataWatcher.a(EntityArrow.class, DataWatcherRegistry.a);
    private int h;
    private int at;
    private int au;
    private Block av;
    private int aw;
    public boolean inGround;
    protected int b;
    public EntityArrow.PickupStatus fromPlayer;
    public int shake;
    public Entity shooter;
    private int ax;
    private int ay;
    private double damage;
    public int knockbackStrength;

    public EntityArrow(World world) {
        super(world);
        this.h = -1;
        this.at = -1;
        this.au = -1;
        this.fromPlayer = EntityArrow.PickupStatus.DISALLOWED;
        this.damage = 2.0D;
        this.setSize(0.5F, 0.5F);
    }

    public EntityArrow(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
    }

    public EntityArrow(World world, EntityLiving entityliving) {
        this(world, entityliving.locX, entityliving.locY + (double) entityliving.getHeadHeight() - 0.10000000149011612D, entityliving.locZ);
        this.shooter = entityliving;
        if (entityliving instanceof EntityHuman) {
            this.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
        }

    }

    protected void i() {
        this.datawatcher.register(EntityArrow.g, Byte.valueOf((byte) 0));
    }

    public void a(Entity entity, float f, float f1, float f2, float f3, float f4) {
        float f5 = -MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);
        float f6 = -MathHelper.sin(f * 0.017453292F);
        float f7 = MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f * 0.017453292F);

        this.shoot((double) f5, (double) f6, (double) f7, f3, f4);
        this.motX += entity.motX;
        this.motZ += entity.motZ;
        if (!entity.onGround) {
            this.motY += entity.motY;
        }

    }

    public void shoot(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

        d0 /= (double) f2;
        d1 /= (double) f2;
        d2 /= (double) f2;
        d0 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d1 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d2 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d0 *= (double) f;
        d1 *= (double) f;
        d2 *= (double) f;
        this.motX = d0;
        this.motY = d1;
        this.motZ = d2;
        float f3 = MathHelper.sqrt(d0 * d0 + d2 * d2);

        this.yaw = (float) (MathHelper.c(d0, d2) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.c(d1, (double) f3) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.ax = 0;
    }

    public void B_() {
        super.B_();
        if (this.lastPitch == 0.0F && this.lastYaw == 0.0F) {
            float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

            this.yaw = (float) (MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);
            this.pitch = (float) (MathHelper.c(this.motY, (double) f) * 57.2957763671875D);
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;
        }

        BlockPosition blockposition = new BlockPosition(this.h, this.at, this.au);
        IBlockData iblockdata = this.world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (iblockdata.getMaterial() != Material.AIR) {
            AxisAlignedBB axisalignedbb = iblockdata.d(this.world, blockposition);

            if (axisalignedbb != Block.k && axisalignedbb.a(blockposition).b(new Vec3D(this.locX, this.locY, this.locZ))) {
                this.inGround = true;
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            int i = block.toLegacyData(iblockdata);

            if ((block != this.av || i != this.aw) && !this.world.a(this.getBoundingBox().g(0.05D))) {
                this.inGround = false;
                this.motX *= (double) (this.random.nextFloat() * 0.2F);
                this.motY *= (double) (this.random.nextFloat() * 0.2F);
                this.motZ *= (double) (this.random.nextFloat() * 0.2F);
                this.ax = 0;
                this.ay = 0;
            } else {
                ++this.ax;
                if (this.ax >= 1200) {
                    this.die();
                }
            }

            ++this.b;
        } else {
            this.b = 0;
            ++this.ay;
            Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
            Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, false, true, false);

            vec3d = new Vec3D(this.locX, this.locY, this.locZ);
            vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            if (movingobjectposition != null) {
                vec3d1 = new Vec3D(movingobjectposition.pos.x, movingobjectposition.pos.y, movingobjectposition.pos.z);
            }

            Entity entity = this.a(vec3d, vec3d1);

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            if (movingobjectposition != null && movingobjectposition.entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) movingobjectposition.entity;

                if (this.shooter instanceof EntityHuman && !((EntityHuman) this.shooter).a(entityhuman)) {
                    movingobjectposition = null;
                }
            }

            if (movingobjectposition != null) {
                this.a(movingobjectposition);
            }

            if (this.isCritical()) {
                for (int j = 0; j < 4; ++j) {
                    this.world.addParticle(EnumParticle.CRIT, this.locX + this.motX * (double) j / 4.0D, this.locY + this.motY * (double) j / 4.0D, this.locZ + this.motZ * (double) j / 4.0D, -this.motX, -this.motY + 0.2D, -this.motZ, new int[0]);
                }
            }

            this.locX += this.motX;
            this.locY += this.motY;
            this.locZ += this.motZ;
            float f1 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

            this.yaw = (float) (MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);

            for (this.pitch = (float) (MathHelper.c(this.motY, (double) f1) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
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
            float f2 = 0.99F;
            float f3 = 0.05F;

            if (this.isInWater()) {
                for (int k = 0; k < 4; ++k) {
                    float f4 = 0.25F;

                    this.world.addParticle(EnumParticle.WATER_BUBBLE, this.locX - this.motX * 0.25D, this.locY - this.motY * 0.25D, this.locZ - this.motZ * 0.25D, this.motX, this.motY, this.motZ, new int[0]);
                }

                f2 = 0.6F;
            }

            if (this.an()) {
                this.extinguish();
            }

            this.motX *= (double) f2;
            this.motY *= (double) f2;
            this.motZ *= (double) f2;
            if (!this.isNoGravity()) {
                this.motY -= 0.05000000074505806D;
            }

            this.setPosition(this.locX, this.locY, this.locZ);
            this.checkBlockCollisions();
        }
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        Entity entity = movingobjectposition.entity;

        if (entity != null) {
            float f = MathHelper.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);
            int i = MathHelper.f((double) f * this.damage);

            if (this.isCritical()) {
                i += this.random.nextInt(i / 2 + 2);
            }

            DamageSource damagesource;

            if (this.shooter == null) {
                damagesource = DamageSource.arrow(this, this);
            } else {
                damagesource = DamageSource.arrow(this, this.shooter);
            }

            if (this.isBurning() && !(entity instanceof EntityEnderman)) {
                entity.setOnFire(5);
            }

            if (entity.damageEntity(damagesource, (float) i)) {
                if (entity instanceof EntityLiving) {
                    EntityLiving entityliving = (EntityLiving) entity;

                    if (!this.world.isClientSide) {
                        entityliving.setArrowCount(entityliving.getArrowCount() + 1);
                    }

                    if (this.knockbackStrength > 0) {
                        float f1 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

                        if (f1 > 0.0F) {
                            entityliving.f(this.motX * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1, 0.1D, this.motZ * (double) this.knockbackStrength * 0.6000000238418579D / (double) f1);
                        }
                    }

                    if (this.shooter instanceof EntityLiving) {
                        EnchantmentManager.a(entityliving, this.shooter);
                        EnchantmentManager.b((EntityLiving) this.shooter, (Entity) entityliving);
                    }

                    this.a(entityliving);
                    if (this.shooter != null && entityliving != this.shooter && entityliving instanceof EntityHuman && this.shooter instanceof EntityPlayer) {
                        ((EntityPlayer) this.shooter).playerConnection.sendPacket(new PacketPlayOutGameStateChange(6, 0.0F));
                    }
                }

                this.a(SoundEffects.u, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                if (!(entity instanceof EntityEnderman)) {
                    this.die();
                }
            } else {
                this.motX *= -0.10000000149011612D;
                this.motY *= -0.10000000149011612D;
                this.motZ *= -0.10000000149011612D;
                this.yaw += 180.0F;
                this.lastYaw += 180.0F;
                this.ay = 0;
                if (!this.world.isClientSide && this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ < 0.0010000000474974513D) {
                    if (this.fromPlayer == EntityArrow.PickupStatus.ALLOWED) {
                        this.a(this.j(), 0.1F);
                    }

                    this.die();
                }
            }
        } else {
            BlockPosition blockposition = movingobjectposition.a();

            this.h = blockposition.getX();
            this.at = blockposition.getY();
            this.au = blockposition.getZ();
            IBlockData iblockdata = this.world.getType(blockposition);

            this.av = iblockdata.getBlock();
            this.aw = this.av.toLegacyData(iblockdata);
            this.motX = (double) ((float) (movingobjectposition.pos.x - this.locX));
            this.motY = (double) ((float) (movingobjectposition.pos.y - this.locY));
            this.motZ = (double) ((float) (movingobjectposition.pos.z - this.locZ));
            float f2 = MathHelper.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);

            this.locX -= this.motX / (double) f2 * 0.05000000074505806D;
            this.locY -= this.motY / (double) f2 * 0.05000000074505806D;
            this.locZ -= this.motZ / (double) f2 * 0.05000000074505806D;
            this.a(SoundEffects.u, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.shake = 7;
            this.setCritical(false);
            if (iblockdata.getMaterial() != Material.AIR) {
                this.av.a(this.world, blockposition, iblockdata, (Entity) this);
            }
        }

    }

    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
        super.move(enummovetype, d0, d1, d2);
        if (this.inGround) {
            this.h = MathHelper.floor(this.locX);
            this.at = MathHelper.floor(this.locY);
            this.au = MathHelper.floor(this.locZ);
        }

    }

    protected void a(EntityLiving entityliving) {}

    @Nullable
    protected Entity a(Vec3D vec3d, Vec3D vec3d1) {
        Entity entity = null;
        List list = this.world.getEntities(this, this.getBoundingBox().b(this.motX, this.motY, this.motZ).g(1.0D), EntityArrow.f);
        double d0 = 0.0D;

        for (int i = 0; i < list.size(); ++i) {
            Entity entity1 = (Entity) list.get(i);

            if (entity1 != this.shooter || this.ay >= 5) {
                AxisAlignedBB axisalignedbb = entity1.getBoundingBox().g(0.30000001192092896D);
                MovingObjectPosition movingobjectposition = axisalignedbb.b(vec3d, vec3d1);

                if (movingobjectposition != null) {
                    double d1 = vec3d.distanceSquared(movingobjectposition.pos);

                    if (d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity;
    }

    public static void a(DataConverterManager dataconvertermanager, String s) {}

    public static void a(DataConverterManager dataconvertermanager) {
        a(dataconvertermanager, "Arrow");
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("xTile", this.h);
        nbttagcompound.setInt("yTile", this.at);
        nbttagcompound.setInt("zTile", this.au);
        nbttagcompound.setShort("life", (short) this.ax);
        MinecraftKey minecraftkey = (MinecraftKey) Block.REGISTRY.b(this.av);

        nbttagcompound.setString("inTile", minecraftkey == null ? "" : minecraftkey.toString());
        nbttagcompound.setByte("inData", (byte) this.aw);
        nbttagcompound.setByte("shake", (byte) this.shake);
        nbttagcompound.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        nbttagcompound.setByte("pickup", (byte) this.fromPlayer.ordinal());
        nbttagcompound.setDouble("damage", this.damage);
        nbttagcompound.setBoolean("crit", this.isCritical());
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.h = nbttagcompound.getInt("xTile");
        this.at = nbttagcompound.getInt("yTile");
        this.au = nbttagcompound.getInt("zTile");
        this.ax = nbttagcompound.getShort("life");
        if (nbttagcompound.hasKeyOfType("inTile", 8)) {
            this.av = Block.getByName(nbttagcompound.getString("inTile"));
        } else {
            this.av = Block.getById(nbttagcompound.getByte("inTile") & 255);
        }

        this.aw = nbttagcompound.getByte("inData") & 255;
        this.shake = nbttagcompound.getByte("shake") & 255;
        this.inGround = nbttagcompound.getByte("inGround") == 1;
        if (nbttagcompound.hasKeyOfType("damage", 99)) {
            this.damage = nbttagcompound.getDouble("damage");
        }

        if (nbttagcompound.hasKeyOfType("pickup", 99)) {
            this.fromPlayer = EntityArrow.PickupStatus.a(nbttagcompound.getByte("pickup"));
        } else if (nbttagcompound.hasKeyOfType("player", 99)) {
            this.fromPlayer = nbttagcompound.getBoolean("player") ? EntityArrow.PickupStatus.ALLOWED : EntityArrow.PickupStatus.DISALLOWED;
        }

        this.setCritical(nbttagcompound.getBoolean("crit"));
    }

    public void d(EntityHuman entityhuman) {
        if (!this.world.isClientSide && this.inGround && this.shake <= 0) {
            boolean flag = this.fromPlayer == EntityArrow.PickupStatus.ALLOWED || this.fromPlayer == EntityArrow.PickupStatus.CREATIVE_ONLY && entityhuman.abilities.canInstantlyBuild;

            if (this.fromPlayer == EntityArrow.PickupStatus.ALLOWED && !entityhuman.inventory.pickup(this.j())) {
                flag = false;
            }

            if (flag) {
                entityhuman.receive(this, 1);
                this.die();
            }

        }
    }

    protected abstract ItemStack j();

    protected boolean playStepSound() {
        return false;
    }

    public void c(double d0) {
        this.damage = d0;
    }

    public double k() {
        return this.damage;
    }

    public void setKnockbackStrength(int i) {
        this.knockbackStrength = i;
    }

    public boolean bd() {
        return false;
    }

    public float getHeadHeight() {
        return 0.0F;
    }

    public void setCritical(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityArrow.g)).byteValue();

        if (flag) {
            this.datawatcher.set(EntityArrow.g, Byte.valueOf((byte) (b0 | 1)));
        } else {
            this.datawatcher.set(EntityArrow.g, Byte.valueOf((byte) (b0 & -2)));
        }

    }

    public boolean isCritical() {
        byte b0 = ((Byte) this.datawatcher.get(EntityArrow.g)).byteValue();

        return (b0 & 1) != 0;
    }

    public void a(EntityLiving entityliving, float f) {
        int i = EnchantmentManager.a(Enchantments.ARROW_DAMAGE, entityliving);
        int j = EnchantmentManager.a(Enchantments.ARROW_KNOCKBACK, entityliving);

        this.c((double) (f * 2.0F) + this.random.nextGaussian() * 0.25D + (double) ((float) this.world.getDifficulty().a() * 0.11F));
        if (i > 0) {
            this.c(this.k() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            this.setKnockbackStrength(j);
        }

        if (EnchantmentManager.a(Enchantments.ARROW_FIRE, entityliving) > 0) {
            this.setOnFire(100);
        }

    }

    public static enum PickupStatus {

        DISALLOWED, ALLOWED, CREATIVE_ONLY;

        private PickupStatus() {}

        public static EntityArrow.PickupStatus a(int i) {
            if (i < 0 || i > values().length) {
                i = 0;
            }

            return values()[i];
        }
    }
}
