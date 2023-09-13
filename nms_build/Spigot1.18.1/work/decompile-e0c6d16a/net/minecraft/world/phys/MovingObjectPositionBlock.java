package net.minecraft.world.phys;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;

public class MovingObjectPositionBlock extends MovingObjectPosition {

    private final EnumDirection direction;
    private final BlockPosition blockPos;
    private final boolean miss;
    private final boolean inside;

    public static MovingObjectPositionBlock miss(Vec3D vec3d, EnumDirection enumdirection, BlockPosition blockposition) {
        return new MovingObjectPositionBlock(true, vec3d, enumdirection, blockposition, false);
    }

    public MovingObjectPositionBlock(Vec3D vec3d, EnumDirection enumdirection, BlockPosition blockposition, boolean flag) {
        this(false, vec3d, enumdirection, blockposition, flag);
    }

    private MovingObjectPositionBlock(boolean flag, Vec3D vec3d, EnumDirection enumdirection, BlockPosition blockposition, boolean flag1) {
        super(vec3d);
        this.miss = flag;
        this.direction = enumdirection;
        this.blockPos = blockposition;
        this.inside = flag1;
    }

    public MovingObjectPositionBlock withDirection(EnumDirection enumdirection) {
        return new MovingObjectPositionBlock(this.miss, this.location, enumdirection, this.blockPos, this.inside);
    }

    public MovingObjectPositionBlock withPosition(BlockPosition blockposition) {
        return new MovingObjectPositionBlock(this.miss, this.location, this.direction, blockposition, this.inside);
    }

    public BlockPosition getBlockPos() {
        return this.blockPos;
    }

    public EnumDirection getDirection() {
        return this.direction;
    }

    @Override
    public MovingObjectPosition.EnumMovingObjectType getType() {
        return this.miss ? MovingObjectPosition.EnumMovingObjectType.MISS : MovingObjectPosition.EnumMovingObjectType.BLOCK;
    }

    public boolean isInside() {
        return this.inside;
    }
}
