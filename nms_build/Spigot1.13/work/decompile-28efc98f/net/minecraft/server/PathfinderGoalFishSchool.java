package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class PathfinderGoalFishSchool extends PathfinderGoal {

    private final EntityFish a;
    private EntityFish b;
    private int c;

    public PathfinderGoalFishSchool(EntityFish entityfish) {
        this.a = entityfish;
    }

    public boolean a() {
        if (!this.a.dC() && !this.a.dA()) {
            List list = this.a.world.a(this.a.getClass(), this.a.getBoundingBox().grow(8.0D, 8.0D, 8.0D));

            if (list.size() <= 1) {
                return false;
            } else {
                Iterator iterator = list.iterator();

                EntityFish entityfish;

                do {
                    if (!iterator.hasNext()) {
                        iterator = list.iterator();

                        do {
                            if (!iterator.hasNext()) {
                                return false;
                            }

                            entityfish = (EntityFish) iterator.next();
                        } while (entityfish.equals(this.a) || entityfish.dA() || entityfish.dC());

                        entityfish.t(true);
                        ++entityfish.a;
                        this.b = entityfish;
                        return true;
                    }

                    entityfish = (EntityFish) iterator.next();
                } while (!entityfish.l() || entityfish.equals(this.a));

                ++entityfish.a;
                this.b = entityfish;
                return true;
            }
        } else {
            return false;
        }
    }

    public boolean b() {
        if (this.b.isAlive() && this.b.dC()) {
            double d0 = this.a.h(this.b);

            return d0 <= 121.0D;
        } else {
            return false;
        }
    }

    public void c() {
        this.a.a(true);
        this.c = 0;
    }

    public void d() {
        this.a.a(false);
        --this.b.a;
        this.b = null;
    }

    public void e() {
        if (--this.c <= 0) {
            this.c = 10;
            this.a.getNavigation().a((Entity) this.b, 1.0D);
        }
    }
}
