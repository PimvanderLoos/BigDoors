package net.minecraft.server;

import javax.annotation.Nullable;

public class PathfinderGoalStrollVillage extends PathfinderGoalRandomStroll {

    public PathfinderGoalStrollVillage(EntityCreature entitycreature, double d0, boolean flag) {
        super(entitycreature, d0, 10, flag);
    }

    @Override
    public boolean a() {
        WorldServer worldserver = (WorldServer) this.a.world;
        BlockPosition blockposition = this.a.getChunkCoordinates();

        return worldserver.a_(blockposition) ? false : super.a();
    }

    @Nullable
    @Override
    protected Vec3D g() {
        WorldServer worldserver = (WorldServer) this.a.world;
        BlockPosition blockposition = this.a.getChunkCoordinates();
        SectionPosition sectionposition = SectionPosition.a(blockposition);
        SectionPosition sectionposition1 = BehaviorUtil.a(worldserver, sectionposition, 2);

        return sectionposition1 != sectionposition ? RandomPositionGenerator.b(this.a, 10, 7, Vec3D.c((BaseBlockPosition) sectionposition1.q())) : null;
    }
}
