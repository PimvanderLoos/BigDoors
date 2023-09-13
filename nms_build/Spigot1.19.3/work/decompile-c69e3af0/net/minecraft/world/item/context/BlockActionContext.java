package net.minecraft.world.item.context;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class BlockActionContext extends ItemActionContext {

    private final BlockPosition relativePos;
    protected boolean replaceClicked;

    public BlockActionContext(EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, MovingObjectPositionBlock movingobjectpositionblock) {
        this(entityhuman.level, entityhuman, enumhand, itemstack, movingobjectpositionblock);
    }

    public BlockActionContext(ItemActionContext itemactioncontext) {
        this(itemactioncontext.getLevel(), itemactioncontext.getPlayer(), itemactioncontext.getHand(), itemactioncontext.getItemInHand(), itemactioncontext.getHitResult());
    }

    protected BlockActionContext(World world, @Nullable EntityHuman entityhuman, EnumHand enumhand, ItemStack itemstack, MovingObjectPositionBlock movingobjectpositionblock) {
        super(world, entityhuman, enumhand, itemstack, movingobjectpositionblock);
        this.replaceClicked = true;
        this.relativePos = movingobjectpositionblock.getBlockPos().relative(movingobjectpositionblock.getDirection());
        this.replaceClicked = world.getBlockState(movingobjectpositionblock.getBlockPos()).canBeReplaced(this);
    }

    public static BlockActionContext at(BlockActionContext blockactioncontext, BlockPosition blockposition, EnumDirection enumdirection) {
        return new BlockActionContext(blockactioncontext.getLevel(), blockactioncontext.getPlayer(), blockactioncontext.getHand(), blockactioncontext.getItemInHand(), new MovingObjectPositionBlock(new Vec3D((double) blockposition.getX() + 0.5D + (double) enumdirection.getStepX() * 0.5D, (double) blockposition.getY() + 0.5D + (double) enumdirection.getStepY() * 0.5D, (double) blockposition.getZ() + 0.5D + (double) enumdirection.getStepZ() * 0.5D), enumdirection, blockposition, false));
    }

    @Override
    public BlockPosition getClickedPos() {
        return this.replaceClicked ? super.getClickedPos() : this.relativePos;
    }

    public boolean canPlace() {
        return this.replaceClicked || this.getLevel().getBlockState(this.getClickedPos()).canBeReplaced(this);
    }

    public boolean replacingClickedOnBlock() {
        return this.replaceClicked;
    }

    public EnumDirection getNearestLookingDirection() {
        return EnumDirection.orderedByNearest(this.getPlayer())[0];
    }

    public EnumDirection getNearestLookingVerticalDirection() {
        return EnumDirection.getFacingAxis(this.getPlayer(), EnumDirection.EnumAxis.Y);
    }

    public EnumDirection[] getNearestLookingDirections() {
        EnumDirection[] aenumdirection = EnumDirection.orderedByNearest(this.getPlayer());

        if (this.replaceClicked) {
            return aenumdirection;
        } else {
            EnumDirection enumdirection = this.getClickedFace();

            int i;

            for (i = 0; i < aenumdirection.length && aenumdirection[i] != enumdirection.getOpposite(); ++i) {
                ;
            }

            if (i > 0) {
                System.arraycopy(aenumdirection, 0, aenumdirection, 1, i);
                aenumdirection[0] = enumdirection.getOpposite();
            }

            return aenumdirection;
        }
    }
}
