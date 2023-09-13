package net.minecraft.server;

public class NavigationSpider extends Navigation {

    private BlockPosition p;

    public NavigationSpider(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    public PathEntity b(BlockPosition blockposition) {
        this.p = blockposition;
        return super.b(blockposition);
    }

    public PathEntity a(Entity entity) {
        this.p = new BlockPosition(entity);
        return super.a(entity);
    }

    public boolean a(Entity entity, double d0) {
        PathEntity pathentity = this.a(entity);

        if (pathentity != null) {
            return this.a(pathentity, d0);
        } else {
            this.p = new BlockPosition(entity);
            this.d = d0;
            return true;
        }
    }

    public void d() {
        if (!this.p()) {
            super.d();
        } else {
            if (this.p != null) {
                double d0 = (double) (this.a.width * this.a.width);

                if (this.a.d(this.p) >= d0 && (this.a.locY <= (double) this.p.getY() || this.a.d(new BlockPosition(this.p.getX(), MathHelper.floor(this.a.locY), this.p.getZ())) >= d0)) {
                    this.a.getControllerMove().a((double) this.p.getX(), (double) this.p.getY(), (double) this.p.getZ(), this.d);
                } else {
                    this.p = null;
                }
            }

        }
    }
}
