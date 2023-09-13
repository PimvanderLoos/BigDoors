package net.minecraft.server;

import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockHugeMushroom extends Block {

    public static final BlockStateBoolean a = BlockSprawling.a;
    public static final BlockStateBoolean b = BlockSprawling.b;
    public static final BlockStateBoolean c = BlockSprawling.c;
    public static final BlockStateBoolean o = BlockSprawling.o;
    public static final BlockStateBoolean p = BlockSprawling.p;
    public static final BlockStateBoolean q = BlockSprawling.q;
    private static final Map<EnumDirection, BlockStateBoolean> r = BlockSprawling.r;
    @Nullable
    private final Block s;

    public BlockHugeMushroom(@Nullable Block block, Block.Info block_info) {
        super(block_info);
        this.s = block;
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockHugeMushroom.a, true)).set(BlockHugeMushroom.b, true)).set(BlockHugeMushroom.c, true)).set(BlockHugeMushroom.o, true)).set(BlockHugeMushroom.p, true)).set(BlockHugeMushroom.q, true));
    }

    public int a(IBlockData iblockdata, Random random) {
        return Math.max(0, random.nextInt(9) - 6);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return (IMaterial) (this.s == null ? Items.AIR : this.s);
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockHugeMushroom.q, this != world.getType(blockposition.down()).getBlock())).set(BlockHugeMushroom.p, this != world.getType(blockposition.up()).getBlock())).set(BlockHugeMushroom.a, this != world.getType(blockposition.north()).getBlock())).set(BlockHugeMushroom.b, this != world.getType(blockposition.east()).getBlock())).set(BlockHugeMushroom.c, this != world.getType(blockposition.south()).getBlock())).set(BlockHugeMushroom.o, this != world.getType(blockposition.west()).getBlock());
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return iblockdata1.getBlock() == this ? (IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.r.get(enumdirection), false) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.r.get(enumblockrotation.a(EnumDirection.NORTH)), iblockdata.get(BlockHugeMushroom.a))).set((IBlockState) BlockHugeMushroom.r.get(enumblockrotation.a(EnumDirection.SOUTH)), iblockdata.get(BlockHugeMushroom.c))).set((IBlockState) BlockHugeMushroom.r.get(enumblockrotation.a(EnumDirection.EAST)), iblockdata.get(BlockHugeMushroom.b))).set((IBlockState) BlockHugeMushroom.r.get(enumblockrotation.a(EnumDirection.WEST)), iblockdata.get(BlockHugeMushroom.o))).set((IBlockState) BlockHugeMushroom.r.get(enumblockrotation.a(EnumDirection.UP)), iblockdata.get(BlockHugeMushroom.p))).set((IBlockState) BlockHugeMushroom.r.get(enumblockrotation.a(EnumDirection.DOWN)), iblockdata.get(BlockHugeMushroom.q));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set((IBlockState) BlockHugeMushroom.r.get(enumblockmirror.b(EnumDirection.NORTH)), iblockdata.get(BlockHugeMushroom.a))).set((IBlockState) BlockHugeMushroom.r.get(enumblockmirror.b(EnumDirection.SOUTH)), iblockdata.get(BlockHugeMushroom.c))).set((IBlockState) BlockHugeMushroom.r.get(enumblockmirror.b(EnumDirection.EAST)), iblockdata.get(BlockHugeMushroom.b))).set((IBlockState) BlockHugeMushroom.r.get(enumblockmirror.b(EnumDirection.WEST)), iblockdata.get(BlockHugeMushroom.o))).set((IBlockState) BlockHugeMushroom.r.get(enumblockmirror.b(EnumDirection.UP)), iblockdata.get(BlockHugeMushroom.p))).set((IBlockState) BlockHugeMushroom.r.get(enumblockmirror.b(EnumDirection.DOWN)), iblockdata.get(BlockHugeMushroom.q));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockHugeMushroom.p, BlockHugeMushroom.q, BlockHugeMushroom.a, BlockHugeMushroom.b, BlockHugeMushroom.c, BlockHugeMushroom.o);
    }
}
