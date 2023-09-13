package net.minecraft.world.item.context;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class BlockActionContextDirectional extends BlockActionContext {

    private final EnumDirection direction;

    public BlockActionContextDirectional(World world, BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack, EnumDirection enumdirection1) {
        super(world, (EntityHuman) null, EnumHand.MAIN_HAND, itemstack, new MovingObjectPositionBlock(Vec3D.atBottomCenterOf(blockposition), enumdirection1, blockposition, false));
        this.direction = enumdirection;
    }

    @Override
    public BlockPosition getClickedPos() {
        return this.getHitResult().getBlockPos();
    }

    @Override
    public boolean canPlace() {
        return this.getLevel().getBlockState(this.getHitResult().getBlockPos()).canBeReplaced((BlockActionContext) this);
    }

    @Override
    public boolean replacingClickedOnBlock() {
        return this.canPlace();
    }

    @Override
    public EnumDirection getNearestLookingDirection() {
        return EnumDirection.DOWN;
    }

    @Override
    public EnumDirection[] getNearestLookingDirections() {
        switch (this.direction) {
            case DOWN:
            default:
                return new EnumDirection[]{EnumDirection.DOWN, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST, EnumDirection.UP};
            case UP:
                return new EnumDirection[]{EnumDirection.DOWN, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};
            case NORTH:
                return new EnumDirection[]{EnumDirection.DOWN, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.WEST, EnumDirection.UP, EnumDirection.SOUTH};
            case SOUTH:
                return new EnumDirection[]{EnumDirection.DOWN, EnumDirection.SOUTH, EnumDirection.EAST, EnumDirection.WEST, EnumDirection.UP, EnumDirection.NORTH};
            case WEST:
                return new EnumDirection[]{EnumDirection.DOWN, EnumDirection.WEST, EnumDirection.SOUTH, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.EAST};
            case EAST:
                return new EnumDirection[]{EnumDirection.DOWN, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.WEST};
        }
    }

    @Override
    public EnumDirection getHorizontalDirection() {
        return this.direction.getAxis() == EnumDirection.EnumAxis.Y ? EnumDirection.NORTH : this.direction;
    }

    @Override
    public boolean isSecondaryUseActive() {
        return false;
    }

    @Override
    public float getRotation() {
        return (float) (this.direction.get2DDataValue() * 90);
    }
}
