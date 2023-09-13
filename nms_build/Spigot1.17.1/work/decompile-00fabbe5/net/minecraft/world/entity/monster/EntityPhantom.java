package net.minecraft.world.entity.monster;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityFlying;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityPose;
import net.minecraft.world.entity.EntitySize;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMonsterType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerLook;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.control.EntityAIBodyControl;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.animal.EntityCat;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;

public class EntityPhantom extends EntityFlying implements IMonster {

    public static final float FLAP_DEGREES_PER_TICK = 7.448451F;
    public static final int TICKS_PER_FLAP = MathHelper.f(24.166098F);
    private static final DataWatcherObject<Integer> ID_SIZE = DataWatcher.a(EntityPhantom.class, DataWatcherRegistry.INT);
    Vec3D moveTargetPoint;
    BlockPosition anchorPoint;
    EntityPhantom.AttackPhase attackPhase;

    public EntityPhantom(EntityTypes<? extends EntityPhantom> entitytypes, World world) {
        super(entitytypes, world);
        this.moveTargetPoint = Vec3D.ZERO;
        this.anchorPoint = BlockPosition.ZERO;
        this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
        this.xpReward = 5;
        this.moveControl = new EntityPhantom.g(this);
        this.lookControl = new EntityPhantom.f(this);
    }

    @Override
    public boolean aF() {
        return (this.p() + this.tickCount) % EntityPhantom.TICKS_PER_FLAP == 0;
    }

