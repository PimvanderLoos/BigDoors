package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class EntityBoat extends Entity {

    private static final DataWatcherObject<Integer> a = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Float> c = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> d = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean>[] e = new DataWatcherObject[] { DataWatcher.a(EntityBoat.class, DataWatcherRegistry.h), DataWatcher.a(EntityBoat.class, DataWatcherRegistry.h)};
    private final float[] f;
    private float g;
    private float h;
    private float at;
    private int au;
    private double av;
    private double aw;
    private double ax;
    private double ay;
    private double az;
    private boolean aA;
    private boolean aB;
    private boolean aC;
    private boolean aD;
    private double aE;
    private float aF;
    private EntityBoat.EnumStatus aG;
    private EntityBoat.EnumStatus aH;
    private double aI;

    public EntityBoat(World world) {
        super(world);
        this.f = new float[2];
        this.i = true;
        this.setSize(1.375F, 0.5625F);
    }

    public EntityBoat(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
    }

    protected boolean playStepSound() {
        return false;
    }

    protected void i() {
        this.datawatcher.register(EntityBoat.a, Integer.valueOf(0));
        this.datawatcher.register(EntityBoat.b, Integer.valueOf(1));
        this.datawatcher.register(EntityBoat.c, Float.valueOf(0.0F));
        this.datawatcher.register(EntityBoat.d, Integer.valueOf(EntityBoat.EnumBoatType.OAK.ordinal()));
        DataWatcherObject[] adatawatcherobject = EntityBoat.e;
        int i = adatawatcherobject.length;

        for (int j = 0; j < i; ++j) {
            DataWatcherObject datawatcherobject = adatawatcherobject[j];

            this.datawatcher.register(datawatcherobject, Boolean.valueOf(false));
        }

    }

    @Nullable
    public AxisAlignedBB j(Entity entity) {
        return entity.isCollidable() ? entity.getBoundingBox() : null;
    }

    @Nullable
    public AxisAlignedBB al() {
        return this.getBoundingBox();
    }

    public boolean isCollidable() {
        return true;
    }

    public double aG() {
        return -0.1D;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!this.world.isClientSide && !this.dead) {
            if (damagesource instanceof EntityDamageSourceIndirect && damagesource.getEntity() != null && this.w(damagesource.getEntity())) {
                return false;
            } else {
                this.d(-this.r());
                this.c(10);
                this.setDamage(this.p() + f * 10.0F);
                this.ax();
                boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).abilities.canInstantlyBuild;

                if (flag || this.p() > 40.0F) {
                    if (!flag && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        this.a(this.j(), 1, 0.0F);
                    }

                    this.die();
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public void collide(Entity entity) {
        if (entity instanceof EntityBoat) {
            if (entity.getBoundingBox().b < this.getBoundingBox().e) {
                super.collide(entity);
            }
        } else if (entity.getBoundingBox().b <= this.getBoundingBox().b) {
            super.collide(entity);
        }

    }

    public Item j() {
        switch (this.getType()) {
        case OAK:
        default:
            return Items.aH;

        case SPRUCE:
            return Items.aI;

        case BIRCH:
            return Items.aJ;

        case JUNGLE:
            return Items.aK;

        case ACACIA:
            return Items.aL;

        case DARK_OAK:
            return Items.aM;
        }
    }

    public boolean isInteractable() {
        return !this.dead;
    }

    public EnumDirection bu() {
        return this.getDirection().e();
    }

    public void B_() {
        this.aH = this.aG;
        this.aG = this.u();
        if (this.aG != EntityBoat.EnumStatus.UNDER_WATER && this.aG != EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
            this.h = 0.0F;
        } else {
            ++this.h;
        }

        if (!this.world.isClientSide && this.h >= 60.0F) {
            this.ejectPassengers();
        }

        if (this.q() > 0) {
            this.c(this.q() - 1);
        }

        if (this.p() > 0.0F) {
            this.setDamage(this.p() - 1.0F);
        }

        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        super.B_();
        this.t();
        if (this.bI()) {
            if (this.bF().isEmpty() || !(this.bF().get(0) instanceof EntityHuman)) {
                this.a(false, false);
            }

            this.x();
            if (this.world.isClientSide) {
                this.y();
                this.world.a((Packet) (new PacketPlayInBoatMove(this.a(0), this.a(1))));
            }

            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
        } else {
            this.motX = 0.0D;
            this.motY = 0.0D;
            this.motZ = 0.0D;
        }

        for (int i = 0; i <= 1; ++i) {
            if (this.a(i)) {
                if (!this.isSilent() && (double) (this.f[i] % 6.2831855F) <= 0.7853981852531433D && ((double) this.f[i] + 0.39269909262657166D) % 6.2831854820251465D >= 0.7853981852531433D) {
                    SoundEffect soundeffect = this.k();

                    if (soundeffect != null) {
                        Vec3D vec3d = this.e(1.0F);
                        double d0 = i == 1 ? -vec3d.z : vec3d.z;
                        double d1 = i == 1 ? vec3d.x : -vec3d.x;

                        this.world.a((EntityHuman) null, this.locX + d0, this.locY, this.locZ + d1, soundeffect, this.bK(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                    }
                }

                this.f[i] = (float) ((double) this.f[i] + 0.39269909262657166D);
            } else {
                this.f[i] = 0.0F;
            }
        }

        this.checkBlockCollisions();
        List list = this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            boolean flag = !this.world.isClientSide && !(this.bE() instanceof EntityHuman);

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = (Entity) list.get(j);

                if (!entity.w(this)) {
                    if (flag && this.bF().size() < 2 && !entity.isPassenger() && entity.width < this.width && entity instanceof EntityLiving && !(entity instanceof EntityWaterAnimal) && !(entity instanceof EntityHuman)) {
                        entity.startRiding(this);
                    } else {
                        this.collide(entity);
                    }
                }
            }
        }

    }

    @Nullable
    protected SoundEffect k() {
        switch (this.u()) {
        case IN_WATER:
        case UNDER_WATER:
        case UNDER_FLOWING_WATER:
            return SoundEffects.I;

        case ON_LAND:
            return SoundEffects.H;

        case IN_AIR:
        default:
            return null;
        }
    }

    private void t() {
        if (this.au > 0 && !this.bI()) {
            double d0 = this.locX + (this.av - this.locX) / (double) this.au;
            double d1 = this.locY + (this.aw - this.locY) / (double) this.au;
            double d2 = this.locZ + (this.ax - this.locZ) / (double) this.au;
            double d3 = MathHelper.g(this.ay - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.au);
            this.pitch = (float) ((double) this.pitch + (this.az - (double) this.pitch) / (double) this.au);
            --this.au;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        }
    }

    public void a(boolean flag, boolean flag1) {
        this.datawatcher.set(EntityBoat.e[0], Boolean.valueOf(flag));
        this.datawatcher.set(EntityBoat.e[1], Boolean.valueOf(flag1));
    }

    private EntityBoat.EnumStatus u() {
        EntityBoat.EnumStatus entityboat_enumstatus = this.w();

        if (entityboat_enumstatus != null) {
            this.aE = this.getBoundingBox().e;
            return entityboat_enumstatus;
        } else if (this.v()) {
            return EntityBoat.EnumStatus.IN_WATER;
        } else {
            float f = this.n();

            if (f > 0.0F) {
                this.aF = f;
                return EntityBoat.EnumStatus.ON_LAND;
            } else {
                return EntityBoat.EnumStatus.IN_AIR;
            }
        }
    }

    public float l() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.f(axisalignedbb.d);
        int k = MathHelper.floor(axisalignedbb.e);
        int l = MathHelper.f(axisalignedbb.e - this.aI);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.f(axisalignedbb.f);
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.s();

        try {
            label108:
            for (int k1 = k; k1 < l; ++k1) {
                float f = 0.0F;

                for (int l1 = i; l1 < j; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.f(l1, k1, i2);
                        IBlockData iblockdata = this.world.getType(blockposition_pooledblockposition);

                        if (iblockdata.getMaterial() == Material.WATER) {
                            f = Math.max(f, BlockFluids.g(iblockdata, this.world, blockposition_pooledblockposition));
                        }

                        if (f >= 1.0F) {
                            continue label108;
                        }
                    }
                }

                if (f < 1.0F) {
                    float f1 = (float) blockposition_pooledblockposition.getY() + f;

                    return f1;
                }
            }

            float f2 = (float) (l + 1);

            return f2;
        } finally {
            blockposition_pooledblockposition.t();
        }
    }

    public float n() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.a, axisalignedbb.b - 0.001D, axisalignedbb.c, axisalignedbb.d, axisalignedbb.b, axisalignedbb.f);
        int i = MathHelper.floor(axisalignedbb1.a) - 1;
        int j = MathHelper.f(axisalignedbb1.d) + 1;
        int k = MathHelper.floor(axisalignedbb1.b) - 1;
        int l = MathHelper.f(axisalignedbb1.e) + 1;
        int i1 = MathHelper.floor(axisalignedbb1.c) - 1;
        int j1 = MathHelper.f(axisalignedbb1.f) + 1;
        ArrayList arraylist = Lists.newArrayList();
        float f = 0.0F;
        int k1 = 0;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.s();

        try {
            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);

                    if (j2 != 2) {
                        for (int k2 = k; k2 < l; ++k2) {
                            if (j2 <= 0 || k2 != k && k2 != l - 1) {
                                blockposition_pooledblockposition.f(l1, k2, i2);
                                IBlockData iblockdata = this.world.getType(blockposition_pooledblockposition);

                                iblockdata.a(this.world, blockposition_pooledblockposition, axisalignedbb1, arraylist, this, false);
                                if (!arraylist.isEmpty()) {
                                    f += iblockdata.getBlock().frictionFactor;
                                    ++k1;
                                }

                                arraylist.clear();
                            }
                        }
                    }
                }
            }
        } finally {
            blockposition_pooledblockposition.t();
        }

        return f / (float) k1;
    }

    private boolean v() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.f(axisalignedbb.d);
        int k = MathHelper.floor(axisalignedbb.b);
        int l = MathHelper.f(axisalignedbb.b + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.f(axisalignedbb.f);
        boolean flag = false;

        this.aE = Double.MIN_VALUE;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.s();

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.f(k1, l1, i2);
                        IBlockData iblockdata = this.world.getType(blockposition_pooledblockposition);

                        if (iblockdata.getMaterial() == Material.WATER) {
                            float f = BlockFluids.h(iblockdata, this.world, blockposition_pooledblockposition);

                            this.aE = Math.max((double) f, this.aE);
                            flag |= axisalignedbb.b < (double) f;
                        }
                    }
                }
            }
        } finally {
            blockposition_pooledblockposition.t();
        }

        return flag;
    }

    @Nullable
    private EntityBoat.EnumStatus w() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.e + 0.001D;
        int i = MathHelper.floor(axisalignedbb.a);
        int j = MathHelper.f(axisalignedbb.d);
        int k = MathHelper.floor(axisalignedbb.e);
        int l = MathHelper.f(d0);
        int i1 = MathHelper.floor(axisalignedbb.c);
        int j1 = MathHelper.f(axisalignedbb.f);
        boolean flag = false;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.s();

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.f(k1, l1, i2);
                        IBlockData iblockdata = this.world.getType(blockposition_pooledblockposition);

                        if (iblockdata.getMaterial() == Material.WATER && d0 < (double) BlockFluids.h(iblockdata, this.world, blockposition_pooledblockposition)) {
                            if (((Integer) iblockdata.get(BlockFluids.LEVEL)).intValue() != 0) {
                                EntityBoat.EnumStatus entityboat_enumstatus = EntityBoat.EnumStatus.UNDER_FLOWING_WATER;

                                return entityboat_enumstatus;
                            }

                            flag = true;
                        }
                    }
                }
            }

            return flag ? EntityBoat.EnumStatus.UNDER_WATER : null;
        } finally {
            blockposition_pooledblockposition.t();
        }
    }

    private void x() {
        double d0 = -0.03999999910593033D;
        double d1 = this.isNoGravity() ? 0.0D : -0.03999999910593033D;
        double d2 = 0.0D;

        this.g = 0.05F;
        if (this.aH == EntityBoat.EnumStatus.IN_AIR && this.aG != EntityBoat.EnumStatus.IN_AIR && this.aG != EntityBoat.EnumStatus.ON_LAND) {
            this.aE = this.getBoundingBox().b + (double) this.length;
            this.setPosition(this.locX, (double) (this.l() - this.length) + 0.101D, this.locZ);
            this.motY = 0.0D;
            this.aI = 0.0D;
            this.aG = EntityBoat.EnumStatus.IN_WATER;
        } else {
            if (this.aG == EntityBoat.EnumStatus.IN_WATER) {
                d2 = (this.aE - this.getBoundingBox().b) / (double) this.length;
                this.g = 0.9F;
            } else if (this.aG == EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4D;
                this.g = 0.9F;
            } else if (this.aG == EntityBoat.EnumStatus.UNDER_WATER) {
                d2 = 0.009999999776482582D;
                this.g = 0.45F;
            } else if (this.aG == EntityBoat.EnumStatus.IN_AIR) {
                this.g = 0.9F;
            } else if (this.aG == EntityBoat.EnumStatus.ON_LAND) {
                this.g = this.aF;
                if (this.bE() instanceof EntityHuman) {
                    this.aF /= 2.0F;
                }
            }

            this.motX *= (double) this.g;
            this.motZ *= (double) this.g;
            this.at *= this.g;
            this.motY += d1;
            if (d2 > 0.0D) {
                double d3 = 0.65D;

                this.motY += d2 * 0.06153846016296973D;
                double d4 = 0.75D;

                this.motY *= 0.75D;
            }
        }

    }

    private void y() {
        if (this.isVehicle()) {
            float f = 0.0F;

            if (this.aA) {
                this.at += -1.0F;
            }

            if (this.aB) {
                ++this.at;
            }

            if (this.aB != this.aA && !this.aC && !this.aD) {
                f += 0.005F;
            }

            this.yaw += this.at;
            if (this.aC) {
                f += 0.04F;
            }

            if (this.aD) {
                f -= 0.005F;
            }

            this.motX += (double) (MathHelper.sin(-this.yaw * 0.017453292F) * f);
            this.motZ += (double) (MathHelper.cos(this.yaw * 0.017453292F) * f);
            this.a(this.aB && !this.aA || this.aC, this.aA && !this.aB || this.aC);
        }
    }

    public void k(Entity entity) {
        if (this.w(entity)) {
            float f = 0.0F;
            float f1 = (float) ((this.dead ? 0.009999999776482582D : this.aG()) + entity.aF());

            if (this.bF().size() > 1) {
                int i = this.bF().indexOf(entity);

                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (entity instanceof EntityAnimal) {
                    f = (float) ((double) f + 0.2D);
                }
            }

            Vec3D vec3d = (new Vec3D((double) f, 0.0D, 0.0D)).b(-this.yaw * 0.017453292F - 1.5707964F);

            entity.setPosition(this.locX + vec3d.x, this.locY + (double) f1, this.locZ + vec3d.z);
            entity.yaw += this.at;
            entity.setHeadRotation(entity.getHeadRotation() + this.at);
            this.a(entity);
            if (entity instanceof EntityAnimal && this.bF().size() > 1) {
                int j = entity.getId() % 2 == 0 ? 90 : 270;

                entity.h(((EntityAnimal) entity).aN + (float) j);
                entity.setHeadRotation(entity.getHeadRotation() + (float) j);
            }

        }
    }

    protected void a(Entity entity) {
        entity.h(this.yaw);
        float f = MathHelper.g(entity.yaw - this.yaw);
        float f1 = MathHelper.a(f, -105.0F, 105.0F);

        entity.lastYaw += f1 - f;
        entity.yaw += f1 - f;
        entity.setHeadRotation(entity.yaw);
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Type", this.getType().a());
    }

    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("Type", 8)) {
            this.setType(EntityBoat.EnumBoatType.a(nbttagcompound.getString("Type")));
        }

    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        if (entityhuman.isSneaking()) {
            return false;
        } else {
            if (!this.world.isClientSide && this.h < 60.0F) {
                entityhuman.startRiding(this);
            }

            return true;
        }
    }

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.aI = this.motY;
        if (!this.isPassenger()) {
            if (flag) {
                if (this.fallDistance > 3.0F) {
                    if (this.aG != EntityBoat.EnumStatus.ON_LAND) {
                        this.fallDistance = 0.0F;
                        return;
                    }

                    this.e(this.fallDistance, 1.0F);
                    if (!this.world.isClientSide && !this.dead) {
                        this.die();
                        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
                            int i;

                            for (i = 0; i < 3; ++i) {
                                this.a(new ItemStack(Item.getItemOf(Blocks.PLANKS), 1, this.getType().b()), 0.0F);
                            }

                            for (i = 0; i < 2; ++i) {
                                this.a(Items.STICK, 1, 0.0F);
                            }
                        }
                    }
                }

                this.fallDistance = 0.0F;
            } else if (this.world.getType((new BlockPosition(this)).down()).getMaterial() != Material.WATER && d0 < 0.0D) {
                this.fallDistance = (float) ((double) this.fallDistance - d0);
            }

        }
    }

    public boolean a(int i) {
        return ((Boolean) this.datawatcher.get(EntityBoat.e[i])).booleanValue() && this.bE() != null;
    }

    public void setDamage(float f) {
        this.datawatcher.set(EntityBoat.c, Float.valueOf(f));
    }

    public float p() {
        return ((Float) this.datawatcher.get(EntityBoat.c)).floatValue();
    }

    public void c(int i) {
        this.datawatcher.set(EntityBoat.a, Integer.valueOf(i));
    }

    public int q() {
        return ((Integer) this.datawatcher.get(EntityBoat.a)).intValue();
    }

    public void d(int i) {
        this.datawatcher.set(EntityBoat.b, Integer.valueOf(i));
    }

    public int r() {
        return ((Integer) this.datawatcher.get(EntityBoat.b)).intValue();
    }

    public void setType(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.datawatcher.set(EntityBoat.d, Integer.valueOf(entityboat_enumboattype.ordinal()));
    }

    public EntityBoat.EnumBoatType getType() {
        return EntityBoat.EnumBoatType.a(((Integer) this.datawatcher.get(EntityBoat.d)).intValue());
    }

    protected boolean q(Entity entity) {
        return this.bF().size() < 2;
    }

    @Nullable
    public Entity bE() {
        List list = this.bF();

        return list.isEmpty() ? null : (Entity) list.get(0);
    }

    public static enum EnumBoatType {

        OAK(BlockWood.EnumLogVariant.OAK.a(), "oak"), SPRUCE(BlockWood.EnumLogVariant.SPRUCE.a(), "spruce"), BIRCH(BlockWood.EnumLogVariant.BIRCH.a(), "birch"), JUNGLE(BlockWood.EnumLogVariant.JUNGLE.a(), "jungle"), ACACIA(BlockWood.EnumLogVariant.ACACIA.a(), "acacia"), DARK_OAK(BlockWood.EnumLogVariant.DARK_OAK.a(), "dark_oak");

        private final String g;
        private final int h;

        private EnumBoatType(int i, String s) {
            this.g = s;
            this.h = i;
        }

        public String a() {
            return this.g;
        }

        public int b() {
            return this.h;
        }

        public String toString() {
            return this.g;
        }

        public static EntityBoat.EnumBoatType a(int i) {
            if (i < 0 || i >= values().length) {
                i = 0;
            }

            return values()[i];
        }

        public static EntityBoat.EnumBoatType a(String s) {
            for (int i = 0; i < values().length; ++i) {
                if (values()[i].a().equals(s)) {
                    return values()[i];
                }
            }

            return values()[0];
        }
    }

    public static enum EnumStatus {

        IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER, ON_LAND, IN_AIR;

        private EnumStatus() {}
    }
}
