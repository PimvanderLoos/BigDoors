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
        return this.a.getGoalTarget() == null && !this.a.isVehicle() && this.a.fc() && !this.a.fb().a() && !((WorldServer) this.a.world).b_(this.a.getChunkCoordinates());
    }

    @Override
    public boolean b() {
        return this.a.fc() && !this.a.fb().a() && this.a.world instanceof WorldServer && !((WorldServer) this.a.world).b_(this.a.getChunkCoordinates());
    }

    @Override
    public void e() {
        if (this.a.fc()) {
            Raid raid = this.a.fb();

            if (this.a.ticksLived % 20 == 0) {
                this.a(raid);
            }

            if (!this.a.eJ()) {
                Vec3D vec3d = RandomPositionGenerator.b(this.a, 15, 4, Vec3D.c((BaseBlockPosition) raid.getCenter()));

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
                return !entityraider.fc() && PersistentRaid.a(entityraider, raid);
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
