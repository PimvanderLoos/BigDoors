package net.minecraft.server;

import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public class EntityStrider extends EntityAnimal implements ISteerable, ISaddleable {

    private static final RecipeItemStack bv = RecipeItemStack.a(Items.bx);
    private static final RecipeItemStack bw = RecipeItemStack.a(Items.bx, Items.WARPED_FUNGUS_ON_A_STICK);
    private static final DataWatcherObject<Integer> bx = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> by = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bz = DataWatcher.a(EntityStrider.class, DataWatcherRegistry.i);
    private final SaddleStorage bA;
    private PathfinderGoalTempt bB;
    private PathfinderGoalPanic bC;

    public EntityStrider(EntityTypes<? extends EntityStrider> entitytypes, World world) {
        super(entitytypes, world);
        this.bA = new SaddleStorage(this.datawatcher, EntityStrider.bx, EntityStrider.bz);
        this.i = true;
        this.a(PathType.WATER, -1.0F);
        this.a(PathType.LAVA, 0.0F);
        this.a(PathType.DANGER_FIRE, 0.0F);
        this.a(PathType.DAMAGE_FIRE, 0.0F);
    }

    public static boolean c(EntityTypes<EntityStrider> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        do {
            blockposition_mutableblockposition.c(EnumDirection.UP);
        } while (generatoraccess.getFluid(blockposition_mutableblockposition).a((Tag) TagsFluid.LAVA));

        return generatoraccess.getType(blockposition_mutableblockposition).isAir();
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityStrider.bx.equals(datawatcherobject) && this.world.isClientSide) {
            this.bA.a();
        }

        super.a(datawatcherobject);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityStrider.bx, 0);
        this.datawatcher.register(EntityStrider.by, false);
        this.datawatcher.register(EntityStrider.bz, false);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        this.bA.a(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.bA.b(nbttagcompound);
    }

    @Override
    public boolean hasSaddle() {
        return this.bA.hasSaddle();
    }

    @Override
    public boolean canSaddle() {
        return this.isAlive() && !this.isBaby();
    }

    @Override
    public void saddle(@Nullable SoundCategory soundcategory) {
        this.bA.setSaddle(true);
        if (soundcategory != null) {
            this.world.playSound((EntityHuman) null, (Entity) this, SoundEffects.ENTITY_STRIDER_SADDLE, soundcategory, 0.5F, 1.0F);
        }

    }

    @Override
    protected void initPathfinder() {
        this.bC = new PathfinderGoalPanic(this, 1.65D);
        this.goalSelector.a(1, this.bC);
        this.goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        this.bB = new PathfinderGoalTempt(this, 1.4D, false, EntityStrider.bw);
        this.goalSelector.a(4, this.bB);
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(7, new PathfinderGoalRandomStroll(this, 1.0D, 60));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityStrider.class, 8.0F));
    }

    public void t(boolean flag) {
        this.datawatcher.set(EntityStrider.by, flag);
    }

    public boolean eL() {
        return this.getVehicle() instanceof EntityStrider ? ((EntityStrider) this.getVehicle()).eL() : (Boolean) this.datawatcher.get(EntityStrider.by);
    }

    @Override
    public boolean a(FluidType fluidtype) {
        return fluidtype.a((Tag) TagsFluid.LAVA);
    }

    @Nullable
    @Override
    public AxisAlignedBB j(Entity entity) {
        return entity.isCollidable() ? entity.getBoundingBox() : null;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public double aY() {
        float f = Math.min(0.25F, this.aC);
        float f1 = this.aD;

        return (double) this.getHeight() - 0.2D + (double) (0.12F * MathHelper.cos(f1 * 1.5F) * 2.0F * f);
    }

    @Override
    public boolean es() {
        Entity entity = this.getRidingPassenger();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().getItem() == Items.WARPED_FUNGUS_ON_A_STICK || entityhuman.getItemInOffHand().getItem() == Items.WARPED_FUNGUS_ON_A_STICK;
        }
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.i(this);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Override
    public Vec3D c(EntityLiving entityliving) {
        Vec3D[] avec3d = new Vec3D[]{a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw - 22.5F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw + 22.5F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw - 45.0F), a((double) this.getWidth(), (double) entityliving.getWidth(), entityliving.yaw + 45.0F)};
        Set<BlockPosition> set = Sets.newLinkedHashSet();
        double d0 = this.getBoundingBox().maxY;
        double d1 = this.getBoundingBox().minY - 0.5D;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Vec3D[] avec3d1 = avec3d;
        int i = avec3d.length;

        double d2;

        for (int j = 0; j < i; ++j) {
            Vec3D vec3d = avec3d1[j];

            blockposition_mutableblockposition.c(this.locX() + vec3d.x, d0, this.locZ() + vec3d.z);

            for (d2 = d0; d2 > d1; --d2) {
                set.add(blockposition_mutableblockposition.immutableCopy());
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
            }
        }

        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            if (!this.world.getFluid(blockposition).a((Tag) TagsFluid.LAVA)) {
                UnmodifiableIterator unmodifiableiterator = entityliving.ei().iterator();

                while (unmodifiableiterator.hasNext()) {
                    EntityPose entitypose = (EntityPose) unmodifiableiterator.next();

                    d2 = this.world.m(blockposition);
                    if (DismountUtil.a(d2)) {
                        AxisAlignedBB axisalignedbb = entityliving.f(entitypose);
                        Vec3D vec3d1 = Vec3D.a((BaseBlockPosition) blockposition, d2);

                        if (DismountUtil.a(this.world, entityliving, axisalignedbb.c(vec3d1))) {
                            entityliving.setPose(entitypose);
                            return vec3d1;
                        }
                    }
                }
            }
        }

        return new Vec3D(this.locX(), this.getBoundingBox().maxY, this.locZ());
    }

    @Override
    public void f(Vec3D vec3d) {
        this.n(this.eM());
        this.a((EntityInsentient) this, this.bA, vec3d);
    }

    public float eM() {
        return (float) this.b(GenericAttributes.MOVEMENT_SPEED) * (this.eL() ? 0.66F : 1.0F);
    }

    @Override
    public float O_() {
        return (float) this.b(GenericAttributes.MOVEMENT_SPEED) * (this.eL() ? 0.23F : 0.55F);
    }

    @Override
    public void a_(Vec3D vec3d) {
        super.f(vec3d);
    }

    @Override
    protected float ao() {
        return this.B + 0.6F;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(this.aN() ? SoundEffects.ENTITY_STRIDER_STEP_LAVA : SoundEffects.ENTITY_STRIDER_STEP, 1.0F, 1.0F);
    }

    @Override
    public boolean P_() {
        return this.bA.a(this.getRandom());
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.checkBlockCollisions();
        if (this.aN()) {
            this.fallDistance = 0.0F;
        } else {
            super.a(d0, flag, iblockdata, blockposition);
        }
    }

    @Override
    public void tick() {
        if (this.eP() && this.random.nextInt(140) == 0) {
            this.playSound(SoundEffects.ENTITY_STRIDER_HAPPY, 1.0F, this.dG());
        } else if (this.eO() && this.random.nextInt(60) == 0) {
            this.playSound(SoundEffects.ENTITY_STRIDER_RETREAT, 1.0F, this.dG());
        }

        IBlockData iblockdata = this.world.getType(this.getChunkCoordinates());
        IBlockData iblockdata1 = this.aJ();
        boolean flag = iblockdata.a((Tag) TagsBlock.STRIDER_WARM_BLOCKS) || iblockdata1.a((Tag) TagsBlock.STRIDER_WARM_BLOCKS) || this.b((Tag) TagsFluid.LAVA) > 0.0D;

        this.t(!flag);
        super.tick();
        this.eV();
        this.checkBlockCollisions();
    }

    private boolean eO() {
        return this.bC != null && this.bC.h();
    }

    private boolean eP() {
        return this.bB != null && this.bB.h();
    }

    @Override
    protected boolean q() {
        return true;
    }

    private void eV() {
        if (this.aN()) {
            VoxelShapeCollision voxelshapecollision = VoxelShapeCollision.a((Entity) this);

            if (voxelshapecollision.a(BlockFluids.c, this.getChunkCoordinates(), true) && !this.world.getFluid(this.getChunkCoordinates().up()).a((Tag) TagsFluid.LAVA)) {
                this.onGround = true;
            } else {
                this.setMot(this.getMot().a(0.5D).add(0.0D, 0.05D, 0.0D));
            }
        }

    }

    public static AttributeProvider.Builder eN() {
        return EntityInsentient.p().a(GenericAttributes.MOVEMENT_SPEED, 0.17499999701976776D).a(GenericAttributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return !this.eO() && !this.eP() ? SoundEffects.ENTITY_STRIDER_AMBIENT : null;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_STRIDER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_STRIDER_DEATH;
    }

    @Override
    protected boolean q(Entity entity) {
        return this.getPassengers().isEmpty() && !this.a((Tag) TagsFluid.LAVA);
    }

    @Override
    public boolean dN() {
        return true;
    }

    @Override
    public boolean isBurning() {
        return false;
    }

    @Override
    protected NavigationAbstract b(World world) {
        return new EntityStrider.b(this, world);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition).getFluid().a((Tag) TagsFluid.LAVA) ? 10.0F : 0.0F;
    }

    @Override
    public EntityStrider createChild(EntityAgeable entityageable) {
        return (EntityStrider) EntityTypes.STRIDER.a(this.world);
    }

    @Override
    public boolean k(ItemStack itemstack) {
        return EntityStrider.bv.test(itemstack);
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.hasSaddle()) {
            this.a((IMaterial) Items.SADDLE);
        }

    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        boolean flag = this.k(entityhuman.b(enumhand));

        if (!flag && this.hasSaddle() && !this.isVehicle()) {
            if (!this.world.isClientSide) {
                entityhuman.startRiding(this);
            }

            return EnumInteractionResult.a(this.world.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = super.b(entityhuman, enumhand);

            if (!enuminteractionresult.a()) {
                ItemStack itemstack = entityhuman.b(enumhand);

                return itemstack.getItem() == Items.SADDLE ? itemstack.a(entityhuman, (EntityLiving) this, enumhand) : EnumInteractionResult.PASS;
            } else {
                if (flag && !this.isSilent()) {
                    this.world.playSound((EntityHuman) null, this.locX(), this.locY(), this.locZ(), SoundEffects.ENTITY_STRIDER_EAT, this.getSoundCategory(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                }

                return enuminteractionresult;
            }
        }
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        EntityZombie.GroupDataZombie entityzombie_groupdatazombie = null;
        EntityStrider$GroupData$Rider entitystrider$groupdata$rider;

        if (groupdataentity instanceof EntityStrider.a) {
            entitystrider$groupdata$rider = ((EntityStrider.a) groupdataentity).a;
        } else if (!this.isBaby()) {
            if (this.random.nextInt(30) == 0) {
                entitystrider$groupdata$rider = EntityStrider$GroupData$Rider.PIGLIN_RIDER;
                entityzombie_groupdatazombie = new EntityZombie.GroupDataZombie(EntityZombie.a(this.random), false);
            } else if (this.random.nextInt(10) == 0) {
                entitystrider$groupdata$rider = EntityStrider$GroupData$Rider.BABY_RIDER;
            } else {
                entitystrider$groupdata$rider = EntityStrider$GroupData$Rider.NO_RIDER;
            }

            groupdataentity = new EntityStrider.a(entitystrider$groupdata$rider);
            ((EntityAgeable.a) groupdataentity).a(entitystrider$groupdata$rider == EntityStrider$GroupData$Rider.NO_RIDER ? 0.5F : 0.0F);
        } else {
            entitystrider$groupdata$rider = EntityStrider$GroupData$Rider.NO_RIDER;
        }

        Object object = null;

        if (entitystrider$groupdata$rider == EntityStrider$GroupData$Rider.BABY_RIDER) {
            EntityStrider entitystrider = (EntityStrider) EntityTypes.STRIDER.a(generatoraccess.getMinecraftWorld());

            if (entitystrider != null) {
                object = entitystrider;
                entitystrider.setAgeRaw(-24000);
            }
        } else if (entitystrider$groupdata$rider == EntityStrider$GroupData$Rider.PIGLIN_RIDER) {
            EntityPigZombie entitypigzombie = (EntityPigZombie) EntityTypes.ZOMBIFIED_PIGLIN.a(generatoraccess.getMinecraftWorld());

            if (entitypigzombie != null) {
                object = entitypigzombie;
                this.saddle((SoundCategory) null);
            }
        }

        if (object != null) {
            ((EntityInsentient) object).setPositionRotation(this.locX(), this.locY(), this.locZ(), this.yaw, 0.0F);
            ((EntityInsentient) object).prepare(generatoraccess, difficultydamagescaler, EnumMobSpawn.JOCKEY, entityzombie_groupdatazombie, (NBTTagCompound) null);
            ((EntityInsentient) object).a((Entity) this, true);
            generatoraccess.addEntity((Entity) object);
        }

        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) groupdataentity, nbttagcompound);
    }

    static class b extends Navigation {

        b(EntityStrider entitystrider, World world) {
            super(entitystrider, world);
        }

        @Override
        protected Pathfinder a(int i) {
            this.o = new PathfinderNormal();
            return new Pathfinder(this.o, i);
        }

        @Override
        protected boolean a(PathType pathtype) {
            return pathtype != PathType.LAVA && pathtype != PathType.DAMAGE_FIRE && pathtype != PathType.DANGER_FIRE ? super.a(pathtype) : true;
        }

        @Override
        public boolean a(BlockPosition blockposition) {
            return this.b.getType(blockposition).a(Blocks.LAVA) || super.a(blockposition);
        }
    }

    public static class a extends EntityAgeable.a {

        public final EntityStrider$GroupData$Rider a;

        public a(EntityStrider$GroupData$Rider entitystrider$groupdata$rider) {
            this.a = entitystrider$groupdata$rider;
        }
    }
}