    @Override
    protected EntityAIBodyControl z() {
        return new EntityPhantom.d(this);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new EntityPhantom.c());
        this.goalSelector.a(2, new EntityPhantom.i());
        this.goalSelector.a(3, new EntityPhantom.e());
        this.targetSelector.a(1, new EntityPhantom.b());
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityPhantom.ID_SIZE, 0);
    }

    public void setSize(int i) {
        this.entityData.set(EntityPhantom.ID_SIZE, MathHelper.clamp(i, 0, 64));
    }

    private void t() {
        this.updateSize();
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double) (6 + this.getSize()));
    }

    public int getSize() {
        return (Integer) this.entityData.get(EntityPhantom.ID_SIZE);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return entitysize.height * 0.35F;
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPhantom.ID_SIZE.equals(datawatcherobject)) {
            this.t();
        }

        super.a(datawatcherobject);
    }

    public int p() {
        return this.getId() * 3;
    }

    @Override
    protected boolean Q() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            float f = MathHelper.cos((float) (this.p() + this.tickCount) * 7.448451F * 0.017453292F + 3.1415927F);
            float f1 = MathHelper.cos((float) (this.p() + this.tickCount + 1) * 7.448451F * 0.017453292F + 3.1415927F);

            if (f > 0.0F && f1 <= 0.0F) {
                this.level.a(this.locX(), this.locY(), this.locZ(), SoundEffects.PHANTOM_FLAP, this.getSoundCategory(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getSize();
            float f2 = MathHelper.cos(this.getYRot() * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f3 = MathHelper.sin(this.getYRot() * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f4 = (0.3F + f * 0.45F) * ((float) i * 0.2F + 1.0F);

            this.level.addParticle(Particles.MYCELIUM, this.locX() + (double) f2, this.locY() + (double) f4, this.locZ() + (double) f3, 0.0D, 0.0D, 0.0D);
            this.level.addParticle(Particles.MYCELIUM, this.locX() - (double) f2, this.locY() + (double) f4, this.locZ() - (double) f3, 0.0D, 0.0D, 0.0D);
        }

    }

    @Override
    public void movementTick() {
        if (this.isAlive() && this.fs()) {
            this.setOnFire(8);
        }

        super.movementTick();
    }

    @Override
    protected void mobTick() {
        super.mobTick();
    }

    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.anchorPoint = this.getChunkCoordinates().up(5);
        this.setSize(0);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("AX")) {
            this.anchorPoint = new BlockPosition(nbttagcompound.getInt("AX"), nbttagcompound.getInt("AY"), nbttagcompound.getInt("AZ"));
        }

        this.setSize(nbttagcompound.getInt("Size"));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        nbttagcompound.setInt("AX", this.anchorPoint.getX());
        nbttagcompound.setInt("AY", this.anchorPoint.getY());
        nbttagcompound.setInt("AZ", this.anchorPoint.getZ());
        nbttagcompound.setInt("Size", this.getSize());
    }

    @Override
    public boolean a(double d0) {
        return true;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.PHANTOM_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.PHANTOM_DEATH;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    public boolean a(EntityTypes<?> entitytypes) {
        return true;
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        int i = this.getSize();
        EntitySize entitysize = super.a(entitypose);
        float f = (entitysize.width + 0.2F * (float) i) / entitysize.width;

        return entitysize.a(f);
    }

    private static enum AttackPhase {

        CIRCLE, SWOOP;

        private AttackPhase() {}
    }

    private class g extends ControllerMove {

        private float speed = 0.1F;

        public g(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void a() {
            if (EntityPhantom.this.horizontalCollision) {
                EntityPhantom.this.setYRot(EntityPhantom.this.getYRot() + 180.0F);
                this.speed = 0.1F;
            }

            float f = (float) (EntityPhantom.this.moveTargetPoint.x - EntityPhantom.this.locX());
            float f1 = (float) (EntityPhantom.this.moveTargetPoint.y - EntityPhantom.this.locY());
            float f2 = (float) (EntityPhantom.this.moveTargetPoint.z - EntityPhantom.this.locZ());
            double d0 = (double) MathHelper.c(f * f + f2 * f2);

            if (Math.abs(d0) > 9.999999747378752E-6D) {
                double d1 = 1.0D - (double) MathHelper.e(f1 * 0.7F) / d0;

                f = (float) ((double) f * d1);
                f2 = (float) ((double) f2 * d1);
                d0 = (double) MathHelper.c(f * f + f2 * f2);
                double d2 = (double) MathHelper.c(f * f + f2 * f2 + f1 * f1);
                float f3 = EntityPhantom.this.getYRot();
                float f4 = (float) MathHelper.d((double) f2, (double) f);
                float f5 = MathHelper.g(EntityPhantom.this.getYRot() + 90.0F);
                float f6 = MathHelper.g(f4 * 57.295776F);

                EntityPhantom.this.setYRot(MathHelper.e(f5, f6, 4.0F) - 90.0F);
                EntityPhantom.this.yBodyRot = EntityPhantom.this.getYRot();
                if (MathHelper.d(f3, EntityPhantom.this.getYRot()) < 3.0F) {
                    this.speed = MathHelper.d(this.speed, 1.8F, 0.005F * (1.8F / this.speed));
                } else {
                    this.speed = MathHelper.d(this.speed, 0.2F, 0.025F);
                }

                float f7 = (float) (-(MathHelper.d((double) (-f1), d0) * 57.2957763671875D));

                EntityPhantom.this.setXRot(f7);
                float f8 = EntityPhantom.this.getYRot() + 90.0F;
                double d3 = (double) (this.speed * MathHelper.cos(f8 * 0.017453292F)) * Math.abs((double) f / d2);
                double d4 = (double) (this.speed * MathHelper.sin(f8 * 0.017453292F)) * Math.abs((double) f2 / d2);
                double d5 = (double) (this.speed * MathHelper.sin(f7 * 0.017453292F)) * Math.abs((double) f1 / d2);
                Vec3D vec3d = EntityPhantom.this.getMot();

                EntityPhantom.this.setMot(vec3d.e((new Vec3D(d3, d5, d4)).d(vec3d).a(0.2D)));
            }

        }
    }

    private class f extends ControllerLook {

        public f(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void a() {}
    }

    private class d extends EntityAIBodyControl {

        public d(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        @Override
        public void a() {
            EntityPhantom.this.yHeadRot = EntityPhantom.this.yBodyRot;
            EntityPhantom.this.yBodyRot = EntityPhantom.this.getYRot();
        }
    }

    private class c extends PathfinderGoal {

        private int nextSweepTick;

        c() {}

        @Override
        public boolean a() {
            EntityLiving entityliving = EntityPhantom.this.getGoalTarget();

            return entityliving != null ? EntityPhantom.this.a(EntityPhantom.this.getGoalTarget(), PathfinderTargetCondition.DEFAULT) : false;
        }

        @Override
        public void c() {
            this.nextSweepTick = 10;
            EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
            this.g();
        }

        @Override
        public void d() {
            EntityPhantom.this.anchorPoint = EntityPhantom.this.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, EntityPhantom.this.anchorPoint).up(10 + EntityPhantom.this.random.nextInt(20));
        }

        @Override
        public void e() {
            if (EntityPhantom.this.attackPhase == EntityPhantom.AttackPhase.CIRCLE) {
                --this.nextSweepTick;
                if (this.nextSweepTick <= 0) {
                    EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.SWOOP;
                    this.g();
                    this.nextSweepTick = (8 + EntityPhantom.this.random.nextInt(4)) * 20;
                    EntityPhantom.this.playSound(SoundEffects.PHANTOM_SWOOP, 10.0F, 0.95F + EntityPhantom.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void g() {
            EntityPhantom.this.anchorPoint = EntityPhantom.this.getGoalTarget().getChunkCoordinates().up(20 + EntityPhantom.this.random.nextInt(20));
            if (EntityPhantom.this.anchorPoint.getY() < EntityPhantom.this.level.getSeaLevel()) {
                EntityPhantom.this.anchorPoint = new BlockPosition(EntityPhantom.this.anchorPoint.getX(), EntityPhantom.this.level.getSeaLevel() + 1, EntityPhantom.this.anchorPoint.getZ());
            }

        }
    }

    private class i extends EntityPhantom.h {

        i() {
            super();
        }

        @Override
        public boolean a() {
            return EntityPhantom.this.getGoalTarget() != null && EntityPhantom.this.attackPhase == EntityPhantom.AttackPhase.SWOOP;
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = EntityPhantom.this.getGoalTarget();

            if (entityliving == null) {
                return false;
            } else if (!entityliving.isAlive()) {
                return false;
            } else if (entityliving instanceof EntityHuman && (((EntityHuman) entityliving).isSpectator() || ((EntityHuman) entityliving).isCreative())) {
                return false;
            } else if (!this.a()) {
                return false;
            } else {
                if (EntityPhantom.this.tickCount % 20 == 0) {
                    List<EntityCat> list = EntityPhantom.this.level.a(EntityCat.class, EntityPhantom.this.getBoundingBox().g(16.0D), IEntitySelector.ENTITY_STILL_ALIVE);

                    if (!list.isEmpty()) {
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            EntityCat entitycat = (EntityCat) iterator.next();

                            entitycat.fJ();
                        }

                        return false;
                    }
                }

                return true;
            }
        }

        @Override
        public void c() {}

        @Override
        public void d() {
            EntityPhantom.this.setGoalTarget((EntityLiving) null);
            EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
        }

        @Override
        public void e() {
            EntityLiving entityliving = EntityPhantom.this.getGoalTarget();

            EntityPhantom.this.moveTargetPoint = new Vec3D(entityliving.locX(), entityliving.e(0.5D), entityliving.locZ());
            if (EntityPhantom.this.getBoundingBox().g(0.20000000298023224D).c(entityliving.getBoundingBox())) {
                EntityPhantom.this.attackEntity(entityliving);
                EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
                if (!EntityPhantom.this.isSilent()) {
                    EntityPhantom.this.level.triggerEffect(1039, EntityPhantom.this.getChunkCoordinates(), 0);
                }
            } else if (EntityPhantom.this.horizontalCollision || EntityPhantom.this.hurtTime > 0) {
                EntityPhantom.this.attackPhase = EntityPhantom.AttackPhase.CIRCLE;
            }

        }
    }

    private class e extends EntityPhantom.h {

        private float angle;
        private float distance;
        private float height;
        private float clockwise;

        e() {
            super();
        }

        @Override
        public boolean a() {
            return EntityPhantom.this.getGoalTarget() == null || EntityPhantom.this.attackPhase == EntityPhantom.AttackPhase.CIRCLE;
        }

        @Override
        public void c() {
            this.distance = 5.0F + EntityPhantom.this.random.nextFloat() * 10.0F;
            this.height = -4.0F + EntityPhantom.this.random.nextFloat() * 9.0F;
            this.clockwise = EntityPhantom.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.h();
        }

        @Override
        public void e() {
            if (EntityPhantom.this.random.nextInt(350) == 0) {
                this.height = -4.0F + EntityPhantom.this.random.nextFloat() * 9.0F;
            }

            if (EntityPhantom.this.random.nextInt(250) == 0) {
                ++this.distance;
                if (this.distance > 15.0F) {
                    this.distance = 5.0F;
                    this.clockwise = -this.clockwise;
                }
            }

            if (EntityPhantom.this.random.nextInt(450) == 0) {
                this.angle = EntityPhantom.this.random.nextFloat() * 2.0F * 3.1415927F;
                this.h();
            }

            if (this.g()) {
                this.h();
            }

            if (EntityPhantom.this.moveTargetPoint.y < EntityPhantom.this.locY() && !EntityPhantom.this.level.isEmpty(EntityPhantom.this.getChunkCoordinates().down(1))) {
                this.height = Math.max(1.0F, this.height);
                this.h();
            }

            if (EntityPhantom.this.moveTargetPoint.y > EntityPhantom.this.locY() && !EntityPhantom.this.level.isEmpty(EntityPhantom.this.getChunkCoordinates().up(1))) {
                this.height = Math.min(-1.0F, this.height);
                this.h();
            }

        }

        private void h() {
            if (BlockPosition.ZERO.equals(EntityPhantom.this.anchorPoint)) {
                EntityPhantom.this.anchorPoint = EntityPhantom.this.getChunkCoordinates();
            }

            this.angle += this.clockwise * 15.0F * 0.017453292F;
            EntityPhantom.this.moveTargetPoint = Vec3D.b((BaseBlockPosition) EntityPhantom.this.anchorPoint).add((double) (this.distance * MathHelper.cos(this.angle)), (double) (-4.0F + this.height), (double) (this.distance * MathHelper.sin(this.angle)));
        }
    }

    private class b extends PathfinderGoal {

        private final PathfinderTargetCondition attackTargeting = PathfinderTargetCondition.a().a(64.0D);
        private int nextScanTick = 20;

        b() {}

        @Override
        public boolean a() {
            if (this.nextScanTick > 0) {
                --this.nextScanTick;
                return false;
            } else {
                this.nextScanTick = 60;
                List<EntityHuman> list = EntityPhantom.this.level.a(this.attackTargeting, (EntityLiving) EntityPhantom.this, EntityPhantom.this.getBoundingBox().grow(16.0D, 64.0D, 16.0D));

                if (!list.isEmpty()) {
                    list.sort(Comparator.comparing(Entity::locY).reversed());
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityHuman entityhuman = (EntityHuman) iterator.next();

                        if (EntityPhantom.this.a((EntityLiving) entityhuman, PathfinderTargetCondition.DEFAULT)) {
                            EntityPhantom.this.setGoalTarget(entityhuman);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        @Override
        public boolean b() {
            EntityLiving entityliving = EntityPhantom.this.getGoalTarget();

            return entityliving != null ? EntityPhantom.this.a(entityliving, PathfinderTargetCondition.DEFAULT) : false;
        }
    }

    private abstract class h extends PathfinderGoal {

        public h() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        protected boolean g() {
            return EntityPhantom.this.moveTargetPoint.c(EntityPhantom.this.locX(), EntityPhantom.this.locY(), EntityPhantom.this.locZ()) < 4.0D;
        }
    }
}
