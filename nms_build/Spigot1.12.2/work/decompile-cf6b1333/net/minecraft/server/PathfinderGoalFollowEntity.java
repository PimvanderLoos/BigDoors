package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class PathfinderGoalFollowEntity extends PathfinderGoal {

    private final EntityInsentient a;
    private final Predicate<EntityInsentient> b;
    private EntityInsentient c;
    private final double d;
    private final NavigationAbstract e;
    private int f;
    private final float g;
    private float h;
    private final float i;

    public PathfinderGoalFollowEntity(final EntityInsentient entityinsentient, double d0, float f, float f1) {
        this.a = entityinsentient;
        this.b = new Predicate() {
            public boolean a(@Nullable EntityInsentient entityinsentient) {
                return entityinsentient != null && entityinsentient1.getClass() != entityinsentient.getClass();
            }

            public boolean apply(@Nullable Object object) {
                return this.a((EntityInsentient) object);
            }
        };
        this.d = d0;
        this.e = entityinsentient.getNavigation();
        this.g = f;
        this.i = f1;
        this.a(3);
        if (!(entityinsentient.getNavigation() instanceof Navigation) && !(entityinsentient.getNavigation() instanceof NavigationFlying)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
        }
    }

    public boolean a() {
        List list = this.a.world.a(EntityInsentient.class, this.a.getBoundingBox().g((double) this.i), this.b);

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();

                if (!entityinsentient.isInvisible()) {
                    this.c = entityinsentient;
                    return true;
                }
            }
        }

        return false;
    }

    public boolean b() {
        return this.c != null && !this.e.o() && this.a.h(this.c) > (double) (this.g * this.g);
    }

    public void c() {
        this.f = 0;
        this.h = this.a.a(PathType.WATER);
        this.a.a(PathType.WATER, 0.0F);
    }

    public void d() {
        this.c = null;
        this.e.p();
        this.a.a(PathType.WATER, this.h);
    }

    public void e() {
        if (this.c != null && !this.a.isLeashed()) {
            this.a.getControllerLook().a(this.c, 10.0F, (float) this.a.N());
            if (--this.f <= 0) {
                this.f = 10;
                double d0 = this.a.locX - this.c.locX;
                double d1 = this.a.locY - this.c.locY;
                double d2 = this.a.locZ - this.c.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > (double) (this.g * this.g)) {
                    this.e.a((Entity) this.c, this.d);
                } else {
                    this.e.p();
                    ControllerLook controllerlook = this.c.getControllerLook();

                    if (d3 <= (double) this.g || controllerlook.e() == this.a.locX && controllerlook.f() == this.a.locY && controllerlook.g() == this.a.locZ) {
                        double d4 = this.c.locX - this.a.locX;
                        double d5 = this.c.locZ - this.a.locZ;

                        this.e.a(this.a.locX - d4, this.a.locY, this.a.locZ - d5, this.d);
                    }

                }
            }
        }
    }
}
