package net.minecraft.world.entity.ai.behavior;

import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public class BehaviorTarget implements BehaviorPosition {

    private final BlockPosition blockPos;
    private final Vec3D centerPosition;

    public BehaviorTarget(BlockPosition blockposition) {
        this.blockPos = blockposition.immutable();
        this.centerPosition = Vec3D.atCenterOf(blockposition);
    }

    public BehaviorTarget(Vec3D vec3d) {
        this.blockPos = BlockPosition.containing(vec3d);
        this.centerPosition = vec3d;
    }

    @Override
    public Vec3D currentPosition() {
        return this.centerPosition;
    }

    @Override
    public BlockPosition currentBlockPosition() {
        return this.blockPos;
    }

    @Override
    public boolean isVisibleBy(EntityLiving entityliving) {
        return true;
    }

    public String toString() {
        return "BlockPosTracker{blockPos=" + this.blockPos + ", centerPosition=" + this.centerPosition + "}";
    }
}
