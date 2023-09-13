package net.minecraft.world.entity.ai.navigation;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.level.ChunkCache;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderAbstract;
import net.minecraft.world.level.pathfinder.PathfinderNormal;
import net.minecraft.world.phys.Vec3D;

public abstract class NavigationAbstract {

    private static final int MAX_TIME_RECOMPUTE = 20;
    protected final EntityInsentient mob;
    protected final World level;
    @Nullable
    protected PathEntity path;
    protected double speedModifier;
    protected int tick;
    protected int lastStuckCheck;
    protected Vec3D lastStuckCheckPos;
    protected BaseBlockPosition timeoutCachedNode;
    protected long timeoutTimer;
    protected long lastTimeoutCheck;
    protected double timeoutLimit;
    protected float maxDistanceToWaypoint;
    protected boolean hasDelayedRecomputation;
    protected long timeLastRecompute;
    protected PathfinderAbstract nodeEvaluator;
    private BlockPosition targetPos;
    private int reachRange;
    private float maxVisitedNodesMultiplier;
    private final Pathfinder pathFinder;
    private boolean isStuck;

    public NavigationAbstract(EntityInsentient entityinsentient, World world) {
        this.lastStuckCheckPos = Vec3D.ZERO;
        this.timeoutCachedNode = BaseBlockPosition.ZERO;
        this.maxDistanceToWaypoint = 0.5F;
        this.maxVisitedNodesMultiplier = 1.0F;
        this.mob = entityinsentient;
        this.level = world;
        int i = MathHelper.floor(entityinsentient.b(GenericAttributes.FOLLOW_RANGE) * 16.0D);

        this.pathFinder = this.a(i);
    }

    public void g() {
        this.maxVisitedNodesMultiplier = 1.0F;
    }

    public void a(float f) {
        this.maxVisitedNodesMultiplier = f;
    }

    public BlockPosition h() {
        return this.targetPos;
    }

    protected abstract Pathfinder a(int i);

    public void a(double d0) {
        this.speedModifier = d0;
    }

    public boolean i() {
        return this.hasDelayedRecomputation;
    }

    public void j() {
        if (this.level.getTime() - this.timeLastRecompute > 20L) {
            if (this.targetPos != null) {
                this.path = null;
                this.path = this.a(this.targetPos, this.reachRange);
                this.timeLastRecompute = this.level.getTime();
                this.hasDelayedRecomputation = false;
            }
        } else {
            this.hasDelayedRecomputation = true;
        }

    }

    @Nullable
    public final PathEntity a(double d0, double d1, double d2, int i) {
        return this.a(new BlockPosition(d0, d1, d2), i);
    }

    @Nullable
    public PathEntity a(Stream<BlockPosition> stream, int i) {
        return this.a((Set) stream.collect(Collectors.toSet()), 8, false, i);
    }

    @Nullable
    public PathEntity a(Set<BlockPosition> set, int i) {
        return this.a(set, 8, false, i);
    }

    @Nullable
    public PathEntity a(BlockPosition blockposition, int i) {
        return this.a(ImmutableSet.of(blockposition), 8, false, i);
    }

    @Nullable
    public PathEntity a(BlockPosition blockposition, int i, int j) {
        return this.a(ImmutableSet.of(blockposition), 8, false, i, (float) j);
    }

    @Nullable
    public PathEntity a(Entity entity, int i) {
        return this.a(ImmutableSet.of(entity.getChunkCoordinates()), 16, true, i);
    }

    @Nullable
    protected PathEntity a(Set<BlockPosition> set, int i, boolean flag, int j) {
        return this.a(set, i, flag, j, (float) this.mob.b(GenericAttributes.FOLLOW_RANGE));
    }

