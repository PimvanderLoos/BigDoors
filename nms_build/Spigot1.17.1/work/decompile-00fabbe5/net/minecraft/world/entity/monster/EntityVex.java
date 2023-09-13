package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.syncher.DataWatcher;
import net.minecraft.network.syncher.DataWatcherObject;
import net.minecraft.network.syncher.DataWatcherRegistry;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.MathHelper;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.attributes.AttributeProvider;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.entity.ai.control.ControllerMove;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.entity.ai.goal.PathfinderGoalLookAtPlayer;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalHurtByTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalNearestAttackableTarget;
import net.minecraft.world.entity.ai.goal.target.PathfinderGoalTarget;
import net.minecraft.world.entity.ai.targeting.PathfinderTargetCondition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.phys.Vec3D;

public class EntityVex extends EntityMonster {

    public static final float FLAP_DEGREES_PER_TICK = 45.836624F;
    public static final int TICKS_PER_FLAP = MathHelper.f(3.9269907F);
    protected static final DataWatcherObject<Byte> DATA_FLAGS_ID = DataWatcher.a(EntityVex.class, DataWatcherRegistry.BYTE);
    private static final int FLAG_IS_CHARGING = 1;
    EntityInsentient owner;
    @Nullable
    private BlockPosition boundOrigin;
    private boolean hasLimitedLife;
    private int limitedLifeTicks;

    public EntityVex(EntityTypes<? extends EntityVex> entitytypes, World world) {
        super(entitytypes, world);
        this.moveControl = new EntityVex.c(this);
        this.xpReward = 3;
    }

    @Override
    public boolean aF() {
        return this.tickCount % EntityVex.TICKS_PER_FLAP == 0;
    }

    @Override
    public void move(EnumMoveType enummovetype, Vec3D vec3d) {
        super.move(enummovetype, vec3d);
        this.checkBlockCollisions();
    }

    @Override
    public void tick() {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
        if (this.hasLimitedLife && --this.limitedLifeTicks <= 0) {
            this.limitedLifeTicks = 20;
            this.damageEntity(DamageSource.STARVE, 1.0F);
        }

    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(4, new EntityVex.a());
        this.goalSelector.a(8, new EntityVex.d());
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a());
        this.targetSelector.a(2, new EntityVex.b(this));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    public static AttributeProvider.Builder n() {
        return EntityMonster.fB().a(GenericAttributes.MAX_HEALTH, 14.0D).a(GenericAttributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.entityData.register(EntityVex.DATA_FLAGS_ID, (byte) 0);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("BoundX")) {
            this.boundOrigin = new BlockPosition(nbttagcompound.getInt("BoundX"), nbttagcompound.getInt("BoundY"), nbttagcompound.getInt("BoundZ"));
        }

        if (nbttagcompound.hasKey("LifeTicks")) {
            this.a(nbttagcompound.getInt("LifeTicks"));
        }

    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.boundOrigin != null) {
            nbttagcompound.setInt("BoundX", this.boundOrigin.getX());
            nbttagcompound.setInt("BoundY", this.boundOrigin.getY());
            nbttagcompound.setInt("BoundZ", this.boundOrigin.getZ());
        }

