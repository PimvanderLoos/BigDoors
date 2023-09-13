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
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockJigsaw.ORIENTATION, BlockPropertyJigsawOrientation.NORTH_UP));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockJigsaw.ORIENTATION);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockJigsaw.ORIENTATION, enumblockrotation.a().a((BlockPropertyJigsawOrientation) iblockdata.get(BlockJigsaw.ORIENTATION)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) iblockdata.set(BlockJigsaw.ORIENTATION, enumblockmirror.a().a((BlockPropertyJigsawOrientation) iblockdata.get(BlockJigsaw.ORIENTATION)));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        EnumDirection enumdirection1;

        if (enumdirection.n() == EnumDirection.EnumAxis.Y) {
            enumdirection1 = blockactioncontext.g().opposite();
        } else {
            enumdirection1 = EnumDirection.UP;
        }

        return (IBlockData) this.getBlockData().set(BlockJigsaw.ORIENTATION, BlockPropertyJigsawOrientation.a(enumdirection, enumdirection1));
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityJigsaw(blockposition, iblockdata);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityJigsaw && entityhuman.isCreativeAndOp()) {
            entityhuman.a((TileEntityJigsaw) tileentity);
            return EnumInteractionResult.a(world.isClientSide);
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public static boolean a(DefinedStructure.BlockInfo definedstructure_blockinfo, DefinedStructure.BlockInfo definedstructure_blockinfo1) {
        EnumDirection enumdirection = h(definedstructure_blockinfo.state);
        EnumDirection enumdirection1 = h(definedstructure_blockinfo1.state);
        EnumDirection enumdirection2 = n(definedstructure_blockinfo.state);
        EnumDirection enumdirection3 = n(definedstructure_blockinfo1.state);
        TileEntityJigsaw.JointType tileentityjigsaw_jointtype = (TileEntityJigsaw.JointType) TileEntityJigsaw.JointType.a(definedstructure_blockinfo.nbt.getString("joint")).orElseGet(() -> {
            return enumdirection.n().d() ? TileEntityJigsaw.JointType.ALIGNED : TileEntityJigsaw.JointType.ROLLABLE;
        });
        boolean flag = tileentityjigsaw_jointtype == TileEntityJigsaw.JointType.ROLLABLE;

        return enumdirection == enumdirection1.opposite() && (flag || enumdirection2 == enumdirection3) && definedstructure_blockinfo.nbt.getString("target").equals(definedstructure_blockinfo1.nbt.getString("name"));
    }

    public static EnumDirection h(IBlockData iblockdata) {
        return ((BlockPropertyJigsawOrientation) iblockdata.get(BlockJigsaw.ORIENTATION)).a();
    }

    public static EnumDirection n(IBlockData iblockdata) {
        return ((BlockPropertyJigsawOrientation) iblockdata.get(BlockJigsaw.ORIENTATION)).b();
    }
}
