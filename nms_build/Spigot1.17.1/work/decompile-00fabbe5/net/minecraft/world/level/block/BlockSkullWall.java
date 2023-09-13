package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockSkullWall extends BlockSkullAbstract {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    private static final Map<EnumDirection, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, Block.a(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D), EnumDirection.SOUTH, Block.a(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D), EnumDirection.EAST, Block.a(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D), EnumDirection.WEST, Block.a(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D)));

    protected BlockSkullWall(BlockSkull.a blockskull_a, BlockBase.Info blockbase_info) {
        super(blockskull_a, blockbase_info);
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockSkullWall.FACING, EnumDirection.NORTH));
    }

    @Override
    public String h() {
        return this.getItem().getName();
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) BlockSkullWall.AABBS.get(iblockdata.get(BlockSkullWall.FACING));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.getBlockData();
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        EnumDirection[] aenumdirection = blockactioncontext.f();
        EnumDirection[] aenumdirection1 = aenumdirection;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection1[j];

            if (enumdirection.n().d()) {
                EnumDirection enumdirection1 = enumdirection.opposite();

                iblockdata = (IBlockData) iblockdata.set(BlockSkullWall.FACING, enumdirection1);
                if (!world.getType(blockposition.shift(enumdirection)).a(blockactioncontext)) {
                    return iblockdata;
                }
            }
        }

        return null;
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockSkullWall.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockSkullWall.FACING)));
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockSkullWall.FACING)));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSkullWall.FACING);
    }
}
