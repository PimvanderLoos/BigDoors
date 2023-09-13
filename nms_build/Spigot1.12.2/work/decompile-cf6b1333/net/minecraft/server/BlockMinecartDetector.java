package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockMinecartDetector extends BlockMinecartTrackAbstract {

    public static final BlockStateEnum<BlockMinecartTrackAbstract.EnumTrackPosition> SHAPE = BlockStateEnum.a("shape", BlockMinecartTrackAbstract.EnumTrackPosition.class, new Predicate() {
        public boolean a(@Nullable BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition) {
            return blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST && blockminecarttrackabstract_enumtrackposition != BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((BlockMinecartTrackAbstract.EnumTrackPosition) object);
        }
    });
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");

    public BlockMinecartDetector() {
        super(true);
        this.w(this.blockStateList.getBlockData().set(BlockMinecartDetector.POWERED, Boolean.valueOf(false)).set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH));
        this.a(true);
    }

    public int a(World world) {
        return 20;
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Entity entity) {
        if (!world.isClientSide) {
            if (!((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)).booleanValue()) {
                this.e(world, blockposition, iblockdata);
            }
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {}

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (!world.isClientSide && ((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)).booleanValue()) {
            this.e(world, blockposition, iblockdata);
        }
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)).booleanValue() ? 15 : 0;
    }

    public int c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)).booleanValue() ? 0 : (enumdirection == EnumDirection.UP ? 15 : 0);
    }

    private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        boolean flag = ((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)).booleanValue();
        boolean flag1 = false;
        List list = this.a(world, blockposition, EntityMinecartAbstract.class, new Predicate[0]);

        if (!list.isEmpty()) {
            flag1 = true;
        }

        if (flag1 && !flag) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockMinecartDetector.POWERED, Boolean.valueOf(true)), 3);
            this.b(world, blockposition, iblockdata, true);
            world.applyPhysics(blockposition, this, false);
            world.applyPhysics(blockposition.down(), this, false);
            world.b(blockposition, blockposition);
        }

        if (!flag1 && flag) {
            world.setTypeAndData(blockposition, iblockdata.set(BlockMinecartDetector.POWERED, Boolean.valueOf(false)), 3);
            this.b(world, blockposition, iblockdata, false);
            world.applyPhysics(blockposition, this, false);
            world.applyPhysics(blockposition.down(), this, false);
            world.b(blockposition, blockposition);
        }

        if (flag1) {
            world.a(new BlockPosition(blockposition), (Block) this, this.a(world));
        }

        world.updateAdjacentComparators(blockposition, this);
    }

    protected void b(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        BlockMinecartTrackAbstract.MinecartTrackLogic blockminecarttrackabstract_minecarttracklogic = new BlockMinecartTrackAbstract.MinecartTrackLogic(world, blockposition, iblockdata);
        List list = blockminecarttrackabstract_minecarttracklogic.a();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();
            IBlockData iblockdata1 = world.getType(blockposition1);

            if (iblockdata1 != null) {
                iblockdata1.doPhysics(world, blockposition1, iblockdata1.getBlock(), blockposition);
            }
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.onPlace(world, blockposition, iblockdata);
        this.e(world, blockposition, iblockdata);
    }

    public IBlockState<BlockMinecartTrackAbstract.EnumTrackPosition> g() {
        return BlockMinecartDetector.SHAPE;
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        if (((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)).booleanValue()) {
            List list = this.a(world, blockposition, EntityMinecartCommandBlock.class, new Predicate[0]);

            if (!list.isEmpty()) {
                return ((EntityMinecartCommandBlock) list.get(0)).getCommandBlock().k();
            }

            List list1 = this.a(world, blockposition, EntityMinecartAbstract.class, new Predicate[] { IEntitySelector.c});

            if (!list1.isEmpty()) {
                return Container.b((IInventory) list1.get(0));
            }
        }

        return 0;
    }

    protected <T extends EntityMinecartAbstract> List<T> a(World world, BlockPosition blockposition, Class<T> oclass, Predicate<Entity>... apredicate) {
        AxisAlignedBB axisalignedbb = this.a(blockposition);

        return apredicate.length != 1 ? world.a(oclass, axisalignedbb) : world.a(oclass, axisalignedbb, apredicate[0]);
    }

    private AxisAlignedBB a(BlockPosition blockposition) {
        float f = 0.2F;

        return new AxisAlignedBB((double) ((float) blockposition.getX() + 0.2F), (double) blockposition.getY(), (double) ((float) blockposition.getZ() + 0.2F), (double) ((float) (blockposition.getX() + 1) - 0.2F), (double) ((float) (blockposition.getY() + 1) - 0.2F), (double) ((float) (blockposition.getZ() + 1) - 0.2F));
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.a(i & 7)).set(BlockMinecartDetector.POWERED, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE)).a();

        if (((Boolean) iblockdata.get(BlockMinecartDetector.POWERED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE)) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);
            }

        case COUNTERCLOCKWISE_90:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE)) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case ASCENDING_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case NORTH_SOUTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST);

            case EAST_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH);
            }

        case CLOCKWISE_90:
            switch ((BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE)) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case ASCENDING_NORTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_SOUTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.EAST_WEST);

            case EAST_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_SOUTH);
            }

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        BlockMinecartTrackAbstract.EnumTrackPosition blockminecarttrackabstract_enumtrackposition = (BlockMinecartTrackAbstract.EnumTrackPosition) iblockdata.get(BlockMinecartDetector.SHAPE);

        switch (enumblockmirror) {
        case LEFT_RIGHT:
            switch (blockminecarttrackabstract_enumtrackposition) {
            case ASCENDING_NORTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_SOUTH);

            case ASCENDING_SOUTH:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_NORTH);

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            default:
                return super.a(iblockdata, enumblockmirror);
            }

        case FRONT_BACK:
            switch (blockminecarttrackabstract_enumtrackposition) {
            case ASCENDING_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_WEST);

            case ASCENDING_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.ASCENDING_EAST);

            case ASCENDING_NORTH:
            case ASCENDING_SOUTH:
            default:
                break;

            case SOUTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_WEST);

            case SOUTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.SOUTH_EAST);

            case NORTH_WEST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_EAST);

            case NORTH_EAST:
                return iblockdata.set(BlockMinecartDetector.SHAPE, BlockMinecartTrackAbstract.EnumTrackPosition.NORTH_WEST);
            }
        }

        return super.a(iblockdata, enumblockmirror);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockMinecartDetector.SHAPE, BlockMinecartDetector.POWERED});
    }
}
