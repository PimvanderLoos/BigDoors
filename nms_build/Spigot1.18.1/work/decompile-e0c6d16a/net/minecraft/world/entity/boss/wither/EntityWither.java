package net.minecraft.world.entity.boss.wither;

import com.google.common.collect.ImmutableList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.server.level.BossBattleServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.BossBattle;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMoveFlying;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomFly;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationFlying;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.monster.IRangedEntity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntityWitherSkull;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class EntityWither extends EntityMonster implements PowerableMob, IRangedEntity {

    private static final DataWatcherObject<Integer> DATA_TARGET_A = DataWatcher.defineId(EntityWither.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_TARGET_B = DataWatcher.defineId(EntityWither.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_TARGET_C = DataWatcher.defineId(EntityWither.class, DataWatcherRegistry.INT);
    private static final List<DataWatcherObject<Integer>> DATA_TARGETS = ImmutableList.of(EntityWither.DATA_TARGET_A, EntityWither.DATA_TARGET_B, EntityWither.DATA_TARGET_C);
    private static final DataWatcherObject<Integer> DATA_ID_INV = DataWatcher.defineId(EntityWither.class, DataWatcherRegistry.INT);
    private static final int INVULNERABLE_TICKS = 220;
    private final float[] xRotHeads = new float[2];
    private final float[] yRotHeads = new float[2];
    private final float[] xRotOHeads = new float[2];
    private final float[] yRotOHeads = new float[2];
    private final int[] nextHeadUpdate = new int[2];
    private final int[] idleHeadUpdates = new int[2];
    private int destroyBlocksTick;
    public final BossBattleServer bossEvent;
    private static final Predicate<EntityLiving> LIVING_ENTITY_SELECTOR = (entityliving) -> {
        return entityliving.getMobType() != EnumMonsterType.UNDEAD && entityliving.attackable();
    };
    private static final PathfinderTargetCondition TARGETING_CONDITIONS = PathfinderTargetCondition.forCombat().range(20.0D).selector(EntityWither.LIVING_ENTITY_SELECTOR);

    public EntityWither(EntityTypes<? extends EntityWither> entitytypes, World world) {
        super(entitytypes, world);
        this.bossEvent = (BossBattleServer) (new BossBattleServer(this.getDisplayName(), BossBattle.BarColor.PURPLE, BossBattle.BarStyle.PROGRESS)).setDarkenScreen(true);
        this.moveControl = new ControllerMoveFlying(this, 10, false);
        this.setHealth(this.getMaxHealth());
        this.xpReward = 50;
    }

    @Override
    protected NavigationAbstract createNavigation(World world) {
        NavigationFlying navigationflying = new NavigationFlying(this, world);

        navigationflying.setCanOpenDoors(false);
        navigationflying.setCanFloat(true);
        navigationflying.setCanPassDoors(true);
        return navigationflying;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new EntityWither.a());
        this.goalSelector.addGoal(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
        this.goalSelector.addGoal(5, new PathfinderGoalRandomFly(this, 1.0D));
        this.goalSelector.addGoal(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.addGoal(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.addGoal(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.addGoal(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityLiving.class, 0, false, false, EntityWither.LIVING_ENTITY_SELECTOR));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(EntityWither.DATA_TARGET_A, 0);
        this.entityData.define(EntityWither.DATA_TARGET_B, 0);
        this.entityData.define(EntityWither.DATA_TARGET_C, 0);
        this.entityData.define(EntityWither.DATA_ID_INV, 0);
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        nbttagcompound.putInt("Invul", this.getInvulnerableTicks());
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        this.setInvulnerableTicks(nbttagcompound.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }

    }

    @Override
    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        super.setCustomName(ichatbasecomponent);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.WITHER_AMBIENT;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.WITHER_HURT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.WITHER_DEATH;
    }

    @Override
    public void aiStep() {
        Vec3D vec3d = this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D);

        if (!this.level.isClientSide && this.getAlternativeTarget(0) > 0) {
            Entity entity = this.level.getEntity(this.getAlternativeTarget(0));

            if (entity != null) {
                double d0 = vec3d.y;

                if (this.getY() < entity.getY() || !this.isPowered() && this.getY() < entity.getY() + 5.0D) {
                    d0 = Math.max(0.0D, d0);
                    d0 += 0.3D - d0 * 0.6000000238418579D;
                }

                vec3d = new Vec3D(vec3d.x, d0, vec3d.z);
                Vec3D vec3d1 = new Vec3D(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());

                if (vec3d1.horizontalDistanceSqr() > 9.0D) {
                    Vec3D vec3d2 = vec3d1.normalize();

                    vec3d = vec3d.add(vec3d2.x * 0.3D - vec3d.x * 0.6D, 0.0D, vec3d2.z * 0.3D - vec3d.z * 0.6D);
                }
            }
        }

        this.setDeltaMovement(vec3d);
        if (vec3d.horizontalDistanceSqr() > 0.05D) {
            this.setYRot((float) MathHelper.atan2(vec3d.z, vec3d.x) * 57.295776F - 90.0F);
        }

        super.aiStep();

        int i;

        for (i = 0; i < 2; ++i) {
            this.yRotOHeads[i] = this.yRotHeads[i];
            this.xRotOHeads[i] = this.xRotHeads[i];
        }

        int j;

        for (i = 0; i < 2; ++i) {
            j = this.getAlternativeTarget(i + 1);
            Entity entity1 = null;

            if (j > 0) {
                entity1 = this.level.getEntity(j);
            }

            if (entity1 != null) {
                double d1 = this.getHeadX(i + 1);
                double d2 = this.getHeadY(i + 1);
                double d3 = this.getHeadZ(i + 1);
                double d4 = entity1.getX() - d1;
                double d5 = entity1.getEyeY() - d2;
                double d6 = entity1.getZ() - d3;
                double d7 = Math.sqrt(d4 * d4 + d6 * d6);
                float f = (float) (MathHelper.atan2(d6, d4) * 57.2957763671875D) - 90.0F;
                float f1 = (float) (-(MathHelper.atan2(d5, d7) * 57.2957763671875D));

                this.xRotHeads[i] = this.rotlerp(this.xRotHeads[i], f1, 40.0F);
                this.yRotHeads[i] = this.rotlerp(this.yRotHeads[i], f, 10.0F);
            } else {
                this.yRotHeads[i] = this.rotlerp(this.yRotHeads[i], this.yBodyRot, 10.0F);
            }
        }

        boolean flag = this.isPowered();

        for (j = 0; j < 3; ++j) {
            double d8 = this.getHeadX(j);
            double d9 = this.getHeadY(j);
            double d10 = this.getHeadZ(j);

            this.level.addParticle(Particles.SMOKE, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
            if (flag && this.level.random.nextInt(4) == 0) {
                this.level.addParticle(Particles.ENTITY_EFFECT, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
            }
        }

        if (this.getInvulnerableTicks() > 0) {
            for (j = 0; j < 3; ++j) {
                this.level.addParticle(Particles.ENTITY_EFFECT, this.getX() + this.random.nextGaussian(), this.getY() + (double) (this.random.nextFloat() * 3.3F), this.getZ() + this.random.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
            }
        }

    }

    @Override
    protected void customServerAiStep() {
        int i;

        if (this.getInvulnerableTicks() > 0) {
            i = this.getInvulnerableTicks() - 1;
            this.bossEvent.setProgress(1.0F - (float) i / 220.0F);
            if (i <= 0) {
                Explosion.Effect explosion_effect = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.Effect.DESTROY : Explosion.Effect.NONE;

                this.level.explode(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, explosion_effect);
                if (!this.isSilent()) {
                    this.level.globalLevelEvent(1023, this.blockPosition(), 0);
                }
            }

            this.setInvulnerableTicks(i);
            if (this.tickCount % 10 == 0) {
                this.heal(10.0F);
            }

        } else {
            super.customServerAiStep();

            int j;

            for (i = 1; i < 3; ++i) {
                if (this.tickCount >= this.nextHeadUpdate[i - 1]) {
                    this.nextHeadUpdate[i - 1] = this.tickCount + 10 + this.random.nextInt(10);
                    if (this.level.getDifficulty() == EnumDifficulty.NORMAL || this.level.getDifficulty() == EnumDifficulty.HARD) {
                        int k = i - 1;
                        int l = this.idleHeadUpdates[i - 1];

                        this.idleHeadUpdates[k] = this.idleHeadUpdates[i - 1] + 1;
                        if (l > 15) {
                            float f = 10.0F;
                            float f1 = 5.0F;
                            double d0 = MathHelper.nextDouble(this.random, this.getX() - 10.0D, this.getX() + 10.0D);
                            double d1 = MathHelper.nextDouble(this.random, this.getY() - 5.0D, this.getY() + 5.0D);
                            double d2 = MathHelper.nextDouble(this.random, this.getZ() - 10.0D, this.getZ() + 10.0D);

                            this.performRangedAttack(i + 1, d0, d1, d2, true);
                            this.idleHeadUpdates[i - 1] = 0;
                        }
                    }

                    j = this.getAlternativeTarget(i);
                    if (j > 0) {
                        EntityLiving entityliving = (EntityLiving) this.level.getEntity(j);

                        if (entityliving != null && this.canAttack(entityliving) && this.distanceToSqr((Entity) entityliving) <= 900.0D && this.hasLineOfSight(entityliving)) {
                            this.performRangedAttack(i + 1, entityliving);
                            this.nextHeadUpdate[i - 1] = this.tickCount + 40 + this.random.nextInt(20);
                            this.idleHeadUpdates[i - 1] = 0;
                        } else {
                            this.setAlternativeTarget(i, 0);
                        }
                    } else {
                        List<EntityLiving> list = this.level.getNearbyEntities(EntityLiving.class, EntityWither.TARGETING_CONDITIONS, this, this.getBoundingBox().inflate(20.0D, 8.0D, 20.0D));

                        if (!list.isEmpty()) {
                            EntityLiving entityliving1 = (EntityLiving) list.get(this.random.nextInt(list.size()));

                            this.setAlternativeTarget(i, entityliving1.getId());
                        }
                    }
                }
            }

            if (this.getTarget() != null) {
                this.setAlternativeTarget(0, this.getTarget().getId());
            } else {
                this.setAlternativeTarget(0, 0);
            }

            if (this.destroyBlocksTick > 0) {
                --this.destroyBlocksTick;
                if (this.destroyBlocksTick == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    i = MathHelper.floor(this.getY());
                    j = MathHelper.floor(this.getX());
                    int i1 = MathHelper.floor(this.getZ());
                    boolean flag = false;

                    for (int j1 = -1; j1 <= 1; ++j1) {
                        for (int k1 = -1; k1 <= 1; ++k1) {
                            for (int l1 = 0; l1 <= 3; ++l1) {
                                int i2 = j + j1;
                                int j2 = i + l1;
                                int k2 = i1 + k1;
                                BlockPosition blockposition = new BlockPosition(i2, j2, k2);
                                IBlockData iblockdata = this.level.getBlockState(blockposition);

                                if (canDestroy(iblockdata)) {
                                    flag = this.level.destroyBlock(blockposition, true, this) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.level.levelEvent((EntityHuman) null, 1022, this.blockPosition(), 0);
                    }
                }
            }

            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    public static boolean canDestroy(IBlockData iblockdata) {
        return !iblockdata.isAir() && !iblockdata.is((Tag) TagsBlock.WITHER_IMMUNE);
    }

    public void makeInvulnerable() {
        this.setInvulnerableTicks(220);
        this.bossEvent.setProgress(0.0F);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    @Override
    public void makeStuckInBlock(IBlockData iblockdata, Vec3D vec3d) {}

    @Override
    public void startSeenByPlayer(EntityPlayer entityplayer) {
        super.startSeenByPlayer(entityplayer);
        this.bossEvent.addPlayer(entityplayer);
    }

    @Override
    public void stopSeenByPlayer(EntityPlayer entityplayer) {
        super.stopSeenByPlayer(entityplayer);
        this.bossEvent.removePlayer(entityplayer);
    }

    private double getHeadX(int i) {
        if (i <= 0) {
            return this.getX();
        } else {
            float f = (this.yBodyRot + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.cos(f);

            return this.getX() + (double) f1 * 1.3D;
        }
    }

    private double getHeadY(int i) {
        return i <= 0 ? this.getY() + 3.0D : this.getY() + 2.2D;
    }

    private double getHeadZ(int i) {
        if (i <= 0) {
            return this.getZ();
        } else {
            float f = (this.yBodyRot + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.sin(f);

            return this.getZ() + (double) f1 * 1.3D;
        }
    }

    private float rotlerp(float f, float f1, float f2) {
        float f3 = MathHelper.wrapDegrees(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    private void performRangedAttack(int i, EntityLiving entityliving) {
        this.performRangedAttack(i, entityliving.getX(), entityliving.getY() + (double) entityliving.getEyeHeight() * 0.5D, entityliving.getZ(), i == 0 && this.random.nextFloat() < 0.001F);
    }

    private void performRangedAttack(int i, double d0, double d1, double d2, boolean flag) {
        if (!this.isSilent()) {
            this.level.levelEvent((EntityHuman) null, 1024, this.blockPosition(), 0);
        }

        double d3 = this.getHeadX(i);
        double d4 = this.getHeadY(i);
        double d5 = this.getHeadZ(i);
        double d6 = d0 - d3;
        double d7 = d1 - d4;
        double d8 = d2 - d5;
        EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.level, this, d6, d7, d8);

        entitywitherskull.setOwner(this);
        if (flag) {
            entitywitherskull.setDangerous(true);
        }

        entitywitherskull.setPosRaw(d3, d4, d5);
        this.level.addFreshEntity(entitywitherskull);
    }

    @Override
    public void performRangedAttack(EntityLiving entityliving, float f) {
        this.performRangedAttack(0, entityliving);
    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else if (damagesource != DamageSource.DROWN && !(damagesource.getEntity() instanceof EntityWither)) {
            if (this.getInvulnerableTicks() > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                Entity entity;

                if (this.isPowered()) {
                    entity = damagesource.getDirectEntity();
                    if (entity instanceof EntityArrow) {
                        return false;
                    }
                }

                entity = damagesource.getEntity();
                if (entity != null && !(entity instanceof EntityHuman) && entity instanceof EntityLiving && ((EntityLiving) entity).getMobType() == this.getMobType()) {
                    return false;
                } else {
                    if (this.destroyBlocksTick <= 0) {
                        this.destroyBlocksTick = 20;
                    }

                    for (int i = 0; i < this.idleHeadUpdates.length; ++i) {
                        this.idleHeadUpdates[i] += 3;
                    }

                    return super.hurt(damagesource, f);
                }
            }
        } else {
            return false;
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropCustomDeathLoot(damagesource, i, flag);
        EntityItem entityitem = this.spawnAtLocation((IMaterial) Items.NETHER_STAR);

        if (entityitem != null) {
            entityitem.setExtendedLifetime();
        }

    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            this.discard();
        } else {
            this.noActionTime = 0;
        }
    }

    @Override
    public boolean causeFallDamage(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffect mobeffect, @Nullable Entity entity) {
        return false;
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MAX_HEALTH, 300.0D).add(GenericAttributes.MOVEMENT_SPEED, 0.6000000238418579D).add(GenericAttributes.FLYING_SPEED, 0.6000000238418579D).add(GenericAttributes.FOLLOW_RANGE, 40.0D).add(GenericAttributes.ARMOR, 4.0D);
    }

    public float getHeadYRot(int i) {
        return this.yRotHeads[i];
    }

    public float getHeadXRot(int i) {
        return this.xRotHeads[i];
    }

    public int getInvulnerableTicks() {
        return (Integer) this.entityData.get(EntityWither.DATA_ID_INV);
    }

    public void setInvulnerableTicks(int i) {
        this.entityData.set(EntityWither.DATA_ID_INV, i);
    }

    public int getAlternativeTarget(int i) {
        return (Integer) this.entityData.get((DataWatcherObject) EntityWither.DATA_TARGETS.get(i));
    }

    public void setAlternativeTarget(int i, int j) {
        this.entityData.set((DataWatcherObject) EntityWither.DATA_TARGETS.get(i), j);
    }

    @Override
    public boolean isPowered() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    @Override
    public EnumMonsterType getMobType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected boolean canRide(Entity entity) {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean canBeAffected(MobEffect mobeffect) {
        return mobeffect.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(mobeffect);
    }

    private class a extends PathfinderGoal {

        public a() {
            this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.JUMP, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean canUse() {
            return EntityWither.this.getInvulnerableTicks() > 0;
        }
    }
}
