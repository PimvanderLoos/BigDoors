package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.raid.EntityRaider;
import net.minecraft.world.entity.raid.PersistentRaid;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalRaid<T extends EntityRaider> extends PathfinderGoal {

    private static final int RECRUITMENT_SEARCH_TICK_DELAY = 20;
    private static final float SPEED_MODIFIER = 1.0F;
    private final T mob;
    private int recruitmentTick;

    public PathfinderGoalRaid(T t0) {
        this.mob = t0;
        this.setFlags(EnumSet.of(PathfinderGoal.Type.MOVE));
    }

    @Override
    public boolean canUse() {
        return this.mob.getTarget() == null && !this.mob.isVehicle() && this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && !((WorldServer) this.mob.level).isVillage(this.mob.blockPosition());
    }

    @Override
    public boolean canContinueToUse() {
        return this.mob.hasActiveRaid() && !this.mob.getCurrentRaid().isOver() && this.mob.level instanceof WorldServer && !((WorldServer) this.mob.level).isVillage(this.mob.blockPosition());
    }

    @Override
    public void tick() {
        if (this.mob.hasActiveRaid()) {
            Raid raid = this.mob.getCurrentRaid();

            if (this.mob.tickCount > this.recruitmentTick) {
                this.recruitmentTick = this.mob.tickCount + 20;
                this.recruitNearby(raid);
            }

            if (!this.mob.isPathFinding()) {
                Vec3D vec3d = DefaultRandomPos.getPosTowards(this.mob, 15, 4, Vec3D.atBottomCenterOf(raid.getCenter()), 1.5707963705062866D);

                if (vec3d != null) {
                    this.mob.getNavigation().moveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
                }
            }
        }

    }

    private void recruitNearby(Raid raid) {
        if (raid.isActive()) {
            Set<EntityRaider> set = Sets.newHashSet();
            List<EntityRaider> list = this.mob.level.getEntitiesOfClass(EntityRaider.class, this.mob.getBoundingBox().inflate(16.0D), (entityraider) -> {
                return !entityraider.hasActiveRaid() && PersistentRaid.canJoinRaid(entityraider, raid);
            });

            set.addAll(list);
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                EntityRaider entityraider = (EntityRaider) iterator.next();

                raid.joinRaid(raid.getGroupsSpawned(), entityraider, (BlockPosition) null, true);
            }
        }

    }
}
