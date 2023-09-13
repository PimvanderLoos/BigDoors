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
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.PowerableMob;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalArrowAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
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

    private static final DataWatcherObject<Integer> DATA_TARGET_A = DataWatcher.a(EntityWither.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_TARGET_B = DataWatcher.a(EntityWither.class, DataWatcherRegistry.INT);
    private static final DataWatcherObject<Integer> DATA_TARGET_C = DataWatcher.a(EntityWither.class, DataWatcherRegistry.INT);
    private static final List<DataWatcherObject<Integer>> DATA_TARGETS = ImmutableList.of(EntityWither.DATA_TARGET_A, EntityWither.DATA_TARGET_B, EntityWither.DATA_TARGET_C);
    private static final DataWatcherObject<Integer> DATA_ID_INV = DataWatcher.a(EntityWither.class, DataWatcherRegistry.INT);
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
        return entityliving.getMonsterType() != EnumMonsterType.UNDEAD && entityliving.eR();
    };
    private static final PathfinderTargetCondition TARGETING_CONDITIONS = PathfinderTargetCondition.a().a(20.0D).a(EntityWither.LIVING_ENTITY_SELECTOR);

    public EntityWither(EntityTypes<? extends EntityWither> entitytypes, World world) {
        super(entitytypes, world);
        this.bossEvent = (BossBattleServer) (new BossBattleServer(this.getScoreboardDisplayName(), BossBattle.BarColor.PURPLE, BossBattle.BarStyle.PROGRESS)).setDarkenSky(true);
        this.setHealth(this.getMaxHealth());
        this.getNavigation().d(true);
        this.xpReward = 50;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new EntityWither.a());
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 40, 20.0F));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 0, false, false, EntityWither.LIVING_ENTITY_SELECTOR));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityWither.DATA_TARGET_A, 0);
        this.entityData.register(EntityWither.DATA_TARGET_B, 0);
        this.entityData.register(EntityWither.DATA_TARGET_C, 0);
        this.entityData.register(EntityWither.DATA_ID_INV, 0);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Invul", this.getInvul());
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        this.setInvul(nbttagcompound.getInt("Invul"));
        if (this.hasCustomName()) {
            this.bossEvent.a(this.getScoreboardDisplayName());
        }

    }

    @Override
    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        super.setCustomName(ichatbasecomponent);
        this.bossEvent.a(this.getScoreboardDisplayName());
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.WITHER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.WITHER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.WITHER_DEATH;
    }

    @Override
    public void movementTick() {
        Vec3D vec3d = this.getMot().d(1.0D, 0.6D, 1.0D);

        if (!this.level.isClientSide && this.getHeadTarget(0) > 0) {
            Entity entity = this.level.getEntity(this.getHeadTarget(0));

            if (entity != null) {
                double d0 = vec3d.y;

                if (this.locY() < entity.locY() || !this.isPowered() && this.locY() < entity.locY() + 5.0D) {
                    d0 = Math.max(0.0D, d0);
                    d0 += 0.3D - d0 * 0.6000000238418579D;
                }

                vec3d = new Vec3D(vec3d.x, d0, vec3d.z);
                Vec3D vec3d1 = new Vec3D(entity.locX() - this.locX(), 0.0D, entity.locZ() - this.locZ());

                if (vec3d1.i() > 9.0D) {
                    Vec3D vec3d2 = vec3d1.d();

                    vec3d = vec3d.add(vec3d2.x * 0.3D - vec3d.x * 0.6D, 0.0D, vec3d2.z * 0.3D - vec3d.z * 0.6D);
                }
            }
        }

        this.setMot(vec3d);
        if (vec3d.i() > 0.05D) {
            this.setYRot((float) MathHelper.d(vec3d.z, vec3d.x) * 57.295776F - 90.0F);
        }

        super.movementTick();

        int i;

        for (i = 0; i < 2; ++i) {
            this.yRotOHeads[i] = this.yRotHeads[i];
            this.xRotOHeads[i] = this.xRotHeads[i];
        }

        int j;

        for (i = 0; i < 2; ++i) {
            j = this.getHeadTarget(i + 1);
            Entity entity1 = null;

            if (j > 0) {
                entity1 = this.level.getEntity(j);
            }

            if (entity1 != null) {
                double d1 = this.v(i + 1);
                double d2 = this.w(i + 1);
                double d3 = this.x(i + 1);
                double d4 = entity1.locX() - d1;
                double d5 = entity1.getHeadY() - d2;
                double d6 = entity1.locZ() - d3;
                double d7 = Math.sqrt(d4 * d4 + d6 * d6);
                float f = (float) (MathHelper.d(d6, d4) * 57.2957763671875D) - 90.0F;
                float f1 = (float) (-(MathHelper.d(d5, d7) * 57.2957763671875D));

                this.xRotHeads[i] = this.a(this.xRotHeads[i], f1, 40.0F);
                this.yRotHeads[i] = this.a(this.yRotHeads[i], f, 10.0F);
            } else {
                this.yRotHeads[i] = this.a(this.yRotHeads[i], this.yBodyRot, 10.0F);
            }
        }

        boolean flag = this.isPowered();

        for (j = 0; j < 3; ++j) {
            double d8 = this.v(j);
            double d9 = this.w(j);
            double d10 = this.x(j);

            this.level.addParticle(Particles.SMOKE, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.0D, 0.0D, 0.0D);
            if (flag && this.level.random.nextInt(4) == 0) {
                this.level.addParticle(Particles.ENTITY_EFFECT, d8 + this.random.nextGaussian() * 0.30000001192092896D, d9 + this.random.nextGaussian() * 0.30000001192092896D, d10 + this.random.nextGaussian() * 0.30000001192092896D, 0.699999988079071D, 0.699999988079071D, 0.5D);
            }
        }

        if (this.getInvul() > 0) {
            for (j = 0; j < 3; ++j) {
                this.level.addParticle(Particles.ENTITY_EFFECT, this.locX() + this.random.nextGaussian(), this.locY() + (double) (this.random.nextFloat() * 3.3F), this.locZ() + this.random.nextGaussian(), 0.699999988079071D, 0.699999988079071D, 0.8999999761581421D);
            }
        }

    }

    @Override
    protected void mobTick() {
        int i;

        if (this.getInvul() > 0) {
            i = this.getInvul() - 1;
            this.bossEvent.setProgress(1.0F - (float) i / 220.0F);
            if (i <= 0) {
                Explosion.Effect explosion_effect = this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? Explosion.Effect.DESTROY : Explosion.Effect.NONE;

                this.level.createExplosion(this, this.locX(), this.getHeadY(), this.locZ(), 7.0F, false, explosion_effect);
                if (!this.isSilent()) {
                    this.level.b(1023, this.getChunkCoordinates(), 0);
                }
            }

            this.setInvul(i);
            if (this.tickCount % 10 == 0) {
                this.heal(10.0F);
            }

        } else {
            super.mobTick();

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
                            double d0 = MathHelper.a(this.random, this.locX() - 10.0D, this.locX() + 10.0D);
                            double d1 = MathHelper.a(this.random, this.locY() - 5.0D, this.locY() + 5.0D);
                            double d2 = MathHelper.a(this.random, this.locZ() - 10.0D, this.locZ() + 10.0D);

                            this.a(i + 1, d0, d1, d2, true);
                            this.idleHeadUpdates[i - 1] = 0;
                        }
                    }

                    j = this.getHeadTarget(i);
                    if (j > 0) {
                        EntityLiving entityliving = (EntityLiving) this.level.getEntity(j);

                        if (entityliving != null && this.c(entityliving) && this.f((Entity) entityliving) <= 900.0D && this.hasLineOfSight(entityliving)) {
                            this.a(i + 1, entityliving);
                            this.nextHeadUpdate[i - 1] = this.tickCount + 40 + this.random.nextInt(20);
                            this.idleHeadUpdates[i - 1] = 0;
                        } else {
                            this.setHeadTarget(i, 0);
                        }
                    } else {
                        List<EntityLiving> list = this.level.a(EntityLiving.class, EntityWither.TARGETING_CONDITIONS, (EntityLiving) this, this.getBoundingBox().grow(20.0D, 8.0D, 20.0D));

                        if (!list.isEmpty()) {
                            EntityLiving entityliving1 = (EntityLiving) list.get(this.random.nextInt(list.size()));

                            this.setHeadTarget(i, entityliving1.getId());
                        }
                    }
                }
            }

            if (this.getGoalTarget() != null) {
                this.setHeadTarget(0, this.getGoalTarget().getId());
            } else {
                this.setHeadTarget(0, 0);
            }

            if (this.destroyBlocksTick > 0) {
                --this.destroyBlocksTick;
                if (this.destroyBlocksTick == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
                    i = MathHelper.floor(this.locY());
                    j = MathHelper.floor(this.locX());
                    int i1 = MathHelper.floor(this.locZ());
                    boolean flag = false;

                    for (int j1 = -1; j1 <= 1; ++j1) {
                        for (int k1 = -1; k1 <= 1; ++k1) {
                            for (int l1 = 0; l1 <= 3; ++l1) {
                                int i2 = j + j1;
                                int j2 = i + l1;
                                int k2 = i1 + k1;
                                BlockPosition blockposition = new BlockPosition(i2, j2, k2);
                                IBlockData iblockdata = this.level.getType(blockposition);

                                if (c(iblockdata)) {
                                    flag = this.level.a(blockposition, true, this) || flag;
                                }
                            }
                        }
                    }

                    if (flag) {
                        this.level.a((EntityHuman) null, 1022, this.getChunkCoordinates(), 0);
                    }
                }
            }

            if (this.tickCount % 20 == 0) {
                this.heal(1.0F);
            }

            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    public static boolean c(IBlockData iblockdata) {
        return !iblockdata.isAir() && !iblockdata.a((Tag) TagsBlock.WITHER_IMMUNE);
    }

    public void beginSpawnSequence() {
        this.setInvul(220);
        this.bossEvent.setProgress(0.0F);
        this.setHealth(this.getMaxHealth() / 3.0F);
    }

    @Override
    public void a(IBlockData iblockdata, Vec3D vec3d) {}

    @Override
    public void c(EntityPlayer entityplayer) {
        super.c(entityplayer);
        this.bossEvent.addPlayer(entityplayer);
    }

    @Override
    public void d(EntityPlayer entityplayer) {
        super.d(entityplayer);
        this.bossEvent.removePlayer(entityplayer);
    }

    private double v(int i) {
        if (i <= 0) {
            return this.locX();
        } else {
            float f = (this.yBodyRot + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.cos(f);

            return this.locX() + (double) f1 * 1.3D;
        }
    }

    private double w(int i) {
        return i <= 0 ? this.locY() + 3.0D : this.locY() + 2.2D;
    }

    private double x(int i) {
        if (i <= 0) {
            return this.locZ();
        } else {
            float f = (this.yBodyRot + (float) (180 * (i - 1))) * 0.017453292F;
            float f1 = MathHelper.sin(f);

            return this.locZ() + (double) f1 * 1.3D;
        }
    }

    private float a(float f, float f1, float f2) {
        float f3 = MathHelper.g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    private void a(int i, EntityLiving entityliving) {
        this.a(i, entityliving.locX(), entityliving.locY() + (double) entityliving.getHeadHeight() * 0.5D, entityliving.locZ(), i == 0 && this.random.nextFloat() < 0.001F);
    }

    private void a(int i, double d0, double d1, double d2, boolean flag) {
        if (!this.isSilent()) {
            this.level.a((EntityHuman) null, 1024, this.getChunkCoordinates(), 0);
        }

        double d3 = this.v(i);
        double d4 = this.w(i);
        double d5 = this.x(i);
        double d6 = d0 - d3;
        double d7 = d1 - d4;
        double d8 = d2 - d5;
        EntityWitherSkull entitywitherskull = new EntityWitherSkull(this.level, this, d6, d7, d8);

        entitywitherskull.setShooter(this);
        if (flag) {
            entitywitherskull.setCharged(true);
        }

        entitywitherskull.setPositionRaw(d3, d4, d5);
        this.level.addEntity(entitywitherskull);
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        this.a(0, entityliving);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource != DamageSource.DROWN && !(damagesource.getEntity() instanceof EntityWither)) {
            if (this.getInvul() > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                Entity entity;

                if (this.isPowered()) {
                    entity = damagesource.k();
                    if (entity instanceof EntityArrow) {
                        return false;
                    }
                }

                entity = damagesource.getEntity();
                if (entity != null && !(entity instanceof EntityHuman) && entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == this.getMonsterType()) {
                    return false;
                } else {
                    if (this.destroyBlocksTick <= 0) {
                        this.destroyBlocksTick = 20;
                    }

                    for (int i = 0; i < this.idleHeadUpdates.length; ++i) {
                        this.idleHeadUpdates[i] += 3;
                    }

                    return super.damageEntity(damagesource, f);
                }
            }
        } else {
            return false;
        }
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        EntityItem entityitem = this.a((IMaterial) Items.NETHER_STAR);

        if (entityitem != null) {
            entityitem.s();
        }

    }

    @Override
    public void checkDespawn() {
        if (this.level.getDifficulty() == EnumDifficulty.PEACEFUL && this.Q()) {
            this.die();
        } else {
            this.noActionTime = 0;
        }
    }

    @Override
    public boolean a(float f, float f1, DamageSource damagesource) {
        return false;
    }

    @Override
    public boolean addEffect(MobEffect mobeffect, @Nullable Entity entity) {
        return false;
    }

    public static AttributeProvider.Builder p() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 300.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.6000000238418579D).a(GenericAttributes.FOLLOW_RANGE, 40.0D).a(GenericAttributes.ARMOR, 4.0D);
    }

    public float a(int i) {
        return this.yRotHeads[i];
    }

    public float b(int i) {
        return this.xRotHeads[i];
    }

    public int getInvul() {
        return (Integer) this.entityData.get(EntityWither.DATA_ID_INV);
    }

    public void setInvul(int i) {
        this.entityData.set(EntityWither.DATA_ID_INV, i);
    }

    public int getHeadTarget(int i) {
        return (Integer) this.entityData.get((DataWatcherObject) EntityWither.DATA_TARGETS.get(i));
    }

    public void setHeadTarget(int i, int j) {
        this.entityData.set((DataWatcherObject) EntityWither.DATA_TARGETS.get(i), j);
    }

    @Override
    public boolean isPowered() {
        return this.getHealth() <= this.getMaxHealth() / 2.0F;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected boolean l(Entity entity) {
        return false;
    }

    @Override
    public boolean canPortal() {
        return false;
    }

    @Override
    public boolean c(MobEffect mobeffect) {
        return mobeffect.getMobEffect() == MobEffects.WITHER ? false : super.c(mobeffect);
    }

    private class a extends PathfinderGoal {

        public a() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.JUMP, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return EntityWither.this.getInvul() > 0;
        }
    }
}
