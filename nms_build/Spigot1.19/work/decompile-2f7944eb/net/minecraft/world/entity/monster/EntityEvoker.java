package net.minecraft.world.entity.monster;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalAvoidTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.animal.EntitySheep;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityEvokerFangs;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EntityEvoker extends EntityIllagerWizard {

    @Nullable
    private EntitySheep wololoTarget;

    public EntityEvoker(EntityTypes<? extends EntityEvoker> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 10;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(1, new EntityEvoker.b());
        this.goalSelector.addGoal(2, new PathfinderGoalAvoidTarget<>(this, EntityHuman.class, 8.0F, 0.6D, 1.0D));
        this.goalSelector.addGoal(4, new EntityEvoker.c());
        this.goalSelector.addGoal(5, new EntityEvoker.a());
        this.goalSelector.addGoal(6, new EntityEvoker.d());
        this.goalSelector.addGoal(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.addGoal(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.addGoal(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).setAlertOthers());
        this.targetSelector.addGoal(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.5D).add(GenericAttributes.FOLLOW_RANGE, 12.0D).add(GenericAttributes.MAX_HEALTH, 24.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
    }

    @Override
    public SoundEffect getCelebrateSound() {
        return SoundEffects.EVOKER_CELEBRATE;
    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return entity == null ? false : (entity == this ? true : (super.isAlliedTo(entity) ? true : (entity instanceof EntityVex ? this.isAlliedTo((Entity) ((EntityVex) entity).getOwner()) : (entity instanceof EntityLiving && ((EntityLiving) entity).getMobType() == EnumMonsterType.ILLAGER ? this.getTeam() == null && entity.getTeam() == null : false))));
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.EVOKER_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.EVOKER_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.EVOKER_HURT;
    }

    void setWololoTarget(@Nullable EntitySheep entitysheep) {
        this.wololoTarget = entitysheep;
    }

    @Nullable
    EntitySheep getWololoTarget() {
        return this.wololoTarget;
    }

    @Override
    protected SoundEffect getCastingSoundEvent() {
        return SoundEffects.EVOKER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(int i, boolean flag) {}

    private class b extends EntityIllagerWizard.b {

        b() {
            super();
        }

        @Override
        public void tick() {
            if (EntityEvoker.this.getTarget() != null) {
                EntityEvoker.this.getLookControl().setLookAt(EntityEvoker.this.getTarget(), (float) EntityEvoker.this.getMaxHeadYRot(), (float) EntityEvoker.this.getMaxHeadXRot());
            } else if (EntityEvoker.this.getWololoTarget() != null) {
                EntityEvoker.this.getLookControl().setLookAt(EntityEvoker.this.getWololoTarget(), (float) EntityEvoker.this.getMaxHeadYRot(), (float) EntityEvoker.this.getMaxHeadXRot());
            }

        }
    }

    private class c extends EntityIllagerWizard.PathfinderGoalCastSpell {

        private final PathfinderTargetCondition vexCountTargeting = PathfinderTargetCondition.forNonCombat().range(16.0D).ignoreLineOfSight().ignoreInvisibilityTesting();

        c() {
            super();
        }

        @Override
        public boolean canUse() {
            if (!super.canUse()) {
                return false;
            } else {
                int i = EntityEvoker.this.level.getNearbyEntities(EntityVex.class, this.vexCountTargeting, EntityEvoker.this, EntityEvoker.this.getBoundingBox().inflate(16.0D)).size();

                return EntityEvoker.this.random.nextInt(8) + 1 > i;
            }
        }

        @Override
        protected int getCastingTime() {
            return 100;
        }

        @Override
        protected int getCastingInterval() {
            return 340;
        }

        @Override
        protected void performSpellCasting() {
            WorldServer worldserver = (WorldServer) EntityEvoker.this.level;

            for (int i = 0; i < 3; ++i) {
                BlockPosition blockposition = EntityEvoker.this.blockPosition().offset(-2 + EntityEvoker.this.random.nextInt(5), 1, -2 + EntityEvoker.this.random.nextInt(5));
                EntityVex entityvex = (EntityVex) EntityTypes.VEX.create(EntityEvoker.this.level);

                entityvex.moveTo(blockposition, 0.0F, 0.0F);
                entityvex.finalizeSpawn(worldserver, EntityEvoker.this.level.getCurrentDifficultyAt(blockposition), EnumMobSpawn.MOB_SUMMONED, (GroupDataEntity) null, (NBTTagCompound) null);
                entityvex.setOwner(EntityEvoker.this);
                entityvex.setBoundOrigin(blockposition);
                entityvex.setLimitedLife(20 * (30 + EntityEvoker.this.random.nextInt(90)));
                worldserver.addFreshEntityWithPassengers(entityvex);
            }

        }

        @Override
        protected SoundEffect getSpellPrepareSound() {
            return SoundEffects.EVOKER_PREPARE_SUMMON;
        }

        @Override
        protected EntityIllagerWizard.Spell getSpell() {
            return EntityIllagerWizard.Spell.SUMMON_VEX;
        }
    }

    private class a extends EntityIllagerWizard.PathfinderGoalCastSpell {

        a() {
            super();
        }

        @Override
        protected int getCastingTime() {
            return 40;
        }

        @Override
        protected int getCastingInterval() {
            return 100;
        }

        @Override
        protected void performSpellCasting() {
            EntityLiving entityliving = EntityEvoker.this.getTarget();
            double d0 = Math.min(entityliving.getY(), EntityEvoker.this.getY());
            double d1 = Math.max(entityliving.getY(), EntityEvoker.this.getY()) + 1.0D;
            float f = (float) MathHelper.atan2(entityliving.getZ() - EntityEvoker.this.getZ(), entityliving.getX() - EntityEvoker.this.getX());
            int i;

            if (EntityEvoker.this.distanceToSqr((Entity) entityliving) < 9.0D) {
                float f1;

                for (i = 0; i < 5; ++i) {
                    f1 = f + (float) i * 3.1415927F * 0.4F;
                    this.createSpellEntity(EntityEvoker.this.getX() + (double) MathHelper.cos(f1) * 1.5D, EntityEvoker.this.getZ() + (double) MathHelper.sin(f1) * 1.5D, d0, d1, f1, 0);
                }

                for (i = 0; i < 8; ++i) {
                    f1 = f + (float) i * 3.1415927F * 2.0F / 8.0F + 1.2566371F;
                    this.createSpellEntity(EntityEvoker.this.getX() + (double) MathHelper.cos(f1) * 2.5D, EntityEvoker.this.getZ() + (double) MathHelper.sin(f1) * 2.5D, d0, d1, f1, 3);
                }
            } else {
                for (i = 0; i < 16; ++i) {
                    double d2 = 1.25D * (double) (i + 1);
                    int j = 1 * i;

                    this.createSpellEntity(EntityEvoker.this.getX() + (double) MathHelper.cos(f) * d2, EntityEvoker.this.getZ() + (double) MathHelper.sin(f) * d2, d0, d1, f, j);
                }
            }

        }

        private void createSpellEntity(double d0, double d1, double d2, double d3, float f, int i) {
            BlockPosition blockposition = new BlockPosition(d0, d3, d1);
            boolean flag = false;
            double d4 = 0.0D;

            do {
                BlockPosition blockposition1 = blockposition.below();
                IBlockData iblockdata = EntityEvoker.this.level.getBlockState(blockposition1);

                if (iblockdata.isFaceSturdy(EntityEvoker.this.level, blockposition1, EnumDirection.UP)) {
                    if (!EntityEvoker.this.level.isEmptyBlock(blockposition)) {
                        IBlockData iblockdata1 = EntityEvoker.this.level.getBlockState(blockposition);
                        VoxelShape voxelshape = iblockdata1.getCollisionShape(EntityEvoker.this.level, blockposition);

                        if (!voxelshape.isEmpty()) {
                            d4 = voxelshape.max(EnumDirection.EnumAxis.Y);
                        }
                    }

                    flag = true;
                    break;
                }

                blockposition = blockposition.below();
            } while (blockposition.getY() >= MathHelper.floor(d2) - 1);

            if (flag) {
                EntityEvoker.this.level.addFreshEntity(new EntityEvokerFangs(EntityEvoker.this.level, d0, (double) blockposition.getY() + d4, d1, f, i, EntityEvoker.this));
            }

        }

        @Override
        protected SoundEffect getSpellPrepareSound() {
            return SoundEffects.EVOKER_PREPARE_ATTACK;
        }

        @Override
        protected EntityIllagerWizard.Spell getSpell() {
            return EntityIllagerWizard.Spell.FANGS;
        }
    }

    public class d extends EntityIllagerWizard.PathfinderGoalCastSpell {

        private final PathfinderTargetCondition wololoTargeting = PathfinderTargetCondition.forNonCombat().range(16.0D).selector((entityliving) -> {
            return ((EntitySheep) entityliving).getColor() == EnumColor.BLUE;
        });

        public d() {
            super();
        }

        @Override
        public boolean canUse() {
            if (EntityEvoker.this.getTarget() != null) {
                return false;
            } else if (EntityEvoker.this.isCastingSpell()) {
                return false;
            } else if (EntityEvoker.this.tickCount < this.nextAttackTickCount) {
                return false;
            } else if (!EntityEvoker.this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                return false;
            } else {
                List<EntitySheep> list = EntityEvoker.this.level.getNearbyEntities(EntitySheep.class, this.wololoTargeting, EntityEvoker.this, EntityEvoker.this.getBoundingBox().inflate(16.0D, 4.0D, 16.0D));

                if (list.isEmpty()) {
                    return false;
                } else {
                    EntityEvoker.this.setWololoTarget((EntitySheep) list.get(EntityEvoker.this.random.nextInt(list.size())));
                    return true;
                }
            }
        }

        @Override
        public boolean canContinueToUse() {
            return EntityEvoker.this.getWololoTarget() != null && this.attackWarmupDelay > 0;
        }

        @Override
        public void stop() {
            super.stop();
            EntityEvoker.this.setWololoTarget((EntitySheep) null);
        }

        @Override
        protected void performSpellCasting() {
            EntitySheep entitysheep = EntityEvoker.this.getWololoTarget();

            if (entitysheep != null && entitysheep.isAlive()) {
                entitysheep.setColor(EnumColor.RED);
            }

        }

        @Override
        protected int getCastWarmupTime() {
            return 40;
        }

        @Override
        protected int getCastingTime() {
            return 60;
        }

        @Override
        protected int getCastingInterval() {
            return 140;
        }

        @Override
        protected SoundEffect getSpellPrepareSound() {
            return SoundEffects.EVOKER_PREPARE_WOLOLO;
        }

        @Override
        protected EntityIllagerWizard.Spell getSpell() {
            return EntityIllagerWizard.Spell.WOLOLO;
        }
    }
}
