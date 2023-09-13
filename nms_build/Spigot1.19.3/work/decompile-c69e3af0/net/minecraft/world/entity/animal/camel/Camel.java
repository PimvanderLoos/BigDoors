package net.minecraft.world.entity.animal.camel;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Dynamic;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IJumpable;
import net.minecraft.world.entity.ISaddleable;
import net.minecraft.world.entity.RiderShieldingMount;
import net.minecraft.world.entity.ai.BehaviorController;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.EntityAIBodyControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.SoundEffectType;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class Camel extends EntityHorseAbstract implements IJumpable, RiderShieldingMount, ISaddleable {

    public static final RecipeItemStack TEMPTATION_ITEM = RecipeItemStack.of(Items.CACTUS);
    public static final int DASH_COOLDOWN_TICKS = 55;
    private static final float RUNNING_SPEED_BONUS = 0.1F;
    private static final float DASH_VERTICAL_MOMENTUM = 1.4285F;
    private static final float DASH_HORIZONTAL_MOMENTUM = 22.2222F;
    private static final int SITDOWN_DURATION_TICKS = 40;
    private static final int STANDUP_DURATION_TICKS = 52;
    private static final int IDLE_MINIMAL_DURATION_TICKS = 80;
    private static final float SITTING_HEIGHT_DIFFERENCE = 1.43F;
    public static final DataWatcherObject<Boolean> DASH = DataWatcher.defineId(Camel.class, DataWatcherRegistry.BOOLEAN);
    public static final DataWatcherObject<Long> LAST_POSE_CHANGE_TICK = DataWatcher.defineId(Camel.class, DataWatcherRegistry.LONG);
    public final AnimationState walkAnimationState = new AnimationState();
    public final AnimationState sitAnimationState = new AnimationState();
    public final AnimationState sitPoseAnimationState = new AnimationState();
    public final AnimationState sitUpAnimationState = new AnimationState();
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState dashAnimationState = new AnimationState();
    private static final EntitySize SITTING_DIMENSIONS = EntitySize.scalable(EntityTypes.CAMEL.getWidth(), EntityTypes.CAMEL.getHeight() - 1.43F);
    private int dashCooldown = 0;
    private int idleAnimationTimeout = 0;

    public Camel(EntityTypes<? extends Camel> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.5F;
        Navigation navigation = (Navigation) this.getNavigation();

        navigation.setCanFloat(true);
        navigation.setCanWalkOverFences(true);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putBoolean("IsSitting", this.getPose() == EntityPose.SITTING);
        nbttagcompound.putLong("LastPoseTick", (Long) this.entityData.get(Camel.LAST_POSE_CHANGE_TICK));
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.getBoolean("IsSitting")) {
            this.setPose(EntityPose.SITTING);
        }

        this.resetLastPoseChangeTick(nbttagcompound.getLong("LastPoseTick"));
    }

    public static AttributeProvider.Builder createAttributes() {
        return createBaseHorseAttributes().add(GenericAttributes.MAX_HEALTH, 32.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.09000000357627869D).add(GenericAttributes.JUMP_STRENGTH, 0.41999998688697815D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(Camel.DASH, false);
        this.entityData.define(Camel.LAST_POSE_CHANGE_TICK, -52L);
    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        CamelAi.initMemories(this, worldaccess.getRandom());
        this.entityData.set(Camel.LAST_POSE_CHANGE_TICK, worldaccess.getLevel().getGameTime() - 52L);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected BehaviorController.b<Camel> brainProvider() {
        return CamelAi.brainProvider();
    }

    @Override
    protected void registerGoals() {}

    @Override
    protected BehaviorController<?> makeBrain(Dynamic<?> dynamic) {
        return CamelAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public EntitySize getDimensions(EntityPose entitypose) {
        return entitypose == EntityPose.SITTING ? Camel.SITTING_DIMENSIONS.scale(this.getScale()) : super.getDimensions(entitypose);
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height - 0.1F;
    }

    @Override
    public double getRiderShieldingHeight() {
        return 0.5D;
    }

    @Override
    protected void customServerAiStep() {
        this.level.getProfiler().push("camelBrain");
        BehaviorController<?> behaviorcontroller = this.getBrain();

        behaviorcontroller.tick((WorldServer) this.level, this);
        this.level.getProfiler().pop();
        this.level.getProfiler().push("camelActivityUpdate");
        CamelAi.updateActivity(this);
        this.level.getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isDashing() && this.dashCooldown < 55 && (this.onGround || this.isInWater())) {
            this.setDashing(false);
        }

        if (this.dashCooldown > 0) {
            --this.dashCooldown;
            if (this.dashCooldown == 0) {
                this.level.playSound((EntityHuman) null, this.blockPosition(), SoundEffects.CAMEL_DASH_READY, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }
        }

        if (this.level.isClientSide()) {
            this.setupAnimationStates();
        }

    }

    private void setupAnimationStates() {
        if (this.idleAnimationTimeout <= 0) {
            this.idleAnimationTimeout = this.random.nextInt(40) + 80;
            this.idleAnimationState.start(this.tickCount);
        } else {
            --this.idleAnimationTimeout;
        }

        switch (this.getPose()) {
            case STANDING:
                this.sitAnimationState.stop();
                this.sitPoseAnimationState.stop();
                this.dashAnimationState.animateWhen(this.isDashing(), this.tickCount);
                this.sitUpAnimationState.animateWhen(this.isInPoseTransition(), this.tickCount);
                this.walkAnimationState.animateWhen((this.onGround || this.hasControllingPassenger()) && this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6D, this.tickCount);
                break;
            case SITTING:
                this.walkAnimationState.stop();
                this.sitUpAnimationState.stop();
                this.dashAnimationState.stop();
                if (this.isSittingDown()) {
                    this.sitAnimationState.startIfStopped(this.tickCount);
                    this.sitPoseAnimationState.stop();
                } else {
                    this.sitAnimationState.stop();
                    this.sitPoseAnimationState.startIfStopped(this.tickCount);
                }
                break;
            default:
                this.walkAnimationState.stop();
                this.sitAnimationState.stop();
                this.sitPoseAnimationState.stop();
                this.sitUpAnimationState.stop();
                this.dashAnimationState.stop();
        }

    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isAlive()) {
            if (this.refuseToMove() && this.isOnGround()) {
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.0D, 1.0D, 0.0D));
                vec3d = vec3d.multiply(0.0D, 1.0D, 0.0D);
            }

            super.travel(vec3d);
        }
    }

    public boolean refuseToMove() {
        return this.isPoseSitting() || this.isInPoseTransition();
    }

    @Override
    protected float getDrivenMovementSpeed(EntityLiving entityliving) {
        float f = entityliving.isSprinting() && this.getJumpCooldown() == 0 ? 0.1F : 0.0F;

        return (float) this.getAttributeValue(GenericAttributes.MOVEMENT_SPEED) + f;
    }

    @Override
    protected boolean mountIgnoresControllerInput(EntityLiving entityliving) {
        boolean flag = this.isInPoseTransition();

        if (this.isPoseSitting() && !flag && entityliving.zza > 0.0F) {
            this.standUp();
        }

        return this.refuseToMove() || super.mountIgnoresControllerInput(entityliving);
    }

    @Override
    public boolean canJump(EntityHuman entityhuman) {
        return !this.refuseToMove() && this.getControllingPassenger() == entityhuman && super.canJump(entityhuman);
    }

    @Override
    public void onPlayerJump(int i) {
        if (this.isSaddled() && this.dashCooldown <= 0 && this.isOnGround()) {
            super.onPlayerJump(i);
        }
    }

    @Override
    protected void executeRidersJump(float f, float f1, float f2) {
        double d0 = this.getAttributeValue(GenericAttributes.JUMP_STRENGTH) * (double) this.getBlockJumpFactor() + this.getJumpBoostPower();

        this.addDeltaMovement(this.getLookAngle().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double) (22.2222F * f) * this.getAttributeValue(GenericAttributes.MOVEMENT_SPEED) * (double) this.getBlockSpeedFactor()).add(0.0D, (double) (1.4285F * f) * d0, 0.0D));
        this.dashCooldown = 55;
        this.setDashing(true);
        this.hasImpulse = true;
    }

    public boolean isDashing() {
        return (Boolean) this.entityData.get(Camel.DASH);
    }

    public void setDashing(boolean flag) {
        this.entityData.set(Camel.DASH, flag);
    }

    public boolean isPanicking() {
        return this.getBrain().checkMemory(MemoryModuleType.IS_PANICKING, MemoryStatus.VALUE_PRESENT);
    }

    @Override
    public void handleStartJump(int i) {
        this.playSound(SoundEffects.CAMEL_DASH, 1.0F, 1.0F);
        this.setDashing(true);
    }

    @Override
    public void handleStopJump() {}

    @Override
    public int getJumpCooldown() {
        return this.dashCooldown;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.CAMEL_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.CAMEL_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.CAMEL_HURT;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        if (iblockdata.getSoundType() == SoundEffectType.SAND) {
            this.playSound(SoundEffects.CAMEL_STEP_SAND, 1.0F, 1.0F);
        } else {
            this.playSound(SoundEffects.CAMEL_STEP, 1.0F, 1.0F);
        }

    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return Camel.TEMPTATION_ITEM.test(itemstack);
    }

    @Override
    public EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (entityhuman.isSecondaryUseActive()) {
            this.openCustomInventoryScreen(entityhuman);
            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            EnumInteractionResult enuminteractionresult = itemstack.interactLivingEntity(entityhuman, this, enumhand);

            if (enuminteractionresult.consumesAction()) {
                return enuminteractionresult;
            } else if (this.isFood(itemstack)) {
                return this.fedFood(entityhuman, itemstack);
            } else {
                if (this.getPassengers().size() < 2 && !this.isBaby()) {
                    this.doPlayerRide(entityhuman);
                }

                return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
            }
        }
    }

    @Override
    protected void onLeashDistance(float f) {
        if (f > 6.0F && this.isPoseSitting() && !this.isInPoseTransition()) {
            this.standUp();
        }

    }

    @Override
    protected boolean handleEating(EntityHuman entityhuman, ItemStack itemstack) {
        if (!this.isFood(itemstack)) {
            return false;
        } else {
            boolean flag = this.getHealth() < this.getMaxHealth();

            if (flag) {
                this.heal(2.0F);
            }

            boolean flag1 = this.isTamed() && this.getAge() == 0 && this.canFallInLove();

            if (flag1) {
                this.setInLove(entityhuman);
            }

            boolean flag2 = this.isBaby();

            if (flag2) {
                this.level.addParticle(Particles.HAPPY_VILLAGER, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 0.0D, 0.0D, 0.0D);
                if (!this.level.isClientSide) {
                    this.ageUp(10);
                }
            }

            if (!flag && !flag1 && !flag2) {
                return false;
            } else {
                if (!this.isSilent()) {
                    SoundEffect soundeffect = this.getEatingSound();

                    if (soundeffect != null) {
                        this.level.playSound((EntityHuman) null, this.getX(), this.getY(), this.getZ(), soundeffect, this.getSoundSource(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                    }
                }

                return true;
            }
        }
    }

    @Override
    protected boolean canPerformRearing() {
        return false;
    }

    @Override
    public boolean canMate(EntityAnimal entityanimal) {
        boolean flag;

        if (entityanimal != this && entityanimal instanceof Camel) {
            Camel camel = (Camel) entityanimal;

            if (this.canParent() && camel.canParent()) {
                flag = true;
                return flag;
            }
        }

        flag = false;
        return flag;
    }

    @Nullable
    @Override
    public Camel getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return (Camel) EntityTypes.CAMEL.create(worldserver);
    }

    @Nullable
    @Override
    protected SoundEffect getEatingSound() {
        return SoundEffects.CAMEL_EAT;
    }

    @Override
    protected void actuallyHurt(DamageSource damagesource, float f) {
        this.standUpPanic();
        super.actuallyHurt(damagesource, f);
    }

    @Override
    public void positionRider(Entity entity) {
        int i = this.getPassengers().indexOf(entity);

        if (i >= 0) {
            boolean flag = i == 0;
            float f = 0.5F;
            float f1 = (float) (this.isRemoved() ? 0.009999999776482582D : this.getBodyAnchorAnimationYOffset(flag, 0.0F) + entity.getMyRidingOffset());

            if (this.getPassengers().size() > 1) {
                if (!flag) {
                    f = -0.7F;
                }

                if (entity instanceof EntityAnimal) {
                    f += 0.2F;
                }
            }

            Vec3D vec3d = (new Vec3D(0.0D, 0.0D, (double) f)).yRot(-this.yBodyRot * 0.017453292F);

            entity.setPos(this.getX() + vec3d.x, this.getY() + (double) f1, this.getZ() + vec3d.z);
            this.clampRotation(entity);
        }
    }

    private double getBodyAnchorAnimationYOffset(boolean flag, float f) {
        double d0 = this.getPassengersRidingOffset();
        float f1 = this.getScale() * 1.43F;
        float f2 = f1 - this.getScale() * 0.2F;
        float f3 = f1 - f2;
        boolean flag1 = this.isInPoseTransition();
        boolean flag2 = this.getPose() == EntityPose.SITTING;

        if (flag1) {
            int i = flag2 ? 40 : 52;
            int j;
            float f4;

            if (flag2) {
                j = 28;
                f4 = flag ? 0.5F : 0.1F;
            } else {
                j = flag ? 24 : 32;
                f4 = flag ? 0.6F : 0.35F;
            }

            float f5 = (float) this.getPoseTime() + f;
            boolean flag3 = f5 < (float) j;
            float f6 = flag3 ? f5 / (float) j : (f5 - (float) j) / (float) (i - j);
            float f7 = f1 - f4 * f2;

            d0 += flag2 ? (double) MathHelper.lerp(f6, flag3 ? f1 : f7, flag3 ? f7 : f3) : (double) MathHelper.lerp(f6, flag3 ? f3 - f1 : f3 - f7, flag3 ? f3 - f7 : 0.0F);
        }

        if (flag2 && !flag1) {
            d0 += (double) f3;
        }

        return d0;
    }

    @Override
    public Vec3D getLeashOffset(float f) {
        return new Vec3D(0.0D, this.getBodyAnchorAnimationYOffset(true, f) - (double) (0.2F * this.getScale()), (double) (this.getBbWidth() * 0.56F));
    }

    @Override
    public double getPassengersRidingOffset() {
        return (double) (this.getDimensions(this.getPose()).height - (this.isBaby() ? 0.35F : 0.6F));
    }

    @Override
    public void onPassengerTurned(Entity entity) {
        if (this.getControllingPassenger() != entity) {
            this.clampRotation(entity);
        }

    }

    private void clampRotation(Entity entity) {
        entity.setYBodyRot(this.getYRot());
        float f = entity.getYRot();
        float f1 = MathHelper.wrapDegrees(f - this.getYRot());
        float f2 = MathHelper.clamp(f1, -160.0F, 160.0F);

        entity.yRotO += f2 - f1;
        float f3 = f + f2 - f1;

        entity.setYRot(f3);
        entity.setYHeadRot(f3);
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengers().size() <= 2;
    }

    @Nullable
    @Override
    public EntityLiving getControllingPassenger() {
        if (!this.getPassengers().isEmpty() && this.isSaddled()) {
            Entity entity = (Entity) this.getPassengers().get(0);

            if (entity instanceof EntityLiving) {
                EntityLiving entityliving = (EntityLiving) entity;

                return entityliving;
            }
        }

        return null;
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        PacketDebug.sendEntityBrain(this);
    }

    public boolean isPoseSitting() {
        return this.getPose() == EntityPose.SITTING;
    }

    public boolean isInPoseTransition() {
        long i = this.getPoseTime();
        boolean flag;

        switch (this.getPose()) {
            case STANDING:
                flag = i < 52L;
                break;
            case SITTING:
                flag = i < 40L;
                break;
            default:
                flag = false;
        }

        return flag;
    }

    private boolean isSittingDown() {
        return this.getPose() == EntityPose.SITTING && this.getPoseTime() < 40L;
    }

    public void sitDown() {
        if (!this.hasPose(EntityPose.SITTING)) {
            this.playSound(SoundEffects.CAMEL_SIT, 1.0F, 1.0F);
            this.setPose(EntityPose.SITTING);
            this.resetLastPoseChangeTick(this.level.getGameTime());
        }
    }

    public void standUp() {
        if (!this.hasPose(EntityPose.STANDING)) {
            this.playSound(SoundEffects.CAMEL_STAND, 1.0F, 1.0F);
            this.setPose(EntityPose.STANDING);
            this.resetLastPoseChangeTick(this.level.getGameTime());
        }
    }

    public void standUpPanic() {
        this.setPose(EntityPose.STANDING);
        this.resetLastPoseChangeTick(this.level.getGameTime() - 52L);
    }

    @VisibleForTesting
    public void resetLastPoseChangeTick(long i) {
        this.entityData.set(Camel.LAST_POSE_CHANGE_TICK, i);
    }

    public long getPoseTime() {
        return this.level.getGameTime() - (Long) this.entityData.get(Camel.LAST_POSE_CHANGE_TICK);
    }

    @Override
    public SoundEffect getSaddleSoundEvent() {
        return SoundEffects.CAMEL_SADDLE;
    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (!this.firstTick && Camel.DASH.equals(datawatcherobject)) {
            this.dashCooldown = this.dashCooldown == 0 ? 55 : this.dashCooldown;
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    @Override
    protected EntityAIBodyControl createBodyControl() {
        return new Camel.a(this);
    }

    @Override
    public boolean isTamed() {
        return true;
    }

    @Override
    public void openCustomInventoryScreen(EntityHuman entityhuman) {
        if (!this.level.isClientSide) {
            entityhuman.openHorseInventory(this, this.inventory);
        }

    }

    private class a extends EntityAIBodyControl {

        public a(Camel camel) {
            super(camel);
        }

        @Override
        public void clientTick() {
            if (!Camel.this.refuseToMove()) {
                super.clientTick();
            }

        }
    }
}
