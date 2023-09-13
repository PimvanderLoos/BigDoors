package net.minecraft.server;

public class NavigationSpider extends Navigation {

    private BlockPosition i;

    public NavigationSpider(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    public PathEntity b(BlockPosition blockposition) {
        this.i = blockposition;
        return super.b(blockposition);
    }

    public PathEntity a(Entity entity) {
        this.i = new BlockPosition(entity);
        return super.a(entity);
    }

    public boolean a(Entity entity, double d0) {
        PathEntity pathentity = this.a(entity);

        if (pathentity != null) {
            return this.a(pathentity, d0);
        } else {
            this.i = new BlockPosition(entity);
            this.d = d0;
            return true;
        }
    }

    public void d() {
        if (!this.o()) {
            super.d();
        } else {
            if (this.i != null) {
                double d0 = (double) (this.a.width * this.a.width);

                if (this.a.d(this.i) >= d0 && (this.a.locY <= (double) this.i.getY() || this.a.d(new BlockPosition(this.i.getX(), MathHelper.floor(this.a.locY), this.i.getZ())) >= d0)) {
                    this.a.getControllerMove().a((double) this.i.getX(), (double) this.i.getY(), (double) this.i.getZ(), this.d);
                } else {
                    this.i = null;
                }
            }

        }
    }
}
