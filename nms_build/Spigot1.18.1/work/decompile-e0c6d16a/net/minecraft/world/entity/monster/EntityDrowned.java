package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalGotoTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.PathfinderGoalZombieAttack;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationGuardian;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntityTurtle;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityDrowned extends EntityZombie implements IRangedEntity {

    public static final float NAUTILUS_SHELL_CHANCE = 0.03F;
    boolean searchingForLand;
    public final NavigationGuardian waterNavigation;
    public final Navigation groundNavigation;

    public EntityDrowned(EntityTypes<? extends EntityDrowned> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
        this.moveControl = new EntityDrowned.d(this);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        this.waterNavigation = new NavigationGuardian(this, world);
        this.groundNavigation = new Navigation(this, world);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(1, new EntityDrowned.c(this, 1.0D));
        this.goalSelector.addGoal(2, new EntityDrowned.f(this, 1.0D, 40, 10.0F));
        this.goalSelector.addGoal(2, new EntityDrowned.a(this, 1.0D, false));
        this.goalSelector.addGoal(5, new EntityDrowned.b(this, 1.0D));
        this.goalSelector.addGoal(6, new EntityDrowned.e(this, 1.0D, this.level.getSeaLevel()));
        this.goalSelector.addGoal(7, new PathfinderGoalRandomStroll(this, 1.0D));
        this.targetSelector.addGoal(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityDrowned.class})).setAlertOthers(EntityPigZombie.class));
        this.targetSelector.addGoal(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, this::okTarget));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, Axolotl.class, true, false));
        this.targetSelector.addGoal(5, new PathfinderGoalNearestAttackableTarget<>(this, EntityTurtle.class, 10, true, false, EntityTurtle.BABY_ON_LAND_SELECTOR));
    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        if (this.getItemBySlot(EnumItemSlot.OFFHAND).isEmpty() && this.random.nextFloat() < 0.03F) {
            this.setItemSlot(EnumItemSlot.OFFHAND, new ItemStack(Items.NAUTILUS_SHELL));
            this.handDropChances[EnumItemSlot.OFFHAND.getIndex()] = 2.0F;
        }

        return groupdataentity;
    }

    public static boolean checkDrownedSpawnRules(EntityTypes<EntityDrowned> entitytypes, WorldAccess worldaccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (!worldaccess.getFluidState(blockposition.below()).is((Tag) TagsFluid.WATER)) {
            return false;
        } else {
            Optional<ResourceKey<BiomeBase>> optional = worldaccess.getBiomeName(blockposition);
            boolean flag = worldaccess.getDifficulty() != EnumDifficulty.PEACEFUL && isDarkEnoughToSpawn(worldaccess, blockposition, random) && (enummobspawn == EnumMobSpawn.SPAWNER || worldaccess.getFluidState(blockposition).is((Tag) TagsFluid.WATER));

            return !Objects.equals(optional, Optional.of(Biomes.RIVER)) && !Objects.equals(optional, Optional.of(Biomes.FROZEN_RIVER)) ? random.nextInt(40) == 0 && isDeepEnoughToSpawn(worldaccess, blockposition) && flag : random.nextInt(15) == 0 && flag;
        }
    }

    private static boolean isDeepEnoughToSpawn(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        return blockposition.getY() < generatoraccess.getSeaLevel() - 5;
    }

    @Override
    protected boolean supportsBreakDoorGoal() {
        return false;
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return this.isInWater() ? SoundEffects.DROWNED_AMBIENT_WATER : SoundEffects.DROWNED_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return this.isInWater() ? SoundEffects.DROWNED_HURT_WATER : SoundEffects.DROWNED_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return this.isInWater() ? SoundEffects.DROWNED_DEATH_WATER : SoundEffects.DROWNED_DEATH;
    }

    @Override
    protected SoundEffect getStepSound() {
        return SoundEffects.DROWNED_STEP;
    }

    @Override
    protected SoundEffect getSwimSound() {
        return SoundEffects.DROWNED_SWIM;
    }

    @Override
    protected ItemStack getSkull() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyDamageScaler difficultydamagescaler) {
        if ((double) this.random.nextFloat() > 0.9D) {
            int i = this.random.nextInt(16);

            if (i < 10) {
                this.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.TRIDENT));
            } else {
                this.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
            }
        }

    }

    @Override
    protected boolean canReplaceCurrentItem(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack1.is(Items.NAUTILUS_SHELL) ? false : (itemstack1.is(Items.TRIDENT) ? (itemstack.is(Items.TRIDENT) ? itemstack.getDamageValue() < itemstack1.getDamageValue() : false) : (itemstack.is(Items.TRIDENT) ? true : super.canReplaceCurrentItem(itemstack, itemstack1)));
    }

    @Override
    protected boolean convertsInWater() {
        return false;
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        return iworldreader.isUnobstructed(this);
    }

    public boolean okTarget(@Nullable EntityLiving entityliving) {
        return entityliving != null ? !this.level.isDay() || entityliving.isInWater() : false;
    }

    @Override
    public boolean isPushedByFluid() {
        return !this.isSwimming();
    }

    boolean wantsToSwim() {
        if (this.searchingForLand) {
            return true;
        } else {
            EntityLiving entityliving = this.getTarget();

            return entityliving != null && entityliving.isInWater();
        }
    }

    @Override
    public void travel(Vec3D vec3d) {
        if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
            this.moveRelative(0.01F, vec3d);
            this.move(EnumMoveType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().scale(0.9D));
        } else {
            super.travel(vec3d);
        }

    }

    @Override
    public void updateSwimming() {
        if (!this.level.isClientSide) {
            if (this.isEffectiveAi() && this.isInWater() && this.wantsToSwim()) {
                this.navigation = this.waterNavigation;
                this.setSwimming(true);
            } else {
                this.navigation = this.groundNavigation;
                this.setSwimming(false);
            }
        }

    }

    protected boolean closeToNextPos() {
        PathEntity pathentity = this.getNavigation().getPath();

        if (pathentity != null) {
            BlockPosition blockposition = pathentity.getTarget();

            if (blockposition != null) {
                double d0 = this.distanceToSqr((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());

                if (d0 < 4.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void performRangedAttack(EntityLiving entityliving, float f) {
        EntityThrownTrident entitythrowntrident = new EntityThrownTrident(this.level, this, new ItemStack(Items.TRIDENT));
        double d0 = entityliving.getX() - this.getX();
        double d1 = entityliving.getY(0.3333333333333333D) - entitythrowntrident.getY();
        double d2 = entityliving.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        entitythrowntrident.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEffects.DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(entitythrowntrident);
    }

    public void setSearchingForLand(boolean flag) {
        this.searchingForLand = flag;
    }

    private static class d extends ControllerMove {

        private final EntityDrowned drowned;

        public d(EntityDrowned entitydrowned) {
            super(entitydrowned);
            this.drowned = entitydrowned;
        }

        @Override
        public void tick() {
            EntityLiving entityliving = this.drowned.getTarget();

            if (this.drowned.wantsToSwim() && this.drowned.isInWater()) {
                if (entityliving != null && entityliving.getY() > this.drowned.getY() || this.drowned.searchingForLand) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, 0.002D, 0.0D));
                }

                if (this.operation != ControllerMove.Operation.MOVE_TO || this.drowned.getNavigation().isDone()) {
                    this.drowned.setSpeed(0.0F);
                    return;
                }

                double d0 = this.wantedX - this.drowned.getX();
                double d1 = this.wantedY - this.drowned.getY();
                double d2 = this.wantedZ - this.drowned.getZ();
                double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.atan2(d2, d0) * 57.2957763671875D) - 90.0F;

                this.drowned.setYRot(this.rotlerp(this.drowned.getYRot(), f, 90.0F));
                this.drowned.yBodyRot = this.drowned.getYRot();
                float f1 = (float) (this.speedModifier * this.drowned.getAttributeValue(GenericAttributes.MOVEMENT_SPEED));
                float f2 = MathHelper.lerp(0.125F, this.drowned.getSpeed(), f1);

                this.drowned.setSpeed(f2);
                this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add((double) f2 * d0 * 0.005D, (double) f2 * d1 * 0.1D, (double) f2 * d2 * 0.005D));
            } else {
                if (!this.drowned.onGround) {
                    this.drowned.setDeltaMovement(this.drowned.getDeltaMovement().add(0.0D, -0.008D, 0.0D));
                }

                super.tick();
            }

        }
    }

    private static class c extends PathfinderGoal {

        private final EntityCreature mob;
        private double wantedX;
        private double wantedY;
        private double wantedZ;
        private final double speedModifier;
        private final World level;

        public c(EntityCreature entitycreature, double d0) {
            this.mob = entitycreature;
            this.speedModifier = d0;
            this.level = entitycreature.level;
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean canUse() {
            if (!this.level.isDay()) {
                return false;
            } else if (this.mob.isInWater()) {
                return false;
            } else {
                Vec3D vec3d = this.getWaterPos();

                if (vec3d == null) {
                    return false;
                } else {
                    this.wantedX = vec3d.x;
                    this.wantedY = vec3d.y;
                    this.wantedZ = vec3d.z;
                    return true;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone();
        }

        @Override
        public void start() {
            this.mob.getNavigation().moveTo(this.wantedX, this.wantedY, this.wantedZ, this.speedModifier);
        }

        @Nullable
        private Vec3D getWaterPos() {
            Random random = this.mob.getRandom();
            BlockPosition blockposition = this.mob.blockPosition();

            for (int i = 0; i < 10; ++i) {
                BlockPosition blockposition1 = blockposition.offset(random.nextInt(20) - 10, 2 - random.nextInt(8), random.nextInt(20) - 10);

                if (this.level.getBlockState(blockposition1).is(Blocks.WATER)) {
                    return Vec3D.atBottomCenterOf(blockposition1);
                }
            }

            return null;
        }
    }

    private static class f extends PathfinderGoalArrowAttack {

        private final EntityDrowned drowned;

        public f(IRangedEntity irangedentity, double d0, int i, float f) {
            super(irangedentity, d0, i, f);
            this.drowned = (EntityDrowned) irangedentity;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.drowned.getMainHandItem().is(Items.TRIDENT);
        }

        @Override
        public void start() {
            super.start();
            this.drowned.setAggressive(true);
            this.drowned.startUsingItem(EnumHand.MAIN_HAND);
        }

        @Override
        public void stop() {
            super.stop();
            this.drowned.stopUsingItem();
            this.drowned.setAggressive(false);
        }
    }

    private static class a extends PathfinderGoalZombieAttack {

        private final EntityDrowned drowned;

        public a(EntityDrowned entitydrowned, double d0, boolean flag) {
            super((EntityZombie) entitydrowned, d0, flag);
            this.drowned = entitydrowned;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && this.drowned.okTarget(this.drowned.getTarget());
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.drowned.okTarget(this.drowned.getTarget());
        }
    }

    private static class b extends PathfinderGoalGotoTarget {

        private final EntityDrowned drowned;

        public b(EntityDrowned entitydrowned, double d0) {
            super(entitydrowned, d0, 8, 2);
            this.drowned = entitydrowned;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() >= (double) (this.drowned.level.getSeaLevel() - 3);
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse();
        }

        @Override
        protected boolean isValidTarget(IWorldReader iworldreader, BlockPosition blockposition) {
            BlockPosition blockposition1 = blockposition.above();

            return iworldreader.isEmptyBlock(blockposition1) && iworldreader.isEmptyBlock(blockposition1.above()) ? iworldreader.getBlockState(blockposition).entityCanStandOn(iworldreader, blockposition, this.drowned) : false;
        }

        @Override
        public void start() {
            this.drowned.setSearchingForLand(false);
            this.drowned.navigation = this.drowned.groundNavigation;
            super.start();
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    private static class e extends PathfinderGoal {

        private final EntityDrowned drowned;
        private final double speedModifier;
        private final int seaLevel;
        private boolean stuck;

        public e(EntityDrowned entitydrowned, double d0, int i) {
            this.drowned = entitydrowned;
            this.speedModifier = d0;
            this.seaLevel = i;
        }

        @Override
        public boolean canUse() {
            return !this.drowned.level.isDay() && this.drowned.isInWater() && this.drowned.getY() < (double) (this.seaLevel - 2);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse() && !this.stuck;
        }

        @Override
        public void tick() {
            if (this.drowned.getY() < (double) (this.seaLevel - 1) && (this.drowned.getNavigation().isDone() || this.drowned.closeToNextPos())) {
                Vec3D vec3d = DefaultRandomPos.getPosTowards(this.drowned, 4, 8, new Vec3D(this.drowned.getX(), (double) (this.seaLevel - 1), this.drowned.getZ()), 1.5707963705062866D);

                if (vec3d == null) {
                    this.stuck = true;
                    return;
                }

                this.drowned.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, this.speedModifier);
            }

        }

        @Override
        public void start() {
            this.drowned.setSearchingForLand(true);
            this.stuck = false;
        }

        @Override
        public void stop() {
            this.drowned.setSearchingForLand(false);
        }
    }
}