    @Nullable
    protected PathEntity a(Set<BlockPosition> set, int i, boolean flag, int j, float f) {
        if (set.isEmpty()) {
            return null;
        } else if (this.mob.locY() < (double) this.level.getMinBuildHeight()) {
            return null;
        } else if (!this.a()) {
            return null;
        } else if (this.path != null && !this.path.c() && set.contains(this.targetPos)) {
            return this.path;
        } else {
            this.level.getMethodProfiler().enter("pathfind");
            BlockPosition blockposition = flag ? this.mob.getChunkCoordinates().up() : this.mob.getChunkCoordinates();
            int k = (int) (f + (float) i);
            ChunkCache chunkcache = new ChunkCache(this.level, blockposition.c(-k, -k, -k), blockposition.c(k, k, k));
            PathEntity pathentity = this.pathFinder.a(chunkcache, this.mob, set, f, j, this.maxVisitedNodesMultiplier);

            this.level.getMethodProfiler().exit();
            if (pathentity != null && pathentity.m() != null) {
                this.targetPos = pathentity.m();
                this.reachRange = j;
                this.f();
            }

            return pathentity;
        }
    }

    public boolean a(double d0, double d1, double d2, double d3) {
        return this.a(this.a(d0, d1, d2, 1), d3);
    }

    public boolean a(Entity entity, double d0) {
        PathEntity pathentity = this.a(entity, 1);

        return pathentity != null && this.a(pathentity, d0);
    }

    public boolean a(@Nullable PathEntity pathentity, double d0) {
        if (pathentity == null) {
            this.path = null;
            return false;
        } else {
            if (!pathentity.a(this.path)) {
                this.path = pathentity;
            }

            if (this.m()) {
                return false;
            } else {
                this.D_();
                if (this.path.e() <= 0) {
                    return false;
                } else {
                    this.speedModifier = d0;
                    Vec3D vec3d = this.b();

                    this.lastStuckCheck = this.tick;
                    this.lastStuckCheckPos = vec3d;
                    return true;
                }
            }
        }
    }

    @Nullable
    public PathEntity k() {
        return this.path;
    }

    public void c() {
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.j();
        }

