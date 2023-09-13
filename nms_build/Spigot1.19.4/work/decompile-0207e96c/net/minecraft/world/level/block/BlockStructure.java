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
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockStructure.MODE, BlockPropertyStructureMode.LOAD));
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityStructure(blockposition, iblockdata);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        return tileentity instanceof TileEntityStructure ? (((TileEntityStructure) tileentity).usedBy(entityhuman) ? EnumInteractionResult.sidedSuccess(world.isClientSide) : EnumInteractionResult.PASS) : EnumInteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        if (!world.isClientSide) {
            if (entityliving != null) {
                TileEntity tileentity = world.getBlockEntity(blockposition);

                if (tileentity instanceof TileEntityStructure) {
                    ((TileEntityStructure) tileentity).createdBy(entityliving);
                }
            }

        }
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockStructure.MODE);
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (world instanceof WorldServer) {
            TileEntity tileentity = world.getBlockEntity(blockposition);

            if (tileentity instanceof TileEntityStructure) {
                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;
                boolean flag1 = world.hasNeighborSignal(blockposition);
                boolean flag2 = tileentitystructure.isPowered();

                if (flag1 && !flag2) {
                    tileentitystructure.setPowered(true);
                    this.trigger((WorldServer) world, tileentitystructure);
                } else if (!flag1 && flag2) {
                    tileentitystructure.setPowered(false);
                }

            }
        }
    }

    private void trigger(WorldServer worldserver, TileEntityStructure tileentitystructure) {
        switch (tileentitystructure.getMode()) {
            case SAVE:
                tileentitystructure.saveStructure(false);
                break;
            case LOAD:
                tileentitystructure.loadStructure(worldserver, false);
                break;
            case CORNER:
                tileentitystructure.unloadStructure();
            case DATA:
        }

    }
}
