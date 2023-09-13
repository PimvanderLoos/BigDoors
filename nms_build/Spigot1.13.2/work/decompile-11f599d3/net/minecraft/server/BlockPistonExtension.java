package net.minecraft.server;

import java.util.Random;

public class BlockPistonExtension extends BlockDirectional {

    public static final BlockStateEnum<BlockPropertyPistonType> TYPE = BlockProperties.at;
    public static final BlockStateBoolean SHORT = BlockProperties.u;
    protected static final VoxelShape o = Block.a(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape p = Block.a(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    protected static final VoxelShape q = Block.a(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape r = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    protected static final VoxelShape s = Block.a(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape t = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
    protected static final VoxelShape u = Block.a(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    protected static final VoxelShape v = Block.a(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
    protected static final VoxelShape w = Block.a(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
    protected static final VoxelShape x = Block.a(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
    protected static final VoxelShape y = Block.a(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    protected static final VoxelShape z = Block.a(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
    protected static final VoxelShape A = Block.a(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
    protected static final VoxelShape B = Block.a(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
    protected static final VoxelShape C = Block.a(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
    protected static final VoxelShape D = Block.a(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
    protected static final VoxelShape E = Block.a(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
    protected static final VoxelShape F = Block.a(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

    public BlockPistonExtension(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockPistonExtension.FACING, EnumDirection.NORTH)).set(BlockPistonExtension.TYPE, BlockPropertyPistonType.DEFAULT)).set(BlockPistonExtension.SHORT, false));
    }

    private VoxelShape k(IBlockData iblockdata) {
        switch ((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)) {
        case DOWN:
        default:
            return BlockPistonExtension.t;
        case UP:
            return BlockPistonExtension.s;
        case NORTH:
            return BlockPistonExtension.r;
        case SOUTH:
            return BlockPistonExtension.q;
        case WEST:
            return BlockPistonExtension.p;
        case EAST:
            return BlockPistonExtension.o;
        }
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a(this.k(iblockdata), this.w(iblockdata));
    }

    private VoxelShape w(IBlockData iblockdata) {
        boolean flag = (Boolean) iblockdata.get(BlockPistonExtension.SHORT);

        switch ((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)) {
        case DOWN:
        default:
            return flag ? BlockPistonExtension.B : BlockPistonExtension.v;
        case UP:
            return flag ? BlockPistonExtension.A : BlockPistonExtension.u;
        case NORTH:
            return flag ? BlockPistonExtension.D : BlockPistonExtension.x;
        case SOUTH:
            return flag ? BlockPistonExtension.C : BlockPistonExtension.w;
        case WEST:
            return flag ? BlockPistonExtension.F : BlockPistonExtension.z;
        case EAST:
            return flag ? BlockPistonExtension.E : BlockPistonExtension.y;
        }
    }

    public boolean r(IBlockData iblockdata) {
        return iblockdata.get(BlockPistonExtension.FACING) == EnumDirection.UP;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.isClientSide && entityhuman.abilities.canInstantlyBuild) {
            BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite());
            Block block = world.getType(blockposition1).getBlock();

            if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
                world.setAir(blockposition1);
            }
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            EnumDirection enumdirection = ((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite();

            blockposition = blockposition.shift(enumdirection);
            IBlockData iblockdata2 = world.getType(blockposition);

            if ((iblockdata2.getBlock() == Blocks.PISTON || iblockdata2.getBlock() == Blocks.STICKY_PISTON) && (Boolean) iblockdata2.get(BlockPiston.EXTENDED)) {
                iblockdata2.a(world, blockposition, 0);
                world.setAir(blockposition);
            }

        }
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection.opposite() == iblockdata.get(BlockPistonExtension.FACING) && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        Block block = iworldreader.getType(blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite())).getBlock();

        return block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.MOVING_PISTON;
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (iblockdata.canPlace(world, blockposition)) {
            BlockPosition blockposition2 = blockposition.shift(((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)).opposite());

            world.getType(blockposition2).doPhysics(world, blockposition2, block, blockposition1);
        }

    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(iblockdata.get(BlockPistonExtension.TYPE) == BlockPropertyPistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockPistonExtension.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockPistonExtension.FACING)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPistonExtension.FACING, BlockPistonExtension.TYPE, BlockPistonExtension.SHORT);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == iblockdata.get(BlockPistonExtension.FACING) ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
