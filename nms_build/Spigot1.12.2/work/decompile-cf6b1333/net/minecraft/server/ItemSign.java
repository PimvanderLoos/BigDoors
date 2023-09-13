package net.minecraft.server;

public class ItemSign extends Item {

    public ItemSign() {
        this.maxStackSize = 16;
        this.b(CreativeModeTab.c);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);
        boolean flag = iblockdata.getBlock().a((IBlockAccess) world, blockposition);

        if (enumdirection != EnumDirection.DOWN && (iblockdata.getMaterial().isBuildable() || flag) && (!flag || enumdirection == EnumDirection.UP)) {
            blockposition = blockposition.shift(enumdirection);
            ItemStack itemstack = entityhuman.b(enumhand);

            if (entityhuman.a(blockposition, enumdirection, itemstack) && Blocks.STANDING_SIGN.canPlace(world, blockposition)) {
                if (world.isClientSide) {
                    return EnumInteractionResult.SUCCESS;
                } else {
                    blockposition = flag ? blockposition.down() : blockposition;
                    if (enumdirection == EnumDirection.UP) {
                        int i = MathHelper.floor((double) ((entityhuman.yaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;

                        world.setTypeAndData(blockposition, Blocks.STANDING_SIGN.getBlockData().set(BlockFloorSign.ROTATION, Integer.valueOf(i)), 11);
                    } else {
                        world.setTypeAndData(blockposition, Blocks.WALL_SIGN.getBlockData().set(BlockWallSign.FACING, enumdirection), 11);
                    }

                    TileEntity tileentity = world.getTileEntity(blockposition);

                    if (tileentity instanceof TileEntitySign && !ItemBlock.a(world, entityhuman, blockposition, itemstack)) {
                        entityhuman.openSign((TileEntitySign) tileentity);
                    }

                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.x.a((EntityPlayer) entityhuman, blockposition, itemstack);
                    }

                    itemstack.subtract(1);
                    return EnumInteractionResult.SUCCESS;
                }
            } else {
                return EnumInteractionResult.FAIL;
            }
        } else {
            return EnumInteractionResult.FAIL;
        }
    }
}
