package net.minecraft.world.entity.animal;

import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBeg;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowOwner;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.PathfinderGoalSit;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalOwnerHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalOwnerHurtTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalRandomTargetNonTamed;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.animal.horse.EntityLlama;
import net.minecraft.world.entity.monster.EntityCreeper;
import net.minecraft.world.entity.monster.EntityGhast;
import net.minecraft.world.entity.monster.EntitySkeletonAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

public class EntityWolf extends EntityTameableAnimal implements IEntityAngerable {

    private static final DataWatcherObject<Boolean> DATA_INTERESTED_ID = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_COLLAR_COLOR = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_REMAINING_ANGER_TIME = DataWatcher.a(EntityWolf.class, DataWatcherRegistry.INT);
    public static final Predicate<EntityLiving> PREY_SELECTOR = (entityliving) -> {
        EntityTypes<?> entitytypes = entityliving.getEntityType();

        return entitytypes == EntityTypes.SHEEP || entitytypes == EntityTypes.RABBIT || entitytypes == EntityTypes.FOX;
    };
    private static final float START_HEALTH = 8.0F;
    private static final float TAME_HEALTH = 20.0F;
    private float interestedAngle;
    private float interestedAngleO;
    private boolean isWet;
    private boolean isShaking;
    private float shakeAnim;
    private float shakeAnimO;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.a(20, 39);
    private UUID persistentAngerTarget;

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
        this.targetSelector.a(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::a_));
        this.targetSelector.a(5, new PathfinderGoalRandomTargetNonTamed<>(this, EntityAnimal.class, false, EntityWolf.PREY_SELECTOR));
        this.targetSelector.a(6, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.a(7, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeletonAbstract.class, false));
        this.targetSelector.a(8, new PathfinderGoalUniversalAngerReset<>(this, true));
    }

    public static AttributeProvider.Builder fE() {
        return EntityInsentient.w().a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).a(GenericAttributes.MAX_HEALTH, 8.0D).a(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityWolf.DATA_INTERESTED_ID, false);
        this.entityData.register(EntityWolf.DATA_COLLAR_COLOR, EnumColor.RED.getColorIndex());
        this.entityData.register(EntityWolf.DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.WOLF_STEP, 0.15F, 1.0F);
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

        this.a(this.level, nbttagcompound);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.isAngry() ? SoundEffects.WOLF_GROWL : (this.random.nextInt(3) == 0 ? (this.isTamed() && this.getHealth() < 10.0F ? SoundEffects.WOLF_WHINE : SoundEffects.WOLF_PANT) : SoundEffects.WOLF_AMBIENT);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.WOLF_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.level.isClientSide && this.isWet && !this.isShaking && !this.fu() && this.onGround) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level.broadcastEntityEffect(this, (byte) 8);
        }

        if (!this.level.isClientSide) {
            this.a((WorldServer) this.level, true);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.interestedAngleO = this.interestedAngle;
            if (this.fI()) {
                this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
            } else {
                this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
            }

            if (this.aN()) {
                this.isWet = true;
                if (this.isShaking && !this.level.isClientSide) {
                    this.level.broadcastEntityEffect(this, (byte) 56);
                    this.fJ();
                }
            } else if ((this.isWet || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(SoundEffects.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.a(GameEvent.WOLF_SHAKING);
                }

                this.shakeAnimO = this.shakeAnim;
                this.shakeAnim += 0.05F;
                if (this.shakeAnimO >= 2.0F) {
                    this.isWet = false;
                    this.isShaking = false;
                    this.shakeAnimO = 0.0F;
                    this.shakeAnim = 0.0F;
                }

                if (this.shakeAnim > 0.4F) {
                    float f = (float) this.locY();
                    int i = (int) (MathHelper.sin((this.shakeAnim - 0.4F) * 3.1415927F) * 7.0F);
                    Vec3D vec3d = this.getMot();

                    for (int j = 0; j < i; ++j) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getWidth() * 0.5F;

                        this.level.addParticle(Particles.SPLASH, this.locX() + (double) f1, (double) (f + 0.8F), this.locZ() + (double) f2, vec3d.x, vec3d.y, vec3d.z);
                    }
                }
            }

        }
    }

    private void fJ() {
        this.isShaking = false;
        this.shakeAnim = 0.0F;
        this.shakeAnimO = 0.0F;
    }

    @Override
    public void die(DamageSource damagesource) {
        this.isWet = false;
        this.isShaking = false;
        this.shakeAnimO = 0.0F;
        this.shakeAnim = 0.0F;
        super.die(damagesource);
    }

    public boolean fF() {
        return this.isWet;
    }

    public float z(float f) {
        return Math.min(0.5F + MathHelper.h(f, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.5F, 1.0F);
    }

    public float f(float f, float f1) {
        float f2 = (MathHelper.h(f, this.shakeAnimO, this.shakeAnim) + f1) / 1.8F;

        if (f2 < 0.0F) {
            f2 = 0.0F;
        } else if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        return MathHelper.sin(f2 * 3.1415927F) * MathHelper.sin(f2 * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
    }

    public float A(float f) {
        return MathHelper.h(f, this.interestedAngleO, this.interestedAngle) * 0.15F * 3.1415927F;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.8F;
    }

    @Override
    public int eZ() {
        return this.isSitting() ? 20 : super.eZ();
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

        if (this.level.isClientSide) {
            boolean flag = this.j((EntityLiving) entityhuman) || this.isTamed() || itemstack.a(Items.BONE) && !this.isTamed() && !this.isAngry();

            return flag ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS;
        } else {
            if (this.isTamed()) {
                if (this.isBreedItem(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.subtract(1);
                    }

                    this.heal((float) item.getFoodInfo().getNutrition());
                    this.a(GameEvent.MOB_INTERACT, this.cT());
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
                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.subtract(1);
                    }

                    return EnumInteractionResult.SUCCESS;
                }
            } else if (itemstack.a(Items.BONE) && !this.isAngry()) {
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.subtract(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.tame(entityhuman);
                    this.navigation.o();
                    this.setGoalTarget((EntityLiving) null);
                    this.setWillSit(true);
                    this.level.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEffect(this, (byte) 6);
                }

                return EnumInteractionResult.SUCCESS;
            }

            return super.b(entityhuman, enumhand);
        }
    }

    @Override
    public void a(byte b0) {
        if (b0 == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (b0 == 56) {
            this.fJ();
        } else {
            super.a(b0);
        }

    }

    public float fG() {
        return this.isAngry() ? 1.5393804F : (this.isTamed() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * 3.1415927F : 0.62831855F);
    }

    @Override
    public boolean isBreedItem(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return item.isFood() && item.getFoodInfo().c();
    }

    @Override
    public int getMaxSpawnGroup() {
        return 8;
    }

    @Override
    public int getAnger() {
        return (Integer) this.entityData.get(EntityWolf.DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setAnger(int i) {
        this.entityData.set(EntityWolf.DATA_REMAINING_ANGER_TIME, i);
    }

    @Override
    public void anger() {
        this.setAnger(EntityWolf.PERSISTENT_ANGER_TIME.a(this.random));
    }

    @Nullable
    @Override
    public UUID getAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    public EnumColor getCollarColor() {
        return EnumColor.fromColorIndex((Integer) this.entityData.get(EntityWolf.DATA_COLLAR_COLOR));
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.entityData.set(EntityWolf.DATA_COLLAR_COLOR, enumcolor.getColorIndex());
    }

    @Override
    public EntityWolf createChild(WorldServer worldserver, EntityAgeable entityageable) {
        EntityWolf entitywolf = (EntityWolf) EntityTypes.WOLF.a((World) worldserver);
        UUID uuid = this.getOwnerUUID();

        if (uuid != null) {
            entitywolf.setOwnerUUID(uuid);
            entitywolf.setTamed(true);
        }

        return entitywolf;
    }

    public void z(boolean flag) {
        this.entityData.set(EntityWolf.DATA_INTERESTED_ID, flag);
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

    public boolean fI() {
        return (Boolean) this.entityData.get(EntityWolf.DATA_INTERESTED_ID);
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

    @Override
    public Vec3D cu() {
        return new Vec3D(0.0D, (double) (0.6F * this.getHeadHeight()), (double) (this.getWidth() * 0.4F));
    }

    private class a<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityWolf wolf;

        public a(EntityWolf entitywolf, Class oclass, float f, double d0, double d1) {
            super(entitywolf, oclass, f, d0, d1);
            this.wolf = entitywolf;
        }

        @Override
        public boolean a() {
            return super.a() && this.toAvoid instanceof EntityLlama ? !this.wolf.isTamed() && this.a((EntityLlama) this.toAvoid) : false;
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
