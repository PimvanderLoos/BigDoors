package net.minecraft.world.entity.ai.navigation;

import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderWater;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class NavigationGuardian extends NavigationAbstract {

    private boolean allowBreaching;

    public NavigationGuardian(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    protected Pathfinder a(int i) {
        this.allowBreaching = this.mob.getEntityType() == EntityTypes.DOLPHIN;
        this.nodeEvaluator = new PathfinderWater(this.allowBreaching);
        return new Pathfinder(this.nodeEvaluator, i);
    }

    @Override
    protected boolean a() {
        return this.allowBreaching || this.p();
    }

    @Override
    protected Vec3D b() {
        return new Vec3D(this.mob.locX(), this.mob.e(0.5D), this.mob.locZ());
    }

    @Override
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
                vec3d = this.path.a((Entity) this.mob);
                if (this.mob.cW() == MathHelper.floor(vec3d.x) && this.mob.cY() == MathHelper.floor(vec3d.y) && this.mob.dc() == MathHelper.floor(vec3d.z)) {
                    this.path.a();
                }
            }

            PacketDebug.a(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.m()) {
                vec3d = this.path.a((Entity) this.mob);
                this.mob.getControllerMove().a(vec3d.x, vec3d.y, vec3d.z, this.speedModifier);
            }
        }
    }

    @Override
    protected void l() {
        if (this.path != null) {
            Vec3D vec3d = this.b();
            float f = this.mob.getWidth();
            float f1 = f > 0.75F ? f / 2.0F : 0.75F - f / 2.0F;
            Vec3D vec3d1 = this.mob.getMot();

            if (Math.abs(vec3d1.x) > 0.2D || Math.abs(vec3d1.z) > 0.2D) {
                f1 = (float) ((double) f1 * vec3d1.f() * 6.0D);
            }

            boolean flag = true;
            Vec3D vec3d2 = Vec3D.c((BaseBlockPosition) this.path.g());

            if (Math.abs(this.mob.locX() - vec3d2.x) < (double) f1 && Math.abs(this.mob.locZ() - vec3d2.z) < (double) f1 && Math.abs(this.mob.locY() - vec3d2.y) < (double) (f1 * 2.0F)) {
                this.path.a();
            }

            for (int i = Math.min(this.path.f() + 6, this.path.e() - 1); i > this.path.f(); --i) {
                vec3d2 = this.path.a(this.mob, i);
                if (vec3d2.distanceSquared(vec3d) <= 36.0D && this.a(vec3d, vec3d2, 0, 0, 0)) {
                    this.path.c(i);
                    break;
                }
            }

            this.a(vec3d);
        }
    }

    @Override
    protected void a(Vec3D vec3d) {
        if (this.tick - this.lastStuckCheck > 100) {
            if (vec3d.distanceSquared(this.lastStuckCheckPos) < 2.25D) {
                this.o();
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
                double d0 = vec3d.f(Vec3D.a(this.timeoutCachedNode));

                this.timeoutLimit = this.mob.ew() > 0.0F ? d0 / (double) this.mob.ew() * 100.0D : 0.0D;
            }

            if (this.timeoutLimit > 0.0D && (double) this.timeoutTimer > this.timeoutLimit * 2.0D) {
                this.timeoutCachedNode = BaseBlockPosition.ZERO;
                this.timeoutTimer = 0L;
                this.timeoutLimit = 0.0D;
                this.o();
            }

            this.lastTimeoutCheck = SystemUtils.getMonotonicMillis();
        }

    }

    @Override
    protected boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k) {
        Vec3D vec3d2 = new Vec3D(vec3d1.x, vec3d1.y + (double) this.mob.getHeight() * 0.5D, vec3d1.z);

        return this.level.rayTrace(new RayTrace(vec3d, vec3d2, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this.mob)).getType() == MovingObjectPosition.EnumMovingObjectType.MISS;
    }

    @Override
    public boolean a(BlockPosition blockposition) {
        return !this.level.getType(blockposition).i(this.level, blockposition);
    }

    @Override
    public void d(boolean flag) {}
}
