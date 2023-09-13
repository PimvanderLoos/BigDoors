package net.minecraft.server;

public class NavigationGuardian extends NavigationAbstract {

    public NavigationGuardian(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    protected Pathfinder a() {
        return new Pathfinder(new PathfinderWater());
    }

    protected boolean b() {
        return this.q();
    }

    protected Vec3D c() {
        return new Vec3D(this.a.locX, this.a.locY + (double) this.a.length * 0.5D, this.a.locZ);
    }

    protected void n() {
        Vec3D vec3d = this.c();
        float f = this.a.width * this.a.width;
        boolean flag = true;

        if (vec3d.distanceSquared(this.c.a(this.a, this.c.e())) < (double) f) {
            this.c.a();
        }

        for (int i = Math.min(this.c.e() + 6, this.c.d() - 1); i > this.c.e(); --i) {
            Vec3D vec3d1 = this.c.a(this.a, i);

            if (vec3d1.distanceSquared(vec3d) <= 36.0D && this.a(vec3d, vec3d1, 0, 0, 0)) {
                this.c.c(i);
                break;
            }
        }

        this.a(vec3d);
    }

    protected boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k) {
        MovingObjectPosition movingobjectposition = this.b.rayTrace(vec3d, new Vec3D(vec3d1.x, vec3d1.y + (double) this.a.length * 0.5D, vec3d1.z), false, true, false);

        return movingobjectposition == null || movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.MISS;
    }

    public boolean a(BlockPosition blockposition) {
        return !this.b.getType(blockposition).b();
    }
}
