package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import net.minecraft.core.particles.Particles;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomLookaround;
import net.minecraft.world.entity.ai.goal.PathfinderGoalRandomStrollLand;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntitySmallFireball;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3D;

public class EntityBlaze extends EntityMonster {

    private float b = 0.5F;
    private int c;
    private static final DataWatcherObject<Byte> d = DataWatcher.a(EntityBlaze.class, DataWatcherRegistry.a);

    public EntityBlaze(EntityTypes<? extends EntityBlaze> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.WATER, -1.0F);
        this.a(PathType.LAVA, 8.0F);
        this.a(PathType.DANGER_FIRE, 0.0F);
        this.a(PathType.DAMAGE_FIRE, 0.0F);
        this.f = 10;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(4, new EntityBlaze.PathfinderGoalBlazeFireball(this));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[0])).a());
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    public static AttributeProvider.Builder m() {
        return EntityMonster.eR().a(GenericAttributes.ATTACK_DAMAGE, 6.0D).a(GenericAttributes.MOVEMENT_SPEED, 0.23000000417232513D).a(GenericAttributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityBlaze.d, (byte) 0);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_BLAZE_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_BLAZE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_BLAZE_DEATH;
    }

    @Override
    public float aR() {
        return 1.0F;
    }

    @Override
    public void movementTick() {
        if (!this.onGround && this.getMot().y < 0.0D) {
            this.setMot(this.getMot().d(1.0D, 0.6D, 1.0D));
        }

        if (this.world.isClientSide) {
            if (this.random.nextInt(24) == 0 && !this.isSilent()) {
                this.world.a(this.locX() + 0.5D, this.locY() + 0.5D, this.locZ() + 0.5D, SoundEffects.ENTITY_BLAZE_BURN, this.getSoundCategory(), 1.0F + this.random.nextFloat(), this.random.nextFloat() * 0.7F + 0.3F, false);
            }

            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(Particles.LARGE_SMOKE, this.d(0.5D), this.cF(), this.g(0.5D), 0.0D, 0.0D, 0.0D);
            }
        }

        super.movementTick();
    }

    @Override
    public boolean dO() {
        return true;
    }

    @Override
    protected void mobTick() {
        --this.c;
        if (this.c <= 0) {
            this.c = 100;
            this.b = 0.5F + (float) this.random.nextGaussian() * 3.0F;
        }

        EntityLiving entityliving = this.getGoalTarget();

        if (entityliving != null && entityliving.getHeadY() > this.getHeadY() + (double) this.b && this.c(entityliving)) {
            Vec3D vec3d = this.getMot();

            this.setMot(this.getMot().add(0.0D, (0.30000001192092896D - vec3d.y) * 0.30000001192092896D, 0.0D));
            this.impulse = true;
        }

        super.mobTick();
    }

    @Override
    public boolean b(float f, float f1) {
        return false;
    }

    @Override
    public boolean isBurning() {
        return this.eK();
    }

    private boolean eK() {
        return ((Byte) this.datawatcher.get(EntityBlaze.d) & 1) != 0;
    }

    private void t(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityBlaze.d);

        if (flag) {
            b0 = (byte) (b0 | 1);
        } else {
            b0 &= -2;
        }

        this.datawatcher.set(EntityBlaze.d, b0);
    }

    static class PathfinderGoalBlazeFireball extends PathfinderGoal {

        private final EntityBlaze a;
        private int b;
        private int c;
        private int d;

        public PathfinderGoalBlazeFireball(EntityBlaze entityblaze) {
            this.a = entityblaze;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            return entityliving != null && entityliving.isAlive() && this.a.c(entityliving);
        }

        @Override
        public void c() {
            this.b = 0;
        }

        @Override
        public void d() {
            this.a.t(false);
            this.d = 0;
        }

        @Override
        public void e() {
            --this.c;
            EntityLiving entityliving = this.a.getGoalTarget();

            if (entityliving != null) {
                boolean flag = this.a.getEntitySenses().a(entityliving);

                if (flag) {
                    this.d = 0;
                } else {
                    ++this.d;
                }

                double d0 = this.a.h((Entity) entityliving);

                if (d0 < 4.0D) {
                    if (!flag) {
                        return;
                    }

                    if (this.c <= 0) {
                        this.c = 20;
                        this.a.attackEntity(entityliving);
                    }

                    this.a.getControllerMove().a(entityliving.locX(), entityliving.locY(), entityliving.locZ(), 1.0D);
                } else if (d0 < this.g() * this.g() && flag) {
                    double d1 = entityliving.locX() - this.a.locX();
                    double d2 = entityliving.e(0.5D) - this.a.e(0.5D);
                    double d3 = entityliving.locZ() - this.a.locZ();

                    if (this.c <= 0) {
                        ++this.b;
                        if (this.b == 1) {
                            this.c = 60;
                            this.a.t(true);
                        } else if (this.b <= 4) {
                            this.c = 6;
                        } else {
                            this.c = 100;
                            this.b = 0;
                            this.a.t(false);
                        }

                        if (this.b > 1) {
                            float f = MathHelper.c(MathHelper.sqrt(d0)) * 0.5F;

                            if (!this.a.isSilent()) {
                                this.a.world.a((EntityHuman) null, 1018, this.a.getChunkCoordinates(), 0);
                            }

                            for (int i = 0; i < 1; ++i) {
                                EntitySmallFireball entitysmallfireball = new EntitySmallFireball(this.a.world, this.a, d1 + this.a.getRandom().nextGaussian() * (double) f, d2, d3 + this.a.getRandom().nextGaussian() * (double) f);

                                entitysmallfireball.setPosition(entitysmallfireball.locX(), this.a.e(0.5D) + 0.5D, entitysmallfireball.locZ());
                                this.a.world.addEntity(entitysmallfireball);
                            }
                        }
                    }

                    this.a.getControllerLook().a(entityliving, 10.0F, 10.0F);
                } else if (this.d < 5) {
                    this.a.getControllerMove().a(entityliving.locX(), entityliving.locY(), entityliving.locZ(), 1.0D);
                }

                super.e();
            }
        }

        private double g() {
            return this.a.b(GenericAttributes.FOLLOW_RANGE);
        }
    }
}
