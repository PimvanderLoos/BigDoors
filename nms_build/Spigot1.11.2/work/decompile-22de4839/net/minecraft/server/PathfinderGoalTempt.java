package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;

public class PathfinderGoalTempt extends PathfinderGoal {

    private final EntityCreature a;
    private final double b;
    private double c;
    private double d;
    private double e;
    private double f;
    private double g;
    private EntityHuman h;
    private int i;
    private boolean j;
    private final Set<Item> k;
    private final boolean l;

    public PathfinderGoalTempt(EntityCreature entitycreature, double d0, Item item, boolean flag) {
        this(entitycreature, d0, flag, Sets.newHashSet(new Item[] { item}));
    }

    public PathfinderGoalTempt(EntityCreature entitycreature, double d0, boolean flag, Set<Item> set) {
        this.a = entitycreature;
        this.b = d0;
        this.k = set;
        this.l = flag;
        this.a(3);
        if (!(entitycreature.getNavigation() instanceof Navigation)) {
            throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
        }
    }

    public boolean a() {
        if (this.i > 0) {
            --this.i;
            return false;
        } else {
            this.h = this.a.world.findNearbyPlayer(this.a, 10.0D);
            return this.h == null ? false : this.a(this.h.getItemInMainHand()) || this.a(this.h.getItemInOffHand());
        }
    }

    protected boolean a(ItemStack itemstack) {
        return this.k.contains(itemstack.getItem());
    }

    public boolean b() {
        if (this.l) {
            if (this.a.h(this.h) < 36.0D) {
                if (this.h.d(this.c, this.d, this.e) > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs((double) this.h.pitch - this.f) > 5.0D || Math.abs((double) this.h.yaw - this.g) > 5.0D) {
                    return false;
                }
            } else {
                this.c = this.h.locX;
                this.d = this.h.locY;
                this.e = this.h.locZ;
            }

            this.f = (double) this.h.pitch;
            this.g = (double) this.h.yaw;
        }

        return this.a();
    }

    public void c() {
        this.c = this.h.locX;
        this.d = this.h.locY;
        this.e = this.h.locZ;
        this.j = true;
    }

    public void d() {
        this.h = null;
        this.a.getNavigation().o();
        this.i = 100;
        this.j = false;
    }

    public void e() {
        this.a.getControllerLook().a(this.h, (float) (this.a.cL() + 20), (float) this.a.N());
        if (this.a.h(this.h) < 6.25D) {
            this.a.getNavigation().o();
        } else {
            this.a.getNavigation().a((Entity) this.h, this.b);
        }

    }

    public boolean f() {
        return this.j;
    }
}
