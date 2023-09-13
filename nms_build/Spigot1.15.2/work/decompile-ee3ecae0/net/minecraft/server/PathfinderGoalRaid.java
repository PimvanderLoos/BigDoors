package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PathfinderGoalRaid<T extends EntityRaider> extends PathfinderGoal {

    private final T a;

    public PathfinderGoalRaid(T t0) {
        this.a = t0;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        return this.a.getGoalTarget() == null && !this.a.isVehicle() && this.a.eF() && !this.a.eE().a() && !((WorldServer) this.a.world).b_(new BlockPosition(this.a));
    }

    @Override
    public boolean b() {
        return this.a.eF() && !this.a.eE().a() && this.a.world instanceof WorldServer && !((WorldServer) this.a.world).b_(new BlockPosition(this.a));
    }

    @Override
    public void e() {
        if (this.a.eF()) {
            Raid raid = this.a.eE();

            if (this.a.ticksLived % 20 == 0) {
                this.a(raid);
            }

            if (!this.a.eo()) {
                Vec3D vec3d = RandomPositionGenerator.a((EntityCreature) this.a, 15, 4, new Vec3D(raid.getCenter()));

                if (vec3d != null) {
                    this.a.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }
        }

    }

    private void a(Raid raid) {
        if (raid.v()) {
            Set<EntityRaider> set = Sets.newHashSet();
            List<EntityRaider> list = this.a.world.a(EntityRaider.class, this.a.getBoundingBox().g(16.0D), (entityraider) -> {
                return !entityraider.eF() && PersistentRaid.a(entityraider, raid);
            });

            set.addAll(list);
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                EntityRaider entityraider = (EntityRaider) iterator.next();

                raid.a(raid.getGroupsSpawned(), entityraider, (BlockPosition) null, true);
            }
        }

    }
}
