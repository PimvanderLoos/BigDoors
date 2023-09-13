package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalBowShoot;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStroll;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.npc.EntityVillagerAbstract;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.ProjectileHelper;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class EntityIllagerIllusioner extends EntityIllagerWizard implements IRangedEntity {

    private static final int NUM_ILLUSIONS = 4;
    private static final int ILLUSION_TRANSITION_TICKS = 3;
    private static final int ILLUSION_SPREAD = 3;
    private int clientSideIllusionTicks;
    private final Vec3D[][] clientSideIllusionOffsets;

    public EntityIllagerIllusioner(EntityTypes<? extends EntityIllagerIllusioner> entitytypes, World world) {
        super(entitytypes, world);
        this.xpReward = 5;
        this.clientSideIllusionOffsets = new Vec3D[2][4];

        for (int i = 0; i < 4; ++i) {
            this.clientSideIllusionOffsets[0][i] = Vec3D.ZERO;
            this.clientSideIllusionOffsets[1][i] = Vec3D.ZERO;
        }

    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new PathfinderGoalFloat(this));
        this.goalSelector.addGoal(1, new EntityIllagerWizard.b());
        this.goalSelector.addGoal(4, new EntityIllagerIllusioner.b());
        this.goalSelector.addGoal(5, new EntityIllagerIllusioner.a());
        this.goalSelector.addGoal(6, new PathfinderGoalBowShoot<>(this, 0.5D, 20, 15.0F));
        this.goalSelector.addGoal(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.addGoal(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.addGoal(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).setAlertOthers());
        this.targetSelector.addGoal(2, (new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false)).setUnseenMemoryTicks(300));
        this.targetSelector.addGoal(3, (new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, false)).setUnseenMemoryTicks(300));
    }

    public static AttributeProvider.Builder createAttributes() {
        return EntityMonster.createMonsterAttributes().add(GenericAttributes.MOVEMENT_SPEED, 0.5D).add(GenericAttributes.FOLLOW_RANGE, 18.0D).add(GenericAttributes.MAX_HEALTH, 32.0D);
    }

    @Override
    public GroupDataEntity finalizeSpawn(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setItemSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.BOW));
        return super.finalizeSpawn(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public AxisAlignedBB getBoundingBoxForCulling() {
        return this.getBoundingBox().inflate(3.0D, 0.0D, 3.0D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level.isClientSide && this.isInvisible()) {
            --this.clientSideIllusionTicks;
            if (this.clientSideIllusionTicks < 0) {
                this.clientSideIllusionTicks = 0;
            }

            if (this.hurtTime != 1 && this.tickCount % 1200 != 0) {
                if (this.hurtTime == this.hurtDuration - 1) {
                    this.clientSideIllusionTicks = 3;

                    for (int i = 0; i < 4; ++i) {
                        this.clientSideIllusionOffsets[0][i] = this.clientSideIllusionOffsets[1][i];
                        this.clientSideIllusionOffsets[1][i] = new Vec3D(0.0D, 0.0D, 0.0D);
                    }
                }
            } else {
                this.clientSideIllusionTicks = 3;
                float f = -6.0F;
                boolean flag = true;

                int j;

                for (j = 0; j < 4; ++j) {
                    this.clientSideIllusionOffsets[0][j] = this.clientSideIllusionOffsets[1][j];
                    this.clientSideIllusionOffsets[1][j] = new Vec3D((double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D, (double) Math.max(0, this.random.nextInt(6) - 4), (double) (-6.0F + (float) this.random.nextInt(13)) * 0.5D);
                }

                for (j = 0; j < 16; ++j) {
                    this.level.addParticle(Particles.CLOUD, this.getRandomX(0.5D), this.getRandomY(), this.getZ(0.5D), 0.0D, 0.0D, 0.0D);
                }

                this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEffects.ILLUSIONER_MIRROR_MOVE, this.getSoundSource(), 1.0F, 1.0F, false);
            }
        }

    }

    @Override
    public SoundEffect getCelebrateSound() {
        return SoundEffects.ILLUSIONER_AMBIENT;
    }

    public Vec3D[] getIllusionOffsets(float f) {
        if (this.clientSideIllusionTicks <= 0) {
            return this.clientSideIllusionOffsets[1];
        } else {
            double d0 = (double) (((float) this.clientSideIllusionTicks - f) / 3.0F);

            d0 = Math.pow(d0, 0.25D);
            Vec3D[] avec3d = new Vec3D[4];

            for (int i = 0; i < 4; ++i) {
                avec3d[i] = this.clientSideIllusionOffsets[1][i].scale(1.0D - d0).add(this.clientSideIllusionOffsets[0][i].scale(d0));
            }

            return avec3d;
        }
    }

    @Override
    public boolean isAlliedTo(Entity entity) {
        return super.isAlliedTo(entity) ? true : (entity instanceof EntityLiving && ((EntityLiving) entity).getMobType() == EnumMonsterType.ILLAGER ? this.getTeam() == null && entity.getTeam() == null : false);
    }

    @Override
    protected SoundEffect getAmbientSound() {
        return SoundEffects.ILLUSIONER_AMBIENT;
    }

    @Override
    protected SoundEffect getDeathSound() {
        return SoundEffects.ILLUSIONER_DEATH;
    }

    @Override
    protected SoundEffect getHurtSound(DamageSource damagesource) {
        return SoundEffects.ILLUSIONER_HURT;
    }

    @Override
    protected SoundEffect getCastingSoundEvent() {
        return SoundEffects.ILLUSIONER_CAST_SPELL;
    }

    @Override
    public void applyRaidBuffs(int i, boolean flag) {}

    @Override
    public void performRangedAttack(EntityLiving entityliving, float f) {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileHelper.getWeaponHoldingHand(this, Items.BOW)));
        EntityArrow entityarrow = ProjectileHelper.getMobArrow(this, itemstack, f);
        double d0 = entityliving.getX() - this.getX();
        double d1 = entityliving.getY(0.3333333333333333D) - entityarrow.getY();
        double d2 = entityliving.getZ() - this.getZ();
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);

        entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEffects.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(entityarrow);
    }

    @Override
    public EntityIllagerAbstract.a getArmPose() {
        return this.isCastingSpell() ? EntityIllagerAbstract.a.SPELLCASTING : (this.isAggressive() ? EntityIllagerAbstract.a.BOW_AND_ARROW : EntityIllagerAbstract.a.CROSSED);
    }

    private class b extends EntityIllagerWizard.PathfinderGoalCastSpell {

        b() {
            super();
        }

        @Override
        public boolean canUse() {
            return !super.canUse() ? false : !EntityIllagerIllusioner.this.hasEffect(MobEffects.INVISIBILITY);
        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        @Override
        protected int getCastingInterval() {
            return 340;
        }

        @Override
        protected void performSpellCasting() {
            EntityIllagerIllusioner.this.addEffect(new MobEffect(MobEffects.INVISIBILITY, 1200));
        }

        @Nullable
        @Override
        protected SoundEffect getSpellPrepareSound() {
            return SoundEffects.ILLUSIONER_PREPARE_MIRROR;
        }

        @Override
        protected EntityIllagerWizard.Spell getSpell() {
            return EntityIllagerWizard.Spell.DISAPPEAR;
        }
    }

    private class a extends EntityIllagerWizard.PathfinderGoalCastSpell {

        private int lastTargetId;

        a() {
            super();
        }

        @Override
        public boolean canUse() {
            return !super.canUse() ? false : (EntityIllagerIllusioner.this.getTarget() == null ? false : (EntityIllagerIllusioner.this.getTarget().getId() == this.lastTargetId ? false : EntityIllagerIllusioner.this.level.getCurrentDifficultyAt(EntityIllagerIllusioner.this.blockPosition()).isHarderThan((float) EnumDifficulty.NORMAL.ordinal())));
        }

        @Override
        public void start() {
            super.start();
            EntityLiving entityliving = EntityIllagerIllusioner.this.getTarget();

            if (entityliving != null) {
                this.lastTargetId = entityliving.getId();
            }

        }

        @Override
        protected int getCastingTime() {
            return 20;
        }

        @Override
        protected int getCastingInterval() {
            return 180;
        }

        @Override
        protected void performSpellCasting() {
            EntityIllagerIllusioner.this.getTarget().addEffect(new MobEffect(MobEffects.BLINDNESS, 400), EntityIllagerIllusioner.this);
        }

        @Override
        protected SoundEffect getSpellPrepareSound() {
            return SoundEffects.ILLUSIONER_PREPARE_BLINDNESS;
        }

        @Override
        protected EntityIllagerWizard.Spell getSpell() {
            return EntityIllagerWizard.Spell.BLINDNESS;
        }
    }
}
