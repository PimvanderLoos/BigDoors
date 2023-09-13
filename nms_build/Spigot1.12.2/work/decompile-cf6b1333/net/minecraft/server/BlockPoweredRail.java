package net.minecraft.server;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public class BlockPoweredRail extends BlockMinecartTrackAbstract {

    public static final BlockStateEnum<BlockMinecartTrackAbstract.EnumTrackPosition> SHAPE = BlockStateEnum.a("shape", BlockMinecartTrackAbstract.EnumTrackPosition.class, new Predicate() {
        public boolean a(@Nullable BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition) {
            return blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((BlockMinecartTrackAbstract.EnumTrackPosition) object);
        }
    });
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");

    protected BlockPoweredRail() {
        super(true);
        this.w(this.blockStateList.getBlockData().set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH).set(BlockPoweredRail.POWERED, Boolean.valueOf(false)));
    }

    protected boolean a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag, int i) {
        if (i >= 8) {
            return false;
        } else {
            int j = blockposition.getX();
            int k = blockposition.getY();
            int l = blockposition.getZ();
            boolean flag1 = true;
            BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockPoweredRail.SHAPE);

            switch (blockminecarttrackabstract_enumtrackposition) {
            case NORTH_SOUTH:
                if (flag) {
                    ++l;
                } else {
                    --l;
                }
                break;

            case EAST_WEST:
                if (flag) {
                    --j;
                } else {
                    ++j;
                }
                break;

            case ASCENDING_EAST:
                if (flag) {
                    --j;
                } else {
                    ++j;
                    ++k;
                    flag1 = false;
                }

                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST;
                break;

            case ASCENDING_WEST:
                if (flag) {
                    --j;
                    ++k;
                    flag1 = false;
                } else {
                    ++j;
                }

                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST;
                break;

            case ASCENDING_NORTH:
                if (flag) {
                    ++l;
                } else {
                    --l;
                    ++k;
                    flag1 = false;
                }

                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
                break;

            case ASCENDING_SOUTH:
                if (flag) {
                    ++l;
                    ++k;
                    flag1 = false;
                } else {
                    --l;
                }

                blockminecarttrackabstract_enumtrackposition = BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH;
            }

            return this.a(world, new BlockPosition(j, k, l), flag, i, blockminecarttrackabstract_enumtrackposition) ? true : flag1 && this.a(world, new BlockPosition(j, k - 1, l), flag, i, blockminecarttrackabstract_enumtrackposition);
        }
    }

    protected boolean a(World world, BlockPosition blockposition, boolean flag, int i, BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition) {
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() != this) {
            return false;
        } else {
            BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition1 = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockPoweredRail.SHAPE);

            return blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST && (blockminecarttrackabstract_enumtrackposition1 == BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH || blockminecarttrackabstract_enumtrackposition1 == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH || blockminecarttrackabstract_enumtrackposition1 == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH) ? false : (blockminecarttrackabstract_enumtrackposition == BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH && (blockminecarttrackabstract_enumtrackposition1 == BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST || blockminecarttrackabstract_enumtrackposition1 == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST || blockminecarttrackabstract_enumtrackposition1 == BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST) ? false : (((Boolean) iblockdata.get(BlockPoweredRail.POWERED)).booleanValue() ? (world.isBlockIndirectlyPowered(blockposition) ? true : this.a(world, blockposition, iblockdata, flag, i + 1)) : false));
        }
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {
        boolean flag = ((Boolean) iblockdata.get(BlockPoweredRail.POWERED)).booleanValue();
        boolean flag1 = world.isBlockIndirectlyPowered(blockposition) || this.a(world, blockposition, iblockdata, true, 0) || this.a(world, blockposition, iblockdata, false, 0);

        if (flag1 != flag) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockPoweredRail.POWERED, Boolean.valueOf(flag1)), 3);
            world.applyPhysics(blockposition.down(), this, false);
            if (((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockPoweredRail.SHAPE)).c()) {
                world.applyPhysics(blockposition.up(), this, false);
            }
        }

    }

    public IBlockState<BlockMinecartTrackAbstract.EnumTrackPosition> g() {
        return BlockPoweredRail.SHAPE;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.a(i & 7)).set(BlockPoweredRail.POWERED, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockPoweredRail.SHAPE)).a();

        if (((Boolean) iblockdata.get(BlockPoweredRail.POWERED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockPoweredRail.SHAPE)) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case SOUTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);
            }

        case COUNTERCLOCKWISE_90:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockPoweredRail.SHAPE)) {
            case NORTH_SOUTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST);

            case EAST_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH);

            case ASCENDING_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case ASCENDING_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case SOUTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case SOUTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case NORTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);
            }

        case CLOCKWISE_90:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockPoweredRail.SHAPE)) {
            case NORTH_SOUTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST);

            case EAST_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH);

            case ASCENDING_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case SOUTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case NORTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);
            }

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockPoweredRail.SHAPE);

        switch (enumblockmirror) {
        case LEFT_RIGHT:
            switch (blockminecarttrackabstract_enumtrackposition) {
            case ASCENDING_NORTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case SOUTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case SOUTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case NORTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case NORTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            default:
                return super.a(iblockdata, enumblockmirror);
            }

        case FRONT_BACK:
            switch (blockminecarttrackabstract_enumtrackposition) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_NORTH:
            case ASCENDING_SOUTH:
            default:
                break;

            case SOUTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockPoweredRail.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);
            }
        }

        return super.a(iblockdata, enumblockmirror);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockPoweredRail.SHAPE, BlockPoweredRail.POWERED});
    }
}
