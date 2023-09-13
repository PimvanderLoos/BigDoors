package net.minecraft.server;

public class NavigationSpider extends Navigation {

    private BlockPosition f;

    public NavigationSpider(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    public PathEntity a(BlockPosition blockposition) {
        this.f = blockposition;
        return super.a(blockposition);
    }

    public PathEntity a(Entity entity) {
        this.f = new BlockPosition(entity);
        return super.a(entity);
    }

    public boolean a(Entity entity, double d0) {
        PathEntity pathentity = this.a(entity);

        if (pathentity != null) {
            return this.a(pathentity, d0);
        } else {
            this.f = new BlockPosition(entity);
            this.d = d0;
            return true;
        }
    }

    public void l() {
        if (!this.n()) {
            super.l();
        } else {
            if (this.f != null) {
                double d0 = (double) (this.a.width * this.a.width);

                if (this.a.d(this.f) >= d0 && (this.a.locY <= (double) this.f.getY() || this.a.d(new BlockPosition(this.f.getX(), MathHelper.floor(this.a.locY), this.f.getZ())) >= d0)) {
                    this.a.getControllerMove().a((double) this.f.getX(), (double) this.f.getY(), (double) this.f.getZ(), this.d);
                } else {
                    this.f = null;
                }
            }

        }
    }
}
