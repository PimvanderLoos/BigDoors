package net.minecraft.server;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityWolf extends EntityTameableAnimal {

    private static final DataWatcherObject<Boolean> bA = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bB = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.b);
    public static final Predicate<EntityLiving> bz = (entityliving) -> {
        EntityTypes<?> entitytypes = entityliving.getEntityType();

        return entitytypes == EntityTypes.SHEEP || entitytypes == EntityTypes.RABBIT || entitytypes == EntityTypes.FOX;
    };
    private float bC;
    private float bD;
    private boolean bE;
    private boolean bF;
    private float bG;
    private float bH;

    public EntityWolf(EntityTypes<? extends EntityWolf> entitytypes, World world) {
        super(entitytypes, world);
        this.setTamed(false);
    }

    @Override
    protected void initPathfinder() {
        this.goalSit = new PathfinderGoalSit(this);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.goalSit);
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
        this.targetSelector.a(4, new PathfinderGoalRandomTargetNonTamed<>(this, EntityAnimal.class, false, EntityWolf.bz));
        this.targetSelector.a(4, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.bw));
        this.targetSelector.a(5, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeletonAbstract.class, false));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        if (this.isTamed()) {
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(20.0D);
        } else {
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(8.0D);
        }

        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(2.0D);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        super.setGoalTarget(entityliving);
        if (entityliving == null) {
            this.setAngry(false);
        } else if (!this.isTamed()) {
            this.setAngry(true);
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityWolf.bA, false);
        this.datawatcher.register(EntityWolf.bB, EnumColor.RED.getColorIndex());
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Angry", this.isAngry());
        nbttagcompound.setByte("CollarColor", (byte) this.getCollarColor().getColorIndex());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setAngry(nbttagcompound.getBoolean("Angry"));
        if (nbttagcompound.hasKeyOfType("CollarColor", 99)) {
            this.setCollarColor(EnumColor.fromColorIndex(nbttagcompound.getInt("CollarColor")));
        }

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
        if (!this.world.isClientSide && this.bE && !this.bF && !this.eo() && this.onGround) {
            this.bF = true;
            this.bG = 0.0F;
            this.bH = 0.0F;
            this.world.broadcastEntityEffect(this, (byte) 8);
        }

        if (!this.world.isClientSide && this.getGoalTarget() == null && this.isAngry()) {
            this.setAngry(false);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.bD = this.bC;
            if (this.eD()) {
                this.bC += (1.0F - this.bC) * 0.4F;
            } else {
                this.bC += (0.0F - this.bC) * 0.4F;
            }

            if (this.ay()) {
                this.bE = true;
                this.bF = false;
                this.bG = 0.0F;
                this.bH = 0.0F;
            } else if ((this.bE || this.bF) && this.bF) {
                if (this.bG == 0.0F) {
                    this.a(SoundEffects.ENTITY_WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                }

                this.bH = this.bG;
                this.bG += 0.05F;
                if (this.bH >= 2.0F) {
                    this.bE = false;
                    this.bF = false;
                    this.bH = 0.0F;
                    this.bG = 0.0F;
                }

                if (this.bG > 0.4F) {
                    float f = (float) this.locY();
                    int i = (int) (MathHelper.sin((this.bG - 0.4F) * 3.1415927F) * 7.0F);
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
        this.bE = false;
        this.bF = false;
        this.bH = 0.0F;
        this.bG = 0.0F;
        super.die(damagesource);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.8F;
    }

    @Override
    public int dU() {
        return this.isSitting() ? 20 : super.dU();
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            Entity entity = damagesource.getEntity();

            if (this.goalSit != null) {
                this.goalSit.setSitting(false);
            }

            if (entity != null && !(entity instanceof EntityHuman) && !(entity instanceof EntityArrow)) {
                f = (f + 1.0F) / 2.0F;
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Override
    public boolean B(Entity entity) {
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) ((int) this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue()));

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
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else if (this.world.isClientSide) {
            return this.i((EntityLiving) entityhuman) || item == Items.BONE && !this.isAngry();
        } else {
            if (this.isTamed()) {
                if (item.isFood() && item.getFoodInfo().c() && this.getHealth() < this.getMaxHealth()) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    this.heal((float) item.getFoodInfo().getNutrition());
                    return true;
                }

                if (!(item instanceof ItemDye)) {
                    boolean flag = super.a(entityhuman, enumhand);

                    if (!flag || this.isBaby()) {
                        this.goalSit.setSitting(!this.isSitting());
                    }

                    return flag;
                }

                EnumColor enumcolor = ((ItemDye) item).d();

                if (enumcolor != this.getCollarColor()) {
                    this.setCollarColor(enumcolor);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    return true;
                }

                if (this.i((EntityLiving) entityhuman) && !this.i(itemstack)) {
                    this.goalSit.setSitting(!this.isSitting());
                    this.jumping = false;
                    this.navigation.o();
                    this.setGoalTarget((EntityLiving) null);
                }
            } else if (item == Items.BONE && !this.isAngry()) {
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemstack.subtract(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.tame(entityhuman);
                    this.navigation.o();
                    this.setGoalTarget((EntityLiving) null);
                    this.goalSit.setSitting(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }

                return true;
            }

            return super.a(entityhuman, enumhand);
        }
    }

    @Override
    public boolean i(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return item.isFood() && item.getFoodInfo().c();
    }

    @Override
    public int getMaxSpawnGroup() {
        return 8;
    }

    public boolean isAngry() {
        return ((Byte) this.datawatcher.get(EntityWolf.bw) & 2) != 0;
    }

    public void setAngry(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityWolf.bw);

        if (flag) {
            this.datawatcher.set(EntityWolf.bw, (byte) (b0 | 2));
        } else {
            this.datawatcher.set(EntityWolf.bw, (byte) (b0 & -3));
        }

    }

    public EnumColor getCollarColor() {
        return EnumColor.fromColorIndex((Integer) this.datawatcher.get(EntityWolf.bB));
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.datawatcher.set(EntityWolf.bB, enumcolor.getColorIndex());
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

    public void v(boolean flag) {
        this.datawatcher.set(EntityWolf.bA, flag);
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

    public boolean eD() {
        return (Boolean) this.datawatcher.get(EntityWolf.bA);
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
