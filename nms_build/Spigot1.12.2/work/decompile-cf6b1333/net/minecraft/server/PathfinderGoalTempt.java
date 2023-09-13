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
    private EntityHuman target;
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
            this.target = this.a.world.findNearbyPlayer(this.a, 10.0D);
            return this.target == null ? false : this.a(this.target.getItemInMainHand()) || this.a(this.target.getItemInOffHand());
        }
    }

    protected boolean a(ItemStack itemstack) {
        return this.k.contains(itemstack.getItem());
    }

    public boolean b() {
        if (this.l) {
            if (this.a.h(this.target) < 36.0D) {
                if (this.target.d(this.c, this.d, this.e) > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs((double) this.target.pitch - this.f) > 5.0D || Math.abs((double) this.target.yaw - this.g) > 5.0D) {
                    return false;
                }
            } else {
                this.c = this.target.locX;
                this.d = this.target.locY;
                this.e = this.target.locZ;
            }

            this.f = (double) this.target.pitch;
            this.g = (double) this.target.yaw;
        }

        return this.a();
    }

    public void c() {
        this.c = this.target.locX;
        this.d = this.target.locY;
        this.e = this.target.locZ;
        this.j = true;
    }

    public void d() {
        this.target = null;
        this.a.getNavigation().p();
        this.i = 100;
        this.j = false;
    }

    public void e() {
        this.a.getControllerLook().a(this.target, (float) (this.a.O() + 20), (float) this.a.N());
        if (this.a.h(this.target) < 6.25D) {
            this.a.getNavigation().p();
        } else {
            this.a.getNavigation().a((Entity) this.target, this.b);
        }

    }

    public boolean f() {
        return this.j;
    }
}