        if (!this.m()) {
            Vec3D vec3d;

            if (this.a()) {
                this.l();
            } else if (this.path != null && !this.path.c()) {
                vec3d = this.b();
                Vec3D vec3d1 = this.path.a((Entity) this.mob);

                if (vec3d.y > vec3d1.y && !this.mob.isOnGround() && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
                    this.path.a();
                }
            }

            PacketDebug.a(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.m()) {
                vec3d = this.path.a((Entity) this.mob);
                BlockPosition blockposition = new BlockPosition(vec3d);

                this.mob.getControllerMove().a(vec3d.x, this.level.getType(blockposition.down()).isAir() ? vec3d.y : PathfinderNormal.a((IBlockAccess) this.level, blockposition), vec3d.z, this.speedModifier);
            }
        }
    }

    protected void l() {
        Vec3D vec3d = this.b();

        this.maxDistanceToWaypoint = this.mob.getWidth() > 0.75F ? this.mob.getWidth() / 2.0F : 0.75F - this.mob.getWidth() / 2.0F;
        BlockPosition blockposition = this.path.g();
        double d0 = Math.abs(this.mob.locX() - ((double) blockposition.getX() + 0.5D));
        double d1 = Math.abs(this.mob.locY() - (double) blockposition.getY());
        double d2 = Math.abs(this.mob.locZ() - ((double) blockposition.getZ() + 0.5D));
        boolean flag = d0 < (double) this.maxDistanceToWaypoint && d2 < (double) this.maxDistanceToWaypoint && d1 < 1.0D;

        if (flag || this.mob.b(this.path.h().type) && this.b(vec3d)) {
            this.path.a();
        }

        this.a(vec3d);
    }

    private boolean b(Vec3D vec3d) {
        if (this.path.f() + 1 >= this.path.e()) {
            return false;
        } else {
            Vec3D vec3d1 = Vec3D.c((BaseBlockPosition) this.path.g());

            if (!vec3d.a((IPosition) vec3d1, 2.0D)) {
                return false;
            } else {
                Vec3D vec3d2 = Vec3D.c((BaseBlockPosition) this.path.d(this.path.f() + 1));
                Vec3D vec3d3 = vec3d2.d(vec3d1);
                Vec3D vec3d4 = vec3d.d(vec3d1);

                return vec3d3.b(vec3d4) > 0.0D;
            }
        }
    }

    protected void a(Vec3D vec3d) {
        if (this.tick - this.lastStuckCheck > 100) {
            if (vec3d.distanceSquared(this.lastStuckCheckPos) < 2.25D) {
                this.isStuck = true;
                this.o();
            } else {
                this.isStuck = false;
            }

            this.lastStuckCheck = this.tick;
            this.lastStuckCheckPos = vec3d;
        }

        if (this.path != null && !this.path.c()) {
            BlockPosition blockposition = this.path.g();

            if (blockposition.equals(this.timeoutCachedNode)) {
                this.timeoutTimer += SystemUtils.getMonotonicMillis() - this.lastTimeoutCheck;
            } else {
                this.timeoutCachedNode = blockposition;
                double d0 = vec3d.f(Vec3D.c(this.timeoutCachedNode));

                this.timeoutLimit = this.mob.ew() > 0.0F ? d0 / (double) this.mob.ew() * 1000.0D : 0.0D;
            }

            if (this.timeoutLimit > 0.0D && (double) this.timeoutTimer > this.timeoutLimit * 3.0D) {
                this.e();
            }

            this.lastTimeoutCheck = SystemUtils.getMonotonicMillis();
        }

    }

    private void e() {
        this.f();
        this.o();
    }

    private void f() {
        this.timeoutCachedNode = BaseBlockPosition.ZERO;
        this.timeoutTimer = 0L;
        this.timeoutLimit = 0.0D;
        this.isStuck = false;
    }

    public boolean m() {
        return this.path == null || this.path.c();
    }

    public boolean n() {
        return !this.m();
    }

    public void o() {
        this.path = null;
    }

    protected abstract Vec3D b();

    protected abstract boolean a();

    protected boolean p() {
        return this.mob.aO() || this.mob.aX();
    }

    protected void D_() {
        if (this.path != null) {
            for (int i = 0; i < this.path.e(); ++i) {
                PathPoint pathpoint = this.path.a(i);
                PathPoint pathpoint1 = i + 1 < this.path.e() ? this.path.a(i + 1) : null;
                IBlockData iblockdata = this.level.getType(new BlockPosition(pathpoint.x, pathpoint.y, pathpoint.z));

                if (iblockdata.a((Tag) TagsBlock.CAULDRONS)) {
                    this.path.a(i, pathpoint.a(pathpoint.x, pathpoint.y + 1, pathpoint.z));
                    if (pathpoint1 != null && pathpoint.y >= pathpoint1.y) {
                        this.path.a(i + 1, pathpoint.a(pathpoint1.x, pathpoint.y + 1, pathpoint1.z));
                    }
                }
            }

        }
    }

    protected abstract boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k);

    public boolean a(BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();

        return this.level.getType(blockposition1).i(this.level, blockposition1);
    }

    public PathfinderAbstract q() {
        return this.nodeEvaluator;
    }

    public void d(boolean flag) {
        this.nodeEvaluator.c(flag);
    }

    public boolean r() {
        return this.nodeEvaluator.f();
    }

    public void b(BlockPosition blockposition) {
        if (this.path != null && !this.path.c() && this.path.e() != 0) {
            PathPoint pathpoint = this.path.d();
            Vec3D vec3d = new Vec3D(((double) pathpoint.x + this.mob.locX()) / 2.0D, ((double) pathpoint.y + this.mob.locY()) / 2.0D, ((double) pathpoint.z + this.mob.locZ()) / 2.0D);

            if (blockposition.a((IPosition) vec3d, (double) (this.path.e() - this.path.f()))) {
                this.j();
            }

        }
    }

    public float s() {
        return this.maxDistanceToWaypoint;
    }

    public boolean t() {
        return this.isStuck;
    }
}
