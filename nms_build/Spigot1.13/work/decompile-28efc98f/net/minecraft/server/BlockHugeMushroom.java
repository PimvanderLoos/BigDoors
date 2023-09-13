package net.minecraft.server;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockHugeMushroom extends Block {

    public static final BlockStateBoolean a = BlockSprawling.a;
    public static final BlockStateBoolean b = BlockSprawling.b;
    public static final BlockStateBoolean c = BlockSprawling.c;
    public static final BlockStateBoolean p = BlockSprawling.p;
    public static final BlockStateBoolean q = BlockSprawling.q;
    public static final BlockStateBoolean r = BlockSprawling.r;
    private static final Map<EnumDirection, BlockStateBoolean> s = BlockSprawling.s;
    @Nullable
    private final Block t;

    public BlockHugeMushroom(@Nullable Block block, Block.Info block_info) {
        super(block_info);
        this.t = block;
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockHugeMushroom.a, Boolean.valueOf(true))).set(BlockHugeMushroom.b, Boolean.valueOf(true))).set(BlockHugeMushroom.c, Boolean.valueOf(true))).set(BlockHugeMushroom.p, Boolean.valueOf(true))).set(BlockHugeMushroom.q, Boolean.valueOf(true))).set(BlockHugeMushroom.r, Boolean.valueOf(true)));
    }

    public int a(IBlockData iblockdata, Random random) {
        return Math.max(0, random.nextInt(9) - 6);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return (IMaterial) (this.t == null ? Items.AIR : this.t);
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockHugeMushroom.r, Boolean.valueOf(this != world.getType(blockposition.down()).getBlock()))).set(BlockHugeMushroom.q, Boolean.valueOf(this != world.getType(blockposition.up()).getBlock()))).set(BlockHugeMushroom.a, Boolean.valueOf(this != world.getType(blockposition.north()).getBlock()))).set(BlockHugeMushroom.b, Boolean.valueOf(this != world.getType(blockposition.east()).getBlock()))).set(BlockHugeMushroom.c, Boolean.valueOf(this != world.getType(blockposition.south()).getBlock()))).set(BlockHugeMushroom.p, Boolean.valueOf(this != world.getType(blockposition.west()).getBlock()));
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return iblockdata1.getBlock() == this ? (IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.s.get(enumdirection), Boolean.valueOf(false)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.s.get(enumblockrotation.a(EnumDirection.NORTH)), iblockdata.get(BlockHugeMushroom.a))).set((IBlockState) BlockHugeMushroom.s.get(enumblockrotation.a(EnumDirection.SOUTH)), iblockdata.get(BlockHugeMushroom.c))).set((IBlockState) BlockHugeMushroom.s.get(enumblockrotation.a(EnumDirection.EAST)), iblockdata.get(BlockHugeMushroom.b))).set((IBlockState) BlockHugeMushroom.s.get(enumblockrotation.a(EnumDirection.WEST)), iblockdata.get(BlockHugeMushroom.p))).set((IBlockState) BlockHugeMushroom.s.get(enumblockrotation.a(EnumDirection.UP)), iblockdata.get(BlockHugeMushroom.q))).set((IBlockState) BlockHugeMushroom.s.get(enumblockrotation.a(EnumDirection.DOWN)), iblockdata.get(BlockHugeMushroom.r));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.s.get(enumblockmirror.b(EnumDirection.NORTH)), iblockdata.get(BlockHugeMushroom.a))).set((IBlockState) BlockHugeMushroom.s.get(enumblockmirror.b(EnumDirection.SOUTH)), iblockdata.get(BlockHugeMushroom.c))).set((IBlockState) BlockHugeMushroom.s.get(enumblockmirror.b(EnumDirection.EAST)), iblockdata.get(BlockHugeMushroom.b))).set((IBlockState) BlockHugeMushroom.s.get(enumblockmirror.b(EnumDirection.WEST)), iblockdata.get(BlockHugeMushroom.p))).set((IBlockState) BlockHugeMushroom.s.get(enumblockmirror.b(EnumDirection.UP)), iblockdata.get(BlockHugeMushroom.q))).set((IBlockState) BlockHugeMushroom.s.get(enumblockmirror.b(EnumDirection.DOWN)), iblockdata.get(BlockHugeMushroom.r));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(new IBlockState[] { BlockHugeMushroom.q, BlockHugeMushroom.r, BlockHugeMushroom.a, BlockHugeMushroom.b, BlockHugeMushroom.c, BlockHugeMushroom.p});
    }
}
