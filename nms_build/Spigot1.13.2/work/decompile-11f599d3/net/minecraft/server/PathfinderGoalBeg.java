package net.minecraft.server;

public class PathfinderGoalBeg extends PathfinderGoal {

    private final EntityWolf a;
    private EntityHuman b;
    private final IWorldReader c;
    private final float d;
    private int e;

    public PathfinderGoalBeg(EntityWolf entitywolf, float f) {
        this.a = entitywolf;
        this.c = entitywolf.world;
        this.d = f;
        this.a(2);
    }

    public boolean a() {
        this.b = this.c.findNearbyPlayer(this.a, (double) this.d);
        return this.b == null ? false : this.a(this.b);
    }

    public boolean b() {
        return !this.b.isAlive() ? false : (this.a.h(this.b) > (double) (this.d * this.d) ? false : this.e > 0 && this.a(this.b));
    }

    public void c() {
        this.a.w(true);
        this.e = 40 + this.a.getRandom().nextInt(40);
    }

    public void d() {
        this.a.w(false);
        this.b = null;
    }

    public void e() {
        this.a.getControllerLook().a(this.b.locX, this.b.locY + (double) this.b.getHeadHeight(), this.b.locZ, 10.0F, (float) this.a.K());
        --this.e;
    }

    private boolean a(EntityHuman entityhuman) {
        EnumHand[] aenumhand = EnumHand.values();
        int i = aenumhand.length;

        for (int j = 0; j < i; ++j) {
            EnumHand enumhand = aenumhand[j];
            ItemStack itemstack = entityhuman.b(enumhand);

            if (this.a.isTamed() && itemstack.getItem() == Items.BONE) {
                return true;
            }

            if (this.a.f(itemstack)) {
                return true;
            }
        }

        return false;
    }
}
