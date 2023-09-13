package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityVex extends EntityMonster {

    protected static final DataWatcherObject<Byte> a = DataWatcher.a(EntityVex.class, DataWatcherRegistry.a);
    private EntityInsentient b;
    @Nullable
    private BlockPosition c;
    private boolean bx;
    private int by;

    public EntityVex(World world) {
        super(world);
        this.fireProof = true;
        this.moveController = new EntityVex.c(this);
        this.setSize(0.4F, 0.8F);
        this.b_ = 3;
    }

    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
        super.move(enummovetype, d0, d1, d2);
        this.checkBlockCollisions();
    }

    public void B_() {
        this.noclip = true;
        super.B_();
        this.noclip = false;
        this.setNoGravity(true);
        if (this.bx && --this.by <= 0) {
            this.by = 20;
            this.damageEntity(DamageSource.STARVE, 1.0F);
        }

    }

    protected void r() {
        super.r();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(4, new EntityVex.a());
        this.goalSelector.a(8, new EntityVex.d());
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[] { EntityVex.class}));
        this.targetSelector.a(2, new EntityVex.b(this));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(14.0D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityVex.a, Byte.valueOf((byte) 0));
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityVex.class);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("BoundX")) {
            this.c = new BlockPosition(nbttagcompound.getInt("BoundX"), nbttagcompound.getInt("BoundY"), nbttagcompound.getInt("BoundZ"));
        }

        if (nbttagcompound.hasKey("LifeTicks")) {
            this.a(nbttagcompound.getInt("LifeTicks"));
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.c != null) {
            nbttagcompound.setInt("BoundX", this.c.getX());
            nbttagcompound.setInt("BoundY", this.c.getY());
            nbttagcompound.setInt("BoundZ", this.c.getZ());
        }

        if (this.bx) {
            nbttagcompound.setInt("LifeTicks", this.by);
        }

    }

    public EntityInsentient p() {
        return this.b;
    }

    @Nullable
    public BlockPosition dm() {
        return this.c;
    }

    public void g(@Nullable BlockPosition blockposition) {
        this.c = blockposition;
    }

    private boolean c(int i) {
        byte b0 = ((Byte) this.datawatcher.get(EntityVex.a)).byteValue();

        return (b0 & i) != 0;
    }

    private void a(int i, boolean flag) {
        byte b0 = ((Byte) this.datawatcher.get(EntityVex.a)).byteValue();
        int j;

        if (flag) {
            j = b0 | i;
        } else {
            j = b0 & ~i;
        }

        this.datawatcher.set(EntityVex.a, Byte.valueOf((byte) (j & 255)));
    }

    public boolean dn() {
        return this.c(1);
    }

    public void a(boolean flag) {
        this.a(1, flag);
    }

    public void a(EntityInsentient entityinsentient) {
        this.b = entityinsentient;
    }

    public void a(int i) {
        this.bx = true;
        this.by = i;
    }

    protected SoundEffect F() {
        return SoundEffects.ig;
    }

    protected SoundEffect cf() {
        return SoundEffects.ii;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ij;
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.ay;
    }

    public float aw() {
        return 1.0F;
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        return super.prepare(difficultydamagescaler, groupdataentity);
    }

    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.a(EnumItemSlot.MAINHAND, 0.0F);
    }

    class b extends PathfinderGoalTarget {

        public b(EntityCreature entitycreature) {
            super(entitycreature, false);
        }

        public boolean a() {
            return EntityVex.this.b != null && EntityVex.this.b.getGoalTarget() != null && this.a(EntityVex.this.b.getGoalTarget(), false);
        }

        public void c() {
            EntityVex.this.setGoalTarget(EntityVex.this.b.getGoalTarget());
            super.c();
        }
    }

    class d extends PathfinderGoal {

        public d() {
            this.a(1);
        }

        public boolean a() {
            return !EntityVex.this.getControllerMove().b() && EntityVex.this.random.nextInt(7) == 0;
        }

        public boolean b() {
            return false;
        }

        public void e() {
            BlockPosition blockposition = EntityVex.this.dm();

            if (blockposition == null) {
                blockposition = new BlockPosition(EntityVex.this);
            }

            for (int i = 0; i < 3; ++i) {
                BlockPosition blockposition1 = blockposition.a(EntityVex.this.random.nextInt(15) - 7, EntityVex.this.random.nextInt(11) - 5, EntityVex.this.random.nextInt(15) - 7);

                if (EntityVex.this.world.isEmpty(blockposition1)) {
                    EntityVex.this.moveController.a((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.5D, (double) blockposition1.getZ() + 0.5D, 0.25D);
                    if (EntityVex.this.getGoalTarget() == null) {
                        EntityVex.this.getControllerLook().a((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.5D, (double) blockposition1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    class a extends PathfinderGoal {

        public a() {
            this.a(1);
        }

        public boolean a() {
            return EntityVex.this.getGoalTarget() != null && !EntityVex.this.getControllerMove().b() && EntityVex.this.random.nextInt(7) == 0 ? EntityVex.this.h((Entity) EntityVex.this.getGoalTarget()) > 4.0D : false;
        }

        public boolean b() {
            return EntityVex.this.getControllerMove().b() && EntityVex.this.dn() && EntityVex.this.getGoalTarget() != null && EntityVex.this.getGoalTarget().isAlive();
        }

        public void c() {
            EntityLiving entityliving = EntityVex.this.getGoalTarget();
            Vec3D vec3d = entityliving.f(1.0F);

            EntityVex.this.moveController.a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            EntityVex.this.a(true);
            EntityVex.this.a(SoundEffects.ih, 1.0F, 1.0F);
        }

        public void d() {
            EntityVex.this.a(false);
        }

        public void e() {
            EntityLiving entityliving = EntityVex.this.getGoalTarget();

            if (EntityVex.this.getBoundingBox().c(entityliving.getBoundingBox())) {
                EntityVex.this.B(entityliving);
                EntityVex.this.a(false);
            } else {
                double d0 = EntityVex.this.h((Entity) entityliving);

                if (d0 < 9.0D) {
                    Vec3D vec3d = entityliving.f(1.0F);

                    EntityVex.this.moveController.a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }

        }
    }

    class c extends ControllerMove {

        public c(EntityVex entityvex) {
            super(entityvex);
        }

        public void a() {
            if (this.h == ControllerMove.Operation.MOVE_TO) {
                double d0 = this.b - EntityVex.this.locX;
                double d1 = this.c - EntityVex.this.locY;
                double d2 = this.d - EntityVex.this.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                d3 = (double) MathHelper.sqrt(d3);
                if (d3 < EntityVex.this.getBoundingBox().a()) {
                    this.h = ControllerMove.Operation.WAIT;
                    EntityVex.this.motX *= 0.5D;
                    EntityVex.this.motY *= 0.5D;
                    EntityVex.this.motZ *= 0.5D;
                } else {
                    EntityVex.this.motX += d0 / d3 * 0.05D * this.e;
                    EntityVex.this.motY += d1 / d3 * 0.05D * this.e;
                    EntityVex.this.motZ += d2 / d3 * 0.05D * this.e;
                    if (EntityVex.this.getGoalTarget() == null) {
                        EntityVex.this.yaw = -((float) MathHelper.c(EntityVex.this.motX, EntityVex.this.motZ)) * 57.295776F;
                        EntityVex.this.aN = EntityVex.this.yaw;
                    } else {
                        double d4 = EntityVex.this.getGoalTarget().locX - EntityVex.this.locX;
                        double d5 = EntityVex.this.getGoalTarget().locZ - EntityVex.this.locZ;

                        EntityVex.this.yaw = -((float) MathHelper.c(d4, d5)) * 57.295776F;
                        EntityVex.this.aN = EntityVex.this.yaw;
                    }
                }

            }
        }
    }
}
