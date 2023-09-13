package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.BlockPropertyJigsawOrientation;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
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

public class BlockJigsaw extends Block implements ITileEntity {

    public static final BlockStateEnum<BlockPropertyJigsawOrientation> a = BlockProperties.P;

    protected BlockJigsaw(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockJigsaw.a, BlockPropertyJigsawOrientation.NORTH_UP));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockJigsaw.a);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockJigsaw.a, enumblockrotation.a().a((BlockPropertyJigsawOrientation) iblockdata.get(BlockJigsaw.a)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) iblockdata.set(BlockJigsaw.a, enumblockmirror.a().a((BlockPropertyJigsawOrientation) iblockdata.get(BlockJigsaw.a)));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.getClickedFace();
        EnumDirection enumdirection1;

        if (enumdirection.n() == EnumDirection.EnumAxis.Y) {
            enumdirection1 = blockactioncontext.f().opposite();
        } else {
            enumdirection1 = EnumDirection.UP;
        }

        return (IBlockData) this.getBlockData().set(BlockJigsaw.a, BlockPropertyJigsawOrientation.a(enumdirection, enumdirection1));
    }

    @Nullable
    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityJigsaw();
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
        EnumDirection enumdirection = h(definedstructure_blockinfo.b);
        EnumDirection enumdirection1 = h(definedstructure_blockinfo1.b);
        EnumDirection enumdirection2 = l(definedstructure_blockinfo.b);
        EnumDirection enumdirection3 = l(definedstructure_blockinfo1.b);
        TileEntityJigsaw.JointType tileentityjigsaw_jointtype = (TileEntityJigsaw.JointType) TileEntityJigsaw.JointType.a(definedstructure_blockinfo.c.getString("joint")).orElseGet(() -> {
            return enumdirection.n().d() ? TileEntityJigsaw.JointType.ALIGNED : TileEntityJigsaw.JointType.ROLLABLE;
        });
        boolean flag = tileentityjigsaw_jointtype == TileEntityJigsaw.JointType.ROLLABLE;

        return enumdirection == enumdirection1.opposite() && (flag || enumdirection2 == enumdirection3) && definedstructure_blockinfo.c.getString("target").equals(definedstructure_blockinfo1.c.getString("name"));
    }

    public static EnumDirection h(IBlockData iblockdata) {
        return ((BlockPropertyJigsawOrientation) iblockdata.get(BlockJigsaw.a)).b();
    }

    public static EnumDirection l(IBlockData iblockdata) {
        return ((BlockPropertyJigsawOrientation) iblockdata.get(BlockJigsaw.a)).c();
    }
}
