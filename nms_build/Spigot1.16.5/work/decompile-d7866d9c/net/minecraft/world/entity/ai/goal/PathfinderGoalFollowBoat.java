package net.minecraft.world.entity.ai.goal;

import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.vehicle.EntityBoat;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalFollowBoat extends PathfinderGoal {

    private int a;
    private final EntityCreature b;
    private EntityHuman c;
    private PathfinderGoalBoat d;

    public PathfinderGoalFollowBoat(EntityCreature entitycreature) {
        this.b = entitycreature;
    }

    @Override
    public boolean a() {
        List<EntityBoat> list = this.b.world.a(EntityBoat.class, this.b.getBoundingBox().g(5.0D));
        boolean flag = false;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityBoat entityboat = (EntityBoat) iterator.next();
            Entity entity = entityboat.getRidingPassenger();

            if (entity instanceof EntityHuman && (MathHelper.e(((EntityHuman) entity).aR) > 0.0F || MathHelper.e(((EntityHuman) entity).aT) > 0.0F)) {
                flag = true;
                break;
            }
        }

        return this.c != null && (MathHelper.e(this.c.aR) > 0.0F || MathHelper.e(this.c.aT) > 0.0F) || flag;
    }

    @Override
    public boolean C_() {
        return true;
    }

    @Override
    public boolean b() {
        return this.c != null && this.c.isPassenger() && (MathHelper.e(this.c.aR) > 0.0F || MathHelper.e(this.c.aT) > 0.0F);
    }

    @Override
    public void c() {
        List<EntityBoat> list = this.b.world.a(EntityBoat.class, this.b.getBoundingBox().g(5.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityBoat entityboat = (EntityBoat) iterator.next();

            if (entityboat.getRidingPassenger() != null && entityboat.getRidingPassenger() instanceof EntityHuman) {
                this.c = (EntityHuman) entityboat.getRidingPassenger();
                break;
            }
        }

        this.a = 0;
        this.d = PathfinderGoalBoat.GO_TO_BOAT;
    }

    @Override
    public void d() {
        this.c = null;
    }

    @Override
    public void e() {
        boolean flag = MathHelper.e(this.c.aR) > 0.0F || MathHelper.e(this.c.aT) > 0.0F;
        float f = this.d == PathfinderGoalBoat.GO_IN_BOAT_DIRECTION ? (flag ? 0.01F : 0.0F) : 0.015F;

        this.b.a(f, new Vec3D((double) this.b.aR, (double) this.b.aS, (double) this.b.aT));
        this.b.move(EnumMoveType.SELF, this.b.getMot());
        if (--this.a <= 0) {
            this.a = 10;
            if (this.d == PathfinderGoalBoat.GO_TO_BOAT) {
                BlockPosition blockposition = this.c.getChunkCoordinates().shift(this.c.getDirection().opposite());

                blockposition = blockposition.b(0, -1, 0);
                this.b.getNavigation().a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 1.0D);
                if (this.b.g((Entity) this.c) < 4.0F) {
                    this.a = 0;
                    this.d = PathfinderGoalBoat.GO_IN_BOAT_DIRECTION;
                }
            } else if (this.d == PathfinderGoalBoat.GO_IN_BOAT_DIRECTION) {
                EnumDirection enumdirection = this.c.getAdjustedDirection();
                BlockPosition blockposition1 = this.c.getChunkCoordinates().shift(enumdirection, 10);

                this.b.getNavigation().a((double) blockposition1.getX(), (double) (blockposition1.getY() - 1), (double) blockposition1.getZ(), 1.0D);
                if (this.b.g((Entity) this.c) > 12.0F) {
                    this.a = 0;
                    this.d = PathfinderGoalBoat.GO_TO_BOAT;
                }
            }

        }
    }
}
