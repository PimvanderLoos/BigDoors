package net.minecraft.world.level.block;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class PiglinWallSkullBlock extends BlockSkullWall {

    private static final Map<EnumDirection, VoxelShape> AABBS = Maps.immutableEnumMap(Map.of(EnumDirection.NORTH, Block.box(3.0D, 4.0D, 8.0D, 13.0D, 12.0D, 16.0D), EnumDirection.SOUTH, Block.box(3.0D, 4.0D, 0.0D, 13.0D, 12.0D, 8.0D), EnumDirection.EAST, Block.box(0.0D, 4.0D, 3.0D, 8.0D, 12.0D, 13.0D), EnumDirection.WEST, Block.box(8.0D, 4.0D, 3.0D, 16.0D, 12.0D, 13.0D)));

    public PiglinWallSkullBlock(BlockBase.Info blockbase_info) {
        super(BlockSkull.Type.PIGLIN, blockbase_info);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) PiglinWallSkullBlock.AABBS.get(iblockdata.getValue(PiglinWallSkullBlock.FACING));
    }
}
