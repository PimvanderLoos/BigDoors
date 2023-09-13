package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityPig extends EntityAnimal {

    private static final DataWatcherObject<Boolean> bw = DataWatcher.a(EntityPig.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bx = DataWatcher.a(EntityPig.class, DataWatcherRegistry.b);
    private static final RecipeItemStack by = RecipeItemStack.a(Items.CARROT, Items.POTATO, Items.BEETROOT);
    private boolean bz;
    private int bA;
    private int bB;

    public EntityPig(EntityTypes<? extends EntityPig> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(3, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, RecipeItemStack.a(Items.CARROT_ON_A_STICK), false));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.2D, false, EntityPig.by));
        this.goalSelector.a(5, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Nullable
    @Override
    public Entity getRidingPassenger() {
        return this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);
    }

    @Override
    public boolean dY() {
        Entity entity = this.getRidingPassenger();

        if (!(entity instanceof EntityHuman)) {
            return false;
        } else {
            EntityHuman entityhuman = (EntityHuman) entity;

            return entityhuman.getItemInMainHand().getItem() == Items.CARROT_ON_A_STICK || entityhuman.getItemInOffHand().getItem() == Items.CARROT_ON_A_STICK;
        }
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPig.bx.equals(datawatcherobject) && this.world.isClientSide) {
            this.bz = true;
            this.bA = 0;
            this.bB = (Integer) this.datawatcher.get(EntityPig.bx);
        }

        super.a(datawatcherobject);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPig.bw, false);
        this.datawatcher.register(EntityPig.bx, 0);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Saddle", this.hasSaddle());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSaddle(nbttagcompound.getBoolean("Saddle"));
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_PIG_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_PIG_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PIG_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_PIG_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        if (super.a(entityhuman, enumhand)) {
            return true;
        } else {
            ItemStack itemstack = entityhuman.b(enumhand);

            if (itemstack.getItem() == Items.NAME_TAG) {
                itemstack.a(entityhuman, (EntityLiving) this, enumhand);
                return true;
            } else if (this.hasSaddle() && !this.isVehicle()) {
                if (!this.world.isClientSide) {
                    entityhuman.startRiding(this);
                }

                return true;
            } else {
                return itemstack.getItem() == Items.SADDLE && itemstack.a(entityhuman, (EntityLiving) this, enumhand);
            }
        }
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.hasSaddle()) {
            this.a((IMaterial) Items.SADDLE);
        }

    }

    public boolean hasSaddle() {
        return (Boolean) this.datawatcher.get(EntityPig.bw);
    }

    public void setSaddle(boolean flag) {
        if (flag) {
            this.datawatcher.set(EntityPig.bw, true);
        } else {
            this.datawatcher.set(EntityPig.bw, false);
        }

    }

    @Override
    public void onLightningStrike(EntityLightning entitylightning) {
        EntityPigZombie entitypigzombie = (EntityPigZombie) EntityTypes.ZOMBIE_PIGMAN.a(this.world);

        entitypigzombie.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
        entitypigzombie.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.yaw, this.pitch);
        entitypigzombie.setNoAI(this.isNoAI());
        if (this.hasCustomName()) {
            entitypigzombie.setCustomName(this.getCustomName());
            entitypigzombie.setCustomNameVisible(this.getCustomNameVisible());
        }

        this.world.addEntity(entitypigzombie);
        this.die();
    }

    @Override
    public void e(Vec3D vec3d) {
        if (this.isAlive()) {
            Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);

            if (this.isVehicle() && this.dY()) {
                this.yaw = entity.yaw;
                this.lastYaw = this.yaw;
                this.pitch = entity.pitch * 0.5F;
                this.setYawPitch(this.yaw, this.pitch);
                this.aI = this.yaw;
                this.aK = this.yaw;
                this.H = 1.0F;
                this.aM = this.dt() * 0.1F;
                if (this.bz && this.bA++ > this.bB) {
                    this.bz = false;
                }

                if (this.cj()) {
                    float f = (float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue() * 0.225F;

                    if (this.bz) {
                        f += f * 1.15F * MathHelper.sin((float) this.bA / (float) this.bB * 3.1415927F);
                    }

                    this.o(f);
                    super.e(new Vec3D(0.0D, 0.0D, 1.0D));
                    this.bc = 0;
                } else {
                    this.setMot(Vec3D.a);
                }

                this.aC = this.aD;
                double d0 = this.locX() - this.lastX;
                double d1 = this.locZ() - this.lastZ;
                float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

                if (f1 > 1.0F) {
                    f1 = 1.0F;
                }

                this.aD += (f1 - this.aD) * 0.4F;
                this.aE += this.aD;
            } else {
                this.H = 0.5F;
                this.aM = 0.02F;
                super.e(vec3d);
            }
        }
    }

    public boolean er() {
        if (this.bz) {
            return false;
        } else {
            this.bz = true;
            this.bA = 0;
            this.bB = this.getRandom().nextInt(841) + 140;
            this.getDataWatcher().set(EntityPig.bx, this.bB);
            return true;
        }
    }

    @Override
    public EntityPig createChild(EntityAgeable entityageable) {
        return (EntityPig) EntityTypes.PIG.a(this.world);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return EntityPig.by.test(itemstack);
    }
}
