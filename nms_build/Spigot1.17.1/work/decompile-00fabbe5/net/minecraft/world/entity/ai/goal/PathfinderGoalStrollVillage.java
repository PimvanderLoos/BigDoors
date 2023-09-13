package net.minecraft.world.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
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
    public boolean a() {
        WorldServer worldserver = (WorldServer) this.mob.level;
        BlockPosition blockposition = this.mob.getChunkCoordinates();

        return worldserver.b(blockposition) ? false : super.a();
    }

    @Nullable
    @Override
    protected Vec3D g() {
        WorldServer worldserver = (WorldServer) this.mob.level;
        BlockPosition blockposition = this.mob.getChunkCoordinates();
        SectionPosition sectionposition = SectionPosition.a(blockposition);
        SectionPosition sectionposition1 = BehaviorUtil.a(worldserver, sectionposition, 2);

        return sectionposition1 != sectionposition ? DefaultRandomPos.a(this.mob, 10, 7, Vec3D.c((BaseBlockPosition) sectionposition1.q()), 1.5707963705062866D) : null;
    }
}
