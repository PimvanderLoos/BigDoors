package net.minecraft.server;

public abstract class BlockMinecartTrackAbstract extends Block {

    protected static final VoxelShape a = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape b = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private final boolean c;

    public static boolean a(World world, BlockPosition blockposition) {
        return k(world.getType(blockposition));
    }

    public static boolean k(IBlockData iblockdata) {
        return iblockdata.a(TagsBlock.RAILS);
    }

    protected BlockMinecartTrackAbstract(boolean flag, Block.Info block_info) {
        super(block_info);
        this.c = flag;
    }

    public boolean d() {
        return this.c;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.getBlock() == this ? (BlockPropertyTrackPosition) iblockdata.get(this.e()) : null;

        return blockpropertytrackposition != null && blockpropertytrackposition.c() ? BlockMinecartTrackAbstract.b : BlockMinecartTrackAbstract.a;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getType(blockposition.down()).q();
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            if (!world.isClientSide) {
                iblockdata = this.a(world, blockposition, iblockdata, true);
                if (this.c) {
                    iblockdata.doPhysics(world, blockposition, this, blockposition);
                }
            }

        }
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.get(this.e());
            boolean flag = false;

            if (!world.getType(blockposition.down()).q()) {
                flag = true;
            }

            if (blockpropertytrackposition == BlockPropertyTrackPosition.ASCENDING_EAST && !world.getType(blockposition.east()).q()) {
                flag = true;
            } else if (blockpropertytrackposition == BlockPropertyTrackPosition.ASCENDING_WEST && !world.getType(blockposition.west()).q()) {
                flag = true;
            } else if (blockpropertytrackposition == BlockPropertyTrackPosition.ASCENDING_NORTH && !world.getType(blockposition.north()).q()) {
                flag = true;
            } else if (blockpropertytrackposition == BlockPropertyTrackPosition.ASCENDING_SOUTH && !world.getType(blockposition.south()).q()) {
                flag = true;
            }

            if (flag && !world.isEmpty(blockposition)) {
                iblockdata.dropNaturally(world, blockposition, 1.0F, 0);
                world.setAir(blockposition);
            } else {
                this.a(iblockdata, world, blockposition, block);
            }

        }
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {}

    protected IBlockData a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return world.isClientSide ? iblockdata : (new MinecartTrackLogic(world, blockposition, iblockdata)).a(world.isBlockIndirectlyPowered(blockposition), flag).c();
    }

    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            if (((BlockPropertyTrackPosition) iblockdata.get(this.e())).c()) {
                world.applyPhysics(blockposition.up(), this);
            }

            if (this.c) {
                world.applyPhysics(blockposition, this);
                world.applyPhysics(blockposition.down(), this);
            }

        }
    }

    public abstract IBlockState<BlockPropertyTrackPosition> e();
}
