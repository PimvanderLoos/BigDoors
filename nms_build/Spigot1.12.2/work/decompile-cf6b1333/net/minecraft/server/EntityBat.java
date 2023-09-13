package net.minecraft.server;

import java.util.Calendar;
import javax.annotation.Nullable;

public class EntityBat extends EntityAmbient {

    private static final DataWatcherObject<Byte> a = DataWatcher.a(EntityBat.class, DataWatcherRegistry.a);
    private BlockPosition b;

    public EntityBat(World world) {
        super(world);
        this.setSize(0.5F, 0.9F);
        this.setAsleep(true);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityBat.a, Byte.valueOf((byte) 0));
    }

    protected float cq() {
        return 0.1F;
    }

    protected float cr() {
        return super.cr() * 0.95F;
    }

    @Nullable
    public SoundEffect F() {
        return this.isAsleep() && this.random.nextInt(4) != 0 ? null : SoundEffects.x;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.z;
    }

    protected SoundEffect cf() {
        return SoundEffects.y;
    }

    public boolean isCollidable() {
        return false;
    }

    protected void C(Entity entity) {}

    protected void cB() {}

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(6.0D);
    }

    public boolean isAsleep() {
        return (((Byte) this.datawatcher.get(EntityBat.a)).byteValue() & 1) != 0;
    }

    public void setAsleep(boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityBat.a)).byteValue();

        if (flag) {
            this.datawatcher.set(EntityBat.a, Byte.valueOf((byte) (b0 | 1)));
        } else {
            this.datawatcher.set(EntityBat.a, Byte.valueOf((byte) (b0 & -2)));
        }

    }

    public void B_() {
        super.B_();
        if (this.isAsleep()) {
            this.motX = 0.0D;
            this.motY = 0.0D;
            this.motZ = 0.0D;
            this.locY = (double) MathHelper.floor(this.locY) + 1.0D - (double) this.length;
        } else {
            this.motY *= 0.6000000238418579D;
        }

    }

    protected void M() {
        super.M();
        BlockPosition blockposition = new BlockPosition(this);
        BlockPosition blockposition1 = blockposition.up();

        if (this.isAsleep()) {
            if (this.world.getType(blockposition1).l()) {
                if (this.random.nextInt(200) == 0) {
                    this.aP = (float) this.random.nextInt(360);
                }

                if (this.world.b(this, 4.0D) != null) {
                    this.setAsleep(false);
                    this.world.a((EntityHuman) null, 1025, blockposition, 0);
                }
            } else {
                this.setAsleep(false);
                this.world.a((EntityHuman) null, 1025, blockposition, 0);
            }
        } else {
            if (this.b != null && (!this.world.isEmpty(this.b) || this.b.getY() < 1)) {
                this.b = null;
            }

            if (this.b == null || this.random.nextInt(30) == 0 || this.b.distanceSquared((double) ((int) this.locX), (double) ((int) this.locY), (double) ((int) this.locZ)) < 4.0D) {
                this.b = new BlockPosition((int) this.locX + this.random.nextInt(7) - this.random.nextInt(7), (int) this.locY + this.random.nextInt(6) - 2, (int) this.locZ + this.random.nextInt(7) - this.random.nextInt(7));
            }

            double d0 = (double) this.b.getX() + 0.5D - this.locX;
            double d1 = (double) this.b.getY() + 0.1D - this.locY;
            double d2 = (double) this.b.getZ() + 0.5D - this.locZ;

            this.motX += (Math.signum(d0) * 0.5D - this.motX) * 0.10000000149011612D;
            this.motY += (Math.signum(d1) * 0.699999988079071D - this.motY) * 0.10000000149011612D;
            this.motZ += (Math.signum(d2) * 0.5D - this.motZ) * 0.10000000149011612D;
            float f = (float) (MathHelper.c(this.motZ, this.motX) * 57.2957763671875D) - 90.0F;
            float f1 = MathHelper.g(f - this.yaw);

            this.bg = 0.5F;
            this.yaw += f1;
            if (this.random.nextInt(100) == 0 && this.world.getType(blockposition1).l()) {
                this.setAsleep(true);
            }
        }

    }

    protected boolean playStepSound() {
        return false;
    }

    public void e(float f, float f1) {}

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    public boolean isIgnoreBlockTrigger() {
        return true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (!this.world.isClientSide && this.isAsleep()) {
                this.setAsleep(false);
            }

            return super.damageEntity(damagesource, f);
        }
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityBat.class);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.datawatcher.set(EntityBat.a, Byte.valueOf(nbttagcompound.getByte("BatFlags")));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setByte("BatFlags", ((Byte) this.datawatcher.get(EntityBat.a)).byteValue());
    }

    public boolean P() {
        BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().b, this.locZ);

        if (blockposition.getY() >= this.world.getSeaLevel()) {
            return false;
        } else {
            int i = this.world.getLightLevel(blockposition);
            byte b0 = 4;

            if (this.a(this.world.ae())) {
                b0 = 7;
            } else if (this.random.nextBoolean()) {
                return false;
            }

            return i > this.random.nextInt(b0) ? false : super.P();
        }
    }

    private boolean a(Calendar calendar) {
        return calendar.get(2) + 1 == 10 && calendar.get(5) >= 20 || calendar.get(2) + 1 == 11 && calendar.get(5) <= 3;
    }

    public float getHeadHeight() {
        return this.length / 2.0F;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.ag;
    }
}
