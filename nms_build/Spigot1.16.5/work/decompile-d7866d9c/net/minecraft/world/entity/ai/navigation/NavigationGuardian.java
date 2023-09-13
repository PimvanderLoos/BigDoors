package net.minecraft.world.entity.ai.navigation;

import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.animal.EntityDolphin;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderWater;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class NavigationGuardian extends NavigationAbstract {

    private boolean p;

    public NavigationGuardian(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    protected Pathfinder a(int i) {
        this.p = this.a instanceof EntityDolphin;
        this.o = new PathfinderWater(this.p);
        return new Pathfinder(this.o, i);
    }

    @Override
    protected boolean a() {
        return this.p || this.p();
    }

    @Override
    protected Vec3D b() {
        return new Vec3D(this.a.locX(), this.a.e(0.5D), this.a.locZ());
    }

    @Override
    public void c() {
        ++this.e;
        if (this.m) {
            this.j();
        }

        if (!this.m()) {
            Vec3D vec3d;

            if (this.a()) {
                this.l();
            } else if (this.c != null && !this.c.c()) {
                vec3d = this.c.a((Entity) this.a);
                if (MathHelper.floor(this.a.locX()) == MathHelper.floor(vec3d.x) && MathHelper.floor(this.a.locY()) == MathHelper.floor(vec3d.y) && MathHelper.floor(this.a.locZ()) == MathHelper.floor(vec3d.z)) {
                    this.c.a();
                }
            }

            PacketDebug.a(this.b, this.a, this.c, this.l);
            if (!this.m()) {
                vec3d = this.c.a((Entity) this.a);
                this.a.getControllerMove().a(vec3d.x, vec3d.y, vec3d.z, this.d);
            }
        }
    }

    @Override
    protected void l() {
        if (this.c != null) {
            Vec3D vec3d = this.b();
            float f = this.a.getWidth();
            float f1 = f > 0.75F ? f / 2.0F : 0.75F - f / 2.0F;
            Vec3D vec3d1 = this.a.getMot();

            if (Math.abs(vec3d1.x) > 0.2D || Math.abs(vec3d1.z) > 0.2D) {
                f1 = (float) ((double) f1 * vec3d1.f() * 6.0D);
            }

            boolean flag = true;
            Vec3D vec3d2 = Vec3D.c((BaseBlockPosition) this.c.g());

            if (Math.abs(this.a.locX() - vec3d2.x) < (double) f1 && Math.abs(this.a.locZ() - vec3d2.z) < (double) f1 && Math.abs(this.a.locY() - vec3d2.y) < (double) (f1 * 2.0F)) {
                this.c.a();
            }

            for (int i = Math.min(this.c.f() + 6, this.c.e() - 1); i > this.c.f(); --i) {
                vec3d2 = this.c.a(this.a, i);
                if (vec3d2.distanceSquared(vec3d) <= 36.0D && this.a(vec3d, vec3d2, 0, 0, 0)) {
                    this.c.c(i);
                    break;
                }
            }

            this.a(vec3d);
        }
    }

    @Override
    protected void a(Vec3D vec3d) {
        if (this.e - this.f > 100) {
            if (vec3d.distanceSquared(this.g) < 2.25D) {
                this.o();
            }

            this.f = this.e;
            this.g = vec3d;
        }

        if (this.c != null && !this.c.c()) {
            BlockPosition blockposition = this.c.g();

            if (blockposition.equals(this.h)) {
                this.i += SystemUtils.getMonotonicMillis() - this.j;
            } else {
                this.h = blockposition;
                double d0 = vec3d.f(Vec3D.a(this.h));

                this.k = this.a.dN() > 0.0F ? d0 / (double) this.a.dN() * 100.0D : 0.0D;
            }

            if (this.k > 0.0D && (double) this.i > this.k * 2.0D) {
                this.h = BaseBlockPosition.ZERO;
                this.i = 0L;
                this.k = 0.0D;
                this.o();
            }

            this.j = SystemUtils.getMonotonicMillis();
        }

    }

    @Override
    protected boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k) {
        Vec3D vec3d2 = new Vec3D(vec3d1.x, vec3d1.y + (double) this.a.getHeight() * 0.5D, vec3d1.z);

        return this.b.rayTrace(new RayTrace(vec3d, vec3d2, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this.a)).getType() == MovingObjectPosition.EnumMovingObjectType.MISS;
    }

    @Override
    public boolean a(BlockPosition blockposition) {
        return !this.b.getType(blockposition).i(this.b, blockposition);
    }

    @Override
    public void d(boolean flag) {}
}
