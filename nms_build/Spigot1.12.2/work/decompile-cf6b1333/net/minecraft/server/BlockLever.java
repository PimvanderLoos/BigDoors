package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;

public class BlockLever extends Block {

    public static final BlockStateEnum<BlockLever.EnumLeverPosition> FACING = BlockStateEnum.of("facing", BlockLever.EnumLeverPosition.class);
    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.3125D, 0.20000000298023224D, 0.625D, 0.6875D, 0.800000011920929D, 1.0D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.3125D, 0.20000000298023224D, 0.0D, 0.6875D, 0.800000011920929D, 0.375D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.625D, 0.20000000298023224D, 0.3125D, 1.0D, 0.800000011920929D, 0.6875D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.0D, 0.20000000298023224D, 0.3125D, 0.375D, 0.800000011920929D, 0.6875D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.25D, 0.0D, 0.25D, 0.75D, 0.6000000238418579D, 0.75D);
    protected static final AxisAlignedBB B = new AxisAlignedBB(0.25D, 0.4000000059604645D, 0.25D, 0.75D, 1.0D, 0.75D);

    protected BlockLever() {
        super(Material.ORIENTABLE);
        this.w(this.blockStateList.getBlockData().set(BlockLever.FACING, BlockLever.EnumLeverPosition.NORTH).set(BlockLever.POWERED, Boolean.valueOf(false)));
        this.a(CreativeModeTab.d);
    }

    @Nullable
    public AxisAlignedBB a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockLever.k;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return a(world, blockposition, enumdirection);
    }

    public boolean canPlace(World world, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (a(world, blockposition, enumdirection)) {
                return true;
            }
        }

        return false;
    }

    protected static boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return BlockButtonAbstract.a(world, blockposition, enumdirection);
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        IBlockData iblockdata = this.getBlockData().set(BlockLever.POWERED, Boolean.valueOf(false));

        if (a(world, blockposition, enumdirection)) {
            return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.a(enumdirection, entityliving.getDirection()));
        } else {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            EnumDirection enumdirection1;

            do {
                if (!iterator.hasNext()) {
                    if (world.getType(blockposition.down()).q()) {
                        return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.a(EnumDirection.UP, entityliving.getDirection()));
                    }

                    return iblockdata;
                }

                enumdirection1 = (EnumDirection) iterator.next();
            } while (enumdirection1 == enumdirection || !a(world, blockposition, enumdirection1));

            return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.a(enumdirection1, entityliving.getDirection()));
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (this.e(world, blockposition, iblockdata) && !a(world, blockposition, ((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)).c())) {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
        }

    }

    private boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.canPlace(world, blockposition)) {
            return true;
        } else {
            this.b(world, blockposition, iblockdata, 0);
            world.setAir(blockposition);
            return false;
        }
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)) {
        case EAST:
        default:
            return BlockLever.f;

        case WEST:
            return BlockLever.e;

        case SOUTH:
            return BlockLever.d;

        case NORTH:
            return BlockLever.c;

        case UP_Z:
        case UP_X:
            return BlockLever.g;

        case DOWN_X:
        case DOWN_Z:
            return BlockLever.B;
        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            iblockdata = iblockdata.a((IBlockState) BlockLever.POWERED);
            world.setTypeAndData(blockposition, iblockdata, 3);
            float f3 = ((Boolean) iblockdata.get(BlockLever.POWERED)).booleanValue() ? 0.6F : 0.5F;

            world.a((EntityHuman) null, blockposition, SoundEffects.dI, SoundCategory.BLOCKS, 0.3F, f3);
            world.applyPhysics(blockposition, this, false);
            EnumDirection enumdirection1 = ((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)).c();

            world.applyPhysics(blockposition.shift(enumdirection1.opposite()), this, false);
            return true;
        }
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (((Boolean) iblockdata.get(BlockLever.POWERED)).booleanValue()) {
            world.applyPhysics(blockposition, this, false);
            EnumDirection enumdirection = ((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)).c();

            world.applyPhysics(blockposition.shift(enumdirection.opposite()), this, false);
        }

        super.remove(world, blockposition, iblockdata);
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((Boolean) iblockdata.get(BlockLever.POWERED)).booleanValue() ? 15 : 0;
    }

    public int c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !((Boolean) iblockdata.get(BlockLever.POWERED)).booleanValue() ? 0 : (((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)).c() == enumdirection ? 15 : 0);
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockLever.FACING, BlockLever.EnumLeverPosition.a(i & 7)).set(BlockLever.POWERED, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)).a();

        if (((Boolean) iblockdata.get(BlockLever.POWERED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case CLOCKWISE_180:
            switch ((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)) {
            case EAST:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.WEST);

            case WEST:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.EAST);

            case SOUTH:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.NORTH);

            case NORTH:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.SOUTH);

            default:
                return iblockdata;
            }

        case COUNTERCLOCKWISE_90:
            switch ((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)) {
            case EAST:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.NORTH);

            case WEST:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.SOUTH);

            case SOUTH:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.EAST);

            case NORTH:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.WEST);

            case UP_Z:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.UP_X);

            case UP_X:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.UP_Z);

            case DOWN_X:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.DOWN_Z);

            case DOWN_Z:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.DOWN_X);
            }

        case CLOCKWISE_90:
            switch ((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)) {
            case EAST:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.SOUTH);

            case WEST:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.NORTH);

            case SOUTH:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.WEST);

            case NORTH:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.EAST);

            case UP_Z:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.UP_X);

            case UP_X:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.UP_Z);

            case DOWN_X:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.DOWN_Z);

            case DOWN_Z:
                return iblockdata.set(BlockLever.FACING, BlockLever.EnumLeverPosition.DOWN_X);
            }

        default:
            return iblockdata;
        }
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a(((BlockLever.EnumLeverPosition) iblockdata.get(BlockLever.FACING)).c()));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockLever.FACING, BlockLever.POWERED});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public static enum EnumLeverPosition implements INamable {

        DOWN_X(0, "down_x", EnumDirection.DOWN), EAST(1, "east", EnumDirection.EAST), WEST(2, "west", EnumDirection.WEST), SOUTH(3, "south", EnumDirection.SOUTH), NORTH(4, "north", EnumDirection.NORTH), UP_Z(5, "up_z", EnumDirection.UP), UP_X(6, "up_x", EnumDirection.UP), DOWN_Z(7, "down_z", EnumDirection.DOWN);

        private static final BlockLever.EnumLeverPosition[] i = new BlockLever.EnumLeverPosition[values().length];
        private final int j;
        private final String k;
        private final EnumDirection l;

        private EnumLeverPosition(int i, String s, EnumDirection enumdirection) {
            this.j = i;
            this.k = s;
            this.l = enumdirection;
        }

        public int a() {
            return this.j;
        }

        public EnumDirection c() {
            return this.l;
        }

        public String toString() {
            return this.k;
        }

        public static BlockLever.EnumLeverPosition a(int i) {
            if (i < 0 || i >= BlockLever.EnumLeverPosition.i.length) {
                i = 0;
            }

            return BlockLever.EnumLeverPosition.i[i];
        }

        public static BlockLever.EnumLeverPosition a(EnumDirection enumdirection, EnumDirection enumdirection1) {
            switch (enumdirection) {
            case DOWN:
                switch (enumdirection1.k()) {
                case X:
                    return BlockLever.EnumLeverPosition.DOWN_X;

                case Z:
                    return BlockLever.EnumLeverPosition.DOWN_Z;

                default:
                    throw new IllegalArgumentException("Invalid entityFacing " + enumdirection1 + " for facing " + enumdirection);
                }

            case UP:
                switch (enumdirection1.k()) {
                case X:
                    return BlockLever.EnumLeverPosition.UP_X;

                case Z:
                    return BlockLever.EnumLeverPosition.UP_Z;

                default:
                    throw new IllegalArgumentException("Invalid entityFacing " + enumdirection1 + " for facing " + enumdirection);
                }

            case NORTH:
                return BlockLever.EnumLeverPosition.NORTH;

            case SOUTH:
                return BlockLever.EnumLeverPosition.SOUTH;

            case WEST:
                return BlockLever.EnumLeverPosition.WEST;

            case EAST:
                return BlockLever.EnumLeverPosition.EAST;

            default:
                throw new IllegalArgumentException("Invalid facing: " + enumdirection);
            }
        }

        public String getName() {
            return this.k;
        }

        static {
            BlockLever.EnumLeverPosition[] ablocklever_enumleverposition = values();
            int i = ablocklever_enumleverposition.length;

            for (int j = 0; j < i; ++j) {
                BlockLever.EnumLeverPosition blocklever_enumleverposition = ablocklever_enumleverposition[j];

                BlockLever.EnumLeverPosition.i[blocklever_enumleverposition.a()] = blocklever_enumleverposition;
            }

        }
    }
}
