package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyWallHeight;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.OperatorBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockCobbleWall extends Block implements IBlockWaterlogged {

    public static final BlockStateBoolean UP = BlockProperties.G;
    public static final BlockStateEnum<BlockPropertyWallHeight> b = BlockProperties.S;
    public static final BlockStateEnum<BlockPropertyWallHeight> c = BlockProperties.T;
    public static final BlockStateEnum<BlockPropertyWallHeight> d = BlockProperties.U;
    public static final BlockStateEnum<BlockPropertyWallHeight> e = BlockProperties.V;
    public static final BlockStateBoolean f = BlockProperties.C;
    private final Map<IBlockData, VoxelShape> g;
    private final Map<IBlockData, VoxelShape> h;
    private static final VoxelShape i = Block.a(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape j = Block.a(7.0D, 0.0D, 0.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape k = Block.a(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 16.0D);
    private static final VoxelShape o = Block.a(0.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D);
    private static final VoxelShape p = Block.a(7.0D, 0.0D, 7.0D, 16.0D, 16.0D, 9.0D);

    public BlockCobbleWall(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.j((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCobbleWall.UP, true)).set(BlockCobbleWall.c, BlockPropertyWallHeight.NONE)).set(BlockCobbleWall.b, BlockPropertyWallHeight.NONE)).set(BlockCobbleWall.d, BlockPropertyWallHeight.NONE)).set(BlockCobbleWall.e, BlockPropertyWallHeight.NONE)).set(BlockCobbleWall.f, false));
        this.g = this.a(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
        this.h = this.a(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);
    }

    private static VoxelShape a(VoxelShape voxelshape, BlockPropertyWallHeight blockpropertywallheight, VoxelShape voxelshape1, VoxelShape voxelshape2) {
        return blockpropertywallheight == BlockPropertyWallHeight.TALL ? VoxelShapes.a(voxelshape, voxelshape2) : (blockpropertywallheight == BlockPropertyWallHeight.LOW ? VoxelShapes.a(voxelshape, voxelshape1) : voxelshape);
    }

    private Map<IBlockData, VoxelShape> a(float f, float f1, float f2, float f3, float f4, float f5) {
        float f6 = 8.0F - f;
        float f7 = 8.0F + f;
        float f8 = 8.0F - f1;
        float f9 = 8.0F + f1;
        VoxelShape voxelshape = Block.a((double) f6, 0.0D, (double) f6, (double) f7, (double) f2, (double) f7);
        VoxelShape voxelshape1 = Block.a((double) f8, (double) f3, 0.0D, (double) f9, (double) f4, (double) f9);
        VoxelShape voxelshape2 = Block.a((double) f8, (double) f3, (double) f8, (double) f9, (double) f4, 16.0D);
        VoxelShape voxelshape3 = Block.a(0.0D, (double) f3, (double) f8, (double) f9, (double) f4, (double) f9);
        VoxelShape voxelshape4 = Block.a((double) f8, (double) f3, (double) f8, 16.0D, (double) f4, (double) f9);
        VoxelShape voxelshape5 = Block.a((double) f8, (double) f3, 0.0D, (double) f9, (double) f5, (double) f9);
        VoxelShape voxelshape6 = Block.a((double) f8, (double) f3, (double) f8, (double) f9, (double) f5, 16.0D);
        VoxelShape voxelshape7 = Block.a(0.0D, (double) f3, (double) f8, (double) f9, (double) f5, (double) f9);
        VoxelShape voxelshape8 = Block.a((double) f8, (double) f3, (double) f8, 16.0D, (double) f5, (double) f9);
        Builder<IBlockData, VoxelShape> builder = ImmutableMap.builder();
        Iterator iterator = BlockCobbleWall.UP.getValues().iterator();

        while (iterator.hasNext()) {
            Boolean obool = (Boolean) iterator.next();
            Iterator iterator1 = BlockCobbleWall.b.getValues().iterator();

            while (iterator1.hasNext()) {
                BlockPropertyWallHeight blockpropertywallheight = (BlockPropertyWallHeight) iterator1.next();
                Iterator iterator2 = BlockCobbleWall.c.getValues().iterator();

                while (iterator2.hasNext()) {
                    BlockPropertyWallHeight blockpropertywallheight1 = (BlockPropertyWallHeight) iterator2.next();
                    Iterator iterator3 = BlockCobbleWall.e.getValues().iterator();

                    while (iterator3.hasNext()) {
                        BlockPropertyWallHeight blockpropertywallheight2 = (BlockPropertyWallHeight) iterator3.next();
                        Iterator iterator4 = BlockCobbleWall.d.getValues().iterator();

                        while (iterator4.hasNext()) {
                            BlockPropertyWallHeight blockpropertywallheight3 = (BlockPropertyWallHeight) iterator4.next();
                            VoxelShape voxelshape9 = VoxelShapes.a();

                            voxelshape9 = a(voxelshape9, blockpropertywallheight, voxelshape4, voxelshape8);
                            voxelshape9 = a(voxelshape9, blockpropertywallheight2, voxelshape3, voxelshape7);
                            voxelshape9 = a(voxelshape9, blockpropertywallheight1, voxelshape1, voxelshape5);
                            voxelshape9 = a(voxelshape9, blockpropertywallheight3, voxelshape2, voxelshape6);
                            if (obool) {
                                voxelshape9 = VoxelShapes.a(voxelshape9, voxelshape);
                            }

                            IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockCobbleWall.UP, obool)).set(BlockCobbleWall.b, blockpropertywallheight)).set(BlockCobbleWall.e, blockpropertywallheight2)).set(BlockCobbleWall.c, blockpropertywallheight1)).set(BlockCobbleWall.d, blockpropertywallheight3);

                            builder.put(iblockdata.set(BlockCobbleWall.f, false), voxelshape9);
                            builder.put(iblockdata.set(BlockCobbleWall.f, true), voxelshape9);
                        }
                    }
                }
            }
        }

        return builder.build();
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.g.get(iblockdata);
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.h.get(iblockdata);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    private boolean a(IBlockData iblockdata, boolean flag, EnumDirection enumdirection) {
        Block block = iblockdata.getBlock();
        boolean flag1 = block instanceof BlockFenceGate && BlockFenceGate.a(iblockdata, enumdirection);

        return iblockdata.a((Tag) TagsBlock.WALLS) || !b(block) && flag || block instanceof BlockIronBars || flag1;
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        BlockPosition blockposition1 = blockposition.north();
        BlockPosition blockposition2 = blockposition.east();
        BlockPosition blockposition3 = blockposition.south();
        BlockPosition blockposition4 = blockposition.west();
        BlockPosition blockposition5 = blockposition.up();
        IBlockData iblockdata = world.getType(blockposition1);
        IBlockData iblockdata1 = world.getType(blockposition2);
        IBlockData iblockdata2 = world.getType(blockposition3);
        IBlockData iblockdata3 = world.getType(blockposition4);
        IBlockData iblockdata4 = world.getType(blockposition5);
        boolean flag = this.a(iblockdata, iblockdata.d(world, blockposition1, EnumDirection.SOUTH), EnumDirection.SOUTH);
        boolean flag1 = this.a(iblockdata1, iblockdata1.d(world, blockposition2, EnumDirection.WEST), EnumDirection.WEST);
        boolean flag2 = this.a(iblockdata2, iblockdata2.d(world, blockposition3, EnumDirection.NORTH), EnumDirection.NORTH);
        boolean flag3 = this.a(iblockdata3, iblockdata3.d(world, blockposition4, EnumDirection.EAST), EnumDirection.EAST);
        IBlockData iblockdata5 = (IBlockData) this.getBlockData().set(BlockCobbleWall.f, fluid.getType() == FluidTypes.WATER);

        return this.a(world, iblockdata5, blockposition5, iblockdata4, flag, flag1, flag2, flag3);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(BlockCobbleWall.f)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return enumdirection == EnumDirection.DOWN ? super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1) : (enumdirection == EnumDirection.UP ? this.a((IWorldReader) generatoraccess, iblockdata, blockposition1, iblockdata1) : this.a((IWorldReader) generatoraccess, blockposition, iblockdata, blockposition1, iblockdata1, enumdirection));
    }

    private static boolean a(IBlockData iblockdata, IBlockState<BlockPropertyWallHeight> iblockstate) {
        return iblockdata.get(iblockstate) != BlockPropertyWallHeight.NONE;
    }

    private static boolean a(VoxelShape voxelshape, VoxelShape voxelshape1) {
        return !VoxelShapes.c(voxelshape1, voxelshape, OperatorBoolean.ONLY_FIRST);
    }

    private IBlockData a(IWorldReader iworldreader, IBlockData iblockdata, BlockPosition blockposition, IBlockData iblockdata1) {
        boolean flag = a(iblockdata, (IBlockState) BlockCobbleWall.c);
        boolean flag1 = a(iblockdata, (IBlockState) BlockCobbleWall.b);
        boolean flag2 = a(iblockdata, (IBlockState) BlockCobbleWall.d);
        boolean flag3 = a(iblockdata, (IBlockState) BlockCobbleWall.e);

        return this.a(iworldreader, iblockdata, blockposition, iblockdata1, flag, flag1, flag2, flag3);
    }

    private IBlockData a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, BlockPosition blockposition1, IBlockData iblockdata1, EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.opposite();
        boolean flag = enumdirection == EnumDirection.NORTH ? this.a(iblockdata1, iblockdata1.d(iworldreader, blockposition1, enumdirection1), enumdirection1) : a(iblockdata, (IBlockState) BlockCobbleWall.c);
        boolean flag1 = enumdirection == EnumDirection.EAST ? this.a(iblockdata1, iblockdata1.d(iworldreader, blockposition1, enumdirection1), enumdirection1) : a(iblockdata, (IBlockState) BlockCobbleWall.b);
        boolean flag2 = enumdirection == EnumDirection.SOUTH ? this.a(iblockdata1, iblockdata1.d(iworldreader, blockposition1, enumdirection1), enumdirection1) : a(iblockdata, (IBlockState) BlockCobbleWall.d);
        boolean flag3 = enumdirection == EnumDirection.WEST ? this.a(iblockdata1, iblockdata1.d(iworldreader, blockposition1, enumdirection1), enumdirection1) : a(iblockdata, (IBlockState) BlockCobbleWall.e);
        BlockPosition blockposition2 = blockposition.up();
        IBlockData iblockdata2 = iworldreader.getType(blockposition2);

        return this.a(iworldreader, iblockdata, blockposition2, iblockdata2, flag, flag1, flag2, flag3);
    }

    private IBlockData a(IWorldReader iworldreader, IBlockData iblockdata, BlockPosition blockposition, IBlockData iblockdata1, boolean flag, boolean flag1, boolean flag2, boolean flag3) {
        VoxelShape voxelshape = iblockdata1.getCollisionShape(iworldreader, blockposition).a(EnumDirection.DOWN);
        IBlockData iblockdata2 = this.a(iblockdata, flag, flag1, flag2, flag3, voxelshape);

        return (IBlockData) iblockdata2.set(BlockCobbleWall.UP, this.a(iblockdata2, iblockdata1, voxelshape));
    }

    private boolean a(IBlockData iblockdata, IBlockData iblockdata1, VoxelShape voxelshape) {
        boolean flag = iblockdata1.getBlock() instanceof BlockCobbleWall && (Boolean) iblockdata1.get(BlockCobbleWall.UP);

        if (flag) {
            return true;
        } else {
            BlockPropertyWallHeight blockpropertywallheight = (BlockPropertyWallHeight) iblockdata.get(BlockCobbleWall.c);
            BlockPropertyWallHeight blockpropertywallheight1 = (BlockPropertyWallHeight) iblockdata.get(BlockCobbleWall.d);
            BlockPropertyWallHeight blockpropertywallheight2 = (BlockPropertyWallHeight) iblockdata.get(BlockCobbleWall.b);
            BlockPropertyWallHeight blockpropertywallheight3 = (BlockPropertyWallHeight) iblockdata.get(BlockCobbleWall.e);
            boolean flag1 = blockpropertywallheight1 == BlockPropertyWallHeight.NONE;
            boolean flag2 = blockpropertywallheight3 == BlockPropertyWallHeight.NONE;
            boolean flag3 = blockpropertywallheight2 == BlockPropertyWallHeight.NONE;
            boolean flag4 = blockpropertywallheight == BlockPropertyWallHeight.NONE;
            boolean flag5 = flag4 && flag1 && flag2 && flag3 || flag4 != flag1 || flag2 != flag3;

            if (flag5) {
                return true;
            } else {
                boolean flag6 = blockpropertywallheight == BlockPropertyWallHeight.TALL && blockpropertywallheight1 == BlockPropertyWallHeight.TALL || blockpropertywallheight2 == BlockPropertyWallHeight.TALL && blockpropertywallheight3 == BlockPropertyWallHeight.TALL;

                return flag6 ? false : iblockdata1.getBlock().a((Tag) TagsBlock.WALL_POST_OVERRIDE) || a(voxelshape, BlockCobbleWall.i);
            }
        }
    }

    private IBlockData a(IBlockData iblockdata, boolean flag, boolean flag1, boolean flag2, boolean flag3, VoxelShape voxelshape) {
        return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockCobbleWall.c, this.a(flag, voxelshape, BlockCobbleWall.j))).set(BlockCobbleWall.b, this.a(flag1, voxelshape, BlockCobbleWall.p))).set(BlockCobbleWall.d, this.a(flag2, voxelshape, BlockCobbleWall.k))).set(BlockCobbleWall.e, this.a(flag3, voxelshape, BlockCobbleWall.o));
    }

    private BlockPropertyWallHeight a(boolean flag, VoxelShape voxelshape, VoxelShape voxelshape1) {
        return flag ? (a(voxelshape, voxelshape1) ? BlockPropertyWallHeight.TALL : BlockPropertyWallHeight.LOW) : BlockPropertyWallHeight.NONE;
    }

    @Override
    public Fluid d(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(BlockCobbleWall.f) ? FluidTypes.WATER.a(false) : super.d(iblockdata);
    }

    @Override
    public boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !(Boolean) iblockdata.get(BlockCobbleWall.f);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCobbleWall.UP, BlockCobbleWall.c, BlockCobbleWall.b, BlockCobbleWall.e, BlockCobbleWall.d, BlockCobbleWall.f);
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockCobbleWall.c, iblockdata.get(BlockCobbleWall.d))).set(BlockCobbleWall.b, iblockdata.get(BlockCobbleWall.e))).set(BlockCobbleWall.d, iblockdata.get(BlockCobbleWall.c))).set(BlockCobbleWall.e, iblockdata.get(BlockCobbleWall.b));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockCobbleWall.c, iblockdata.get(BlockCobbleWall.b))).set(BlockCobbleWall.b, iblockdata.get(BlockCobbleWall.d))).set(BlockCobbleWall.d, iblockdata.get(BlockCobbleWall.e))).set(BlockCobbleWall.e, iblockdata.get(BlockCobbleWall.c));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockCobbleWall.c, iblockdata.get(BlockCobbleWall.e))).set(BlockCobbleWall.b, iblockdata.get(BlockCobbleWall.c))).set(BlockCobbleWall.d, iblockdata.get(BlockCobbleWall.b))).set(BlockCobbleWall.e, iblockdata.get(BlockCobbleWall.d));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.set(BlockCobbleWall.c, iblockdata.get(BlockCobbleWall.d))).set(BlockCobbleWall.d, iblockdata.get(BlockCobbleWall.c));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.set(BlockCobbleWall.b, iblockdata.get(BlockCobbleWall.e))).set(BlockCobbleWall.e, iblockdata.get(BlockCobbleWall.b));
            default:
                return super.a(iblockdata, enumblockmirror);
        }
    }
}
