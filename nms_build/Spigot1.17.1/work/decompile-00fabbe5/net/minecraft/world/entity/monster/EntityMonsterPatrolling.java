package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.DifficultyDamageScaler;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.GroupDataEntity;
import net.minecraft.world.entity.ai.goal.PathfinderGoal;
import net.minecraft.world.entity.ai.navigation.NavigationAbstract;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldAccess;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityMonsterPatrolling extends EntityMonster {

    private BlockPosition patrolTarget;
    private boolean patrolLeader;
    private boolean patrolling;

    protected EntityMonsterPatrolling(EntityTypes<? extends EntityMonsterPatrolling> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(4, new EntityMonsterPatrolling.a<>(this, 0.7D, 0.595D));
    }

    @Override
    public void saveData(NBTTagCompound nbttagcompound) {
        super.saveData(nbttagcompound);
        if (this.patrolTarget != null) {
            nbttagcompound.set("PatrolTarget", GameProfileSerializer.a(this.patrolTarget));
        }

        nbttagcompound.setBoolean("PatrolLeader", this.patrolLeader);
        nbttagcompound.setBoolean("Patrolling", this.patrolling);
    }

    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("PatrolTarget")) {
            this.patrolTarget = GameProfileSerializer.b(nbttagcompound.getCompound("PatrolTarget"));
        }

        this.patrolLeader = nbttagcompound.getBoolean("PatrolLeader");
        this.patrolling = nbttagcompound.getBoolean("Patrolling");
    }

    @Override
    public double bk() {
        return -0.45D;
    }

    public boolean fx() {
        return true;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(WorldAccess worldaccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        if (enummobspawn != EnumMobSpawn.PATROL && enummobspawn != EnumMobSpawn.EVENT && enummobspawn != EnumMobSpawn.STRUCTURE && this.random.nextFloat() < 0.06F && this.fx()) {
            this.patrolLeader = true;
        }

        if (this.isPatrolLeader()) {
            this.setSlot(EnumItemSlot.HEAD, Raid.s());
            this.a(EnumItemSlot.HEAD, 2.0F);
        }

        if (enummobspawn == EnumMobSpawn.PATROL) {
            this.patrolling = true;
        }

        return super.prepare(worldaccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public static boolean b(EntityTypes<? extends EntityMonsterPatrolling> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 8 ? false : c(entitytypes, generatoraccess, enummobspawn, blockposition, random);
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return !this.patrolling || d0 > 16384.0D;
    }

    public void setPatrolTarget(BlockPosition blockposition) {
        this.patrolTarget = blockposition;
        this.patrolling = true;
    }

    public BlockPosition getPatrolTarget() {
        return this.patrolTarget;
    }

    public boolean fz() {
        return this.patrolTarget != null;
    }

    public void setPatrolLeader(boolean flag) {
        this.patrolLeader = flag;
        this.patrolling = true;
    }

    public boolean isPatrolLeader() {
        return this.patrolLeader;
    }

    public boolean fD() {
        return true;
    }

    public void fE() {
        this.patrolTarget = this.getChunkCoordinates().c(-500 + this.random.nextInt(1000), 0, -500 + this.random.nextInt(1000));
        this.patrolling = true;
    }

    protected boolean isPatrolling() {
        return this.patrolling;
    }

    protected void w(boolean flag) {
        this.patrolling = flag;
    }

    public static class a<T extends EntityMonsterPatrolling> extends PathfinderGoal {

        private static final int NAVIGATION_FAILED_COOLDOWN = 200;
        private final T mob;
        private final double speedModifier;
        private final double leaderSpeedModifier;
        private long cooldownUntil;

        public a(T t0, double d0, double d1) {
            this.mob = t0;
            this.speedModifier = d0;
            this.leaderSpeedModifier = d1;
            this.cooldownUntil = -1L;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            boolean flag = this.mob.level.getTime() < this.cooldownUntil;

            return this.mob.isPatrolling() && this.mob.getGoalTarget() == null && !this.mob.isVehicle() && this.mob.fz() && !flag;
        }

        @Override
        public void c() {}

        @Override
        public void d() {}

        @Override
        public void e() {
            boolean flag = this.mob.isPatrolLeader();
            NavigationAbstract navigationabstract = this.mob.getNavigation();

            if (navigationabstract.m()) {
                List<EntityMonsterPatrolling> list = this.g();

                if (this.mob.isPatrolling() && list.isEmpty()) {
                    this.mob.w(false);
                } else if (flag && this.mob.getPatrolTarget().a((IPosition) this.mob.getPositionVector(), 10.0D)) {
                    this.mob.fE();
                } else {
                    Vec3D vec3d = Vec3D.c((BaseBlockPosition) this.mob.getPatrolTarget());
                    Vec3D vec3d1 = this.mob.getPositionVector();
                    Vec3D vec3d2 = vec3d1.d(vec3d);

                    vec3d = vec3d2.b(90.0F).a(0.4D).e(vec3d);
                    Vec3D vec3d3 = vec3d.d(vec3d1).d().a(10.0D).e(vec3d1);
                    BlockPosition blockposition = new BlockPosition(vec3d3);

                    blockposition = this.mob.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition);
                    if (!navigationabstract.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), flag ? this.leaderSpeedModifier : this.speedModifier)) {
                        this.h();
                        this.cooldownUntil = this.mob.level.getTime() + 200L;
                    } else if (flag) {
                        Iterator iterator = list.iterator();

                        while (iterator.hasNext()) {
                            EntityMonsterPatrolling entitymonsterpatrolling = (EntityMonsterPatrolling) iterator.next();

                            entitymonsterpatrolling.setPatrolTarget(blockposition);
                        }
                    }
                }
            }

        }

        private List<EntityMonsterPatrolling> g() {
            return this.mob.level.a(EntityMonsterPatrolling.class, this.mob.getBoundingBox().g(16.0D), (entitymonsterpatrolling) -> {
                return entitymonsterpatrolling.fD() && !entitymonsterpatrolling.q(this.mob);
            });
        }

        private boolean h() {
            Random random = this.mob.getRandom();
            BlockPosition blockposition = this.mob.level.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, this.mob.getChunkCoordinates().c(-8 + random.nextInt(16), 0, -8 + random.nextInt(16)));

            return this.mob.getNavigation().a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), this.speedModifier);
        }
    }
}
