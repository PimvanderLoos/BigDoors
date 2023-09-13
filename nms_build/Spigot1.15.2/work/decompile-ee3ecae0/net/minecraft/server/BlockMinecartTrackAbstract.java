package net.minecraft.server;

public abstract class BlockMinecartTrackAbstract extends Block {

    protected static final VoxelShape a = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    protected static final VoxelShape b = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private final boolean c;

    public static boolean a(World world, BlockPosition blockposition) {
        return h(world.getType(blockposition));
    }

    public static boolean h(IBlockData iblockdata) {
        return iblockdata.a(TagsBlock.RAILS);
    }

    protected BlockMinecartTrackAbstract(boolean flag, Block.Info block_info) {
        super(block_info);
        this.c = flag;
    }

    public boolean c() {
        return this.c;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.getBlock() == this ? (BlockPropertyTrackPosition) iblockdata.get(this.d()) : null;

        return blockpropertytrackposition != null && blockpropertytrackposition.b() ? BlockMinecartTrackAbstract.b : BlockMinecartTrackAbstract.a;
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return c((IBlockAccess) iworldreader, blockposition.down());
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            iblockdata = this.a(world, blockposition, iblockdata, true);
            if (this.c) {
                iblockdata.doPhysics(world, blockposition, this, blockposition, flag);
            }

        }
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.get(this.d());
            boolean flag1 = false;
            BlockPosition blockposition2 = blockposition.down();

            if (!c((IBlockAccess) world, blockposition2)) {
                flag1 = true;
            }

            BlockPosition blockposition3 = blockposition.east();

            if (blockpropertytrackposition == BlockPropertyTrackPosition.ASCENDING_EAST && !c((IBlockAccess) world, blockposition3)) {
                flag1 = true;
            } else {
                BlockPosition blockposition4 = blockposition.west();

                if (blockpropertytrackposition == BlockPropertyTrackPosition.ASCENDING_WEST && !c((IBlockAccess) world, blockposition4)) {
                    flag1 = true;
                } else {
                    BlockPosition blockposition5 = blockposition.north();

                    if (blockpropertytrackposition == BlockPropertyTrackPosition.ASCENDING_NORTH && !c((IBlockAccess) world, blockposition5)) {
                        flag1 = true;
                    } else {
                        BlockPosition blockposition6 = blockposition.south();

                        if (blockpropertytrackposition == BlockPropertyTrackPosition.ASCENDING_SOUTH && !c((IBlockAccess) world, blockposition6)) {
                            flag1 = true;
                        }
                    }
                }
            }

            if (flag1 && !world.isEmpty(blockposition)) {
                if (!flag) {
                    c(iblockdata, world, blockposition);
                }

                world.a(blockposition, flag);
            } else {
                this.a(iblockdata, world, blockposition, block);
            }

        }
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block) {}

    protected IBlockData a(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        if (world.isClientSide) {
            return iblockdata;
        } else {
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.get(this.d());

            return (new MinecartTrackLogic(world, blockposition, iblockdata)).a(world.isBlockIndirectlyPowered(blockposition), flag, blockpropertytrackposition).c();
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.NORMAL;
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            if (((BlockPropertyTrackPosition) iblockdata.get(this.d())).b()) {
                world.applyPhysics(blockposition.up(), this);
            }

            if (this.c) {
                world.applyPhysics(blockposition, this);
                world.applyPhysics(blockposition.down(), this);
            }

        }
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = super.getBlockData();
        EnumDirection enumdirection = blockactioncontext.f();
        boolean flag = enumdirection == EnumDirection.EAST || enumdirection == EnumDirection.WEST;

        return (IBlockData) iblockdata.set(this.d(), flag ? BlockPropertyTrackPosition.EAST_WEST : BlockPropertyTrackPosition.NORTH_SOUTH);
    }

    public abstract IBlockState<BlockPropertyTrackPosition> d();
}
