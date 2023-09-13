package net.minecraft.world.entity.ai.navigation;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.Pathfinder;
import net.minecraft.world.level.pathfinder.PathfinderFlying;
import net.minecraft.world.phys.Vec3D;

public class NavigationFlying extends NavigationAbstract {

    public NavigationFlying(EntityInsentient entityinsentient, World world) {
        super(entityinsentient, world);
    }

    @Override
    protected Pathfinder createPathFinder(int i) {
        this.nodeEvaluator = new PathfinderFlying();
        this.nodeEvaluator.setCanPassDoors(true);
        return new Pathfinder(this.nodeEvaluator, i);
    }

    @Override
    protected boolean canMoveDirectly(Vec3D vec3d, Vec3D vec3d1) {
        return isClearForMovementBetween(this.mob, vec3d, vec3d1, true);
    }

    @Override
    protected boolean canUpdatePath() {
        return this.canFloat() && this.isInLiquid() || !this.mob.isPassenger();
    }

    @Override
    protected Vec3D getTempMobPos() {
        return this.mob.position();
    }

    @Override
    public PathEntity createPath(Entity entity, int i) {
        return this.createPath(entity.blockPosition(), i);
    }

    @Override
    public void tick() {
        ++this.tick;
        if (this.hasDelayedRecomputation) {
            this.recomputePath();
        }

        if (!this.isDone()) {
            Vec3D vec3d;

            if (this.canUpdatePath()) {
                this.followThePath();
            } else if (this.path != null && !this.path.isDone()) {
                vec3d = this.path.getNextEntityPos(this.mob);
                if (this.mob.getBlockX() == MathHelper.floor(vec3d.x) && this.mob.getBlockY() == MathHelper.floor(vec3d.y) && this.mob.getBlockZ() == MathHelper.floor(vec3d.z)) {
                    this.path.advance();
                }
            }

            PacketDebug.sendPathFindingPacket(this.level, this.mob, this.path, this.maxDistanceToWaypoint);
            if (!this.isDone()) {
                vec3d = this.path.getNextEntityPos(this.mob);
                this.mob.getMoveControl().setWantedPosition(vec3d.x, vec3d.y, vec3d.z, this.speedModifier);
            }
        }
    }

    public void setCanOpenDoors(boolean flag) {
        this.nodeEvaluator.setCanOpenDoors(flag);
    }

    public boolean canPassDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    public void setCanPassDoors(boolean flag) {
        this.nodeEvaluator.setCanPassDoors(flag);
    }

    public boolean canOpenDoors() {
        return this.nodeEvaluator.canPassDoors();
    }

    @Override
    public boolean isStableDestination(BlockPosition blockposition) {
        return this.level.getBlockState(blockposition).entityCanStandOn(this.level, blockposition, this.mob);
    }
}
