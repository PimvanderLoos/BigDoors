package net.minecraft.server;

import java.util.Iterator;

public abstract class BlockFireAbstract extends Block {

    private final float g;
    protected static final VoxelShape a = Block.a(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape b = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    protected static final VoxelShape c = Block.a(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    protected static final VoxelShape d = Block.a(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape e = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    protected static final VoxelShape f = Block.a(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

    public BlockFireAbstract(BlockBase.Info blockbase_info, float f) {
        super(blockbase_info);
        this.g = f;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return a((IBlockAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }

    public static IBlockData a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata = iblockaccess.getType(blockposition1);

        return BlockSoulFire.c(iblockdata.getBlock()) ? Blocks.SOUL_FIRE.getBlockData() : ((BlockFire) Blocks.FIRE).getPlacedState(iblockaccess, blockposition);
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockFireAbstract.b;
    }

    protected abstract boolean e(IBlockData iblockdata);

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isFireProof()) {
            entity.setFireTicks(entity.getFireTicks() + 1);
            if (entity.getFireTicks() == 0) {
                entity.setOnFire(8);
            }

            entity.damageEntity(DamageSource.FIRE, this.g);
        }

        super.a(iblockdata, world, blockposition, entity);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.a(iblockdata.getBlock())) {
            if (world.getDimensionKey() != World.OVERWORLD && world.getDimensionKey() != World.THE_NETHER || !BlockPortal.a((GeneratorAccess) world, blockposition)) {
                if (!iblockdata.canPlace(world, blockposition)) {
                    world.a(blockposition, false);
                }

            }
        }
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.s_()) {
            world.a((EntityHuman) null, 1009, blockposition, 0);
        }

    }

    public static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata = generatoraccess.getType(blockposition);
        IBlockData iblockdata1 = a((IBlockAccess) generatoraccess, blockposition);

        return iblockdata.isAir() && (iblockdata1.canPlace(generatoraccess, blockposition) || b(generatoraccess, blockposition));
    }

    private static boolean b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        EnumDirection enumdirection;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            enumdirection = (EnumDirection) iterator.next();
        } while (!generatoraccess.getType(blockposition.shift(enumdirection)).a(Blocks.OBSIDIAN) || BlockPortal.b(generatoraccess, blockposition) == null);

        return true;
    }
}