        if (this.hasLimitedLife) {
            nbttagcompound.setInt("LifeTicks", this.limitedLifeTicks);
        }

    }

    public EntityInsentient p() {
        return this.owner;
    }

    @Nullable
    public BlockPosition t() {
        return this.boundOrigin;
    }

    public void g(@Nullable BlockPosition blockposition) {
        this.boundOrigin = blockposition;
    }

    private boolean b(int i) {
        byte b0 = (Byte) this.entityData.get(EntityVex.DATA_FLAGS_ID);

        return (b0 & i) != 0;
    }

    private void a(int i, boolean flag) {
        byte b0 = (Byte) this.entityData.get(EntityVex.DATA_FLAGS_ID);
        int j;

        if (flag) {
            j = b0 | i;
        } else {
            j = b0 & ~i;
        }

        this.entityData.set(EntityVex.DATA_FLAGS_ID, (byte) (j & 255));
    }

    public boolean isCharging() {
        return this.b(1);
    }

    public void setCharging(boolean flag) {
        this.a(1, flag);
    }

    public void a(EntityInsentient entityinsentient) {
        this.owner = entityinsentient;
    }

    public void a(int i) {
        this.hasLimitedLife = true;
        this.limitedLifeTicks = i;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.VEX_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.VEX_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.VEX_HURT;
    }

    @Override
    public float aY() {
        return 1.0F;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
        this.a(EnumItemSlot.MAINHAND, 0.0F);
    }

    private class c extends ControllerMove {

        public c(EntityVex entityvex) {
            super(entityvex);
        }

        @Override
        public void a() {
            if (this.operation == ControllerMove.Operation.MOVE_TO) {
                Vec3D vec3d = new Vec3D(this.wantedX - EntityVex.this.locX(), this.wantedY - EntityVex.this.locY(), this.wantedZ - EntityVex.this.locZ());
                double d0 = vec3d.f();

                if (d0 < EntityVex.this.getBoundingBox().a()) {
                    this.operation = ControllerMove.Operation.WAIT;
                    EntityVex.this.setMot(EntityVex.this.getMot().a(0.5D));
                } else {
                    EntityVex.this.setMot(EntityVex.this.getMot().e(vec3d.a(this.speedModifier * 0.05D / d0)));
                    if (EntityVex.this.getGoalTarget() == null) {
                        Vec3D vec3d1 = EntityVex.this.getMot();

                        EntityVex.this.setYRot(-((float) MathHelper.d(vec3d1.x, vec3d1.z)) * 57.295776F);
                        EntityVex.this.yBodyRot = EntityVex.this.getYRot();
                    } else {
                        double d1 = EntityVex.this.getGoalTarget().locX() - EntityVex.this.locX();
                        double d2 = EntityVex.this.getGoalTarget().locZ() - EntityVex.this.locZ();

                        EntityVex.this.setYRot(-((float) MathHelper.d(d1, d2)) * 57.295776F);
                        EntityVex.this.yBodyRot = EntityVex.this.getYRot();
                    }
                }

            }
        }
    }

    private class a extends PathfinderGoal {

        public a() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return EntityVex.this.getGoalTarget() != null && !EntityVex.this.getControllerMove().b() && EntityVex.this.random.nextInt(7) == 0 ? EntityVex.this.f((Entity) EntityVex.this.getGoalTarget()) > 4.0D : false;
        }

        @Override
        public boolean b() {
            return EntityVex.this.getControllerMove().b() && EntityVex.this.isCharging() && EntityVex.this.getGoalTarget() != null && EntityVex.this.getGoalTarget().isAlive();
        }

        @Override
        public void c() {
            EntityLiving entityliving = EntityVex.this.getGoalTarget();
            Vec3D vec3d = entityliving.bb();

            EntityVex.this.moveControl.a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
            EntityVex.this.setCharging(true);
            EntityVex.this.playSound(SoundEffects.VEX_CHARGE, 1.0F, 1.0F);
        }

        @Override
        public void d() {
            EntityVex.this.setCharging(false);
        }

        @Override
        public void e() {
            EntityLiving entityliving = EntityVex.this.getGoalTarget();

            if (EntityVex.this.getBoundingBox().c(entityliving.getBoundingBox())) {
                EntityVex.this.attackEntity(entityliving);
                EntityVex.this.setCharging(false);
            } else {
                double d0 = EntityVex.this.f((Entity) entityliving);

                if (d0 < 9.0D) {
                    Vec3D vec3d = entityliving.bb();

                    EntityVex.this.moveControl.a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }

        }
    }

    private class d extends PathfinderGoal {

        public d() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            return !EntityVex.this.getControllerMove().b() && EntityVex.this.random.nextInt(7) == 0;
        }

        @Override
        public boolean b() {
            return false;
        }

        @Override
        public void e() {
            BlockPosition blockposition = EntityVex.this.t();

            if (blockposition == null) {
                blockposition = EntityVex.this.getChunkCoordinates();
            }

            for (int i = 0; i < 3; ++i) {
                BlockPosition blockposition1 = blockposition.c(EntityVex.this.random.nextInt(15) - 7, EntityVex.this.random.nextInt(11) - 5, EntityVex.this.random.nextInt(15) - 7);

                if (EntityVex.this.level.isEmpty(blockposition1)) {
                    EntityVex.this.moveControl.a((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.5D, (double) blockposition1.getZ() + 0.5D, 0.25D);
                    if (EntityVex.this.getGoalTarget() == null) {
                        EntityVex.this.getControllerLook().a((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.5D, (double) blockposition1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    private class b extends PathfinderGoalTarget {

        private final PathfinderTargetCondition copyOwnerTargeting = PathfinderTargetCondition.b().d().e();

        public b(EntityCreature entitycreature) {
            super(entitycreature, false);
        }

        @Override
        public boolean a() {
            return EntityVex.this.owner != null && EntityVex.this.owner.getGoalTarget() != null && this.a(EntityVex.this.owner.getGoalTarget(), this.copyOwnerTargeting);
        }

        @Override
        public void c() {
            EntityVex.this.setGoalTarget(EntityVex.this.owner.getGoalTarget());
            super.c();
        }
    }
}
