package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.SectionPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.ai.behavior.BehaviorUtil;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3D;

public class PathfinderGoalStrollVillage extends PathfinderGoalRandomStroll {

    private static final int MAX_XZ_DIST = 10;
    private static final int MAX_Y_DIST = 7;

    public PathfinderGoalStrollVillage(EntityCreature entitycreature, double d0, boolean flag) {
        super(entitycreature, d0, 10, flag);
    }

    @Override
    public boolean canUse() {
        WorldServer worldserver = (WorldServer) this.mob.level;
        BlockPosition blockposition = this.mob.blockPosition();

        return worldserver.isVillage(blockposition) ? false : super.canUse();
    }

    @Nullable
    @Override
    protected Vec3D getPosition() {
        WorldServer worldserver = (WorldServer) this.mob.level;
        BlockPosition blockposition = this.mob.blockPosition();
        SectionPosition sectionposition = SectionPosition.of(blockposition);
        SectionPosition sectionposition1 = BehaviorUtil.findSectionClosestToVillage(worldserver, sectionposition, 2);

        return sectionposition1 != sectionposition ? DefaultRandomPos.getPosTowards(this.mob, 10, 7, Vec3D.atBottomCenterOf(sectionposition1.center()), 1.5707963705062866D) : null;
    }
}
