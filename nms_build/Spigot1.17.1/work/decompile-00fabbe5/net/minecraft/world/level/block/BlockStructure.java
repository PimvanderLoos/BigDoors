package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityStructure;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyStructureMode;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockStructure extends BlockTileEntity implements GameMasterBlock {

    public static final BlockStateEnum<BlockPropertyStructureMode> MODE = BlockProperties.STRUCTUREBLOCK_MODE;

    protected BlockStructure(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockStructure.MODE, BlockPropertyStructureMode.LOAD));
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityStructure(blockposition, iblockdata);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityStructure ? (((TileEntityStructure) tileentity).a(entityhuman) ? EnumInteractionResult.a(world.isClientSide) : EnumInteractionResult.PASS) : EnumInteractionResult.PASS;
    }

    @Override
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

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStructure.MODE);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (world instanceof WorldServer) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityStructure) {
                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;
                boolean flag1 = world.isBlockIndirectlyPowered(blockposition);
                boolean flag2 = tileentitystructure.C();

                if (flag1 && !flag2) {
                    tileentitystructure.c(true);
                    this.a((WorldServer) world, tileentitystructure);
                } else if (!flag1 && flag2) {
                    tileentitystructure.c(false);
                }

            }
        }
    }

    private void a(WorldServer worldserver, TileEntityStructure tileentitystructure) {
        switch (tileentitystructure.getUsageMode()) {
            case SAVE:
                tileentitystructure.b(false);
                break;
            case LOAD:
                tileentitystructure.a(worldserver, false);
                break;
            case CORNER:
                tileentitystructure.A();
            case DATA:
        }

    }
}
