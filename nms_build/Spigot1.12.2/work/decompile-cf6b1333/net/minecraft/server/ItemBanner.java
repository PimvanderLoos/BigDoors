package net.minecraft.server;

import javax.annotation.Nullable;

public class ItemBanner extends ItemBlock {

    public ItemBanner() {
        super(Blocks.STANDING_BANNER);
        this.maxStackSize = 16;
        this.b(CreativeModeTab.c);
        this.a(true);
        this.setMaxDurability(0);
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);
        boolean flag = iblockdata.getBlock().a((IBlockAccess) world, blockposition);

        if (enumdirection != EnumDirection.DOWN && (iblockdata.getMaterial().isBuildable() || flag) && (!flag || enumdirection == EnumDirection.UP)) {
            blockposition = blockposition.shift(enumdirection);
            ItemStack itemstack = entityhuman.b(enumhand);

            if (entityhuman.a(blockposition, enumdirection, itemstack) && Blocks.STANDING_BANNER.canPlace(world, blockposition)) {
                if (world.isClientSide) {
                    return EnumInteractionResult.SUCCESS;
                } else {
                    blockposition = flag ? blockposition.down() : blockposition;
                    if (enumdirection == EnumDirection.UP) {
                        int i = MathHelper.floor((double) ((entityhuman.yaw + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;

                        world.setTypeAndData(blockposition, Blocks.STANDING_BANNER.getBlockData().set(BlockFloorSign.ROTATION, Integer.valueOf(i)), 3);
                    } else {
                        world.setTypeAndData(blockposition, Blocks.WALL_BANNER.getBlockData().set(BlockWallSign.FACING, enumdirection), 3);
                    }

                    TileEntity tileentity = world.getTileEntity(blockposition);

                    if (tileentity instanceof TileEntityBanner) {
                        ((TileEntityBanner) tileentity).a(itemstack, false);
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

    public String b(ItemStack itemstack) {
        String s = "item.banner.";
        EnumColor enumcolor = c(itemstack);

        s = s + enumcolor.d() + ".name";
        return LocaleI18n.get(s);
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            EnumColor[] aenumcolor = EnumColor.values();
            int i = aenumcolor.length;

            for (int j = 0; j < i; ++j) {
                EnumColor enumcolor = aenumcolor[j];

                nonnulllist.add(a(enumcolor, (NBTTagList) null));
            }
        }

    }

    public static ItemStack a(EnumColor enumcolor, @Nullable NBTTagList nbttaglist) {
        ItemStack itemstack = new ItemStack(Items.BANNER, 1, enumcolor.getInvColorIndex());

        if (nbttaglist != null && !nbttaglist.isEmpty()) {
            itemstack.c("BlockEntityTag").set("Patterns", nbttaglist.d());
        }

        return itemstack;
    }

    public CreativeModeTab b() {
        return CreativeModeTab.c;
    }

    public static EnumColor c(ItemStack itemstack) {
        return EnumColor.fromInvColorIndex(itemstack.getData() & 15);
    }
}
