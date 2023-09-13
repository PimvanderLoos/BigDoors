package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderFlying;
import net.minecraft.world.phys.Vec3D;

public class NavigationFlying extends NavigationAbstract {

    public NavigationFlying(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    protected Pathfinder a(int i) {
        this.nodeEvaluator = new PathfinderFlying();
        this.nodeEvaluator.a(true);
        return new Pathfinder(this.nodeEvaluator, i);
    }

    @Override
    protected boolean a() {
        return this.r() && this.p() || !this.mob.isPassenger();
    }

    @Override
    protected Vec3D b() {
        return this.mob.getPositionVector();
    }

    @Override
    public PathEntity a(Entity entity, int i) {
        return this.a(entity.getChunkCoordinates(), i);
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
    protected boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k) {
        int l = MathHelper.floor(vec3d.x);
        int i1 = MathHelper.floor(vec3d.y);
        int j1 = MathHelper.floor(vec3d.z);
        double d0 = vec3d1.x - vec3d.x;
        double d1 = vec3d1.y - vec3d.y;
        double d2 = vec3d1.z - vec3d.z;
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;

        if (d3 < 1.0E-8D) {
            return false;
        } else {
            double d4 = 1.0D / Math.sqrt(d3);

            d0 *= d4;
            d1 *= d4;
            d2 *= d4;
            double d5 = 1.0D / Math.abs(d0);
            double d6 = 1.0D / Math.abs(d1);
            double d7 = 1.0D / Math.abs(d2);
            double d8 = (double) l - vec3d.x;
            double d9 = (double) i1 - vec3d.y;
            double d10 = (double) j1 - vec3d.z;

            if (d0 >= 0.0D) {
                ++d8;
            }

            if (d1 >= 0.0D) {
                ++d9;
            }

            if (d2 >= 0.0D) {
                ++d10;
            }

            d8 /= d0;
            d9 /= d1;
            d10 /= d2;
            int k1 = d0 < 0.0D ? -1 : 1;
            int l1 = d1 < 0.0D ? -1 : 1;
            int i2 = d2 < 0.0D ? -1 : 1;
            int j2 = MathHelper.floor(vec3d1.x);
            int k2 = MathHelper.floor(vec3d1.y);
            int l2 = MathHelper.floor(vec3d1.z);
            int i3 = j2 - l;
            int j3 = k2 - i1;
            int k3 = l2 - j1;

            while (i3 * k1 > 0 || j3 * l1 > 0 || k3 * i2 > 0) {
                if (d8 < d10 && d8 <= d9) {
                    d8 += d5;
                    l += k1;
                    i3 = j2 - l;
                } else if (d9 < d8 && d9 <= d10) {
                    d9 += d6;
                    i1 += l1;
                    j3 = k2 - i1;
                } else {
                    d10 += d7;
                    j1 += i2;
                    k3 = l2 - j1;
                }
            }

            return true;
        }
    }

    public void a(boolean flag) {
        this.nodeEvaluator.b(flag);
    }

    public boolean d() {
        return this.nodeEvaluator.d();
    }

    public void b(boolean flag) {
        this.nodeEvaluator.a(flag);
    }

    public boolean e() {
        return this.nodeEvaluator.d();
    }

    @Override
    public boolean a(BlockPosition blockposition) {
        return this.level.getType(blockposition).a((IBlockAccess) this.level, blockposition, (Entity) this.mob);
    }
}
