package net.minecraft.world.entity.animal;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.tags.TagsItem;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingLookControl;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreath;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFollowBoat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomSwim;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWater;
import net.minecraft.world.entity.ai.goal.PathfinderGoalWaterJump;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityGuardian;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.Vec3D;

public class EntityDolphin extends EntityWaterAnimal {

    private static final DataWatcherObject<BlockPosition> TREASURE_POS = DataWatcher.defineId(EntityDolphin.class, DataWatcherRegistry.BLOCK_POS);
    private static final DataWatcherObject<Boolean> GOT_FISH = DataWatcher.defineId(EntityDolphin.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Integer> MOISTNESS_LEVEL = DataWatcher.defineId(EntityDolphin.class, DataWatcherRegistry.INT);
    static final PathfinderTargetCondition SWIM_WITH_PLAYER_TARGETING = PathfinderTargetCondition.forNonCombat().range(10.0D).ignoreLineOfSight();
    public static final int TOTAL_AIR_SUPPLY = 4800;
    private static final int TOTAL_MOISTNESS_LEVEL = 2400;
    public static final Predicate<EntityItem> ALLOWED_ITEMS = (entityitem) -> {
        return !entityitem.hasPickUpDelay() && entityitem.isAlive() && entityitem.isInWater();
    };

    public EntityDolphin(EntityTypes<? extends EntityDolphin> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new SmoothSwimmingMoveControl(this, 85, 10, 0.02F, 0.1F, true);
        this.lookControl = new SmoothSwimmingLookControl(this, 10);
        this.setCanPickUpLoot(true);
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setAirSupply(this.getMaxAirSupply());
        this.setXRot(0.0F);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return false;
    }

    @Override
    protected void handleAirSupply(int i) {}

    public void setTreasurePos(BlockPosition blockposition) {
        this.entityData.set(EntityDolphin.TREASURE_POS, blockposition);
    }

    public BlockPosition getTreasurePos() {
        return (BlockPosition) this.entityData.get(EntityDolphin.TREASURE_POS);
    }

    public boolean gotFish() {
        return (Boolean) this.entityData.get(EntityDolphin.GOT_FISH);
    }

    public void setGotFish(boolean flag) {
        this.entityData.set(EntityDolphin.GOT_FISH, flag);
    }

    public int getMoistnessLevel() {
        return (Integer) this.entityData.get(EntityDolphin.MOISTNESS_LEVEL);
    }

    public void setMoisntessLevel(int i) {
        this.entityData.set(EntityDolphin.MOISTNESS_LEVEL, i);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityDolphin.TREASURE_POS, BlockPosition.ZERO);
        this.entityData.define(EntityDolphin.GOT_FISH, false);
        this.entityData.define(EntityDolphin.MOISTNESS_LEVEL, 2400);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("TreasurePosX", this.getTreasurePos().getX());
        nbttagcompound.putInt("TreasurePosY", this.getTreasurePos().getY());
        nbttagcompound.putInt("TreasurePosZ", this.getTreasurePos().getZ());
        nbttagcompound.putBoolean("GotFish", this.gotFish());
        nbttagcompound.putInt("Moistness", this.getMoistnessLevel());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("TreasurePosX");
        int j = nbttagcompound.getInt("TreasurePosY");
        int k = nbttagcompound.getInt("TreasurePosZ");

        this.setTreasurePos(new BlockPosition(i, j, k));
        super.readAdditionalSaveData(nbttagcompound);
        this.setGotFish(nbttagcompound.getBoolean("GotFish"));
        this.setMoisntessLevel(nbttagcompound.getInt("Moistness"));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new PathfinderGoalBreath(this));
        this.goalSelector.addGoal(0, new PathfinderGoalWater(this));
        this.goalSelector.addGoal(1, new EntityDolphin.a(this));
        this.goalSelector.addGoal(2, new EntityDolphin.b(this, 4.0D));
        this.goalSelector.addGoal(4, new PathfinderGoalRandomSwim(this, 1.0D, 10));
        this.goalSelector.addGoal(4, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.addGoal(5, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.addGoal(5, new PathfinderGoalWaterJump(this, 10));
        this.goalSelector.addGoal(6, new PathfinderGoalMeleeAttack(this, 1.2000000476837158D, true));
        this.goalSelector.addGoal(8, new EntityDolphin.c());
        this.goalSelector.addGoal(8, new PathfinderGoalFollowBoat(this));
        this.goalSelector.addGoal(9, new PathfinderGoalAvoidTarget<>(this, EntityGuardian.class, 8.0F, 1.0D, 1.0D));
        this.targetSelector.addGoal(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityGuardian.class})).setAlertOthers());
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 10.0D).add(GenericAttributes.MOVEMENT_SPEED, 1.2000000476837158D).add(GenericAttributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new NavigationGuardian(this, world);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        boolean flag = entity.hurt(DamageSource.mobAttack(this), (float) ((int) this.getAttributeValue(GenericAttributes.ATTACK_DAMAGE)));

        if (flag) {
            this.doEnchantDamageEffects(this, entity);
            this.playSound(SoundEffects.DOLPHIN_ATTACK, 1.0F, 1.0F);
        }

        return flag;
    }

    @Override
    public int getMaxAirSupply() {
        return 4800;
    }

    @Override
    protected int increaseAirSupply(int i) {
        return this.getMaxAirSupply();
    }

    @Override
    protected float getStandingEyeHeight(EntityPose entitypose, EntitySize entitysize) {
        return 0.3F;
    }

    @Override
    public int getMaxHeadXRot() {
        return 1;
    }

    @Override
    public int getMaxHeadYRot() {
        return 1;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return true;
    }

    @Override
    public boolean canTakeItem(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.getEquipmentSlotForItem(itemstack);

        return !this.getItemBySlot(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.canTakeItem(itemstack);
    }

    @Override
    protected void pickUpItem(EntityItem entityitem) {
        if (this.getItemBySlot(EnumItemSlot.MAINHAND).isEmpty()) {
            ItemStack itemstack = entityitem.getItem();

            if (this.canHoldItem(itemstack)) {
                this.onItemPickup(entityitem);
                this.setItemSlot(EnumItemSlot.MAINHAND, itemstack);
                this.handDropChances[EnumItemSlot.MAINHAND.getIndex()] = 2.0F;
                this.take(entityitem, itemstack.getCount());
                entityitem.discard();
            }
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (this.isNoAi()) {
            this.setAirSupply(this.getMaxAirSupply());
        } else {
            if (this.isInWaterRainOrBubble()) {
                this.setMoisntessLevel(2400);
            } else {
                this.setMoisntessLevel(this.getMoistnessLevel() - 1);
                if (this.getMoistnessLevel() <= 0) {
                    this.hurt(DamageSource.DRY_OUT, 1.0F);
                }

                if (this.onGround) {
                    this.setDeltaMovement(this.getDeltaMovement().add((double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F), 0.5D, (double) ((this.random.nextFloat() * 2.0F - 1.0F) * 0.2F)));
                    this.setYRot(this.random.nextFloat() * 360.0F);
                    this.onGround = false;
                    this.hasImpulse = true;
                }
            }

            if (this.level.isClientSide && this.isInWater() && this.getDeltaMovement().lengthSqr() > 0.03D) {
                Vec3D vec3d = this.getViewVector(0.0F);
                float f = MathHelper.cos(this.getYRot() * 0.017453292F) * 0.3F;
                float f1 = MathHelper.sin(this.getYRot() * 0.017453292F) * 0.3F;
                float f2 = 1.2F - this.random.nextFloat() * 0.7F;

                for (int i = 0; i < 2; ++i) {
                    this.level.addParticle(Particles.DOLPHIN, this.getX() - vec3d.x * (double) f2 + (double) f, this.getY() - vec3d.y, this.getZ() - vec3d.z * (double) f2 + (double) f1, 0.0D, 0.0D, 0.0D);
                    this.level.addParticle(Particles.DOLPHIN, this.getX() - vec3d.x * (double) f2 - (double) f, this.getY() - vec3d.y, this.getZ() - vec3d.z * (double) f2 - (double) f1, 0.0D, 0.0D, 0.0D);
                }
            }

        }
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 38) {
            this.addParticlesAroundSelf(Particles.HAPPY_VILLAGER);
        } else {
            super.handleEntityEvent(b0);
        }

    }

    private void addParticlesAroundSelf(ParticleParam particleparam) {
        for (int i = 0; i < 7; ++i) {
            double d0 = this.random.nextGaussian() * 0.01D;
            double d1 = this.random.nextGaussian() * 0.01D;
            double d2 = this.random.nextGaussian() * 0.01D;

            this.level.addParticle(particleparam, this.getRandomX(1.0D), this.getRandomY() + 0.2D, this.getRandomZ(1.0D), d0, d1, d2);
        }

    }

    @Override
    protected EnumInteractionResult mobInteract(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.getItemInHand(enumhand);

        if (!itemstack.isEmpty() && itemstack.is((Tag) TagsItem.FISHES)) {
            if (!this.level.isClientSide) {
                this.playSound(SoundEffects.DOLPHIN_EAT, 1.0F, 1.0F);
            }

            this.setGotFish(true);
            if (!entityhuman.getAbilities().instabuild) {
                itemstack.shrink(1);
            }

            return EnumInteractionResult.sidedSuccess(this.level.isClientSide);
        } else {
            return super.mobInteract(entityhuman, enumhand);
        }
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.DOLPHIN_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.DOLPHIN_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return this.isInWater() ? SoundEffects.DOLPHIN_AMBIENT_WATER : SoundEffects.DOLPHIN_AMBIENT;
    }

    @Override
    protected SoundEffect getSwimSplashSound() {
        return SoundEffects.DOLPHIN_SPLASH;
    }

    @Override
    protected SoundEffect getSwimSound() {
        return SoundEffects.DOLPHIN_SWIM;
    }

    protected boolean closeToNextPos() {
        BlockPosition blockposition = this.getNavigation().getTargetPos();

        return blockposition != null ? blockposition.closerThan((IPosition) this.position(), 12.0D) : false;
    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(this.getSpeed(), vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(vec3d);
        }

    }

    @Override
    public boolean canBeLeashed(EntityHuman entityhuman) {
        return true;
    }

    private static class a extends PathfinderGoal {

        private final EntityDolphin dolphin;
        private boolean stuck;

        a(EntityDolphin entitydolphin) {
            this.dolphin = entitydolphin;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean isInterruptable() {
            return false;
        }

        @Override
        public boolean canUse() {
            return this.dolphin.gotFish() && this.dolphin.getAirSupply() >= 100;
        }

        @Override
        public boolean canContinueToUse() {
            BlockPosition blockposition = this.dolphin.getTreasurePos();

            return !(new BlockPosition((double) blockposition.getX(), this.dolphin.getY(), (double) blockposition.getZ())).closerThan((IPosition) this.dolphin.position(), 4.0D) && !this.stuck && this.dolphin.getAirSupply() >= 100;
        }

        @Override
        public void start() {
            if (this.dolphin.level instanceof WorldServer) {
                WorldServer worldserver = (WorldServer) this.dolphin.level;

                this.stuck = false;
                this.dolphin.getNavigation().stop();
                BlockPosition blockposition = this.dolphin.blockPosition();
                StructureGenerator<?> structuregenerator = (double) worldserver.random.nextFloat() >= 0.5D ? StructureGenerator.OCEAN_RUIN : StructureGenerator.SHIPWRECK;
                BlockPosition blockposition1 = worldserver.findNearestMapFeature(structuregenerator, blockposition, 50, false);

                if (blockposition1 == null) {
                    StructureGenerator<?> structuregenerator1 = structuregenerator.equals(StructureGenerator.OCEAN_RUIN) ? StructureGenerator.SHIPWRECK : StructureGenerator.OCEAN_RUIN;
                    BlockPosition blockposition2 = worldserver.findNearestMapFeature(structuregenerator1, blockposition, 50, false);

                    if (blockposition2 == null) {
                        this.stuck = true;
                        return;
                    }

                    this.dolphin.setTreasurePos(blockposition2);
                } else {
                    this.dolphin.setTreasurePos(blockposition1);
                }

                worldserver.broadcastEntityEvent(this.dolphin, (byte) 38);
            }
        }

        @Override
        public void stop() {
            BlockPosition blockposition = this.dolphin.getTreasurePos();

            if ((new BlockPosition((double) blockposition.getX(), this.dolphin.getY(), (double) blockposition.getZ())).closerThan((IPosition) this.dolphin.position(), 4.0D) || this.stuck) {
                this.dolphin.setGotFish(false);
            }

        }

        @Override
        public void tick() {
            World world = this.dolphin.level;

            if (this.dolphin.closeToNextPos() || this.dolphin.getNavigation().isDone()) {
                Vec3D vec3d = Vec3D.atCenterOf(this.dolphin.getTreasurePos());
                Vec3D vec3d1 = DefaultRandomPos.getPosTowards(this.dolphin, 16, 1, vec3d, 0.39269909262657166D);

                if (vec3d1 == null) {
                    vec3d1 = DefaultRandomPos.getPosTowards(this.dolphin, 8, 4, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 != null) {
                    BlockPosition blockposition = new BlockPosition(vec3d1);

                    if (!world.getFluidState(blockposition).is((Tag) TagsFluid.WATER) || !world.getBlockState(blockposition).isPathfindable(world, blockposition, PathMode.WATER)) {
                        vec3d1 = DefaultRandomPos.getPosTowards(this.dolphin, 8, 5, vec3d, 1.5707963705062866D);
                    }
                }

                if (vec3d1 == null) {
                    this.stuck = true;
                    return;
                }

                this.dolphin.getLookControl().setLookAt(vec3d1.x, vec3d1.y, vec3d1.z, (float) (this.dolphin.getMaxHeadYRot() + 20), (float) this.dolphin.getMaxHeadXRot());
                this.dolphin.getNavigation().moveTo(vec3d1.x, vec3d1.y, vec3d1.z, 1.3D);
                if (world.random.nextInt(this.adjustedTickDelay(80)) == 0) {
                    world.broadcastEntityEvent(this.dolphin, (byte) 38);
                }
            }

        }
    }

    private static class b extends PathfinderGoal {

        private final EntityDolphin dolphin;
        private final double speedModifier;
        @Nullable
        private EntityHuman player;

        b(EntityDolphin entitydolphin, double d0) {
            this.dolphin = entitydolphin;
            this.speedModifier = d0;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            this.player = this.dolphin.level.getNearestPlayer(EntityDolphin.SWIM_WITH_PLAYER_TARGETING, this.dolphin);
            return this.player == null ? false : this.player.isSwimming() && this.dolphin.getTarget() != this.player;
        }

        @Override
        public boolean canContinueToUse() {
            return this.player != null && this.player.isSwimming() && this.dolphin.distanceToSqr((Entity) this.player) < 256.0D;
        }

        @Override
        public void start() {
            this.player.addEffect(new MobEffect(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
        }

        @Override
        public void stop() {
            this.player = null;
            this.dolphin.getNavigation().stop();
        }

        @Override
        public void tick() {
            this.dolphin.getLookControl().setLookAt(this.player, (float) (this.dolphin.getMaxHeadYRot() + 20), (float) this.dolphin.getMaxHeadXRot());
            if (this.dolphin.distanceToSqr((Entity) this.player) < 6.25D) {
                this.dolphin.getNavigation().stop();
            } else {
                this.dolphin.getNavigation().moveTo((Entity) this.player, this.speedModifier);
            }

            if (this.player.isSwimming() && this.player.level.random.nextInt(6) == 0) {
                this.player.addEffect(new MobEffect(MobEffects.DOLPHINS_GRACE, 100), this.dolphin);
            }

        }
    }

    private class c extends PathfinderGoal {

        private int cooldown;

        c() {}

        @Override
        public boolean canUse() {
            if (this.cooldown > EntityDolphin.this.tickCount) {
                return false;
            } else {
                List<EntityItem> list = EntityDolphin.this.level.getEntitiesOfClass(EntityItem.class, EntityDolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), EntityDolphin.ALLOWED_ITEMS);

                return !list.isEmpty() || !EntityDolphin.this.getItemBySlot(EnumItemSlot.MAINHAND).isEmpty();
            }
        }

        @Override
        public void start() {
            List<EntityItem> list = EntityDolphin.this.level.getEntitiesOfClass(EntityItem.class, EntityDolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), EntityDolphin.ALLOWED_ITEMS);

            if (!list.isEmpty()) {
                EntityDolphin.this.getNavigation().moveTo((Entity) list.get(0), 1.2000000476837158D);
                EntityDolphin.this.playSound(SoundEffects.DOLPHIN_PLAY, 1.0F, 1.0F);
            }

            this.cooldown = 0;
        }

        @Override
        public void stop() {
            ItemStack itemstack = EntityDolphin.this.getItemBySlot(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                this.drop(itemstack);
                EntityDolphin.this.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
                this.cooldown = EntityDolphin.this.tickCount + EntityDolphin.this.random.nextInt(100);
            }

        }

        @Override
        public void tick() {
            List<EntityItem> list = EntityDolphin.this.level.getEntitiesOfClass(EntityItem.class, EntityDolphin.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), EntityDolphin.ALLOWED_ITEMS);
            ItemStack itemstack = EntityDolphin.this.getItemBySlot(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                this.drop(itemstack);
                EntityDolphin.this.setItemSlot(EnumItemSlot.MAINHAND, ItemStack.EMPTY);
            } else if (!list.isEmpty()) {
                EntityDolphin.this.getNavigation().moveTo((Entity) list.get(0), 1.2000000476837158D);
            }

        }

        private void drop(ItemStack itemstack) {
            if (!itemstack.isEmpty()) {
                double d0 = EntityDolphin.this.getEyeY() - 0.30000001192092896D;
                EntityItem entityitem = new EntityItem(EntityDolphin.this.level, EntityDolphin.this.getX(), d0, EntityDolphin.this.getZ(), itemstack);

                entityitem.setPickUpDelay(40);
                entityitem.setThrower(EntityDolphin.this.getUUID());
                float f = 0.3F;
                float f1 = EntityDolphin.this.random.nextFloat() * 6.2831855F;
                float f2 = 0.02F * EntityDolphin.this.random.nextFloat();

                entityitem.setDeltaMovement((double) (0.3F * -MathHelper.sin(EntityDolphin.this.getYRot() * 0.017453292F) * MathHelper.cos(EntityDolphin.this.getXRot() * 0.017453292F) + MathHelper.cos(f1) * f2), (double) (0.3F * MathHelper.sin(EntityDolphin.this.getXRot() * 0.017453292F) * 1.5F), (double) (0.3F * MathHelper.cos(EntityDolphin.this.getYRot() * 0.017453292F) * MathHelper.cos(EntityDolphin.this.getXRot() * 0.017453292F) + MathHelper.sin(f1) * f2));
                EntityDolphin.this.level.addFreshEntity(entityitem);
            }
        }
    }
}
