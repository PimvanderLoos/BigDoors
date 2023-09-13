package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.BlockPropertyJigsawOrientation;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityJigsaw;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class BlockJigsaw extends Block implements ITileEntity, GameMasterBlock {

    public static final BlockStateEnum<BlockPropertyJigsawOrientation> ORIENTATION = BlockProperties.ORIENTATION;

    protected BlockJigsaw(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockJigsaw.ORIENTATION, BlockPropertyJigsawOrientation.NORTH_UP));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockJigsaw.ORIENTATION);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockJigsaw.ORIENTATION, enumblockrotation.rotation().rotate((BlockPropertyJigsawOrientation) iblockdata.getValue(BlockJigsaw.ORIENTATION)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) iblockdata.setValue(BlockJigsaw.ORIENTATION, enumblockmirror.rotation().rotate((BlockPropertyJigsawOrientation) iblockdata.getValue(BlockJigsaw.ORIENTATION)));
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        EnumDirection enumdirection1;

        if (enumdirection.getAxis() == EnumDirection.EnumAxis.Y) {
            enumdirection1 = blockactioncontext.getHorizontalDirection().getOpposite();
        } else {
            enumdirection1 = EnumDirection.UP;
        }

        return (IBlockData) this.defaultBlockState().setValue(BlockJigsaw.ORIENTATION, BlockPropertyJigsawOrientation.fromFrontAndTop(enumdirection, enumdirection1));
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityJigsaw(blockposition, iblockdata);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityJigsaw && entityhuman.canUseGameMasterBlocks()) {
            entityhuman.openJigsawBlock((TileEntityJigsaw) tileentity);
            return EnumInteractionResult.sidedSuccess(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public static boolean canAttach(DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1) {
        EnumDirection enumdirection = getFrontFacing(definedstructure_blockinfo.state);
        EnumDirection enumdirection1 = getFrontFacing(definedstructure_blockinfo1.state);
        EnumDirection enumdirection2 = getTopFacing(definedstructure_blockinfo.state);
        EnumDirection enumdirection3 = getTopFacing(definedstructure_blockinfo1.state);
        TileEntityJigsaw.JointType tileentityjigsaw_jointtype = (TileEntityJigsaw.JointType) TileEntityJigsaw.JointType.byName(definedstructure_blockinfo.nbt.getString("joint")).orElseGet(() -> {
            return enumdirection.getAxis().isHorizontal() ? TileEntityJigsaw.JointType.ALIGNED : TileEntityJigsaw.JointType.ROLLABLE;
        });
        boolean flag = tileentityjigsaw_jointtype == TileEntityJigsaw.JointType.ROLLABLE;

        return enumdirection == enumdirection1.getOpposite() && (flag || enumdirection2 == enumdirection3) && definedstructure_blockinfo.nbt.getString("target").equals(definedstructure_blockinfo1.nbt.getString("name"));
    }

    public static EnumDirection getFrontFacing(IBlockData iblockdata) {
        return ((BlockPropertyJigsawOrientation) iblockdata.getValue(BlockJigsaw.ORIENTATION)).front();
    }

    public static EnumDirection getTopFacing(IBlockData iblockdata) {
        return ((BlockPropertyJigsawOrientation) iblockdata.getValue(BlockJigsaw.ORIENTATION)).top();
    }
}
