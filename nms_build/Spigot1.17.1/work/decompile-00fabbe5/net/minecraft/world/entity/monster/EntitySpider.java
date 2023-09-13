package net.minecraft.world.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectList;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLeapAtTarget;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMeleeAttack;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.ai.navigation.NavigationSpider;
import net.minecraft.world.entity.animal.EntityIronGolem;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.Vec3D;

public class EntitySpider extends EntityMonster {

    private static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.a(EntitySpider.class, DataWatcherRegistry.BYTE);
    private static final float SPIDER_SPECIAL_EFFECT_CHANCE = 0.1F;

    public EntitySpider(EntityTypes<? extends EntitySpider> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, new PathfinderGoalLeapAtTarget(this, 0.4F));
        this.goalSelector.a(4, new EntitySpider.PathfinderGoalSpiderMeleeAttack(this));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 0.8D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(2, new EntitySpider.PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityHuman.class));
        this.targetSelector.a(3, new EntitySpider.PathfinderGoalSpiderNearestAttackableTarget<>(this, EntityIronGolem.class));
    }

    @Override
    public double bl() {
        return (double) (this.getHeight() * 0.5F);
    }

    @Override
    protected NavigationAbstract a(World world) {
        return new NavigationSpider(this, world);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntitySpider.DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.v(this.horizontalCollision);
        }

    }

    public static AttributeProvider.Builder p() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 16.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.30000001192092896D);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.SPIDER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.SPIDER_DEATH;
    }

    @Override
    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.playSound(SoundEffects.SPIDER_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean isClimbing() {
        return this.t();
    }

    @Override
    public void a(IBlockData iblockdata, Vec3D vec3d) {
        if (!iblockdata.a(Blocks.COBWEB)) {
            super.a(iblockdata, vec3d);
        }

    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    @Override
    public boolean c(MobEffect mobeffect) {
        return mobeffect.getMobEffect() == MobEffects.POISON ? false : super.c(mobeffect);
    }

    public boolean t() {
        return ((Byte) this.entityData.get(EntitySpider.DATA_FLAGS_ID) & 1) != 0;
    }

    public void v(boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntitySpider.DATA_FLAGS_ID);

        if (flag) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 &= -2;
        }

        this.entityData.set(EntitySpider.DATA_FLAGS_ID, b0);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);

        if (worldaccess.getRandom().nextInt(100) == 0) {
            EntitySkeleton entityskeleton = (EntitySkeleton) EntityTypes.SKELETON.a(this.level);

            entityskeleton.setPositionRotation(this.locX(), this.locY(), this.locZ(), this.getYRot(), 0.0F);
            entityskeleton.prepare(worldaccess, difficultydamagescaler, enummobspawn, (GroupDataEntity) null, (NBTTagCompound) null);
            entityskeleton.startRiding(this);
        }

        if (object == null) {
            object = new EntitySpider.GroupDataSpider();
            if (worldaccess.getDifficulty() == EnumDifficulty.HARD && worldaccess.getRandom().nextFloat() < 0.1F * difficultydamagescaler.d()) {
                ((EntitySpider.GroupDataSpider) object).a(worldaccess.getRandom());
            }
        }

        if (object instanceof EntitySpider.GroupDataSpider) {
            MobEffectList mobeffectlist = ((EntitySpider.GroupDataSpider) object).effect;

            if (mobeffectlist != null) {
                this.addEffect(new MobEffect(mobeffectlist, Integer.MAX_VALUE));
            }
        }

        return (GroupDataEntity) object;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.65F;
    }

    private static class PathfinderGoalSpiderMeleeAttack extends PathfinderGoalMeleeAttack {

        public PathfinderGoalSpiderMeleeAttack(EntitySpider entityspider) {
            super(entityspider, 1.0D, true);
        }

        @Override
        public boolean a() {
            return super.a() && !this.mob.isVehicle();
        }

        @Override
        public boolean b() {
            float f = this.mob.aY();

            if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
                this.mob.setGoalTarget((EntityLiving) null);
                return false;
            } else {
                return super.b();
            }
        }

        @Override
        protected double a(EntityLiving entityliving) {
            return (double) (4.0F + entityliving.getWidth());
        }
    }

    private static class PathfinderGoalSpiderNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalNearestAttackableTarget<T> {

        public PathfinderGoalSpiderNearestAttackableTarget(EntitySpider entityspider, Class<T> oclass) {
            super(entityspider, oclass, true);
        }

        @Override
        public boolean a() {
            float f = this.mob.aY();

            return f >= 0.5F ? false : super.a();
        }
    }

    public static class GroupDataSpider implements GroupDataEntity {

        public MobEffectList effect;

        public GroupDataSpider() {}

        public void a(Random random) {
            int i = random.nextInt(5);

            if (i <= 1) {
                this.effect = MobEffects.MOVEMENT_SPEED;
            } else if (i <= 2) {
                this.effect = MobEffects.DAMAGE_BOOST;
            } else if (i <= 3) {
                this.effect = MobEffects.REGENERATION;
            } else if (i <= 4) {
                this.effect = MobEffects.INVISIBILITY;
            }

        }
    }
}
