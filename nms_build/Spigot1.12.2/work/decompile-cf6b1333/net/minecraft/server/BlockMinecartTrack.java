package net.minecraft.server;

public class BlockMinecartTrack extends BlockMinecartTrackAbstract {

    public static final BlockStateEnum<BlockMinecartTrackAbstract.EnumTrackPosition> SHAPE = BlockStateEnum.of("shape", BlockMinecartTrackAbstract.EnumTrackPosition.class);

    protected BlockMinecartTrack() {
        super(false);
        this.w(this.blockStateList.getBlockData().set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH));
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {
        if (block.getBlockData().m() && (new BlockMinecartTrackAbstract.MinecartTrackLogic(world, blockposition, iblockdata)).b() == 3) {
            this.a(world, blockposition, iblockdata, false);
        }

    }

    public IBlockState<BlockMinecartTrackAbstract.EnumTrackPosition> g() {
        return BlockMinecartTrack.SHAPE;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.a(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartTrack.SHAPE)).a();
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartTrack.SHAPE)) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);
            }

        case COUNTERCLOCKWISE_90:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartTrack.SHAPE)) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case ASCENDING_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case NORTH_SOUTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST);

            case EAST_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH);
            }

        case CLOCKWISE_90:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartTrack.SHAPE)) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_SOUTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST);

            case EAST_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH);
            }

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartTrack.SHAPE);

        switch (enumblockmirror) {
        case LEFT_RIGHT:
            switch (blockminecarttrackabstract_enumtrackposition) {
            case ASCENDING_NORTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            default:
                return super.a(iblockdata, enumblockmirror);
            }

        case FRONT_BACK:
            switch (blockminecarttrackabstract_enumtrackposition) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_NORTH:
            case ASCENDING_SOUTH:
            default:
                break;

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartTrack.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);
            }
        }

        return super.a(iblockdata, enumblockmirror);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockMinecartTrack.SHAPE});
    }
}
