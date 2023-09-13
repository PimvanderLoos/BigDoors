package net.minecraft.server;

public class ItemDye extends Item {

    public static final int[] a = new int[] { 1973019, 11743532, 3887386, 5320730, 2437522, 8073150, 2651799, 11250603, 4408131, 14188952, 4312372, 14602026, 6719955, 12801229, 15435844, 15790320};

    public ItemDye() {
        this.a(true);
        this.setMaxDurability(0);
        this.b(CreativeModeTab.l);
    }

    public String a(ItemStack itemstack) {
        int i = itemstack.getData();

        return super.getName() + "." + EnumColor.fromInvColorIndex(i).d();
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
            return EnumInteractionResult.FAIL;
        } else {
            EnumColor enumcolor = EnumColor.fromInvColorIndex(itemstack.getData());

            if (enumcolor == EnumColor.WHITE) {
                if (a(itemstack, world, blockposition)) {
                    if (!world.isClientSide) {
                        world.triggerEffect(2005, blockposition, 0);
                    }

                    return EnumInteractionResult.SUCCESS;
                }
            } else if (enumcolor == EnumColor.BROWN) {
                IBlockData iblockdata = world.getType(blockposition);
                Block block = iblockdata.getBlock();

                if (block == Blocks.LOG && iblockdata.get(BlockLog1.VARIANT) == BlockWood.EnumLogVariant.JUNGLE) {
                    if (enumdirection == EnumDirection.DOWN || enumdirection == EnumDirection.UP) {
                        return EnumInteractionResult.FAIL;
                    }

                    blockposition = blockposition.shift(enumdirection);
                    if (world.isEmpty(blockposition)) {
                        IBlockData iblockdata1 = Blocks.COCOA.getPlacedState(world, blockposition, enumdirection, f, f1, f2, 0, entityhuman);

                        world.setTypeAndData(blockposition, iblockdata1, 10);
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        return EnumInteractionResult.SUCCESS;
                    }
                }

                return EnumInteractionResult.FAIL;
            }

            return EnumInteractionResult.PASS;
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

    public boolean a(ItemStack itemstack, EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        if (entityliving instanceof EntitySheep) {
            EntitySheep entitysheep = (EntitySheep) entityliving;
            EnumColor enumcolor = EnumColor.fromInvColorIndex(itemstack.getData());

            if (!entitysheep.isSheared() && entitysheep.getColor() != enumcolor) {
                entitysheep.setColor(enumcolor);
                itemstack.subtract(1);
            }

            return true;
        } else {
            return false;
        }
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            for (int i = 0; i < 16; ++i) {
                nonnulllist.add(new ItemStack(this, 1, i));
            }
        }

    }
}
