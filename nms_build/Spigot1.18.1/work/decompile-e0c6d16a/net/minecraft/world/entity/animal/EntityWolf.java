package net.minecraft.world.entity.animal;

import java.util.Random;
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
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
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
import net.minecraft.world.entity.EnumMobSpawn;
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
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3D;

public class EntityWolf extends EntityTameableAnimal implements IEntityAngerable {

    private static final DataWatcherObject<Boolean> DATA_INTERESTED_ID = DataWatcher.defineId(EntityWolf.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> DATA_COLLAR_COLOR = DataWatcher.defineId(EntityWolf.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_REMAINING_ANGER_TIME = DataWatcher.defineId(EntityWolf.class, DataWatcherRegistry.INT);
    public static final Predicate<EntityLiving> PREY_SELECTOR = (entityliving) -> {
        EntityTypes<?> entitytypes = entityliving.getType();

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
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.rangeOfSeconds(20, 39);
    @Nullable
    private UUID persistentAngerTarget;

    public EntityWolf(EntityTypes<? extends EntityWolf> entitytypes, World world) {
        super(entitytypes, world);
        this.setTame(false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(2, new PathfinderGoalSit(this));
        this.goalSelector.addGoal(3, new EntityWolf.a<>(this, EntityLlama.class, 24.0F, 1.5D, 1.5D));
        this.goalSelector.addGoal(4, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.addGoal(5, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.addGoal(6, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(7, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.addGoal(9, new PathfinderGoalBeg(this, 8.0F));
        this.goalSelector.addGoal(10, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(10, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, new PathfinderGoalOwnerHurtByTarget(this));
        this.targetSelector.addGoal(2, new PathfinderGoalOwnerHurtTarget(this));
        this.targetSelector.addGoal(3, (new PathfinderGoalHurtByTarget(this, new Class[0])).setAlertOthers());
        this.targetSelector.addGoal(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::isAngryAt));
        this.targetSelector.addGoal(5, new PathfinderGoalRandomTargetNonTamed<>(this, EntityAnimal.class, false, EntityWolf.PREY_SELECTOR));
        this.targetSelector.addGoal(6, new PathfinderGoalRandomTargetNonTamed<>(this, EntityTurtle.class, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
        this.targetSelector.addGoal(7, new PathfinderGoalNearestAttackableTarget<>(this, EntitySkeletonAbstract.class, false));
        this.targetSelector.addGoal(8, new PathfinderGoalUniversalAngerReset<>(this, true));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).add(GenericAttributes.MAX_HEALTH, 8.0D).add(GenericAttributes.ATTACK_DAMAGE, 2.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityWolf.DATA_INTERESTED_ID, false);
        this.entityData.define(EntityWolf.DATA_COLLAR_COLOR, EnumColor.RED.getId());
        this.entityData.define(EntityWolf.DATA_REMAINING_ANGER_TIME, 0);
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putByte("CollarColor", (byte) this.getCollarColor().getId());
        this.addPersistentAngerSaveData(nbttagcompound);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("CollarColor", 99)) {
            this.setCollarColor(EnumColor.byId(nbttagcompound.getInt("CollarColor")));
        }

        this.readPersistentAngerSaveData(this.level, nbttagcompound);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.isAngry() ? SoundEffects.WOLF_GROWL : (this.random.nextInt(3) == 0 ? (this.isTame() && this.getHealth() < 10.0F ? SoundEffects.WOLF_WHINE : SoundEffects.WOLF_PANT) : SoundEffects.WOLF_AMBIENT);
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.WOLF_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.WOLF_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level.isClientSide && this.isWet && !this.isShaking && !this.isPathFinding() && this.onGround) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
            this.level.broadcastEntityEvent(this, (byte) 8);
        }

        if (!this.level.isClientSide) {
            this.updatePersistentAnger((WorldServer) this.level, true);
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            this.interestedAngleO = this.interestedAngle;
            if (this.isInterested()) {
                this.interestedAngle += (1.0F - this.interestedAngle) * 0.4F;
            } else {
                this.interestedAngle += (0.0F - this.interestedAngle) * 0.4F;
            }

            if (this.isInWaterRainOrBubble()) {
                this.isWet = true;
                if (this.isShaking && !this.level.isClientSide) {
                    this.level.broadcastEntityEvent(this, (byte) 56);
                    this.cancelShake();
                }
            } else if ((this.isWet || this.isShaking) && this.isShaking) {
                if (this.shakeAnim == 0.0F) {
                    this.playSound(SoundEffects.WOLF_SHAKE, this.getSoundVolume(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                    this.gameEvent(GameEvent.WOLF_SHAKING);
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
                    float f = (float) this.getY();
                    int i = (int) (MathHelper.sin((this.shakeAnim - 0.4F) * 3.1415927F) * 7.0F);
                    Vec3D vec3d = this.getDeltaMovement();

                    for (int j = 0; j < i; ++j) {
                        float f1 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;
                        float f2 = (this.random.nextFloat() * 2.0F - 1.0F) * this.getBbWidth() * 0.5F;

                        this.level.addParticle(Particles.SPLASH, this.getX() + (double) f1, (double) (f + 0.8F), this.getZ() + (double) f2, vec3d.x, vec3d.y, vec3d.z);
                    }
                }
            }

        }
    }

    private void cancelShake() {
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

    public boolean isWet() {
        return this.isWet;
    }

    public float getWetShade(float f) {
        return Math.min(0.5F + MathHelper.lerp(f, this.shakeAnimO, this.shakeAnim) / 2.0F * 0.5F, 1.0F);
    }

    public float getBodyRollAngle(float f, float f1) {
        float f2 = (MathHelper.lerp(f, this.shakeAnimO, this.shakeAnim) + f1) / 1.8F;

        if (f2 < 0.0F) {
            f2 = 0.0F;
        } else if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        return MathHelper.sin(f2 * 3.1415927F) * MathHelper.sin(f2 * 3.1415927F * 11.0F) * 0.15F * 3.1415927F;
    }

    public float getHeadRollAngle(float f) {
        return MathHelper.lerp(f, this.interestedAngleO, this.interestedAngle) * 0.15F * 3.1415927F;
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.8F;
    }

    @Override
    public int getMaxHeadXRot() {
        return this.isInSittingPose() ? 20 : super.getMaxHeadXRot();
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else {
            Entity entity = damagesource.getEntity();

            this.setOrderedToSit(false);
            if (entity != null && !(entity instanceof EntityHuman) && !(entity instanceof EntityArrow)) {
                f = (f + 1.0F) / 2.0F;
            }

            return super.hurt(damagesource, f);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = entity.hurt(DamageSource.mobAttack(this), (float) ((int) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.doEnchantDamageEffects(this, entity);
        }

        return flag;
    }

    @Override
    public void setTame(boolean flag) {
        super.setTame(flag);
        if (flag) {
            this.getAttribute(GenericAttributes.MAX_HEALTH).setBaseValue(20.0D);
            this.setHealth(20.0F);
        } else {
            this.getAttribute(GenericAttributes.MAX_HEALTH).setBaseValue(8.0D);
        }

        this.getAttribute(GenericAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);
        Item item = itemstack.getItem();

        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(entityhuman) || this.isTame() || itemstack.is(Items.BONE) && !this.isTame() && !this.isAngry();

            return flag ? EnumInteractionResult.CONSUME : EnumInteractionResult.PASS;
        } else {
            if (this.isTame()) {
                if (this.isFood(itemstack) && this.getHealth() < this.getMaxHealth()) {
                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    this.heal((float) item.getFoodProperties().getNutrition());
                    this.gameEvent(GameEvent.MOB_INTERACT, this.eyeBlockPosition());
                    return EnumInteractionResult.SUCCESS;
                }

                if (!(item instanceof ItemDye)) {
                    EnumInteractionResult enuminteractionresult = super.mobInteract(entityhuman, enumhand);

                    if ((!enuminteractionresult.consumesAction() || this.isBaby()) && this.isOwnedBy(entityhuman)) {
                        this.setOrderedToSit(!this.isOrderedToSit());
                        this.jumping = false;
                        this.navigation.stop();
                        this.setTarget((EntityLiving) null);
                        return EnumInteractionResult.SUCCESS;
                    }

                    return enuminteractionresult;
                }

                EnumColor enumcolor = ((ItemDye) item).getDyeColor();

                if (enumcolor != this.getCollarColor()) {
                    this.setCollarColor(enumcolor);
                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }

                    return EnumInteractionResult.SUCCESS;
                }
            } else if (itemstack.is(Items.BONE) && !this.isAngry()) {
                if (!entityhuman.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.tame(entityhuman);
                    this.navigation.stop();
                    this.setTarget((EntityLiving) null);
                    this.setOrderedToSit(true);
                    this.level.broadcastEntityEvent(this, (byte) 7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte) 6);
                }

                return EnumInteractionResult.SUCCESS;
            }

            return super.mobInteract(entityhuman, enumhand);
        }
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 8) {
            this.isShaking = true;
            this.shakeAnim = 0.0F;
            this.shakeAnimO = 0.0F;
        } else if (b0 == 56) {
            this.cancelShake();
        } else {
            super.handleEntityEvent(b0);
        }

    }

    public float getTailAngle() {
        return this.isAngry() ? 1.5393804F : (this.isTame() ? (0.55F - (this.getMaxHealth() - this.getHealth()) * 0.02F) * 3.1415927F : 0.62831855F);
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return item.isEdible() && item.getFoodProperties().isMeat();
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 8;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return (Integer) this.entityData.get(EntityWolf.DATA_REMAINING_ANGER_TIME);
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        this.entityData.set(EntityWolf.DATA_REMAINING_ANGER_TIME, i);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(EntityWolf.PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    public EnumColor getCollarColor() {
        return EnumColor.byId((Integer) this.entityData.get(EntityWolf.DATA_COLLAR_COLOR));
    }

    public void setCollarColor(EnumColor enumcolor) {
        this.entityData.set(EntityWolf.DATA_COLLAR_COLOR, enumcolor.getId());
    }

    @Override
    public EntityWolf getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        EntityWolf entitywolf = (EntityWolf) EntityTypes.WOLF.create(worldserver);
        UUID uuid = this.getOwnerUUID();

        if (uuid != null) {
            entitywolf.setOwnerUUID(uuid);
            entitywolf.setTame(true);
        }

        return entitywolf;
    }

    public void setIsInterested(boolean flag) {
        this.entityData.set(EntityWolf.DATA_INTERESTED_ID, flag);
    }

    @Override
    public boolean canMate(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (!this.isTame()) {
            return false;
        } else if (!(entityanimal instanceof EntityWolf)) {
            return false;
        } else {
            EntityWolf entitywolf = (EntityWolf) entityanimal;

            return !entitywolf.isTame() ? false : (entitywolf.isInSittingPose() ? false : this.isInLove() && entitywolf.isInLove());
        }
    }

    public boolean isInterested() {
        return (Boolean) this.entityData.get(EntityWolf.DATA_INTERESTED_ID);
    }

    @Override
    public boolean wantsToAttack(EntityLiving entityliving, EntityLiving entityliving1) {
        if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
            if (entityliving instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entityliving;

                return !entitywolf.isTame() || entitywolf.getOwner() != entityliving1;
            } else {
                return entityliving instanceof EntityHuman && entityliving1 instanceof EntityHuman && !((EntityHuman) entityliving1).canHarmPlayer((EntityHuman) entityliving) ? false : (entityliving instanceof EntityHorseAbstract && ((EntityHorseAbstract) entityliving).isTamed() ? false : !(entityliving instanceof EntityTameableAnimal) || !((EntityTameableAnimal) entityliving).isTame());
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean canBeLeashed(EntityHuman entityhuman) {
        return !this.isAngry() && super.canBeLeashed(entityhuman);
    }

    @Override
    public Vec3D getLeashOffset() {
        return new Vec3D(0.0D, (double) (0.6F * this.getEyeHeight()), (double) (this.getBbWidth() * 0.4F));
    }

    public static boolean checkWolfSpawnRules(EntityTypes<EntityWolf> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getBlockState(blockposition.below()).is((Tag) TagsBlock.WOLVES_SPAWNABLE_ON) && isBrightEnoughToSpawn(generatoraccess, blockposition);
    }

    private class a<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityWolf wolf;

        public a(EntityWolf entitywolf, Class oclass, float f, double d0, double d1) {
            super(entitywolf, oclass, f, d0, d1);
            this.wolf = entitywolf;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.toAvoid instanceof EntityLlama ? !this.wolf.isTame() && this.avoidLlama((EntityLlama) this.toAvoid) : false;
        }

        private boolean avoidLlama(EntityLlama entityllama) {
            return entityllama.getStrength() >= EntityWolf.this.random.nextInt(5);
        }

        @Override
        public void start() {
            EntityWolf.this.setTarget((EntityLiving) null);
            super.start();
        }

        @Override
        public void tick() {
            EntityWolf.this.setTarget((EntityLiving) null);
            super.tick();
        }
    }
}
