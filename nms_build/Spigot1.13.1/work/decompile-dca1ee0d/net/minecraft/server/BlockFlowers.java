package net.minecraft.server;

public class BlockFlowers extends BlockPlant {

    protected static final VoxelShape a = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);

    public BlockFlowers(Block.Info block_info) {
        super(block_info);
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Vec3D vec3d = iblockdata.k(iblockaccess, blockposition);

        return BlockFlowers.a.a(vec3d.x, vec3d.y, vec3d.z);
    }

    public Block.EnumRandomOffset q() {
        return Block.EnumRandomOffset.XZ;
    }
}
