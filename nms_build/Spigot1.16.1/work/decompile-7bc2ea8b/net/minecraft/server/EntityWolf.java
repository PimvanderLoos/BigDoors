package net.minecraft.server;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityWolf extends EntityTameableAnimal implements IEntityAngerable {

    private static final DataWatcherObject<Boolean> by = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bz = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bA = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.b);
    public static final Predicate<EntityLiving> bx = (entityliving) -> {
        EntityTypes<?> entitytypes = entityliving.getEntityType();

        return entitytypes == EntityTypes.SHEEP || entitytypes == EntityTypes.RABBIT || entitytypes == EntityTypes.FOX;
    };
    private float bB;
    private float bC;
    private boolean bD;
    private boolean bE;
    private float bF;
    private float bG;
    private static final IntRange bH = TimeRange.a(20, 39);
    private UUID bI;

    public EntityWolf(EntityTypes<? extends EntityWolf> entitytypes, World world) {
        super(entitytypes, world);
        this.setTamed(false);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalSit(this));
        this.goalSelector.a(3, new EntityWolf.a<>(this, EntityLlama.class, 24.0F, 1.5D, 1.5D));
        this.goalSelector.a(4, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(5, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(6, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.a(7, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(9, new PathfinderGoalBeg(this, 8.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(10, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalOwnerHurtByTarget(this));
        this.targetSelector.a(2, new PathfinderGoalOwnerHurtTarget(this));
        this.targetSelector.a(3, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::b));
        this.targetSelector.a(5, new PathfinderGoalRandomTargetNonTamed<>(this, EntityAnimal.class, false, EntityWolf.bx));
        this.targetSelector.a(6, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.bv));
        this.targetSelector.a(7, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeletonAbstract.class, false));
        this.targetSelector.a(8, new PathfinderGoalUniversalAngerReset<>(this, true));
    }

    public static AttributeProvider.Builder eV() {
        return EntityInsentient.p().a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.MAX_HEALTH, 8.0D).a(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityWolf.by, false);
        this.datawatcher.register(EntityWolf.bz, EnumColor.RED.getColorIndex());
        this.datawatcher.register(EntityWolf.bA, 0);
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.ENTITY_WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setByte("CollarColor", (byte) this.getCollarColor().getColorIndex());
        this.c(nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
            this.setCollarColor(EnumColor.fromColorIndex(nbttagcompound.getInt("CollarColor")));
        }

        this.a((WorldServer) this.world, nbttagcompound);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isAngry() ? SoundEffects.ENTITY_WOLF_GROWL : (this.random.nextInt(3) == 0 ? (this.isTamed() && this.getHealth() < 10.0F ? SoundEffects.ENTITY_WOLF_WHINE : SoundEffects.ENTITY_WOLF_PANT) : SoundEffects.ENTITY_WOLF_AMBIENT);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_WOLF_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.world.isClientSide && this.bD && !this.bE && !this.eJ() && this.onGround) {
            this.bE = true;
            this.bF = 0.0F;
            this.bG = 0.0F;
            this.world.broadcastEntityEffect(this, (byte) 8);
        }

        if (!this.world.isClientSide) {
            this.a((WorldServer) this.world, true);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.bC = this.bB;
            if (this.eZ()) {
                this.bB += (1.0F - this.bB) * 0.4F;
            } else {
                this.bB += (0.0F - this.bB) * 0.4F;
            }

            if (this.aC()) {
                this.bD = true;
                this.bE = false;
                this.bF = 0.0F;
                this.bG = 0.0F;
            } else if ((this.bD || this.bE) && this.bE) {
                if (this.bF == 0.0F) {
                    this.playSound(SoundEffects.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                this.bG = this.bF;
                this.bF += 0.05F;
                if (this.bG >= 2.0F) {
                    this.bD = false;
                    this.bE = false;
                    this.bG = 0.0F;
                    this.bF = 0.0F;
                }

                if (this.bF > 0.4F) {
                    float f = (float) this.locY();
                    int i = (int) (MathHelper.sin((this.bF - 0.4F) * 3.1415927F) * 7.0F);
                    Vec3D vec3d = this.getMot();

                    for (int j = 0; j < i; ++j) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;

                        this.world.addParticle(Particles.SPLASH, this.locX() + (double) f1, (double) (f + 0.8F), this.locZ() + (double) f2, vec3d.x, vec3d.y, vec3d.z);
                    }
                }
            }

        }
    }

    @Override
    public void die(DamageSource damagesource) {
        this.bD = false;
        this.bE = false;
        this.bG = 0.0F;
        this.bF = 0.0F;
        super.die(damagesource);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.8F;
    }

    @Override
    public int eo() {
        return this.isSitting() ? 20 : super.eo();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            Entity entity = damagesource.getEntity();

            this.setWillSit(false);
            if (entity != null && !(entity instanceof EntityHuman) && !(entity instanceof EntityArrow)) {
                f = (f + 1.0F) / 2.0F;
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Override
    public boolean attackEntity(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.b(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.a((EntityLiving) this, entity);
        }

        return flag;
    }

    @Override
    public void setTamed(boolean flag) {
        super.setTamed(flag);
        if (flag) {
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(20.0D);
            this.setHealth(20.0F);
        } else {
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(8.0D);
        }

        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(4.0D);
    }

    @Override
    public EnumInteractionResult b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (this.world.isClientSide) {
            boolean flag = this.j((EntityLiving) entityhuman) || this.isTamed() || item == Items.BONE && !this.isTamed() && !this.isAngry();

            return flag ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS;
        } else {
            if (this.isTamed()) {
                if (this.k(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    this.heal((float) item.getFoodInfo().getNutrition());
                    return EnumInteractionResult.SUCCESS;
                }

                if (!(item instanceof ItemDye)) {
                    EnumInteractionResult enuminteractionresult = super.b(entityhuman, enumhand);

                    if ((!enuminteractionresult.a() || this.isBaby()) && this.j((EntityLiving) entityhuman)) {
                        this.setWillSit(!this.isWillSit());
                        this.jumping = false;
                        this.navigation.o();
                        this.setGoalTarget((EntityLiving) null);
                        return EnumInteractionResult.SUCCESS;
                    }

                    return enuminteractionresult;
                }

                EnumColor enumcolor = ((ItemDye) item).d();

                if (enumcolor != this.getCollarColor()) {
                    this.setCollarColor(enumcolor);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    return EnumInteractionResult.SUCCESS;
                }
            } else if (item == Items.BONE && !this.isAngry()) {
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemstack.subtract(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.tame(entityhuman);
                    this.navigation.o();
                    this.setGoalTarget((EntityLiving) null);
                    this.setWillSit(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }

                return EnumInteractionResult.SUCCESS;
            }

            return super.b(entityhuman, enumhand);
        }
    }

    @Override
    public boolean k(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return item.isFood() && item.getFoodInfo().c();
    }

    @Override
    public int getMaxSpawnGroup() {
        return 8;
    }

    @Override
    public int getAnger() {
        return (Integer) this.datawatcher.get(EntityWolf.bA);
    }

    @Override
    public void setAnger(int i) {
        this.datawatcher.set(EntityWolf.bA, i);
    }

    @Override
    public void anger() {
        this.setAnger(EntityWolf.bH.a(this.random));
    }

    @Nullable
    @Override
    public UUID getAngerTarget() {
        return this.bI;
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.bI = uuid;
    }

    public EnumColor getCollarColor() {
        return EnumColor.fromColorIndex((Integer) this.datawatcher.get(EntityWolf.bz));
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.datawatcher.set(EntityWolf.bz, enumcolor.getColorIndex());
    }

    @Override
    public EntityWolf createChild(EntityAgeable entityageable) {
        EntityWolf entitywolf = (EntityWolf) EntityTypes.WOLF.a(this.world);
        UUID uuid = this.getOwnerUUID();

        if (uuid != null) {
            entitywolf.setOwnerUUID(uuid);
            entitywolf.setTamed(true);
        }

        return entitywolf;
    }

    public void x(boolean flag) {
        this.datawatcher.set(EntityWolf.by, flag);
    }

    @Override
    public boolean mate(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(entityanimal instanceof EntityWolf)) {
            return false;
        } else {
            EntityWolf entitywolf = (EntityWolf) entityanimal;

            return !entitywolf.isTamed() ? false : (entitywolf.isSitting() ? false : this.isInLove() && entitywolf.isInLove());
        }
    }

    public boolean eZ() {
        return (Boolean) this.datawatcher.get(EntityWolf.by);
    }

    @Override
    public boolean a(EntityLiving entityliving, EntityLiving entityliving1) {
        if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
            if (entityliving instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entityliving;

                return !entitywolf.isTamed() || entitywolf.getOwner() != entityliving1;
            } else {
                return entityliving instanceof EntityHuman && entityliving1 instanceof EntityHuman && !((EntityHuman) entityliving1).a((EntityHuman) entityliving) ? false : (entityliving instanceof EntityHorseAbstract && ((EntityHorseAbstract) entityliving).isTamed() ? false : !(entityliving instanceof EntityTameableAnimal) || !((EntityTameableAnimal) entityliving).isTamed());
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return !this.isAngry() && super.a(entityhuman);
    }

    class a<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityWolf j;

        public a(EntityWolf entitywolf, Class oclass, float f, double d0, double d1) {
            super(entitywolf, oclass, f, d0, d1);
            this.j = entitywolf;
        }

        @Override
        public boolean a() {
            return super.a() && this.b instanceof EntityLlama ? !this.j.isTamed() && this.a((EntityLlama) this.b) : false;
        }

        private boolean a(EntityLlama entityllama) {
            return entityllama.getStrength() >= EntityWolf.this.random.nextInt(5);
        }

        @Override
        public void c() {
            EntityWolf.this.setGoalTarget((EntityLiving) null);
            super.c();
        }

        @Override
        public void e() {
            EntityWolf.this.setGoalTarget((EntityLiving) null);
            super.e();
        }
    }
}
