package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class PathfinderGoalMoveThroughVillage extends PathfinderGoal {

    private final EntityCreature a;
    private final double b;
    private PathEntity c;
    private VillageDoor d;
    private final boolean e;
    private final List<VillageDoor> f = Lists.newArrayList();

    public PathfinderGoalMoveThroughVillage(EntityCreature entitycreature, double d0, boolean flag) {
        this.a = entitycreature;
        this.b = d0;
        this.e = flag;
        this.a(1);
        if (!(entitycreature.getNavigation() instanceof Navigation)) {
            throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
        }
    }

    public boolean a() {
        this.g();
        if (this.e && this.a.world.L()) {
            return false;
        } else {
            Village village = this.a.world.af().getClosestVillage(new BlockPosition(this.a), 0);

            if (village == null) {
                return false;
            } else {
                this.d = this.a(village);
                if (this.d == null) {
                    return false;
                } else {
                    Navigation navigation = (Navigation) this.a.getNavigation();
                    boolean flag = navigation.g();

                    navigation.a(false);
                    this.c = navigation.b(this.d.d());
                    navigation.a(flag);
                    if (this.c != null) {
                        return true;
                    } else {
                        Vec3D vec3d = RandomPositionGenerator.a(this.a, 10, 7, new Vec3D((double) this.d.d().getX(), (double) this.d.d().getY(), (double) this.d.d().getZ()));

                        if (vec3d == null) {
                            return false;
                        } else {
                            navigation.a(false);
                            this.c = this.a.getNavigation().a(vec3d.x, vec3d.y, vec3d.z);
                            navigation.a(flag);
                            return this.c != null;
                        }
                    }
                }
            }
        }
    }

    public boolean b() {
        if (this.a.getNavigation().p()) {
            return false;
        } else {
            float f = this.a.width + 4.0F;

            return this.a.c(this.d.d()) > (double) (f * f);
        }
    }

    public void c() {
        this.a.getNavigation().a(this.c, this.b);
    }

    public void d() {
        if (this.a.getNavigation().p() || this.a.c(this.d.d()) < 16.0D) {
            this.f.add(this.d);
        }

    }

    private VillageDoor a(Village village) {
        VillageDoor villagedoor = null;
        int i = Integer.MAX_VALUE;
        List<VillageDoor> list = village.f();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            VillageDoor villagedoor1 = (VillageDoor) iterator.next();
            int j = villagedoor1.b(MathHelper.floor(this.a.locX), MathHelper.floor(this.a.locY), MathHelper.floor(this.a.locZ));

            if (j < i && !this.a(villagedoor1)) {
                villagedoor = villagedoor1;
                i = j;
            }
        }

        return villagedoor;
    }

    private boolean a(VillageDoor villagedoor) {
        Iterator iterator = this.f.iterator();

        VillageDoor villagedoor1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            villagedoor1 = (VillageDoor) iterator.next();
        } while (!villagedoor.d().equals(villagedoor1.d()));

        return true;
    }

    private void g() {
        if (this.f.size() > 15) {
            this.f.remove(0);
        }

    }
}
