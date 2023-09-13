package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityRabbit extends EntityAnimal {

    private static final DataWatcherObject<Integer> bx = DataWatcher.a(EntityRabbit.class, DataWatcherRegistry.b);
    private int by;
    private int bz;
    private boolean bB;
    private int bC;
    private int bD;

    public EntityRabbit(World world) {
        super(world);
        this.setSize(0.4F, 0.5F);
        this.g = new EntityRabbit.ControllerJumpRabbit(this);
        this.moveController = new EntityRabbit.ControllerMoveRabbit(this);
        this.c(0.0D);
    }

    protected void r() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityRabbit.PathfinderGoalRabbitPanic(this, 2.2D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, Items.CARROT, false));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, Items.GOLDEN_CARROT, false));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, Item.getItemOf(Blocks.YELLOW_FLOWER), false));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget(this, EntityHuman.class, 8.0F, 2.2D, 2.2D));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget(this, EntityWolf.class, 10.0F, 2.2D, 2.2D));
        this.goalSelector.a(4, new EntityRabbit.PathfinderGoalRabbitAvoidTarget(this, EntityMonster.class, 4.0F, 2.2D, 2.2D));
        this.goalSelector.a(5, new EntityRabbit.PathfinderGoalEatCarrots(this));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
    }

    protected float ct() {
        if (!this.positionChanged && (!this.moveController.b() || this.moveController.e() <= this.locY + 0.5D)) {
            PathEntity pathentity = this.navigation.l();

            if (pathentity != null && pathentity.e() < pathentity.d()) {
                Vec3D vec3d = pathentity.a((Entity) this);

                if (vec3d.y > this.locY + 0.5D) {
                    return 0.5F;
                }
            }

            return this.moveController.c() <= 0.6D ? 0.2F : 0.3F;
        } else {
            return 0.5F;
        }
    }

    protected void cu() {
        super.cu();
        double d0 = this.moveController.c();

        if (d0 > 0.0D) {
            double d1 = this.motX * this.motX + this.motZ * this.motZ;

            if (d1 < 0.010000000000000002D) {
                this.b(0.0F, 0.0F, 1.0F, 0.1F);
            }
        }

        if (!this.world.isClientSide) {
            this.world.broadcastEntityEffect(this, (byte) 1);
        }

    }

    public void c(double d0) {
        this.getNavigation().a(d0);
        this.moveController.a(this.moveController.d(), this.moveController.e(), this.moveController.f(), d0);
    }

    public void l(boolean flag) {
        super.l(flag);
        if (flag) {
            this.a(this.dm(), this.cq(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }

    }

    public void dl() {
        this.l(true);
        this.bz = 10;
        this.by = 0;
    }

    protected void i() {
        super.i();
        this.datawatcher.register(EntityRabbit.bx, Integer.valueOf(0));
    }

    public void M() {
        if (this.bC > 0) {
            --this.bC;
        }

        if (this.bD > 0) {
            this.bD -= this.random.nextInt(3);
            if (this.bD < 0) {
                this.bD = 0;
            }
        }

        if (this.onGround) {
            if (!this.bB) {
                this.l(false);
                this.dv();
            }

            if (this.getRabbitType() == 99 && this.bC == 0) {
                EntityLiving entityliving = this.getGoalTarget();

                if (entityliving != null && this.h(entityliving) < 16.0D) {
                    this.a(entityliving.locX, entityliving.locZ);
                    this.moveController.a(entityliving.locX, entityliving.locY, entityliving.locZ, this.moveController.c());
                    this.dl();
                    this.bB = true;
                }
            }

            EntityRabbit.ControllerJumpRabbit entityrabbit_controllerjumprabbit = (EntityRabbit.ControllerJumpRabbit) this.g;

            if (!entityrabbit_controllerjumprabbit.c()) {
                if (this.moveController.b() && this.bC == 0) {
                    PathEntity pathentity = this.navigation.l();
                    Vec3D vec3d = new Vec3D(this.moveController.d(), this.moveController.e(), this.moveController.f());

                    if (pathentity != null && pathentity.e() < pathentity.d()) {
                        vec3d = pathentity.a((Entity) this);
                    }

                    this.a(vec3d.x, vec3d.z);
                    this.dl();
                }
            } else if (!entityrabbit_controllerjumprabbit.d()) {
                this.dp();
            }
        }

        this.bB = this.onGround;
    }

    public void as() {}

    private void a(double d0, double d1) {
        this.yaw = (float) (MathHelper.c(d1 - this.locZ, d0 - this.locX) * 57.2957763671875D) - 90.0F;
    }

    private void dp() {
        ((EntityRabbit.ControllerJumpRabbit) this.g).a(true);
    }

    private void dt() {
        ((EntityRabbit.ControllerJumpRabbit) this.g).a(false);
    }

    private void du() {
        if (this.moveController.c() < 2.2D) {
            this.bC = 10;
        } else {
            this.bC = 1;
        }

    }

    private void dv() {
        this.du();
        this.dt();
    }

    public void n() {
        super.n();
        if (this.by != this.bz) {
            ++this.by;
        } else if (this.bz != 0) {
            this.by = 0;
            this.bz = 0;
            this.l(false);
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(3.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
    }

    public static void a(DataConverterManager dataconvertermanager) {
        EntityInsentient.a(dataconvertermanager, EntityRabbit.class);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("RabbitType", this.getRabbitType());
        nbttagcompound.setInt("MoreCarrotTicks", this.bD);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setRabbitType(nbttagcompound.getInt("RabbitType"));
        this.bD = nbttagcompound.getInt("MoreCarrotTicks");
    }

    protected SoundEffect dm() {
        return SoundEffects.fZ;
    }

    protected SoundEffect F() {
        return SoundEffects.fV;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.fY;
    }

    protected SoundEffect cf() {
        return SoundEffects.fX;
    }

    public boolean B(Entity entity) {
        if (this.getRabbitType() == 99) {
            this.a(SoundEffects.fW, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            return entity.damageEntity(DamageSource.mobAttack(this), 8.0F);
        } else {
            return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
        }
    }

    public SoundCategory bK() {
        return this.getRabbitType() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
    }

    @Nullable
    protected MinecraftKey J() {
        return LootTables.C;
    }

    private boolean a(Item item) {
        return item == Items.CARROT || item == Items.GOLDEN_CARROT || item == Item.getItemOf(Blocks.YELLOW_FLOWER);
    }

    public EntityRabbit b(EntityAgeable entityageable) {
        EntityRabbit entityrabbit = new EntityRabbit(this.world);
        int i = this.dw();

        if (this.random.nextInt(20) != 0) {
            if (entityageable instanceof EntityRabbit && this.random.nextBoolean()) {
                i = ((EntityRabbit) entityageable).getRabbitType();
            } else {
                i = this.getRabbitType();
            }
        }

        entityrabbit.setRabbitType(i);
        return entityrabbit;
    }

    public boolean e(ItemStack itemstack) {
        return this.a(itemstack.getItem());
    }

    public int getRabbitType() {
        return ((Integer) this.datawatcher.get(EntityRabbit.bx)).intValue();
    }

    public void setRabbitType(int i) {
        if (i == 99) {
            this.getAttributeInstance(GenericAttributes.h).setValue(8.0D);
            this.goalSelector.a(4, new EntityRabbit.PathfinderGoalKillerRabbitMeleeAttack(this));
            this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
            this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
            this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityWolf.class, true));
            if (!this.hasCustomName()) {
                this.setCustomName(LocaleI18n.get("entity.KillerBunny.name"));
            }
        }

        this.datawatcher.set(EntityRabbit.bx, Integer.valueOf(i));
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity);
        int i = this.dw();
        boolean flag = false;

        if (object instanceof EntityRabbit.GroupDataRabbit) {
            i = ((EntityRabbit.GroupDataRabbit) object).a;
            flag = true;
        } else {
            object = new EntityRabbit.GroupDataRabbit(i);
        }

        this.setRabbitType(i);
        if (flag) {
            this.setAgeRaw(-24000);
        }

        return (GroupDataEntity) object;
    }

    private int dw() {
        BiomeBase biomebase = this.world.getBiome(new BlockPosition(this));
        int i = this.random.nextInt(100);

        return biomebase.p() ? (i < 80 ? 1 : 3) : (biomebase instanceof BiomeDesert ? 4 : (i < 50 ? 0 : (i < 90 ? 5 : 2)));
    }

    private boolean dx() {
        return this.bD == 0;
    }

    protected void do_() {
        BlockCarrots blockcarrots = (BlockCarrots) Blocks.CARROTS;
        IBlockData iblockdata = blockcarrots.setAge(blockcarrots.g());

        this.world.addParticle(EnumParticle.BLOCK_DUST, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D, new int[] { Block.getCombinedId(iblockdata)});
        this.bD = 40;
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }

    static class PathfinderGoalKillerRabbitMeleeAttack extends PathfinderGoalMeleeAttack {

        public PathfinderGoalKillerRabbitMeleeAttack(EntityRabbit entityrabbit) {
            super(entityrabbit, 1.4D, true);
        }

        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.width);
        }
    }

    static class PathfinderGoalRabbitPanic extends PathfinderGoalPanic {

        private final EntityRabbit f;

        public PathfinderGoalRabbitPanic(EntityRabbit entityrabbit, double d0) {
            super(entityrabbit, d0);
            this.f = entityrabbit;
        }

        public void e() {
            super.e();
            this.f.c(this.b);
        }
    }

    static class PathfinderGoalEatCarrots extends PathfinderGoalGotoTarget {

        private final EntityRabbit c;
        private boolean d;
        private boolean e;

        public PathfinderGoalEatCarrots(EntityRabbit entityrabbit) {
            super(entityrabbit, 0.699999988079071D, 16);
            this.c = entityrabbit;
        }

        public boolean a() {
            if (this.a <= 0) {
                if (!this.c.world.getGameRules().getBoolean("mobGriefing")) {
                    return false;
                }

                this.e = false;
                this.d = this.c.dx();
                this.d = true;
            }

            return super.a();
        }

        public boolean b() {
            return this.e && super.b();
        }

        public void e() {
            super.e();
            this.c.getControllerLook().a((double) this.b.getX() + 0.5D, (double) (this.b.getY() + 1), (double) this.b.getZ() + 0.5D, 10.0F, (float) this.c.N());
            if (this.f()) {
                World world = this.c.world;
                BlockPosition blockposition = this.b.up();
                IBlockData iblockdata = world.getType(blockposition);
                Block block = iblockdata.getBlock();

                if (this.e && block instanceof BlockCarrots) {
                    Integer integer = (Integer) iblockdata.get(BlockCarrots.AGE);

                    if (integer.intValue() == 0) {
                        world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
                        world.setAir(blockposition, true);
                    } else {
                        world.setTypeAndData(blockposition, iblockdata.set(BlockCarrots.AGE, Integer.valueOf(integer.intValue() - 1)), 2);
                        world.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
                    }

                    this.c.do_();
                }

                this.e = false;
                this.a = 10;
            }

        }

        protected boolean a(World world, BlockPosition blockposition) {
            Block block = world.getType(blockposition).getBlock();

            if (block == Blocks.FARMLAND && this.d && !this.e) {
                blockposition = blockposition.up();
                IBlockData iblockdata = world.getType(blockposition);

                block = iblockdata.getBlock();
                if (block instanceof BlockCarrots && ((BlockCarrots) block).z(iblockdata)) {
                    this.e = true;
                    return true;
                }
            }

            return false;
        }
    }

    static class PathfinderGoalRabbitAvoidTarget<T extends Entity> extends PathfinderGoalAvoidTarget<T> {

        private final EntityRabbit c;

        public PathfinderGoalRabbitAvoidTarget(EntityRabbit entityrabbit, Class<T> oclass, float f, double d0, double d1) {
            super(entityrabbit, oclass, f, d0, d1);
            this.c = entityrabbit;
        }

        public boolean a() {
            return this.c.getRabbitType() != 99 && super.a();
        }
    }

    static class ControllerMoveRabbit extends ControllerMove {

        private final EntityRabbit i;
        private double j;

        public ControllerMoveRabbit(EntityRabbit entityrabbit) {
            super(entityrabbit);
            this.i = entityrabbit;
        }

        public void a() {
            if (this.i.onGround && !this.i.bd && !((EntityRabbit.ControllerJumpRabbit) this.i.g).c()) {
                this.i.c(0.0D);
            } else if (this.b()) {
                this.i.c(this.j);
            }

            super.a();
        }

        public void a(double d0, double d1, double d2, double d3) {
            if (this.i.isInWater()) {
                d3 = 1.5D;
            }

            super.a(d0, d1, d2, d3);
            if (d3 > 0.0D) {
                this.j = d3;
            }

        }
    }

    public class ControllerJumpRabbit extends ControllerJump {

        private final EntityRabbit c;
        private boolean d;

        public ControllerJumpRabbit(EntityRabbit entityrabbit) {
            super(entityrabbit);
            this.c = entityrabbit;
        }

        public boolean c() {
            return this.a;
        }

        public boolean d() {
            return this.d;
        }

        public void a(boolean flag) {
            this.d = flag;
        }

        public void b() {
            if (this.a) {
                this.c.dl();
                this.a = false;
            }

        }
    }

    public static class GroupDataRabbit implements GroupDataEntity {

        public int a;

        public GroupDataRabbit(int i) {
            this.a = i;
        }
    }
}
