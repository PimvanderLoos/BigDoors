package net.minecraft.server;

import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;

public class EntityBoat extends Entity {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Float> d = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> e = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> f = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> g = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> an = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private final float[] ao;
    private float ap;
    private float aq;
    private float ar;
    private int as;
    private double at;
    private double au;
    private double av;
    private double aw;
    private double ax;
    private boolean ay;
    private boolean az;
    private boolean aA;
    private boolean aB;
    private double aC;
    private float aD;
    private EntityBoat.EnumStatus aE;
    private EntityBoat.EnumStatus aF;
    private double aG;
    private boolean aH;
    private boolean aI;
    private float aJ;
    private float aK;
    private float aL;

    public EntityBoat(EntityTypes<? extends EntityBoat> entitytypes, World world) {
        super(entitytypes, world);
        this.ao = new float[2];
        this.i = true;
    }

    public EntityBoat(World world, double d0, double d1, double d2) {
        this(EntityTypes.BOAT, world);
        this.setPosition(d0, d1, d2);
        this.setMot(Vec3D.a);
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
    }

    @Override
    protected float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height;
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityBoat.b, 0);
        this.datawatcher.register(EntityBoat.c, 1);
        this.datawatcher.register(EntityBoat.d, 0.0F);
        this.datawatcher.register(EntityBoat.e, EntityBoat.EnumBoatType.OAK.ordinal());
        this.datawatcher.register(EntityBoat.f, false);
        this.datawatcher.register(EntityBoat.g, false);
        this.datawatcher.register(EntityBoat.an, 0);
    }

    @Nullable
    @Override
    public AxisAlignedBB j(Entity entity) {
        return entity.isCollidable() ? entity.getBoundingBox() : null;
    }

    @Nullable
    @Override
    public AxisAlignedBB ay() {
        return this.getBoundingBox();
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public double aY() {
        return -0.1D;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!this.world.isClientSide && !this.dead) {
            this.c(-this.o());
            this.b(10);
            this.setDamage(this.getDamage() + f * 10.0F);
            this.velocityChanged();
            boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).abilities.canInstantlyBuild;

            if (flag || this.getDamage() > 40.0F) {
                if (!flag && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                    this.a((IMaterial) this.g());
                }

                this.die();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public void k(boolean flag) {
        if (!this.world.isClientSide) {
            this.aH = true;
            this.aI = flag;
            if (this.z() == 0) {
                this.d(60);
            }
        }

        this.world.addParticle(Particles.SPLASH, this.locX() + (double) this.random.nextFloat(), this.locY() + 0.7D, this.locZ() + (double) this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (this.random.nextInt(20) == 0) {
            this.world.a(this.locX(), this.locY(), this.locZ(), this.getSoundSplash(), this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
        }

    }

    @Override
    public void collide(Entity entity) {
        if (entity instanceof EntityBoat) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.collide(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.collide(entity);
        }

    }

    public Item g() {
        switch (this.getType()) {
            case OAK:
            default:
                return Items.OAK_BOAT;
            case SPRUCE:
                return Items.SPRUCE_BOAT;
            case BIRCH:
                return Items.BIRCH_BOAT;
            case JUNGLE:
                return Items.JUNGLE_BOAT;
            case ACACIA:
                return Items.ACACIA_BOAT;
            case DARK_OAK:
                return Items.DARK_OAK_BOAT;
        }
    }

    @Override
    public boolean isInteractable() {
        return !this.dead;
    }

    @Override
    public EnumDirection getAdjustedDirection() {
        return this.getDirection().g();
    }

    @Override
    public void tick() {
        this.aF = this.aE;
        this.aE = this.s();
        if (this.aE != EntityBoat.EnumStatus.UNDER_WATER && this.aE != EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
            this.aq = 0.0F;
        } else {
            ++this.aq;
        }

        if (!this.world.isClientSide && this.aq >= 60.0F) {
            this.ejectPassengers();
        }

        if (this.n() > 0) {
            this.b(this.n() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        super.tick();
        this.r();
        if (this.cr()) {
            if (this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof EntityHuman)) {
                this.a(false, false);
            }

            this.v();
            if (this.world.isClientSide) {
                this.x();
                this.world.a((Packet) (new PacketPlayInBoatMove(this.a(0), this.a(1))));
            }

            this.move(EnumMoveType.SELF, this.getMot());
        } else {
            this.setMot(Vec3D.a);
        }

        this.q();

        for (int i = 0; i <= 1; ++i) {
            if (this.a(i)) {
                if (!this.isSilent() && (double) (this.ao[i] % 6.2831855F) <= 0.7853981852531433D && ((double) this.ao[i] + 0.39269909262657166D) % 6.2831854820251465D >= 0.7853981852531433D) {
                    SoundEffect soundeffect = this.h();

                    if (soundeffect != null) {
                        Vec3D vec3d = this.f(1.0F);
                        double d0 = i == 1 ? -vec3d.z : vec3d.z;
                        double d1 = i == 1 ? vec3d.x : -vec3d.x;

                        this.world.playSound((EntityHuman) null, this.locX() + d0, this.locY(), this.locZ() + d1, soundeffect, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                    }
                }

                this.ao[i] = (float) ((double) this.ao[i] + 0.39269909262657166D);
            } else {
                this.ao[i] = 0.0F;
            }
        }

        this.checkBlockCollisions();
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            boolean flag = !this.world.isClientSide && !(this.getRidingPassenger() instanceof EntityHuman);

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = (Entity) list.get(j);

                if (!entity.w(this)) {
                    if (flag && this.getPassengers().size() < 2 && !entity.isPassenger() && entity.getWidth() < this.getWidth() && entity instanceof EntityLiving && !(entity instanceof EntityWaterAnimal) && !(entity instanceof EntityHuman)) {
                        entity.startRiding(this);
                    } else {
                        this.collide(entity);
                    }
                }
            }
        }

    }

    private void q() {
        int i;

        if (this.world.isClientSide) {
            i = this.z();
            if (i > 0) {
                this.aJ += 0.05F;
            } else {
                this.aJ -= 0.1F;
            }

            this.aJ = MathHelper.a(this.aJ, 0.0F, 1.0F);
            this.aL = this.aK;
            this.aK = 10.0F * (float) Math.sin((double) (0.5F * (float) this.world.getTime())) * this.aJ;
        } else {
            if (!this.aH) {
                this.d(0);
            }

            i = this.z();
            if (i > 0) {
                --i;
                this.d(i);
                int j = 60 - i - 1;

                if (j > 0 && i == 0) {
                    this.d(0);
                    Vec3D vec3d = this.getMot();

                    if (this.aI) {
                        this.setMot(vec3d.add(0.0D, -0.7D, 0.0D));
                        this.ejectPassengers();
                    } else {
                        this.setMot(vec3d.x, this.a(EntityHuman.class) ? 2.7D : 0.6D, vec3d.z);
                    }
                }

                this.aH = false;
            }
        }

    }

    @Nullable
    protected SoundEffect h() {
        switch (this.s()) {
            case IN_WATER:
            case UNDER_WATER:
            case UNDER_FLOWING_WATER:
                return SoundEffects.ENTITY_BOAT_PADDLE_WATER;
            case ON_LAND:
                return SoundEffects.ENTITY_BOAT_PADDLE_LAND;
            case IN_AIR:
            default:
                return null;
        }
    }

    private void r() {
        if (this.cr()) {
            this.as = 0;
            this.c(this.locX(), this.locY(), this.locZ());
        }

        if (this.as > 0) {
            double d0 = this.locX() + (this.at - this.locX()) / (double) this.as;
            double d1 = this.locY() + (this.au - this.locY()) / (double) this.as;
            double d2 = this.locZ() + (this.av - this.locZ()) / (double) this.as;
            double d3 = MathHelper.g(this.aw - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.as);
            this.pitch = (float) ((double) this.pitch + (this.ax - (double) this.pitch) / (double) this.as);
            --this.as;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        }
    }

    public void a(boolean flag, boolean flag1) {
        this.datawatcher.set(EntityBoat.f, flag);
        this.datawatcher.set(EntityBoat.g, flag1);
    }

    private EntityBoat.EnumStatus s() {
        EntityBoat.EnumStatus entityboat_enumstatus = this.u();

        if (entityboat_enumstatus != null) {
            this.aC = this.getBoundingBox().maxY;
            return entityboat_enumstatus;
        } else if (this.t()) {
            return EntityBoat.EnumStatus.IN_WATER;
        } else {
            float f = this.k();

            if (f > 0.0F) {
                this.aD = f;
                return EntityBoat.EnumStatus.ON_LAND;
            } else {
                return EntityBoat.EnumStatus.IN_AIR;
            }
        }
    }

    public float i() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.f(axisalignedbb.maxY - this.aG);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        int k1 = k;

        while (k1 < l) {
            float f = 0.0F;
            int l1 = i;

            label35:
            while (true) {
                if (l1 < j) {
                    int i2 = i1;

                    while (true) {
                        if (i2 >= j1) {
                            ++l1;
                            continue label35;
                        }

                        blockposition_mutableblockposition.d(l1, k1, i2);
                        Fluid fluid = this.world.getFluid(blockposition_mutableblockposition);

                        if (fluid.a((Tag) TagsFluid.WATER)) {
                            f = Math.max(f, fluid.getHeight(this.world, blockposition_mutableblockposition));
                        }

                        if (f >= 1.0F) {
                            break;
                        }

                        ++i2;
                    }
                } else if (f < 1.0F) {
                    return (float) blockposition_mutableblockposition.getY() + f;
                }

                ++k1;
                break;
            }
        }

        return (float) (l + 1);
    }

    public float k() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = MathHelper.floor(axisalignedbb1.minX) - 1;
        int j = MathHelper.f(axisalignedbb1.maxX) + 1;
        int k = MathHelper.floor(axisalignedbb1.minY) - 1;
        int l = MathHelper.f(axisalignedbb1.maxY) + 1;
        int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
        int j1 = MathHelper.f(axisalignedbb1.maxZ) + 1;
        VoxelShape voxelshape = VoxelShapes.a(axisalignedbb1);
        float f = 0.0F;
        int k1 = 0;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l1 = i; l1 < j; ++l1) {
            for (int i2 = i1; i2 < j1; ++i2) {
                int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);

                if (j2 != 2) {
                    for (int k2 = k; k2 < l; ++k2) {
                        if (j2 <= 0 || k2 != k && k2 != l - 1) {
                            blockposition_mutableblockposition.d(l1, k2, i2);
                            IBlockData iblockdata = this.world.getType(blockposition_mutableblockposition);

                            if (!(iblockdata.getBlock() instanceof BlockWaterLily) && VoxelShapes.c(iblockdata.getCollisionShape(this.world, blockposition_mutableblockposition).a((double) l1, (double) k2, (double) i2), voxelshape, OperatorBoolean.AND)) {
                                f += iblockdata.getBlock().getFrictionFactor();
                                ++k1;
                            }
                        }
                    }
                }
            }
        }

        return f / (float) k1;
    }

    private boolean t() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.minY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        boolean flag = false;

        this.aC = Double.MIN_VALUE;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockposition_mutableblockposition.d(k1, l1, i2);
                    Fluid fluid = this.world.getFluid(blockposition_mutableblockposition);

                    if (fluid.a((Tag) TagsFluid.WATER)) {
                        float f = (float) l1 + fluid.getHeight(this.world, blockposition_mutableblockposition);

                        this.aC = Math.max((double) f, this.aC);
                        flag |= axisalignedbb.minY < (double) f;
                    }
                }
            }
        }

        return flag;
    }

    @Nullable
    private EntityBoat.EnumStatus u() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.f(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int k1 = i; k1 < j; ++k1) {
            for (int l1 = k; l1 < l; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    blockposition_mutableblockposition.d(k1, l1, i2);
                    Fluid fluid = this.world.getFluid(blockposition_mutableblockposition);

                    if (fluid.a((Tag) TagsFluid.WATER) && d0 < (double) ((float) blockposition_mutableblockposition.getY() + fluid.getHeight(this.world, blockposition_mutableblockposition))) {
                        if (!fluid.isSource()) {
                            return EntityBoat.EnumStatus.UNDER_FLOWING_WATER;
                        }

                        flag = true;
                    }
                }
            }
        }

        return flag ? EntityBoat.EnumStatus.UNDER_WATER : null;
    }

    private void v() {
        double d0 = -0.03999999910593033D;
        double d1 = this.isNoGravity() ? 0.0D : -0.03999999910593033D;
        double d2 = 0.0D;

        this.ap = 0.05F;
        if (this.aF == EntityBoat.EnumStatus.IN_AIR && this.aE != EntityBoat.EnumStatus.IN_AIR && this.aE != EntityBoat.EnumStatus.ON_LAND) {
            this.aC = this.e(1.0D);
            this.setPosition(this.locX(), (double) (this.i() - this.getHeight()) + 0.101D, this.locZ());
            this.setMot(this.getMot().d(1.0D, 0.0D, 1.0D));
            this.aG = 0.0D;
            this.aE = EntityBoat.EnumStatus.IN_WATER;
        } else {
            if (this.aE == EntityBoat.EnumStatus.IN_WATER) {
                d2 = (this.aC - this.locY()) / (double) this.getHeight();
                this.ap = 0.9F;
            } else if (this.aE == EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4D;
                this.ap = 0.9F;
            } else if (this.aE == EntityBoat.EnumStatus.UNDER_WATER) {
                d2 = 0.009999999776482582D;
                this.ap = 0.45F;
            } else if (this.aE == EntityBoat.EnumStatus.IN_AIR) {
                this.ap = 0.9F;
            } else if (this.aE == EntityBoat.EnumStatus.ON_LAND) {
                this.ap = this.aD;
                if (this.getRidingPassenger() instanceof EntityHuman) {
                    this.aD /= 2.0F;
                }
            }

            Vec3D vec3d = this.getMot();

            this.setMot(vec3d.x * (double) this.ap, vec3d.y + d1, vec3d.z * (double) this.ap);
            this.ar *= this.ap;
            if (d2 > 0.0D) {
                Vec3D vec3d1 = this.getMot();

                this.setMot(vec3d1.x, (vec3d1.y + d2 * 0.06153846016296973D) * 0.75D, vec3d1.z);
            }
        }

    }

    private void x() {
        if (this.isVehicle()) {
            float f = 0.0F;

            if (this.ay) {
                --this.ar;
            }

            if (this.az) {
                ++this.ar;
            }

            if (this.az != this.ay && !this.aA && !this.aB) {
                f += 0.005F;
            }

            this.yaw += this.ar;
            if (this.aA) {
                f += 0.04F;
            }

            if (this.aB) {
                f -= 0.005F;
            }

            this.setMot(this.getMot().add((double) (MathHelper.sin(-this.yaw * 0.017453292F) * f), 0.0D, (double) (MathHelper.cos(this.yaw * 0.017453292F) * f)));
            this.a(this.az && !this.ay || this.aA, this.ay && !this.az || this.aA);
        }
    }

    @Override
    public void k(Entity entity) {
        if (this.w(entity)) {
            float f = 0.0F;
            float f1 = (float) ((this.dead ? 0.009999999776482582D : this.aY()) + entity.aX());

            if (this.getPassengers().size() > 1) {
                int i = this.getPassengers().indexOf(entity);

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

            entity.setPosition(this.locX() + vec3d.x, this.locY() + (double) f1, this.locZ() + vec3d.z);
            entity.yaw += this.ar;
            entity.setHeadRotation(entity.getHeadRotation() + this.ar);
            this.a(entity);
            if (entity instanceof EntityAnimal && this.getPassengers().size() > 1) {
                int j = entity.getId() % 2 == 0 ? 90 : 270;

                entity.l(((EntityAnimal) entity).aH + (float) j);
                entity.setHeadRotation(entity.getHeadRotation() + (float) j);
            }

        }
    }

    @Override
    public Vec3D c(EntityLiving entityliving) {
        Vec3D vec3d = a((double) (this.getWidth() * MathHelper.a), (double) entityliving.getWidth(), this.yaw);
        double d0 = this.locX() + vec3d.x;
        double d1 = this.locZ() + vec3d.z;
        BlockPosition blockposition = new BlockPosition(d0, this.getBoundingBox().maxY, d1);
        BlockPosition blockposition1 = blockposition.down();

        if (!this.world.A(blockposition1)) {
            UnmodifiableIterator unmodifiableiterator = entityliving.ei().iterator();

            while (unmodifiableiterator.hasNext()) {
                EntityPose entitypose = (EntityPose) unmodifiableiterator.next();
                AxisAlignedBB axisalignedbb = entityliving.f(entitypose);
                double d2 = this.world.m(blockposition);

                if (DismountUtil.a(d2)) {
                    Vec3D vec3d1 = new Vec3D(d0, (double) blockposition.getY() + d2, d1);

                    if (DismountUtil.a(this.world, entityliving, axisalignedbb.c(vec3d1))) {
                        entityliving.setPose(entitypose);
                        return vec3d1;
                    }
                }

                double d3 = this.world.m(blockposition1);

                if (DismountUtil.a(d3)) {
                    Vec3D vec3d2 = new Vec3D(d0, (double) blockposition1.getY() + d3, d1);

                    if (DismountUtil.a(this.world, entityliving, axisalignedbb.c(vec3d2))) {
                        entityliving.setPose(entitypose);
                        return vec3d2;
                    }
                }
            }
        }

        return super.c(entityliving);
    }

    protected void a(Entity entity) {
        entity.l(this.yaw);
        float f = MathHelper.g(entity.yaw - this.yaw);
        float f1 = MathHelper.a(f, -105.0F, 105.0F);

        entity.lastYaw += f1 - f;
        entity.yaw += f1 - f;
        entity.setHeadRotation(entity.yaw);
    }

    @Override
    protected void saveData(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Type", this.getType().a());
    }

    @Override
    protected void loadData(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("Type", 8)) {
            this.setType(EntityBoat.EnumBoatType.a(nbttagcompound.getString("Type")));
        }

    }

    @Override
    public EnumInteractionResult a(EntityHuman entityhuman, EnumHand enumhand) {
        return entityhuman.ep() ? EnumInteractionResult.PASS : (this.aq < 60.0F ? (!this.world.isClientSide ? (entityhuman.startRiding(this) ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS) : EnumInteractionResult.SUCCESS) : EnumInteractionResult.PASS);
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.aG = this.getMot().y;
        if (!this.isPassenger()) {
            if (flag) {
                if (this.fallDistance > 3.0F) {
                    if (this.aE != EntityBoat.EnumStatus.ON_LAND) {
                        this.fallDistance = 0.0F;
                        return;
                    }

                    this.b(this.fallDistance, 1.0F);
                    if (!this.world.isClientSide && !this.dead) {
                        this.die();
                        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                            int i;

                            for (i = 0; i < 3; ++i) {
                                this.a((IMaterial) this.getType().b());
                            }

                            for (i = 0; i < 2; ++i) {
                                this.a((IMaterial) Items.STICK);
                            }
                        }
                    }
                }

                this.fallDistance = 0.0F;
            } else if (!this.world.getFluid(this.getChunkCoordinates().down()).a((Tag) TagsFluid.WATER) && d0 < 0.0D) {
                this.fallDistance = (float) ((double) this.fallDistance - d0);
            }

        }
    }

    public boolean a(int i) {
        return (Boolean) this.datawatcher.get(i == 0 ? EntityBoat.f : EntityBoat.g) && this.getRidingPassenger() != null;
    }

    public void setDamage(float f) {
        this.datawatcher.set(EntityBoat.d, f);
    }

    public float getDamage() {
        return (Float) this.datawatcher.get(EntityBoat.d);
    }

    public void b(int i) {
        this.datawatcher.set(EntityBoat.b, i);
    }

    public int n() {
        return (Integer) this.datawatcher.get(EntityBoat.b);
    }

    private void d(int i) {
        this.datawatcher.set(EntityBoat.an, i);
    }

    private int z() {
        return (Integer) this.datawatcher.get(EntityBoat.an);
    }

    public void c(int i) {
        this.datawatcher.set(EntityBoat.c, i);
    }

    public int o() {
        return (Integer) this.datawatcher.get(EntityBoat.c);
    }

    public void setType(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.datawatcher.set(EntityBoat.e, entityboat_enumboattype.ordinal());
    }

    public EntityBoat.EnumBoatType getType() {
        return EntityBoat.EnumBoatType.a((Integer) this.datawatcher.get(EntityBoat.e));
    }

    @Override
    protected boolean q(Entity entity) {
        return this.getPassengers().size() < 2 && !this.a((Tag) TagsFluid.WATER);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        List<Entity> list = this.getPassengers();

        return list.isEmpty() ? null : (Entity) list.get(0);
    }

    @Override
    public Packet<?> O() {
        return new PacketPlayOutSpawnEntity(this);
    }

    @Override
    public boolean aE() {
        return this.aE == EntityBoat.EnumStatus.UNDER_WATER || this.aE == EntityBoat.EnumStatus.UNDER_FLOWING_WATER;
    }

    public static enum EnumBoatType {

        OAK(Blocks.OAK_PLANKS, "oak"), SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"), BIRCH(Blocks.BIRCH_PLANKS, "birch"), JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"), ACACIA(Blocks.ACACIA_PLANKS, "acacia"), DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

        private final String g;
        private final Block h;

        private EnumBoatType(Block block, String s) {
            this.g = s;
            this.h = block;
        }

        public String a() {
            return this.g;
        }

        public Block b() {
            return this.h;
        }

        public String toString() {
            return this.g;
        }

        public static EntityBoat.EnumBoatType a(int i) {
            EntityBoat.EnumBoatType[] aentityboat_enumboattype = values();

            if (i < 0 || i >= aentityboat_enumboattype.length) {
                i = 0;
            }

            return aentityboat_enumboattype[i];
        }

        public static EntityBoat.EnumBoatType a(String s) {
            EntityBoat.EnumBoatType[] aentityboat_enumboattype = values();

            for (int i = 0; i < aentityboat_enumboattype.length; ++i) {
                if (aentityboat_enumboattype[i].a().equals(s)) {
                    return aentityboat_enumboattype[i];
                }
            }

            return aentityboat_enumboattype[0];
        }
    }

    public static enum EnumStatus {

        IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER, ON_LAND, IN_AIR;

        private EnumStatus() {}
    }
}
