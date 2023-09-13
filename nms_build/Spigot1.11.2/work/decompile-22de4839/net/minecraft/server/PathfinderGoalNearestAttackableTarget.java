package net.minecraft.server;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;

public class PathfinderGoalNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalTarget {

    protected final Class<T> a;
    private final int i;
    protected final PathfinderGoalNearestAttackableTarget.DistanceComparator b;
    protected final Predicate<? super T> c;
    protected T d;

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, boolean flag) {
        this(entitycreature, oclass, flag, false);
    }

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, boolean flag, boolean flag1) {
        this(entitycreature, oclass, 10, flag, flag1, (Predicate) null);
    }

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, int i, boolean flag, boolean flag1, @Nullable final Predicate<? super T> predicate) {
        super(entitycreature, flag, flag1);
        this.a = oclass;
        this.i = i;
        this.b = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entitycreature);
        this.a(1);
        this.c = new Predicate() {
            public boolean a(@Nullable T t0) {
                return t0 == null ? false : (predicate != null && !predicate.apply(t0) ? false : (!IEntitySelector.e.apply(t0) ? false : PathfinderGoalNearestAttackableTarget.this.a(t0, false)));
            }

            public boolean apply(@Nullable Object object) {
                return this.a((EntityLiving) object);
            }
        };
    }

    public boolean a() {
        if (this.i > 0 && this.e.getRandom().nextInt(this.i) != 0) {
            return false;
        } else if (this.a != EntityHuman.class && this.a != EntityPlayer.class) {
            List list = this.e.world.a(this.a, this.a(this.i()), this.c);

            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.b);
                this.d = (EntityLiving) list.get(0);
                return true;
            }
        } else {
            this.d = this.e.world.a(this.e.locX, this.e.locY + (double) this.e.getHeadHeight(), this.e.locZ, this.i(), this.i(), new Function() {
                @Nullable
                public Double a(@Nullable EntityHuman entityhuman) {
                    ItemStack itemstack = entityhuman.getEquipment(EnumItemSlot.HEAD);

                    if (itemstack.getItem() == Items.SKULL) {
                        int i = itemstack.i();
                        boolean flag = PathfinderGoalNearestAttackableTarget.this.e instanceof EntitySkeleton && i == 0;
                        boolean flag1 = PathfinderGoalNearestAttackableTarget.this.e instanceof EntityZombie && i == 2;
                        boolean flag2 = PathfinderGoalNearestAttackableTarget.this.e instanceof EntityCreeper && i == 4;

                        if (flag || flag1 || flag2) {
                            return Double.valueOf(0.5D);
                        }
                    }

                    return Double.valueOf(1.0D);
                }

                @Nullable
                public Object apply(@Nullable Object object) {
                    return this.a((EntityHuman) object);
                }
            }, this.c);
            return this.d != null;
        }
    }

    protected AxisAlignedBB a(double d0) {
        return this.e.getBoundingBox().grow(d0, 4.0D, d0);
    }

    public void c() {
        this.e.setGoalTarget(this.d);
        super.c();
    }

    public static class DistanceComparator implements Comparator<Entity> {

        private final Entity a;

        public DistanceComparator(Entity entity) {
            this.a = entity;
        }

        public int a(Entity entity, Entity entity1) {
            double d0 = this.a.h(entity);
            double d1 = this.a.h(entity1);

            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }

        public int compare(Object object, Object object1) {
            return this.a((Entity) object, (Entity) object1);
        }
    }
}
