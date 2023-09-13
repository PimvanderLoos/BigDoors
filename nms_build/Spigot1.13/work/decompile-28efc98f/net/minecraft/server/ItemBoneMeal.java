package net.minecraft.server;

public class ItemBoneMeal extends ItemDye {

    public ItemBoneMeal(EnumColor enumcolor, Item.Info item_info) {
        super(enumcolor, item_info);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();

        if (a(itemactioncontext.getItemStack(), world, blockposition)) {
            if (!world.isClientSide) {
                world.triggerEffect(2005, blockposition, 0);
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            BlockPosition blockposition1 = blockposition.shift(itemactioncontext.getClickedFace());

            if (b(itemactioncontext.getItemStack(), world, blockposition1)) {
                if (!world.isClientSide) {
                    world.triggerEffect(2005, blockposition1, 0);
                }

                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    public static boolean a(ItemStack itemstack, World world, BlockPosition blockposition) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() instanceof IBlockFragilePlantElement) {
            IBlockFragilePlantElement iblockfragileplantelement = (IBlockFragilePlantElement) iblockdata.getBlock();

            if (iblockfragileplantelement.a(world, blockposition, iblockdata, world.isClientSide)) {
                if (!world.isClientSide) {
                    if (iblockfragileplantelement.a(world, world.random, blockposition, iblockdata)) {
                        iblockfragileplantelement.b(world, world.random, blockposition, iblockdata);
                    }

                    itemstack.subtract(1);
                }

                return true;
            }
        }

        return false;
    }

    public static boolean b(ItemStack itemstack, World world, BlockPosition blockposition) {
        IBlockData iblockdata = Blocks.SEAGRASS.getBlockData();

        if (world.getType(blockposition).getBlock() == Blocks.WATER && world.b(blockposition).g() == 8) {
            if (!world.isClientSide) {
                label36:
                for (int i = 0; i < 128; ++i) {
                    BlockPosition blockposition1 = blockposition;

                    for (int j = 0; j < i / 16; ++j) {
                        blockposition1 = blockposition1.a(ItemBoneMeal.k.nextInt(3) - 1, (ItemBoneMeal.k.nextInt(3) - 1) * ItemBoneMeal.k.nextInt(3) / 2, ItemBoneMeal.k.nextInt(3) - 1);
                        if (!iblockdata.canPlace(world, blockposition1)) {
                            continue label36;
                        }
                    }

                    if (world.getType(blockposition1).getBlock() == Blocks.WATER && world.b(blockposition1).g() == 8 && iblockdata.canPlace(world, blockposition1)) {
                        world.setTypeAndData(blockposition1, iblockdata, 3);
                    }
                }

                itemstack.subtract(1);
            }

            return true;
        } else {
            return false;
        }
    }
}
