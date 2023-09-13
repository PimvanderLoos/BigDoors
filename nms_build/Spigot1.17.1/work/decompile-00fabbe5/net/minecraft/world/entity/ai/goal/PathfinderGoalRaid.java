package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.entity.raid.PersistentRaid;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRaid<T extends EntityRaider> extends PathfinderGoal {

    private static final float SPEED_MODIFIER = 1.0F;
    private final T mob;

    public PathfinderGoalRaid(T t0) {
        this.mob = t0;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean a() {
        return this.mob.getGoalTarget() == null && !this.mob.isVehicle() && this.mob.fL() && !this.mob.fK().a() && !((WorldServer) this.mob.level).b(this.mob.getChunkCoordinates());
    }

    @Override
    public boolean b() {
        return this.mob.fL() && !this.mob.fK().a() && this.mob.level instanceof WorldServer && !((WorldServer) this.mob.level).b(this.mob.getChunkCoordinates());
    }

    @Override
    public void e() {
        if (this.mob.fL()) {
            Raid raid = this.mob.fK();

            if (this.mob.tickCount % 20 == 0) {
                this.a(raid);
            }

            if (!this.mob.fu()) {
                Vec3D vec3d = DefaultRandomPos.a(this.mob, 15, 4, Vec3D.c((BaseBlockPosition) raid.getCenter()), 1.5707963705062866D);

                if (vec3d != null) {
                    this.mob.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }
        }

    }

    private void a(Raid raid) {
        if (raid.v()) {
            Set<EntityRaider> set = Sets.newHashSet();
            List<EntityRaider> list = this.mob.level.a(EntityRaider.class, this.mob.getBoundingBox().g(16.0D), (entityraider) -> {
                return !entityraider.fL() && PersistentRaid.a(entityraider, raid);
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
