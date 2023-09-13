package net.minecraft.server;

public class BlockShulkerBox extends BlockTileEntity {

    public static final BlockStateEnum<EnumDirection> a = BlockStateDirection.of("facing");
    public final EnumColor color;

    public BlockShulkerBox(EnumColor enumcolor) {
        super(Material.STONE, MaterialMapColor.c);
        this.color = enumcolor;
        this.a(CreativeModeTab.c);
        this.w(this.blockStateList.getBlockData().set(BlockShulkerBox.a, EnumDirection.UP));
    }

    public TileEntity a(World world, int i) {
        return new TileEntityShulkerBox(this.color);
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean t(IBlockData iblockdata) {
        return true;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else if (entityhuman.isSpectator()) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityShulkerBox) {
                EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockShulkerBox.a);
                boolean flag;

                if (((TileEntityShulkerBox) tileentity).p() == TileEntityShulkerBox.AnimationPhase.CLOSED) {
                    AxisAlignedBB axisalignedbb = BlockShulkerBox.j.b((double) (0.5F * (float) enumdirection1.getAdjacentX()), (double) (0.5F * (float) enumdirection1.getAdjacentY()), (double) (0.5F * (float) enumdirection1.getAdjacentZ())).a((double) enumdirection1.getAdjacentX(), (double) enumdirection1.getAdjacentY(), (double) enumdirection1.getAdjacentZ());

                    flag = !world.a(axisalignedbb.a(blockposition.shift(enumdirection1)));
                } else {
                    flag = true;
                }

                if (flag) {
                    entityhuman.b(StatisticList.ac);
                    entityhuman.openContainer((IInventory) tileentity);
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockShulkerBox.a, enumdirection);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockShulkerBox.a});
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((EnumDirection) iblockdata.get(BlockShulkerBox.a)).a();
    }

    public IBlockData fromLegacyData(int i) {
        EnumDirection enumdirection = EnumDirection.fromType1(i);

        return this.getBlockData().set(BlockShulkerBox.a, enumdirection);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (world.getTileEntity(blockposition) instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) world.getTileEntity(blockposition);

            tileentityshulkerbox.a(entityhuman.abilities.canInstantlyBuild);
            tileentityshulkerbox.d(entityhuman);
        }

    }

    public void dropNaturally(World world, BlockPosition blockposition, IBlockData iblockdata, float f, int i) {}

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityShulkerBox) {
                ((TileEntityShulkerBox) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;

            if (!tileentityshulkerbox.r() && tileentityshulkerbox.F()) {
                ItemStack itemstack = new ItemStack(Item.getItemOf(this));
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound.set("BlockEntityTag", ((TileEntityShulkerBox) tileentity).f(nbttagcompound1));
                itemstack.setTag(nbttagcompound);
                if (tileentityshulkerbox.hasCustomName()) {
                    itemstack.g(tileentityshulkerbox.getName());
                    tileentityshulkerbox.setCustomName("");
                }

                a(world, blockposition, itemstack);
            }

            world.updateAdjacentComparators(blockposition, iblockdata.getBlock());
        }

        super.remove(world, blockposition, iblockdata);
    }

    public EnumPistonReaction h(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityShulkerBox ? ((TileEntityShulkerBox) tileentity).a(iblockdata) : BlockShulkerBox.j;
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.b((IInventory) world.getTileEntity(blockposition));
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        ItemStack itemstack = super.a(world, blockposition, iblockdata);
        TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) world.getTileEntity(blockposition);
        NBTTagCompound nbttagcompound = tileentityshulkerbox.f(new NBTTagCompound());

        if (!nbttagcompound.isEmpty()) {
            itemstack.a("BlockEntityTag", (NBTBase) nbttagcompound);
        }

        return itemstack;
    }

    public static Block a(EnumColor enumcolor) {
        switch (enumcolor) {
        case WHITE:
            return Blocks.WHITE_SHULKER_BOX;

        case ORANGE:
            return Blocks.dm;

        case MAGENTA:
            return Blocks.dn;

        case LIGHT_BLUE:
            return Blocks.LIGHT_BLUE_SHULKER_BOX;

        case YELLOW:
            return Blocks.dp;

        case LIME:
            return Blocks.dq;

        case PINK:
            return Blocks.dr;

        case GRAY:
            return Blocks.ds;

        case SILVER:
            return Blocks.dt;

        case CYAN:
            return Blocks.du;

        case PURPLE:
        default:
            return Blocks.dv;

        case BLUE:
            return Blocks.dw;

        case BROWN:
            return Blocks.dx;

        case GREEN:
            return Blocks.dy;

        case RED:
            return Blocks.dz;

        case BLACK:
            return Blocks.dA;
        }
    }

    public static ItemStack b(EnumColor enumcolor) {
        return new ItemStack(a(enumcolor));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockShulkerBox.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockShulkerBox.a)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockShulkerBox.a)));
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
        EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockShulkerBox.a);
        TileEntityShulkerBox.AnimationPhase tileentityshulkerbox_animationphase = ((TileEntityShulkerBox) iblockaccess.getTileEntity(blockposition)).p();

        return tileentityshulkerbox_animationphase != TileEntityShulkerBox.AnimationPhase.CLOSED && (tileentityshulkerbox_animationphase != TileEntityShulkerBox.AnimationPhase.OPENED || enumdirection1 != enumdirection.opposite() && enumdirection1 != enumdirection) ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID;
    }
}
