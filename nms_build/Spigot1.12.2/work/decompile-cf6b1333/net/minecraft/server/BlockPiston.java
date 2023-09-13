package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class BlockPiston extends BlockDirectional {

    public static final BlockStateBoolean EXTENDED = BlockStateBoolean.of("extended");
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D);
    protected static final AxisAlignedBB e = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB f = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
    protected static final AxisAlignedBB g = new AxisAlignedBB(0.0D, 0.25D, 0.0D, 1.0D, 1.0D, 1.0D);
    private final boolean sticky;

    public BlockPiston(boolean flag) {
        super(Material.PISTON);
        this.w(this.blockStateList.getBlockData().set(BlockPiston.FACING, EnumDirection.NORTH).set(BlockPiston.EXTENDED, Boolean.valueOf(false)));
        this.sticky = flag;
        this.a(SoundEffectType.d);
        this.c(0.5F);
        this.a(CreativeModeTab.d);
    }

    public boolean t(IBlockData iblockdata) {
        return !((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue();
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        if (((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
            switch ((EnumDirection) iblockdata.get(BlockPiston.FACING)) {
            case DOWN:
                return BlockPiston.g;

            case UP:
            default:
                return BlockPiston.f;

            case NORTH:
                return BlockPiston.e;

            case SOUTH:
                return BlockPiston.d;

            case WEST:
                return BlockPiston.c;

            case EAST:
                return BlockPiston.b;
            }
        } else {
            return BlockPiston.j;
        }
    }

    public boolean k(IBlockData iblockdata) {
        return !((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue() || iblockdata.get(BlockPiston.FACING) == EnumDirection.DOWN;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        a(blockposition, axisalignedbb, list, iblockdata.e(world, blockposition));
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        world.setTypeAndData(blockposition, iblockdata.set(BlockPiston.FACING, EnumDirection.a(blockposition, entityliving)), 2);
        if (!world.isClientSide) {
            this.e(world, blockposition, iblockdata);
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            this.e(world, blockposition, iblockdata);
        }

    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.isClientSide && world.getTileEntity(blockposition) == null) {
            this.e(world, blockposition, iblockdata);
        }

    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockPiston.FACING, EnumDirection.a(blockposition, entityliving)).set(BlockPiston.EXTENDED, Boolean.valueOf(false));
    }

    private void e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockPiston.FACING);
        boolean flag = this.a(world, blockposition, enumdirection);

        if (flag && !((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
            if ((new PistonExtendsChecker(world, blockposition, enumdirection, true)).a()) {
                world.playBlockAction(blockposition, this, 0, enumdirection.a());
            }
        } else if (!flag && ((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
            world.playBlockAction(blockposition, this, 1, enumdirection.a());
        }

    }

    private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        int j;

        for (j = 0; j < i; ++j) {
            EnumDirection enumdirection1 = aenumdirection[j];

            if (enumdirection1 != enumdirection && world.isBlockFacePowered(blockposition.shift(enumdirection1), enumdirection1)) {
                return true;
            }
        }

        if (world.isBlockFacePowered(blockposition, EnumDirection.DOWN)) {
            return true;
        } else {
            BlockPosition blockposition1 = blockposition.up();
            EnumDirection[] aenumdirection1 = EnumDirection.values();

            j = aenumdirection1.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection2 = aenumdirection1[k];

                if (enumdirection2 != EnumDirection.DOWN && world.isBlockFacePowered(blockposition1.shift(enumdirection2), enumdirection2)) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockPiston.FACING);

        if (!world.isClientSide) {
            boolean flag = this.a(world, blockposition, enumdirection);

            if (flag && i == 1) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockPiston.EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!flag && i == 0) {
                return false;
            }
        }

        if (i == 0) {
            if (!this.a(world, blockposition, enumdirection, true)) {
                return false;
            }

            world.setTypeAndData(blockposition, iblockdata.set(BlockPiston.EXTENDED, Boolean.valueOf(true)), 3);
            world.a((EntityHuman) null, blockposition, SoundEffects.fu, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.25F + 0.6F);
        } else if (i == 1) {
            TileEntity tileentity = world.getTileEntity(blockposition.shift(enumdirection));

            if (tileentity instanceof TileEntityPiston) {
                ((TileEntityPiston) tileentity).j();
            }

            world.setTypeAndData(blockposition, Blocks.PISTON_EXTENSION.getBlockData().set(BlockPistonMoving.FACING, enumdirection).set(BlockPistonMoving.TYPE, this.sticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
            world.setTileEntity(blockposition, BlockPistonMoving.a(this.fromLegacyData(j), enumdirection, false, true));
            if (this.sticky) {
                BlockPosition blockposition1 = blockposition.a(enumdirection.getAdjacentX() * 2, enumdirection.getAdjacentY() * 2, enumdirection.getAdjacentZ() * 2);
                IBlockData iblockdata1 = world.getType(blockposition1);
                Block block = iblockdata1.getBlock();
                boolean flag1 = false;

                if (block == Blocks.PISTON_EXTENSION) {
                    TileEntity tileentity1 = world.getTileEntity(blockposition1);

                    if (tileentity1 instanceof TileEntityPiston) {
                        TileEntityPiston tileentitypiston = (TileEntityPiston) tileentity1;

                        if (tileentitypiston.h() == enumdirection && tileentitypiston.f()) {
                            tileentitypiston.j();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && iblockdata1.getMaterial() != Material.AIR && a(iblockdata1, world, blockposition1, enumdirection.opposite(), false, enumdirection) && (iblockdata1.o() == EnumPistonReaction.NORMAL || block == Blocks.PISTON || block == Blocks.STICKY_PISTON)) {
                    this.a(world, blockposition, enumdirection, false);
                }
            } else {
                world.setAir(blockposition.shift(enumdirection));
            }

            world.a((EntityHuman) null, blockposition, SoundEffects.ft, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    @Nullable
    public static EnumDirection b(int i) {
        int j = i & 7;

        return j > 5 ? null : EnumDirection.fromType1(j);
    }

    public static boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag, EnumDirection enumdirection1) {
        Block block = iblockdata.getBlock();

        if (block == Blocks.OBSIDIAN) {
            return false;
        } else if (!world.getWorldBorder().a(blockposition)) {
            return false;
        } else if (blockposition.getY() >= 0 && (enumdirection != EnumDirection.DOWN || blockposition.getY() != 0)) {
            if (blockposition.getY() <= world.getHeight() - 1 && (enumdirection != EnumDirection.UP || blockposition.getY() != world.getHeight() - 1)) {
                if (block != Blocks.PISTON && block != Blocks.STICKY_PISTON) {
                    if (iblockdata.b(world, blockposition) == -1.0F) {
                        return false;
                    }

                    switch (iblockdata.o()) {
                    case BLOCK:
                        return false;

                    case DESTROY:
                        return flag;

                    case PUSH_ONLY:
                        return enumdirection == enumdirection1;
                    }
                } else if (((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
                    return false;
                }

                return !block.isTileEntity();
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean a(World world, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        if (!flag) {
            world.setAir(blockposition.shift(enumdirection));
        }

        PistonExtendsChecker pistonextendschecker = new PistonExtendsChecker(world, blockposition, enumdirection, flag);

        if (!pistonextendschecker.a()) {
            return false;
        } else {
            List list = pistonextendschecker.getMovedBlocks();
            ArrayList arraylist = Lists.newArrayList();

            for (int i = 0; i < list.size(); ++i) {
                BlockPosition blockposition1 = (BlockPosition) list.get(i);

                arraylist.add(world.getType(blockposition1).c(world, blockposition1));
            }

            List list1 = pistonextendschecker.getBrokenBlocks();
            int j = list.size() + list1.size();
            IBlockData[] aiblockdata = new IBlockData[j];
            EnumDirection enumdirection1 = flag ? enumdirection : enumdirection.opposite();

            int k;
            BlockPosition blockposition2;
            IBlockData iblockdata;

            for (k = list1.size() - 1; k >= 0; --k) {
                blockposition2 = (BlockPosition) list1.get(k);
                iblockdata = world.getType(blockposition2);
                iblockdata.getBlock().b(world, blockposition2, iblockdata, 0);
                world.setTypeAndData(blockposition2, Blocks.AIR.getBlockData(), 4);
                --j;
                aiblockdata[j] = iblockdata;
            }

            for (k = list.size() - 1; k >= 0; --k) {
                blockposition2 = (BlockPosition) list.get(k);
                iblockdata = world.getType(blockposition2);
                world.setTypeAndData(blockposition2, Blocks.AIR.getBlockData(), 2);
                blockposition2 = blockposition2.shift(enumdirection1);
                world.setTypeAndData(blockposition2, Blocks.PISTON_EXTENSION.getBlockData().set(BlockPiston.FACING, enumdirection), 4);
                world.setTileEntity(blockposition2, BlockPistonMoving.a((IBlockData) arraylist.get(k), enumdirection, flag, false));
                --j;
                aiblockdata[j] = iblockdata;
            }

            BlockPosition blockposition3 = blockposition.shift(enumdirection);

            if (flag) {
                BlockPistonExtension.EnumPistonType blockpistonextension_enumpistontype = this.sticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;

                iblockdata = Blocks.PISTON_HEAD.getBlockData().set(BlockPistonExtension.FACING, enumdirection).set(BlockPistonExtension.TYPE, blockpistonextension_enumpistontype);
                IBlockData iblockdata1 = Blocks.PISTON_EXTENSION.getBlockData().set(BlockPistonMoving.FACING, enumdirection).set(BlockPistonMoving.TYPE, this.sticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);

                world.setTypeAndData(blockposition3, iblockdata1, 4);
                world.setTileEntity(blockposition3, BlockPistonMoving.a(iblockdata, enumdirection, true, true));
            }

            int l;

            for (l = list1.size() - 1; l >= 0; --l) {
                world.applyPhysics((BlockPosition) list1.get(l), aiblockdata[j++].getBlock(), false);
            }

            for (l = list.size() - 1; l >= 0; --l) {
                world.applyPhysics((BlockPosition) list.get(l), aiblockdata[j++].getBlock(), false);
            }

            if (flag) {
                world.applyPhysics(blockposition3, Blocks.PISTON_HEAD, false);
            }

            return true;
        }
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockPiston.FACING, b(i)).set(BlockPiston.EXTENDED, Boolean.valueOf((i & 8) > 0));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockPiston.FACING)).a();

        if (((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockPiston.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockPiston.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockPiston.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockPiston.FACING, BlockPiston.EXTENDED});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        iblockdata = this.updateState(iblockdata, iblockaccess, blockposition);
        return iblockdata.get(BlockPiston.FACING) != enumdirection.opposite() && ((Boolean) iblockdata.get(BlockPiston.EXTENDED)).booleanValue() ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID;
    }
}
