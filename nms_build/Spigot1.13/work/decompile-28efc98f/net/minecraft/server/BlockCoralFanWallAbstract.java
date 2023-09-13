package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

public class BlockCoralFanWallAbstract extends BlockCoralFanAbstract {

    public static final BlockStateDirection b = BlockFacingHorizontal.FACING;
    private static final Map<EnumDirection, VoxelShape> c = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, Block.a(0.0D, 4.0D, 5.0D, 16.0D, 12.0D, 16.0D), EnumDirection.SOUTH, Block.a(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 11.0D), EnumDirection.WEST, Block.a(5.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D), EnumDirection.EAST, Block.a(0.0D, 4.0D, 0.0D, 11.0D, 12.0D, 16.0D)));

    protected BlockCoralFanWallAbstract(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCoralFanWallAbstract.b, EnumDirection.NORTH)).set(BlockCoralFanWallAbstract.a, Boolean.valueOf(true)));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return (VoxelShape) BlockCoralFanWallAbstract.c.get(iblockdata.get(BlockCoralFanWallAbstract.b));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockCoralFanWallAbstract.b, enumblockrotation.a((EnumDirection) iblockdata.get(BlockCoralFanWallAbstract.b)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockCoralFanWallAbstract.b)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(new IBlockState[] { BlockCoralFanWallAbstract.b, BlockCoralFanWallAbstract.a});
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (((Boolean) iblockdata.get(BlockCoralFanWallAbstract.a)).booleanValue()) {
            generatoraccess.H().a(blockposition, FluidTypes.c, FluidTypes.c.a((IWorldReader) generatoraccess));
        }

        return enumdirection.opposite() == iblockdata.get(BlockCoralFanWallAbstract.b) && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : iblockdata;
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockCoralFanWallAbstract.b);
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);

        return iblockdata1.c(iworldreader, blockposition1, enumdirection) == EnumBlockFaceShape.SOLID && !b(iblockdata1.getBlock());
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getPlacedState(blockactioncontext);
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        EnumDirection[] aenumdirection = blockactioncontext.e();
        EnumDirection[] aenumdirection1 = aenumdirection;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection1[j];

            if (enumdirection.k().c()) {
                iblockdata = (IBlockData) iblockdata.set(BlockCoralFanWallAbstract.b, enumdirection.opposite());
                if (iblockdata.canPlace(world, blockposition)) {
                    return iblockdata;
                }
            }
        }

        return null;
    }
}
