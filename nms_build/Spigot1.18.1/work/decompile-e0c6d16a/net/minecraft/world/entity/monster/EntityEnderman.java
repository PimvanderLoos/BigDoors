package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.util.TimeRange;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSourceIndirect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalUniversalAngerReset;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.item.alchemy.PotionUtil;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class EntityEnderman extends EntityMonster implements IEntityAngerable {

    private static final UUID SPEED_MODIFIER_ATTACKING_UUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier SPEED_MODIFIER_ATTACKING = new AttributeModifier(EntityEnderman.SPEED_MODIFIER_ATTACKING_UUID, "Attacking speed boost", 0.15000000596046448D, AttributeModifier.Operation.ADDITION);
    private static final int DELAY_BETWEEN_CREEPY_STARE_SOUND = 400;
    private static final int MIN_DEAGGRESSION_TIME = 600;
    private static final DataWatcherObject<Optional<IBlockData>> DATA_CARRY_STATE = DataWatcher.defineId(EntityEnderman.class, DataWatcherRegistry.BLOCK_STATE);
    private static final DataWatcherObject<Boolean> DATA_CREEPY = DataWatcher.defineId(EntityEnderman.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> DATA_STARED_AT = DataWatcher.defineId(EntityEnderman.class, DataWatcherRegistry.BOOLEAN);
    private int lastStareSound = Integer.MIN_VALUE;
    private int targetChangeTime;
    private static final UniformInt PERSISTENT_ANGER_TIME = TimeRange.rangeOfSeconds(20, 39);
    private int remainingPersistentAngerTime;
    @Nullable
    private UUID persistentAngerTarget;

    public EntityEnderman(EntityTypes<? extends EntityEnderman> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
        this.setPathfindingMalus(PathType.WATER, -1.0F);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(1, new EntityEnderman.a(this));
        this.goalSelector.addGoal(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.addGoal(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.addGoal(10, new EntityEnderman.PathfinderGoalEndermanPlaceBlock(this));
        this.goalSelector.addGoal(11, new EntityEnderman.PathfinderGoalEndermanPickupBlock(this));
        this.targetSelector.addGoal(1, new EntityEnderman.PathfinderGoalPlayerWhoLookedAtTarget(this, this::isAngryAt));
        this.targetSelector.addGoal(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityEndermite.class, true, false));
        this.targetSelector.addGoal(4, new PathfinderGoalUniversalAngerReset<>(this, false));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MAX_HEALTH, 40.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D).add(GenericAttributes.ATTACK_DAMAGE, 7.0D).add(GenericAttributes.FOLLOW_RANGE, 64.0D);
    }

    @Override
    public void setTarget(@Nullable EntityLiving entityliving) {
        super.setTarget(entityliving);
        AttributeModifiable attributemodifiable = this.getAttribute(GenericAttributes.MOVEMENT_SPEED);

        if (entityliving == null) {
            this.targetChangeTime = 0;
            this.entityData.set(EntityEnderman.DATA_CREEPY, false);
            this.entityData.set(EntityEnderman.DATA_STARED_AT, false);
            attributemodifiable.removeModifier(EntityEnderman.SPEED_MODIFIER_ATTACKING);
        } else {
            this.targetChangeTime = this.tickCount;
            this.entityData.set(EntityEnderman.DATA_CREEPY, true);
            if (!attributemodifiable.hasModifier(EntityEnderman.SPEED_MODIFIER_ATTACKING)) {
                attributemodifiable.addTransientModifier(EntityEnderman.SPEED_MODIFIER_ATTACKING);
            }
        }

    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityEnderman.DATA_CARRY_STATE, Optional.empty());
        this.entityData.define(EntityEnderman.DATA_CREEPY, false);
        this.entityData.define(EntityEnderman.DATA_STARED_AT, false);
    }

    @Override
    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(EntityEnderman.PERSISTENT_ANGER_TIME.sample(this.random));
    }

    @Override
    public void setRemainingPersistentAngerTime(int i) {
        this.remainingPersistentAngerTime = i;
    }

    @Override
    public int getRemainingPersistentAngerTime() {
        return this.remainingPersistentAngerTime;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable UUID uuid) {
        this.persistentAngerTarget = uuid;
    }

    @Nullable
    @Override
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void playStareSound() {
        if (this.tickCount >= this.lastStareSound + 400) {
            this.lastStareSound = this.tickCount;
            if (!this.isSilent()) {
                this.level.playLocalSound(this.getX(), this.getEyeY(), this.getZ(), SoundEffects.ENDERMAN_STARE, this.getSoundSource(), 2.5F, 1.0F, false);
            }
        }

    }

    @Override
    public void onSyncedDataUpdated(DataWatcherObject<?> datawatcherobject) {
        if (EntityEnderman.DATA_CREEPY.equals(datawatcherobject) && this.hasBeenStaredAt() && this.level.isClientSide) {
            this.playStareSound();
        }

        super.onSyncedDataUpdated(datawatcherobject);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        IBlockData iblockdata = this.getCarriedBlock();

        if (iblockdata != null) {
            nbttagcompound.put("carriedBlockState", GameProfileSerializer.writeBlockState(iblockdata));
        }

        this.addPersistentAngerSaveData(nbttagcompound);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        IBlockData iblockdata = null;

        if (nbttagcompound.contains("carriedBlockState", 10)) {
            iblockdata = GameProfileSerializer.readBlockState(nbttagcompound.getCompound("carriedBlockState"));
            if (iblockdata.isAir()) {
                iblockdata = null;
            }
        }

        this.setCarriedBlock(iblockdata);
        this.readPersistentAngerSaveData(this.level, nbttagcompound);
    }

    boolean isLookingAtMe(EntityHuman entityhuman) {
        ItemStack itemstack = (ItemStack) entityhuman.getInventory().armor.get(3);

        if (itemstack.is(Blocks.CARVED_PUMPKIN.asItem())) {
            return false;
        } else {
            Vec3D vec3d = entityhuman.getViewVector(1.0F).normalize();
            Vec3D vec3d1 = new Vec3D(this.getX() - entityhuman.getX(), this.getEyeY() - entityhuman.getEyeY(), this.getZ() - entityhuman.getZ());
            double d0 = vec3d1.length();

            vec3d1 = vec3d1.normalize();
            double d1 = vec3d.dot(vec3d1);

            return d1 > 1.0D - 0.025D / d0 ? entityhuman.hasLineOfSight(this) : false;
        }
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 2.55F;
    }

    @Override
    public void aiStep() {
        if (this.level.isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.level.addParticle(Particles.PORTAL, this.getRandomX(0.5D), this.getRandomY() - 0.25D, this.getRandomZ(0.5D), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        this.jumping = false;
        if (!this.level.isClientSide) {
            this.updatePersistentAnger((WorldServer) this.level, true);
        }

        super.aiStep();
    }

    @Override
    public boolean isSensitiveToWater() {
        return true;
    }

    @Override
    protected void customServerAiStep() {
        if (this.level.isDay() && this.tickCount >= this.targetChangeTime + 600) {
            float f = this.getBrightness();

            if (f > 0.5F && this.level.canSeeSky(this.blockPosition()) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setTarget((EntityLiving) null);
                this.teleport();
            }
        }

        super.customServerAiStep();
    }

    protected boolean teleport() {
        if (!this.level.isClientSide() && this.isAlive()) {
            double d0 = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
            double d1 = this.getY() + (double) (this.random.nextInt(64) - 32);
            double d2 = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;

            return this.teleport(d0, d1, d2);
        } else {
            return false;
        }
    }

    boolean teleportTowards(Entity entity) {
        Vec3D vec3d = new Vec3D(this.getX() - entity.getX(), this.getY(0.5D) - entity.getEyeY(), this.getZ() - entity.getZ());

        vec3d = vec3d.normalize();
        double d0 = 16.0D;
        double d1 = this.getX() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double d2 = this.getY() + (double) (this.random.nextInt(16) - 8) - vec3d.y * 16.0D;
        double d3 = this.getZ() + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;

        return this.teleport(d1, d2, d3);
    }

    private boolean teleport(double d0, double d1, double d2) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(d0, d1, d2);

        while (blockposition_mutableblockposition.getY() > this.level.getMinBuildHeight() && !this.level.getBlockState(blockposition_mutableblockposition).getMaterial().blocksMotion()) {
            blockposition_mutableblockposition.move(EnumDirection.DOWN);
        }

        IBlockData iblockdata = this.level.getBlockState(blockposition_mutableblockposition);
        boolean flag = iblockdata.getMaterial().blocksMotion();
        boolean flag1 = iblockdata.getFluidState().is((Tag) TagsFluid.WATER);

        if (flag && !flag1) {
            boolean flag2 = this.randomTeleport(d0, d1, d2, true);

            if (flag2 && !this.isSilent()) {
                this.level.playSound((EntityHuman) null, this.xo, this.yo, this.zo, SoundEffects.ENDERMAN_TELEPORT, this.getSoundSource(), 1.0F, 1.0F);
                this.playSound(SoundEffects.ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag2;
        } else {
            return false;
        }
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.isCreepy() ? SoundEffects.ENDERMAN_SCREAM : SoundEffects.ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.ENDERMAN_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.ENDERMAN_DEATH;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropCustomDeathLoot(damagesource, i, flag);
        IBlockData iblockdata = this.getCarriedBlock();

        if (iblockdata != null) {
            this.spawnAtLocation((IMaterial) iblockdata.getBlock());
        }

    }

    public void setCarriedBlock(@Nullable IBlockData iblockdata) {
        this.entityData.set(EntityEnderman.DATA_CARRY_STATE, Optional.ofNullable(iblockdata));
    }

    @Nullable
    public IBlockData getCarriedBlock() {
        return (IBlockData) ((Optional) this.entityData.get(EntityEnderman.DATA_CARRY_STATE)).orElse((Object) null);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else if (damagesource instanceof EntityDamageSourceIndirect) {
            Entity entity = damagesource.getDirectEntity();
            boolean flag;

            if (entity instanceof EntityPotion) {
                flag = this.hurtWithCleanWater(damagesource, (EntityPotion) entity, f);
            } else {
                flag = false;
            }

            for (int i = 0; i < 64; ++i) {
                if (this.teleport()) {
                    return true;
                }
            }

            return flag;
        } else {
            boolean flag1 = super.hurt(damagesource, f);

            if (!this.level.isClientSide() && !(damagesource.getEntity() instanceof EntityLiving) && this.random.nextInt(10) != 0) {
                this.teleport();
            }

            return flag1;
        }
    }

    private boolean hurtWithCleanWater(DamageSource damagesource, EntityPotion entitypotion, float f) {
        ItemStack itemstack = entitypotion.getItem();
        PotionRegistry potionregistry = PotionUtil.getPotion(itemstack);
        List<MobEffect> list = PotionUtil.getMobEffects(itemstack);
        boolean flag = potionregistry == Potions.WATER && list.isEmpty();

        return flag ? super.hurt(damagesource, f) : false;
    }

    public boolean isCreepy() {
        return (Boolean) this.entityData.get(EntityEnderman.DATA_CREEPY);
    }

    public boolean hasBeenStaredAt() {
        return (Boolean) this.entityData.get(EntityEnderman.DATA_STARED_AT);
    }

    public void setBeingStaredAt() {
        this.entityData.set(EntityEnderman.DATA_STARED_AT, true);
    }

    @Override
    public boolean requiresCustomPersistence() {
        return super.requiresCustomPersistence() || this.getCarriedBlock() != null;
    }

    private static class a extends PathfinderGoal {

        private final EntityEnderman enderman;
        @Nullable
        private EntityLiving target;

        public a(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            this.target = this.enderman.getTarget();
            if (!(this.target instanceof EntityHuman)) {
                return false;
            } else {
                double d0 = this.target.distanceToSqr((Entity) this.enderman);

                return d0 > 256.0D ? false : this.enderman.isLookingAtMe((EntityHuman) this.target);
            }
        }

        @Override
        public void start() {
            this.enderman.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.enderman.getLookControl().setLookAt(this.target.getX(), this.target.getEyeY(), this.target.getZ());
        }
    }

    private static class PathfinderGoalEndermanPlaceBlock extends PathfinderGoal {

        private final EntityEnderman enderman;

        public PathfinderGoalEndermanPlaceBlock(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
        }

        @Override
        public boolean canUse() {
            return this.enderman.getCarriedBlock() == null ? false : (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? false : this.enderman.getRandom().nextInt(reducedTickDelay(2000)) == 0);
        }

        @Override
        public void tick() {
            Random random = this.enderman.getRandom();
            World world = this.enderman.level;
            int i = MathHelper.floor(this.enderman.getX() - 1.0D + random.nextDouble() * 2.0D);
            int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 2.0D);
            int k = MathHelper.floor(this.enderman.getZ() - 1.0D + random.nextDouble() * 2.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getBlockState(blockposition);
            BlockPosition blockposition1 = blockposition.below();
            IBlockData iblockdata1 = world.getBlockState(blockposition1);
            IBlockData iblockdata2 = this.enderman.getCarriedBlock();

            if (iblockdata2 != null) {
                iblockdata2 = Block.updateFromNeighbourShapes(iblockdata2, this.enderman.level, blockposition);
                if (this.canPlaceBlock(world, blockposition, iblockdata2, iblockdata, iblockdata1, blockposition1)) {
                    world.setBlock(blockposition, iblockdata2, 3);
                    world.gameEvent(this.enderman, GameEvent.BLOCK_PLACE, blockposition);
                    this.enderman.setCarriedBlock((IBlockData) null);
                }

            }
        }

        private boolean canPlaceBlock(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, BlockPosition blockposition1) {
            return iblockdata1.isAir() && !iblockdata2.isAir() && !iblockdata2.is(Blocks.BEDROCK) && iblockdata2.isCollisionShapeFullBlock(world, blockposition1) && iblockdata.canSurvive(world, blockposition) && world.getEntities(this.enderman, AxisAlignedBB.unitCubeFromLowerCorner(Vec3D.atLowerCornerOf(blockposition))).isEmpty();
        }
    }

    private static class PathfinderGoalEndermanPickupBlock extends PathfinderGoal {

        private final EntityEnderman enderman;

        public PathfinderGoalEndermanPickupBlock(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
        }

        @Override
        public boolean canUse() {
            return this.enderman.getCarriedBlock() != null ? false : (!this.enderman.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? false : this.enderman.getRandom().nextInt(reducedTickDelay(20)) == 0);
        }

        @Override
        public void tick() {
            Random random = this.enderman.getRandom();
            World world = this.enderman.level;
            int i = MathHelper.floor(this.enderman.getX() - 2.0D + random.nextDouble() * 4.0D);
            int j = MathHelper.floor(this.enderman.getY() + random.nextDouble() * 3.0D);
            int k = MathHelper.floor(this.enderman.getZ() - 2.0D + random.nextDouble() * 4.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getBlockState(blockposition);
            Vec3D vec3d = new Vec3D((double) this.enderman.getBlockX() + 0.5D, (double) j + 0.5D, (double) this.enderman.getBlockZ() + 0.5D);
            Vec3D vec3d1 = new Vec3D((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D);
            MovingObjectPositionBlock movingobjectpositionblock = world.clip(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.OUTLINE, RayTrace.FluidCollisionOption.NONE, this.enderman));
            boolean flag = movingobjectpositionblock.getBlockPos().equals(blockposition);

            if (iblockdata.is((Tag) TagsBlock.ENDERMAN_HOLDABLE) && flag) {
                world.removeBlock(blockposition, false);
                world.gameEvent(this.enderman, GameEvent.BLOCK_DESTROY, blockposition);
                this.enderman.setCarriedBlock(iblockdata.getBlock().defaultBlockState());
            }

        }
    }

    private static class PathfinderGoalPlayerWhoLookedAtTarget extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        private final EntityEnderman enderman;
        @Nullable
        private EntityHuman pendingTarget;
        private int aggroTime;
        private int teleportTime;
        private final PathfinderTargetCondition startAggroTargetConditions;
        private final PathfinderTargetCondition continueAggroTargetConditions = PathfinderTargetCondition.forCombat().ignoreLineOfSight();

        public PathfinderGoalPlayerWhoLookedAtTarget(EntityEnderman entityenderman, @Nullable Predicate<EntityLiving> predicate) {
            super(entityenderman, EntityHuman.class, 10, false, false, predicate);
            this.enderman = entityenderman;
            this.startAggroTargetConditions = PathfinderTargetCondition.forCombat().range(this.getFollowDistance()).selector((entityliving) -> {
                return entityenderman.isLookingAtMe((EntityHuman) entityliving);
            });
        }

        @Override
        public boolean canUse() {
            this.pendingTarget = this.enderman.level.getNearestPlayer(this.startAggroTargetConditions, this.enderman);
            return this.pendingTarget != null;
        }

        @Override
        public void start() {
            this.aggroTime = this.adjustedTickDelay(5);
            this.teleportTime = 0;
            this.enderman.setBeingStaredAt();
        }

        @Override
        public void stop() {
            this.pendingTarget = null;
            super.stop();
        }

        @Override
        public boolean canContinueToUse() {
            if (this.pendingTarget != null) {
                if (!this.enderman.isLookingAtMe(this.pendingTarget)) {
                    return false;
                } else {
                    this.enderman.lookAt(this.pendingTarget, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.target != null && this.continueAggroTargetConditions.test(this.enderman, this.target) ? true : super.canContinueToUse();
            }
        }

        @Override
        public void tick() {
            if (this.enderman.getTarget() == null) {
                super.setTarget((EntityLiving) null);
            }

            if (this.pendingTarget != null) {
                if (--this.aggroTime <= 0) {
                    this.target = this.pendingTarget;
                    this.pendingTarget = null;
                    super.start();
                }
            } else {
                if (this.target != null && !this.enderman.isPassenger()) {
                    if (this.enderman.isLookingAtMe((EntityHuman) this.target)) {
                        if (this.target.distanceToSqr((Entity) this.enderman) < 16.0D) {
                            this.enderman.teleport();
                        }

                        this.teleportTime = 0;
                    } else if (this.target.distanceToSqr((Entity) this.enderman) > 256.0D && this.teleportTime++ >= this.adjustedTickDelay(30) && this.enderman.teleportTowards(this.target)) {
                        this.teleportTime = 0;
                    }
                }

                super.tick();
            }

        }
    }
}
