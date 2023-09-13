package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.ParticleParam;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.GeneratorAccessSeed;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.SeededRandom;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.phys.Vec3D;

public class EntitySlime extends EntityInsentient implements IMonster {

    private static final DataWatcherObject<Integer> ID_SIZE = DataWatcher.a(EntitySlime.class, DataWatcherRegistry.INT);
    public static final int MIN_SIZE = 1;
    public static final int MAX_SIZE = 127;
    public float targetSquish;
    public float squish;
    public float oSquish;
    private boolean wasOnGround;

    public EntitySlime(EntityTypes<? extends EntitySlime> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new EntitySlime.ControllerMoveSlime(this);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new EntitySlime.PathfinderGoalSlimeRandomJump(this));
        this.goalSelector.a(2, new EntitySlime.PathfinderGoalSlimeNearestPlayer(this));
        this.goalSelector.a(3, new EntitySlime.PathfinderGoalSlimeRandomDirection(this));
        this.goalSelector.a(5, new EntitySlime.PathfinderGoalSlimeIdle(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, 10, true, false, (entityliving) -> {
            return Math.abs(entityliving.locY() - this.locY()) <= 4.0D;
        }));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntitySlime.ID_SIZE, 1);
    }

    public void setSize(int i, boolean flag) {
        int j = MathHelper.clamp(i, 1, 127);

        this.entityData.set(EntitySlime.ID_SIZE, j);
        this.ah();
        this.updateSize();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue((double) (j * j));
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue((double) (0.2F + 0.1F * (float) j));
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double) j);
        if (flag) {
            this.setHealth(this.getMaxHealth());
        }

        this.xpReward = j;
    }

    public int getSize() {
        return (Integer) this.entityData.get(EntitySlime.ID_SIZE);
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("Size", this.getSize() - 1);
        nbttagcompound.setBoolean("wasOnGround", this.wasOnGround);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        this.setSize(nbttagcompound.getInt("Size") + 1, false);
        super.loadData(nbttagcompound);
        this.wasOnGround = nbttagcompound.getBoolean("wasOnGround");
    }

    public boolean fA() {
        return this.getSize() <= 1;
    }

    protected ParticleParam p() {
        return Particles.ITEM_SLIME;
    }

    @Override
    protected boolean Q() {
        return this.getSize() > 0;
    }

    @Override
    public void tick() {
        this.squish += (this.targetSquish - this.squish) * 0.5F;
        this.oSquish = this.squish;
        super.tick();
        if (this.onGround && !this.wasOnGround) {
            int i = this.getSize();

            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 6.2831855F;
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;

                this.level.addParticle(this.p(), this.locX() + (double) f2, this.locY(), this.locZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            this.playSound(this.getSoundSquish(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetSquish = -0.5F;
        } else if (!this.onGround && this.wasOnGround) {
            this.targetSquish = 1.0F;
        }

        this.wasOnGround = this.onGround;
        this.fu();
    }

    protected void fu() {
        this.targetSquish *= 0.6F;
    }

    protected int t() {
        return this.random.nextInt(20) + 10;
    }

    @Override
    public void updateSize() {
        double d0 = this.locX();
        double d1 = this.locY();
        double d2 = this.locZ();

        super.updateSize();
        this.setPosition(d0, d1, d2);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntitySlime.ID_SIZE.equals(datawatcherobject)) {
            this.updateSize();
            this.setYRot(this.yHeadRot);
            this.yBodyRot = this.yHeadRot;
            if (this.isInWater() && this.random.nextInt(20) == 0) {
                this.aT();
            }
        }

        super.a(datawatcherobject);
    }

    @Override
    public EntityTypes<? extends EntitySlime> getEntityType() {
        return super.getEntityType();
    }

    @Override
    public void a(Entity.RemovalReason entity_removalreason) {
        int i = this.getSize();

        if (!this.level.isClientSide && i > 1 && this.dV()) {
            IChatBaseComponent ichatbasecomponent = this.getCustomName();
            boolean flag = this.isNoAI();
            float f = (float) i / 4.0F;
            int j = i / 2;
            int k = 2 + this.random.nextInt(3);

            for (int l = 0; l < k; ++l) {
                float f1 = ((float) (l % 2) - 0.5F) * f;
                float f2 = ((float) (l / 2) - 0.5F) * f;
                EntitySlime entityslime = (EntitySlime) this.getEntityType().a(this.level);

                if (this.isPersistent()) {
                    entityslime.setPersistent();
                }

                entityslime.setCustomName(ichatbasecomponent);
                entityslime.setNoAI(flag);
                entityslime.setInvulnerable(this.isInvulnerable());
                entityslime.setSize(j, true);
                entityslime.setPositionRotation(this.locX() + (double) f1, this.locY() + 0.5D, this.locZ() + (double) f2, this.random.nextFloat() * 360.0F, 0.0F);
                this.level.addEntity(entityslime);
            }
        }

        super.a(entity_removalreason);
    }

    @Override
    public void collide(Entity entity) {
        super.collide(entity);
        if (entity instanceof EntityIronGolem && this.fv()) {
            this.j((EntityLiving) entity);
        }

    }

    @Override
    public void pickup(EntityHuman entityhuman) {
        if (this.fv()) {
            this.j((EntityLiving) entityhuman);
        }

    }

    protected void j(EntityLiving entityliving) {
        if (this.isAlive()) {
            int i = this.getSize();

            if (this.f((Entity) entityliving) < 0.6D * (double) i * 0.6D * (double) i && this.hasLineOfSight(entityliving) && entityliving.damageEntity(DamageSource.mobAttack(this), this.fw())) {
                this.playSound(SoundEffects.SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.a((EntityLiving) this, (Entity) entityliving);
            }
        }

    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.625F * entitysize.height;
    }

    protected boolean fv() {
        return !this.fA() && this.doAITick();
    }

    protected float fw() {
        return (float) this.b(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.fA() ? SoundEffects.SLIME_HURT_SMALL : SoundEffects.SLIME_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return this.fA() ? SoundEffects.SLIME_DEATH_SMALL : SoundEffects.SLIME_DEATH;
    }

    protected SoundEffect getSoundSquish() {
        return this.fA() ? SoundEffects.SLIME_SQUISH_SMALL : SoundEffects.SLIME_SQUISH;
    }

    @Override
    public MinecraftKey getDefaultLootTable() {
        return this.getSize() == 1 ? this.getEntityType().j() : LootTables.EMPTY;
    }

    public static boolean c(EntityTypes<EntitySlime> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        if (generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL) {
            if (Objects.equals(generatoraccess.j(blockposition), Optional.of(Biomes.SWAMP)) && blockposition.getY() > 50 && blockposition.getY() < 70 && random.nextFloat() < 0.5F && random.nextFloat() < generatoraccess.ak() && generatoraccess.getLightLevel(blockposition) <= random.nextInt(8)) {
                return a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
            }

            if (!(generatoraccess instanceof GeneratorAccessSeed)) {
                return false;
            }

            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition);
            boolean flag = SeededRandom.a(chunkcoordintpair.x, chunkcoordintpair.z, ((GeneratorAccessSeed) generatoraccess).getSeed(), 987234911L).nextInt(10) == 0;

            if (random.nextInt(10) == 0 && flag && blockposition.getY() < 40) {
                return a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
            }
        }

        return false;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F * (float) this.getSize();
    }

    @Override
    public int eZ() {
        return 0;
    }

    protected boolean fB() {
        return this.getSize() > 0;
    }

    @Override
    protected void jump() {
        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x, (double) this.er(), vec3d.z);
        this.hasImpulse = true;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        int i = this.random.nextInt(3);

        if (i < 2 && this.random.nextFloat() < 0.5F * difficultydamagescaler.d()) {
            ++i;
        }

        int j = 1 << i;

        this.setSize(j, true);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    float n() {
        float f = this.fA() ? 1.4F : 0.8F;

        return ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * f;
    }

    protected SoundEffect getSoundJump() {
        return this.fA() ? SoundEffects.SLIME_JUMP_SMALL : SoundEffects.SLIME_JUMP;
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return super.a(entitypose).a(0.255F * (float) this.getSize());
    }

    private static class ControllerMoveSlime extends ControllerMove {

        private float yRot;
        private int jumpDelay;
        private final EntitySlime slime;
        private boolean isAggressive;

        public ControllerMoveSlime(EntitySlime entityslime) {
            super(entityslime);
            this.slime = entityslime;
            this.yRot = 180.0F * entityslime.getYRot() / 3.1415927F;
        }

        public void a(float f, boolean flag) {
            this.yRot = f;
            this.isAggressive = flag;
        }

        public void a(double d0) {
            this.speedModifier = d0;
            this.operation = ControllerMove.Operation.MOVE_TO;
        }

        @Override
        public void a() {
            this.mob.setYRot(this.a(this.mob.getYRot(), this.yRot, 90.0F));
            this.mob.yHeadRot = this.mob.getYRot();
            this.mob.yBodyRot = this.mob.getYRot();
            if (this.operation != ControllerMove.Operation.MOVE_TO) {
                this.mob.u(0.0F);
            } else {
                this.operation = ControllerMove.Operation.WAIT;
                if (this.mob.isOnGround()) {
                    this.mob.r((float) (this.speedModifier * this.mob.b(GenericAttributes.MOVEMENT_SPEED)));
                    if (this.jumpDelay-- <= 0) {
                        this.jumpDelay = this.slime.t();
                        if (this.isAggressive) {
                            this.jumpDelay /= 3;
                        }

                        this.slime.getControllerJump().jump();
                        if (this.slime.fB()) {
                            this.slime.playSound(this.slime.getSoundJump(), this.slime.getSoundVolume(), this.slime.n());
                        }
                    } else {
                        this.slime.xxa = 0.0F;
                        this.slime.zza = 0.0F;
                        this.mob.r(0.0F);
                    }
                } else {
                    this.mob.r((float) (this.speedModifier * this.mob.b(GenericAttributes.MOVEMENT_SPEED)));
                }

            }
        }
    }

    private static class PathfinderGoalSlimeRandomJump extends PathfinderGoal {

        private final EntitySlime slime;

        public PathfinderGoalSlimeRandomJump(EntitySlime entityslime) {
            this.slime = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
            entityslime.getNavigation().d(true);
        }

        @Override
        public boolean a() {
            return (this.slime.isInWater() || this.slime.aX()) && this.slime.getControllerMove() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public void e() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getControllerJump().jump();
            }

            ((EntitySlime.ControllerMoveSlime) this.slime.getControllerMove()).a(1.2D);
        }
    }

    private static class PathfinderGoalSlimeNearestPlayer extends PathfinderGoal {

        private final EntitySlime slime;
        private int growTiredTimer;

        public PathfinderGoalSlimeNearestPlayer(EntitySlime entityslime) {
            this.slime = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.slime.getGoalTarget();

            return entityliving == null ? false : (!this.slime.c(entityliving) ? false : this.slime.getControllerMove() instanceof EntitySlime.ControllerMoveSlime);
        }

        @Override
        public void c() {
            this.growTiredTimer = 300;
            super.c();
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = this.slime.getGoalTarget();

            return entityliving == null ? false : (!this.slime.c(entityliving) ? false : --this.growTiredTimer > 0);
        }

        @Override
        public void e() {
            this.slime.a((Entity) this.slime.getGoalTarget(), 10.0F, 10.0F);
            ((EntitySlime.ControllerMoveSlime) this.slime.getControllerMove()).a(this.slime.getYRot(), this.slime.fv());
        }
    }

    private static class PathfinderGoalSlimeRandomDirection extends PathfinderGoal {

        private final EntitySlime slime;
        private float chosenDegrees;
        private int nextRandomizeTime;

        public PathfinderGoalSlimeRandomDirection(EntitySlime entityslime) {
            this.slime = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            return this.slime.getGoalTarget() == null && (this.slime.onGround || this.slime.isInWater() || this.slime.aX() || this.slime.hasEffect(MobEffects.LEVITATION)) && this.slime.getControllerMove() instanceof EntitySlime.ControllerMoveSlime;
        }

        @Override
        public void e() {
            if (--this.nextRandomizeTime <= 0) {
                this.nextRandomizeTime = 40 + this.slime.getRandom().nextInt(60);
                this.chosenDegrees = (float) this.slime.getRandom().nextInt(360);
            }

            ((EntitySlime.ControllerMoveSlime) this.slime.getControllerMove()).a(this.chosenDegrees, false);
        }
    }

    private static class PathfinderGoalSlimeIdle extends PathfinderGoal {

        private final EntitySlime slime;

        public PathfinderGoalSlimeIdle(EntitySlime entityslime) {
            this.slime = entityslime;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return !this.slime.isPassenger();
        }

        @Override
        public void e() {
            ((EntitySlime.ControllerMoveSlime) this.slime.getControllerMove()).a(1.0D);
        }
    }
}
