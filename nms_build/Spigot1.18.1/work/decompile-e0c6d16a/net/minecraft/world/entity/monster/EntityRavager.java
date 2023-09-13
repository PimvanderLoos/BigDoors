package net.minecraft.world.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.TagsEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.Navigation;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockLeaves;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityRavager extends EntityRaider {

    private static final Predicate<Entity> NO_RAVAGER_AND_ALIVE = (entity) -> {
        return entity.isAlive() && !(entity instanceof EntityRavager);
    };
    private static final double BASE_MOVEMENT_SPEED = 0.3D;
    private static final double ATTACK_MOVEMENT_SPEED = 0.35D;
    private static final int STUNNED_COLOR = 8356754;
    private static final double STUNNED_COLOR_BLUE = 0.5725490196078431D;
    private static final double STUNNED_COLOR_GREEN = 0.5137254901960784D;
    private static final double STUNNED_COLOR_RED = 0.4980392156862745D;
    private static final int ATTACK_DURATION = 10;
    public static final int STUN_DURATION = 40;
    private int attackTick;
    private int stunnedTick;
    private int roarTick;

    public EntityRavager(EntityTypes<? extends EntityRavager> entitytypes, World world) {
        super(entitytypes, world);
        this.maxUpStep = 1.0F;
        this.xpReward = 20;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(4, new EntityRavager.a());
        this.goalSelector.addGoal(5, new PathfinderGoalRandomStrollLand(this, 0.4D));
        this.goalSelector.addGoal(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.addGoal(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.addGoal(2, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).setAlertOthers());
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.addGoal(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, true, (entityliving) -> {
            return !entityliving.isBaby();
        }));
        this.targetSelector.addGoal(4, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void updateControlFlags() {
        boolean flag = !(this.getControllingPassenger() instanceof EntityInsentient) || this.getControllingPassenger().getType().is(TagsEntity.RAIDERS);
        boolean flag1 = !(this.getVehicle() instanceof EntityBoat);

        this.goalSelector.setControlFlag(PathfinderGoal.Type.MOVE, flag);
        this.goalSelector.setControlFlag(PathfinderGoal.Type.JUMP, flag && flag1);
        this.goalSelector.setControlFlag(PathfinderGoal.Type.LOOK, flag);
        this.goalSelector.setControlFlag(PathfinderGoal.Type.TARGET, flag);
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MAX_HEALTH, 100.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.3D).add(GenericAttributes.KNOCKBACK_RESISTANCE, 0.75D).add(GenericAttributes.ATTACK_DAMAGE, 12.0D).add(GenericAttributes.ATTACK_KNOCKBACK, 1.5D).add(GenericAttributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("AttackTick", this.attackTick);
        nbttagcompound.putInt("StunTick", this.stunnedTick);
        nbttagcompound.putInt("RoarTick", this.roarTick);
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.attackTick = nbttagcompound.getInt("AttackTick");
        this.stunnedTick = nbttagcompound.getInt("StunTick");
        this.roarTick = nbttagcompound.getInt("RoarTick");
    }

    @Override
    public SoundEffect getCelebrateSound() {
        return SoundEffects.RAVAGER_CELEBRATE;
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        return new EntityRavager.b(this, world);
    }

    @Override
    public int getMaxHeadYRot() {
        return 45;
    }

    @Override
    public double getPassengersRidingOffset() {
        return 2.1D;
    }

    @Override
    public boolean canBeControlledByRider() {
        return !this.isNoAi() && this.getControllingPassenger() instanceof EntityLiving;
    }

    @Nullable
    @Override
    public Entity getControllingPassenger() {
        return this.getFirstPassenger();
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.isAlive()) {
            if (this.isImmobile()) {
                this.getAttribute(GenericAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
            } else {
                double d0 = this.getTarget() != null ? 0.35D : 0.3D;
                double d1 = this.getAttribute(GenericAttributes.MOVEMENT_SPEED).getBaseValue();

                this.getAttribute(GenericAttributes.MOVEMENT_SPEED).setBaseValue(MathHelper.lerp(0.1D, d1, d0));
            }

            if (this.horizontalCollision && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                boolean flag = false;
                AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(0.2D);
                Iterator iterator = BlockPosition.betweenClosed(MathHelper.floor(axisalignedbb.minX), MathHelper.floor(axisalignedbb.minY), MathHelper.floor(axisalignedbb.minZ), MathHelper.floor(axisalignedbb.maxX), MathHelper.floor(axisalignedbb.maxY), MathHelper.floor(axisalignedbb.maxZ)).iterator();

                while (iterator.hasNext()) {
                    BlockPosition blockposition = (BlockPosition) iterator.next();
                    IBlockData iblockdata = this.level.getBlockState(blockposition);
                    Block block = iblockdata.getBlock();

                    if (block instanceof BlockLeaves) {
                        flag = this.level.destroyBlock(blockposition, true, this) || flag;
                    }
                }

                if (!flag && this.onGround) {
                    this.jumpFromGround();
                }
            }

            if (this.roarTick > 0) {
                --this.roarTick;
                if (this.roarTick == 10) {
                    this.roar();
                }
            }

            if (this.attackTick > 0) {
                --this.attackTick;
            }

            if (this.stunnedTick > 0) {
                --this.stunnedTick;
                this.stunEffect();
                if (this.stunnedTick == 0) {
                    this.playSound(SoundEffects.RAVAGER_ROAR, 1.0F, 1.0F);
                    this.roarTick = 20;
                }
            }

        }
    }

    private void stunEffect() {
        if (this.random.nextInt(6) == 0) {
            double d0 = this.getX() - (double) this.getBbWidth() * Math.sin((double) (this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
            double d1 = this.getY() + (double) this.getBbHeight() - 0.3D;
            double d2 = this.getZ() + (double) this.getBbWidth() * Math.cos((double) (this.yBodyRot * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);

            this.level.addParticle(Particles.ENTITY_EFFECT, d0, d1, d2, 0.4980392156862745D, 0.5137254901960784D, 0.5725490196078431D);
        }

    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunnedTick > 0 || this.roarTick > 0;
    }

    @Override
    public boolean hasLineOfSight(Entity entity) {
        return this.stunnedTick <= 0 && this.roarTick <= 0 ? super.hasLineOfSight(entity) : false;
    }

    @Override
    protected void blockedByShield(EntityLiving entityliving) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5D) {
                this.stunnedTick = 40;
                this.playSound(SoundEffects.RAVAGER_STUNNED, 1.0F, 1.0F);
                this.level.broadcastEntityEvent(this, (byte) 39);
                entityliving.push(this);
            } else {
                this.strongKnockback(entityliving);
            }

            entityliving.hurtMarked = true;
        }

    }

    private void roar() {
        if (this.isAlive()) {
            List<? extends EntityLiving> list = this.level.getEntitiesOfClass(EntityLiving.class, this.getBoundingBox().inflate(4.0D), EntityRavager.NO_RAVAGER_AND_ALIVE);

            EntityLiving entityliving;

            for (Iterator iterator = list.iterator(); iterator.hasNext(); this.strongKnockback(entityliving)) {
                entityliving = (EntityLiving) iterator.next();
                if (!(entityliving instanceof EntityIllagerAbstract)) {
                    entityliving.hurt(DamageSource.mobAttack(this), 6.0F);
                }
            }

            Vec3D vec3d = this.getBoundingBox().getCenter();

            for (int i = 0; i < 40; ++i) {
                double d0 = this.random.nextGaussian() * 0.2D;
                double d1 = this.random.nextGaussian() * 0.2D;
                double d2 = this.random.nextGaussian() * 0.2D;

                this.level.addParticle(Particles.POOF, vec3d.x, vec3d.y, vec3d.z, d0, d1, d2);
            }

            this.level.gameEvent(this, GameEvent.RAVAGER_ROAR, this.eyeBlockPosition());
        }

    }

    private void strongKnockback(Entity entity) {
        double d0 = entity.getX() - this.getX();
        double d1 = entity.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);

        entity.push(d0 / d2 * 4.0D, 0.2D, d1 / d2 * 4.0D);
    }

    @Override
    public void handleEntityEvent(byte b0) {
        if (b0 == 4) {
            this.attackTick = 10;
            this.playSound(SoundEffects.RAVAGER_ATTACK, 1.0F, 1.0F);
        } else if (b0 == 39) {
            this.stunnedTick = 40;
        }

        super.handleEntityEvent(b0);
    }

    public int getAttackTick() {
        return this.attackTick;
    }

    public int getStunnedTick() {
        return this.stunnedTick;
    }

    public int getRoarTick() {
        return this.roarTick;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        this.attackTick = 10;
        this.level.broadcastEntityEvent(this, (byte) 4);
        this.playSound(SoundEffects.RAVAGER_ATTACK, 1.0F, 1.0F);
        return super.doHurtTarget(entity);
    }

    @Nullable
    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.RAVAGER_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.RAVAGER_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.RAVAGER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.RAVAGER_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean checkSpawnObstruction(IWorldReader iworldreader) {
        return !iworldreader.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    public void applyRaidBuffs(int i, boolean flag) {}

    @Override
    public boolean canBeLeader() {
        return false;
    }

    private class a extends PathfinderGoalMeleeAttack {

        public a() {
            super(EntityRavager.this, 1.0D, true);
        }

        @Override
        protected double getAttackReachSqr(EntityLiving entityliving) {
            float f = EntityRavager.this.getBbWidth() - 0.1F;

            return (double) (f * 2.0F * f * 2.0F + entityliving.getBbWidth());
        }
    }

    private static class b extends Navigation {

        public b(EntityInsentient entityinsentient, World world) {
            super(entityinsentient, world);
        }

        @Override
        protected Pathfinder createPathFinder(int i) {
            this.nodeEvaluator = new EntityRavager.c();
            return new Pathfinder(this.nodeEvaluator, i);
        }
    }

    private static class c extends PathfinderNormal {

        c() {}

        @Override
        protected PathType evaluateBlockPathType(IBlockAccess iblockaccess, boolean flag, boolean flag1, BlockPosition blockposition, PathType pathtype) {
            return pathtype == PathType.LEAVES ? PathType.OPEN : super.evaluateBlockPathType(iblockaccess, flag, flag1, blockposition, pathtype);
        }
    }
}
