package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockStructure extends BlockTileEntity {

    public static final BlockStateEnum<BlockPropertyStructureMode> a = BlockProperties.aw;

    protected BlockStructure(Block.Info block_info) {
        super(block_info);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityStructure();
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityStructure ? ((TileEntityStructure) tileentity).a(entityhuman) : false;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        if (!world.isClientSide) {
            if (entityliving != null) {
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity instanceof TileEntityStructure) {
                    ((TileEntityStructure) tileentity).setAuthor(entityliving);
                }
            }

        }
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockStructure.a, BlockPropertyStructureMode.DATA);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStructure.a);
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityStructure) {
                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;
                boolean flag = world.isBlockIndirectlyPowered(blockposition);
                boolean flag1 = tileentitystructure.E();

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
        switch (tileentitystructure.getUsageMode()) {
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
