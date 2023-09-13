package net.minecraft.server;

import java.util.Random;

public class BlockStructure extends BlockTileEntity {

    public static final BlockStateEnum<TileEntityStructure.UsageMode> a = BlockStateEnum.of("mode", TileEntityStructure.UsageMode.class);

    public BlockStructure() {
        super(Material.ORE, MaterialMapColor.y);
        this.w(this.blockStateList.getBlockData());
    }

    public TileEntity a(World world, int i) {
        return new TileEntityStructure();
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityStructure ? ((TileEntityStructure) tileentity).a(entityhuman) : false;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityStructure) {
                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;

                tileentitystructure.a(entityliving);
            }
        }
    }

    public int a(Random random) {
        return 0;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockStructure.a, TileEntityStructure.UsageMode.DATA);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockStructure.a, TileEntityStructure.UsageMode.a(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((TileEntityStructure.UsageMode) iblockdata.get(BlockStructure.a)).a();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockStructure.a});
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityStructure) {
                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;
                boolean flag = world.isBlockIndirectlyPowered(blockposition);
                boolean flag1 = tileentitystructure.F();

                if (flag && !flag1) {
                    tileentitystructure.d(true);
                    this.a(tileentitystructure);
                } else if (!flag && flag1) {
                    tileentitystructure.d(false);
                }

            }
        }
    }

    private void a(TileEntityStructure tileentitystructure) {
        switch (tileentitystructure.k()) {
        case SAVE:
            tileentitystructure.b(false);
            break;

        case LOAD:
            tileentitystructure.c(false);
            break;

        case CORNER:
            tileentitystructure.s();

        case DATA:
        }

    }
}
