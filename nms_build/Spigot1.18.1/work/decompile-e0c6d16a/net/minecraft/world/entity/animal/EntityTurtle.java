package net.minecraft.world.entity.animal;

import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityExperienceOrb;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBreed;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalPanic;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.PathfinderGoalTempt;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTurtleEgg;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.AmphibiousNodeEvaluator;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.phys.Vec3D;

public class EntityTurtle extends EntityAnimal {

    private static final DataWatcherObject<BlockPosition> HOME_POS = DataWatcher.defineId(EntityTurtle.class, DataWatcherRegistry.BLOCK_POS);
    private static final DataWatcherObject<Boolean> HAS_EGG = DataWatcher.defineId(EntityTurtle.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> LAYING_EGG = DataWatcher.defineId(EntityTurtle.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<BlockPosition> TRAVEL_POS = DataWatcher.defineId(EntityTurtle.class, DataWatcherRegistry.BLOCK_POS);
    private static final DataWatcherObject<Boolean> GOING_HOME = DataWatcher.defineId(EntityTurtle.class, DataWatcherRegistry.BOOLEAN);
    private static final DataWatcherObject<Boolean> TRAVELLING = DataWatcher.defineId(EntityTurtle.class, DataWatcherRegistry.BOOLEAN);
    public static final RecipeItemStack FOOD_ITEMS = RecipeItemStack.of(Blocks.SEAGRASS.asItem());
    int layEggCounter;
    public static final Predicate<EntityLiving> BABY_ON_LAND_SELECTOR = (entityliving) -> {
        return entityliving.isBaby() && !entityliving.isInWater();
    };

    public EntityTurtle(EntityTypes<? extends EntityTurtle> entitytypes, World world) {
        super(entitytypes, world);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.setPathfindingMalus(PathType.DOOR_IRON_CLOSED, -1.0F);
        this.setPathfindingMalus(PathType.DOOR_WOOD_CLOSED, -1.0F);
        this.setPathfindingMalus(PathType.DOOR_OPEN, -1.0F);
        this.moveControl = new EntityTurtle.e(this);
        this.maxUpStep = 1.0F;
    }

    public void setHomePos(BlockPosition blockposition) {
        this.entityData.set(EntityTurtle.HOME_POS, blockposition);
    }

    BlockPosition getHomePos() {
        return (BlockPosition) this.entityData.get(EntityTurtle.HOME_POS);
    }

    void setTravelPos(BlockPosition blockposition) {
        this.entityData.set(EntityTurtle.TRAVEL_POS, blockposition);
    }

    BlockPosition getTravelPos() {
        return (BlockPosition) this.entityData.get(EntityTurtle.TRAVEL_POS);
    }

    public boolean hasEgg() {
        return (Boolean) this.entityData.get(EntityTurtle.HAS_EGG);
    }

    void setHasEgg(boolean flag) {
        this.entityData.set(EntityTurtle.HAS_EGG, flag);
    }

    public boolean isLayingEgg() {
        return (Boolean) this.entityData.get(EntityTurtle.LAYING_EGG);
    }

    void setLayingEgg(boolean flag) {
        this.layEggCounter = flag ? 1 : 0;
        this.entityData.set(EntityTurtle.LAYING_EGG, flag);
    }

    boolean isGoingHome() {
        return (Boolean) this.entityData.get(EntityTurtle.GOING_HOME);
    }

    void setGoingHome(boolean flag) {
        this.entityData.set(EntityTurtle.GOING_HOME, flag);
    }

    boolean isTravelling() {
        return (Boolean) this.entityData.get(EntityTurtle.TRAVELLING);
    }

    void setTravelling(boolean flag) {
        this.entityData.set(EntityTurtle.TRAVELLING, flag);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityTurtle.HOME_POS, BlockPosition.ZERO);
        this.entityData.define(EntityTurtle.HAS_EGG, false);
        this.entityData.define(EntityTurtle.TRAVEL_POS, BlockPosition.ZERO);
        this.entityData.define(EntityTurtle.GOING_HOME, false);
        this.entityData.define(EntityTurtle.TRAVELLING, false);
        this.entityData.define(EntityTurtle.LAYING_EGG, false);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("HomePosX", this.getHomePos().getX());
        nbttagcompound.putInt("HomePosY", this.getHomePos().getY());
        nbttagcompound.putInt("HomePosZ", this.getHomePos().getZ());
        nbttagcompound.putBoolean("HasEgg", this.hasEgg());
        nbttagcompound.putInt("TravelPosX", this.getTravelPos().getX());
        nbttagcompound.putInt("TravelPosY", this.getTravelPos().getY());
        nbttagcompound.putInt("TravelPosZ", this.getTravelPos().getZ());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("HomePosX");
        int j = nbttagcompound.getInt("HomePosY");
        int k = nbttagcompound.getInt("HomePosZ");

        this.setHomePos(new BlockPosition(i, j, k));
        super.readAdditionalSaveData(nbttagcompound);
        this.setHasEgg(nbttagcompound.getBoolean("HasEgg"));
        int l = nbttagcompound.getInt("TravelPosX");
        int i1 = nbttagcompound.getInt("TravelPosY");
        int j1 = nbttagcompound.getInt("TravelPosZ");

        this.setTravelPos(new BlockPosition(l, i1, j1));
    }

    @Nullable
    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setHomePos(this.blockPosition());
        this.setTravelPos(BlockPosition.ZERO);
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public static boolean checkTurtleSpawnRules(EntityTypes<EntityTurtle> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return blockposition.getY() < generatoraccess.getSeaLevel() + 4 && BlockTurtleEgg.onSand(generatoraccess, blockposition) && isBrightEnoughToSpawn(generatoraccess, blockposition);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityTurtle.f(this, 1.2D));
        this.goalSelector.addGoal(1, new EntityTurtle.a(this, 1.0D));
        this.goalSelector.addGoal(1, new EntityTurtle.d(this, 1.0D));
        this.goalSelector.addGoal(2, new PathfinderGoalTempt(this, 1.1D, EntityTurtle.FOOD_ITEMS, false));
        this.goalSelector.addGoal(3, new EntityTurtle.c(this, 1.0D));
        this.goalSelector.addGoal(4, new EntityTurtle.b(this, 1.0D));
        this.goalSelector.addGoal(7, new EntityTurtle.i(this, 1.0D));
        this.goalSelector.addGoal(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(9, new EntityTurtle.h(this, 1.0D, 100));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityInsentient.createMobAttributes().add(GenericAttributes.MAX_HEALTH, 30.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.WATER;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 200;
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return !this.isInWater() && this.onGround && !this.isBaby() ? SoundEffects.TURTLE_AMBIENT_LAND : super.getAmbientSound();
    }

    @Override
    protected void playSwimSound(float f) {
        super.playSwimSound(f * 1.5F);
    }

    @Override
    protected SoundEffect getSwimSound() {
        return SoundEffects.TURTLE_SWIM;
    }

    @Nullable
    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return this.isBaby() ? SoundEffects.TURTLE_HURT_BABY : SoundEffects.TURTLE_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getDeathSound() {
        return this.isBaby() ? SoundEffects.TURTLE_DEATH_BABY : SoundEffects.TURTLE_DEATH;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        SoundEffect soundeffect = this.isBaby() ? SoundEffects.TURTLE_SHAMBLE_BABY : SoundEffects.TURTLE_SHAMBLE;

        this.playSound(soundeffect, 0.15F, 1.0F);
    }

    @Override
    public boolean canFallInLove() {
        return super.canFallInLove() && !this.hasEgg();
    }

    @Override
    protected float nextStep() {
        return this.moveDist + 0.15F;
    }

    @Override
    public float getScale() {
        return this.isBaby() ? 0.3F : 1.0F;
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new EntityTurtle.g(this, world);
    }

    @Nullable
    @Override
    public EntityAgeable getBreedOffspring(WorldServer worldserver, EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.TURTLE.create(worldserver);
    }

    @Override
    public boolean isFood(ItemStack itemstack) {
        return itemstack.is(Blocks.SEAGRASS.asItem());
    }

    @Override
    public float getWalkTargetValue(BlockPosition blockposition, IWorldReader iworldreader) {
        return !this.isGoingHome() && iworldreader.getFluidState(blockposition).is((Tag) TagsFluid.WATER) ? 10.0F : (BlockTurtleEgg.onSand(iworldreader, blockposition) ? 10.0F : iworldreader.getBrightness(blockposition) - 0.5F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive() && this.isLayingEgg() && this.layEggCounter >= 1 && this.layEggCounter % 5 == 0) {
            BlockPosition blockposition = this.blockPosition();

            if (BlockTurtleEgg.onSand(this.level, blockposition)) {
                this.level.levelEvent(2001, blockposition, Block.getId(this.level.getBlockState(blockposition.below())));
            }
        }

    }

    @Override
    protected void ageBoundaryReached() {
        super.ageBoundaryReached();
        if (!this.isBaby() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.spawnAtLocation(Items.SCUTE, 1);
        }

    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isEffectiveAi() && this.isInWater()) {
            this.moveRelative(0.1F, vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
            if (this.getTarget() == null && (!this.isGoingHome() || !this.getHomePos().closerThan((IPosition) this.position(), 20.0D))) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(vec3d);
        }

    }

    @Override
    public boolean canBeLeashed(EntityHuman entityhuman) {
        return false;
    }

    @Override
    public void thunderHit(WorldServer worldserver, EntityLightning entitylightning) {
        this.hurt(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
    }

    private static class e extends ControllerMove {

        private final EntityTurtle turtle;

        e(EntityTurtle entityturtle) {
            super(entityturtle);
            this.turtle = entityturtle;
        }

        private void updateSpeed() {
            if (this.turtle.isInWater()) {
                this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0D, 0.005D, 0.0D));
                if (!this.turtle.getHomePos().closerThan((IPosition) this.turtle.position(), 16.0D)) {
                    this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.08F));
                }

                if (this.turtle.isBaby()) {
                    this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 3.0F, 0.06F));
                }
            } else if (this.turtle.onGround) {
                this.turtle.setSpeed(Math.max(this.turtle.getSpeed() / 2.0F, 0.06F));
            }

        }

        @Override
        public void tick() {
            this.updateSpeed();
            if (this.operation == ControllerMove.Operation.MOVE_TO && !this.turtle.getNavigation().isDone()) {
                double d0 = this.wantedX - this.turtle.getX();
                double d1 = this.wantedY - this.turtle.getY();
                double d2 = this.wantedZ - this.turtle.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F;

                this.turtle.setYRot(this.rotlerp(this.turtle.getYRot(), f, 90.0F));
                this.turtle.yBodyRot = this.turtle.getYRot();
                float f1 = (float) (this.speedModifier * this.turtle.getAttributeValue(GenericAttributes.MOVEMENT_SPEED));

                this.turtle.setSpeed(MathHelper.lerp(0.125F, this.turtle.getSpeed(), f1));
                this.turtle.setDeltaMovement(this.turtle.getDeltaMovement().add(0.0D, (double) this.turtle.getSpeed() * d1 * 0.1D, 0.0D));
            } else {
                this.turtle.setSpeed(0.0F);
            }
        }
    }

    private static class f extends PathfinderGoalPanic {

        f(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0);
        }

        @Override
        public boolean canUse() {
            if (this.mob.getLastHurtByMob() == null && !this.mob.isOnFire()) {
                return false;
            } else {
                BlockPosition blockposition = this.lookForWater(this.mob.level, this.mob, 7);

                if (blockposition != null) {
                    this.posX = (double) blockposition.getX();
                    this.posY = (double) blockposition.getY();
                    this.posZ = (double) blockposition.getZ();
                    return true;
                } else {
                    return this.findRandomPosition();
                }
            }
        }
    }

    private static class a extends PathfinderGoalBreed {

        private final EntityTurtle turtle;

        a(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0);
            this.turtle = entityturtle;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.turtle.hasEgg();
        }

        @Override
        protected void breed() {
            EntityPlayer entityplayer = this.animal.getLoveCause();

            if (entityplayer == null && this.partner.getLoveCause() != null) {
                entityplayer = this.partner.getLoveCause();
            }

            if (entityplayer != null) {
                entityplayer.awardStat(StatisticList.ANIMALS_BRED);
                CriterionTriggers.BRED_ANIMALS.trigger(entityplayer, this.animal, this.partner, (EntityAgeable) null);
            }

            this.turtle.setHasEgg(true);
            this.animal.resetLove();
            this.partner.resetLove();
            Random random = this.animal.getRandom();

            if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                this.level.addFreshEntity(new EntityExperienceOrb(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), random.nextInt(7) + 1));
            }

        }
    }

    private static class d extends PathfinderGoalGotoTarget {

        private final EntityTurtle turtle;

        d(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0, 16);
            this.turtle = entityturtle;
        }

        @Override
        public boolean canUse() {
            return this.turtle.hasEgg() && this.turtle.getHomePos().closerThan((IPosition) this.turtle.position(), 9.0D) ? super.canUse() : false;
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.turtle.hasEgg() && this.turtle.getHomePos().closerThan((IPosition) this.turtle.position(), 9.0D);
        }

        @Override
        public void tick() {
            super.tick();
            BlockPosition blockposition = this.turtle.blockPosition();

            if (!this.turtle.isInWater() && this.isReachedTarget()) {
                if (this.turtle.layEggCounter < 1) {
                    this.turtle.setLayingEgg(true);
                } else if (this.turtle.layEggCounter > this.adjustedTickDelay(200)) {
                    World world = this.turtle.level;

                    world.playSound((EntityHuman) null, blockposition, SoundEffects.TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                    world.setBlock(this.blockPos.above(), (IBlockData) Blocks.TURTLE_EGG.defaultBlockState().setValue(BlockTurtleEgg.EGGS, this.turtle.random.nextInt(4) + 1), 3);
                    this.turtle.setHasEgg(false);
                    this.turtle.setLayingEgg(false);
                    this.turtle.setInLoveTime(600);
                }

                if (this.turtle.isLayingEgg()) {
                    ++this.turtle.layEggCounter;
                }
            }

        }

        @Override
        protected boolean isValidTarget(IWorldReader iworldreader, BlockPosition blockposition) {
            return !iworldreader.isEmptyBlock(blockposition.above()) ? false : BlockTurtleEgg.isSand(iworldreader, blockposition);
        }
    }

    private static class c extends PathfinderGoalGotoTarget {

        private static final int GIVE_UP_TICKS = 1200;
        private final EntityTurtle turtle;

        c(EntityTurtle entityturtle, double d0) {
            super(entityturtle, entityturtle.isBaby() ? 2.0D : d0, 24);
            this.turtle = entityturtle;
            this.verticalSearchStart = -1;
        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.isInWater() && this.tryTicks <= 1200 && this.isValidTarget(this.turtle.level, this.blockPos);
        }

        @Override
        public boolean canUse() {
            return this.turtle.isBaby() && !this.turtle.isInWater() ? super.canUse() : (!this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.canUse() : false);
        }

        @Override
        public boolean shouldRecalculatePath() {
            return this.tryTicks % 160 == 0;
        }

        @Override
        protected boolean isValidTarget(IWorldReader iworldreader, BlockPosition blockposition) {
            return iworldreader.getBlockState(blockposition).is(Blocks.WATER);
        }
    }

    private static class b extends PathfinderGoal {

        private final EntityTurtle turtle;
        private final double speedModifier;
        private boolean stuck;
        private int closeToHomeTryTicks;
        private static final int GIVE_UP_TICKS = 600;

        b(EntityTurtle entityturtle, double d0) {
            this.turtle = entityturtle;
            this.speedModifier = d0;
        }

        @Override
        public boolean canUse() {
            return this.turtle.isBaby() ? false : (this.turtle.hasEgg() ? true : (this.turtle.getRandom().nextInt(reducedTickDelay(700)) != 0 ? false : !this.turtle.getHomePos().closerThan((IPosition) this.turtle.position(), 64.0D)));
        }

        @Override
        public void start() {
            this.turtle.setGoingHome(true);
            this.stuck = false;
            this.closeToHomeTryTicks = 0;
        }

        @Override
        public void stop() {
            this.turtle.setGoingHome(false);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.getHomePos().closerThan((IPosition) this.turtle.position(), 7.0D) && !this.stuck && this.closeToHomeTryTicks <= this.adjustedTickDelay(600);
        }

        @Override
        public void tick() {
            BlockPosition blockposition = this.turtle.getHomePos();
            boolean flag = blockposition.closerThan((IPosition) this.turtle.position(), 16.0D);

            if (flag) {
                ++this.closeToHomeTryTicks;
            }

            if (this.turtle.getNavigation().isDone()) {
                Vec3D vec3d = Vec3D.atBottomCenterOf(blockposition);
                Vec3D vec3d1 = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, vec3d, 0.3141592741012573D);

                if (vec3d1 == null) {
                    vec3d1 = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 != null && !flag && !this.turtle.level.getBlockState(new BlockPosition(vec3d1)).is(Blocks.WATER)) {
                    vec3d1 = DefaultRandomPos.getPosTowards(this.turtle, 16, 5, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 == null) {
                    this.stuck = true;
                    return;
                }

                this.turtle.getNavigation().moveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
            }

        }
    }

    private static class i extends PathfinderGoal {

        private final EntityTurtle turtle;
        private final double speedModifier;
        private boolean stuck;

        i(EntityTurtle entityturtle, double d0) {
            this.turtle = entityturtle;
            this.speedModifier = d0;
        }

        @Override
        public boolean canUse() {
            return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
        }

        @Override
        public void start() {
            boolean flag = true;
            boolean flag1 = true;
            Random random = this.turtle.random;
            int i = random.nextInt(1025) - 512;
            int j = random.nextInt(9) - 4;
            int k = random.nextInt(1025) - 512;

            if ((double) j + this.turtle.getY() > (double) (this.turtle.level.getSeaLevel() - 1)) {
                j = 0;
            }

            BlockPosition blockposition = new BlockPosition((double) i + this.turtle.getX(), (double) j + this.turtle.getY(), (double) k + this.turtle.getZ());

            this.turtle.setTravelPos(blockposition);
            this.turtle.setTravelling(true);
            this.stuck = false;
        }

        @Override
        public void tick() {
            if (this.turtle.getNavigation().isDone()) {
                Vec3D vec3d = Vec3D.atBottomCenterOf(this.turtle.getTravelPos());
                Vec3D vec3d1 = DefaultRandomPos.getPosTowards(this.turtle, 16, 3, vec3d, 0.3141592741012573D);

                if (vec3d1 == null) {
                    vec3d1 = DefaultRandomPos.getPosTowards(this.turtle, 8, 7, vec3d, 1.5707963705062866D);
                }

                if (vec3d1 != null) {
                    int i = MathHelper.floor(vec3d1.x);
                    int j = MathHelper.floor(vec3d1.z);
                    boolean flag = true;

                    if (!this.turtle.level.hasChunksAt(i - 34, j - 34, i + 34, j + 34)) {
                        vec3d1 = null;
                    }
                }

                if (vec3d1 == null) {
                    this.stuck = true;
                    return;
                }

                this.turtle.getNavigation().moveTo(vec3d1.x, vec3d1.y, vec3d1.z, this.speedModifier);
            }

        }

        @Override
        public boolean canContinueToUse() {
            return !this.turtle.getNavigation().isDone() && !this.stuck && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
        }

        @Override
        public void stop() {
            this.turtle.setTravelling(false);
            super.stop();
        }
    }

    private static class h extends PathfinderGoalRandomStroll {

        private final EntityTurtle turtle;

        h(EntityTurtle entityturtle, double d0, int i) {
            super(entityturtle, d0, i);
            this.turtle = entityturtle;
        }

        @Override
        public boolean canUse() {
            return !this.mob.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() ? super.canUse() : false;
        }
    }

    private static class g extends NavigationGuardian {

        g(EntityTurtle entityturtle, World world) {
            super(entityturtle, world);
        }

        @Override
        protected boolean canUpdatePath() {
            return true;
        }

        @Override
        protected Pathfinder createPathFinder(int i) {
            this.nodeEvaluator = new AmphibiousNodeEvaluator(true);
            return new Pathfinder(this.nodeEvaluator, i);
        }

        @Override
        public boolean isStableDestination(BlockPosition blockposition) {
            if (this.mob instanceof EntityTurtle) {
                EntityTurtle entityturtle = (EntityTurtle) this.mob;

                if (entityturtle.isTravelling()) {
                    return this.level.getBlockState(blockposition).is(Blocks.WATER);
                }
            }

            return !this.level.getBlockState(blockposition.below()).isAir();
        }
    }
}
