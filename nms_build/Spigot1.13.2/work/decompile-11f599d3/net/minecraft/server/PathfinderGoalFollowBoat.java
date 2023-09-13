package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class PathfinderGoalFollowBoat extends PathfinderGoal {

    private int a;
    private final EntityCreature b;
    private EntityLiving c;
    private PathfinderGoalBoat d;

    public PathfinderGoalFollowBoat(EntityCreature entitycreature) {
        this.b = entitycreature;
    }

    public boolean a() {
        List<EntityBoat> list = this.b.world.a(EntityBoat.class, this.b.getBoundingBox().g(5.0D));
        boolean flag = false;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityBoat entityboat = (EntityBoat) iterator.next();

            if (entityboat.bO() != null && (MathHelper.e(((EntityLiving) entityboat.bO()).bh) > 0.0F || MathHelper.e(((EntityLiving) entityboat.bO()).bj) > 0.0F)) {
                flag = true;
                break;
            }
        }

        return this.c != null && (MathHelper.e(this.c.bh) > 0.0F || MathHelper.e(this.c.bj) > 0.0F) || flag;
    }

    public boolean f() {
        return true;
    }

    public boolean b() {
        return this.c != null && this.c.isPassenger() && (MathHelper.e(this.c.bh) > 0.0F || MathHelper.e(this.c.bj) > 0.0F);
    }

    public void c() {
        List<EntityBoat> list = this.b.world.a(EntityBoat.class, this.b.getBoundingBox().g(5.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityBoat entityboat = (EntityBoat) iterator.next();

            if (entityboat.bO() != null && entityboat.bO() instanceof EntityLiving) {
                this.c = (EntityLiving) entityboat.bO();
                break;
            }
        }

        this.a = 0;
        this.d = PathfinderGoalBoat.GO_TO_BOAT;
    }

    public void d() {
        this.c = null;
    }

    public void e() {
        boolean flag = MathHelper.e(this.c.bh) > 0.0F || MathHelper.e(this.c.bj) > 0.0F;
        float f = this.d == PathfinderGoalBoat.GO_IN_BOAT_DIRECTION ? (flag ? 0.17999999F : 0.0F) : 0.135F;

        this.b.a(this.b.bh, this.b.bi, this.b.bj, f);
        this.b.move(EnumMoveType.SELF, this.b.motX, this.b.motY, this.b.motZ);
        if (--this.a <= 0) {
            this.a = 10;
            if (this.d == PathfinderGoalBoat.GO_TO_BOAT) {
                BlockPosition blockposition = (new BlockPosition(this.c)).shift(this.c.getDirection().opposite());

                blockposition = blockposition.a(0, -1, 0);
                this.b.getNavigation().a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
                if (this.b.g(this.c) < 4.0F) {
                    this.a = 0;
                    this.d = PathfinderGoalBoat.GO_IN_BOAT_DIRECTION;
                }
            } else if (this.d == PathfinderGoalBoat.GO_IN_BOAT_DIRECTION) {
                EnumDirection enumdirection = this.c.getAdjustedDirection();
                BlockPosition blockposition1 = (new BlockPosition(this.c)).shift(enumdirection, 10);

                this.b.getNavigation().a((double) blockposition1.getX(), (double) (blockposition1.getY() - 1), (double) blockposition1.getZ(), 1.0D);
                if (this.b.g(this.c) > 12.0F) {
                    this.a = 0;
                    this.d = PathfinderGoalBoat.GO_TO_BOAT;
                }
            }

        }
    }
}
