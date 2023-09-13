package net.minecraft.server;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockRedstoneComparator extends BlockDiodeAbstract implements ITileEntity {

    public static final BlockStateBoolean POWERED = BlockStateBoolean.of("powered");
    public static final BlockStateEnum<BlockRedstoneComparator.EnumComparatorMode> MODE = BlockStateEnum.of("mode", BlockRedstoneComparator.EnumComparatorMode.class);

    public BlockRedstoneComparator(boolean flag) {
        super(flag);
        this.w(this.blockStateList.getBlockData().set(BlockRedstoneComparator.FACING, EnumDirection.NORTH).set(BlockRedstoneComparator.POWERED, Boolean.valueOf(false)).set(BlockRedstoneComparator.MODE, BlockRedstoneComparator.EnumComparatorMode.COMPARE));
        this.isTileEntity = true;
    }

    public String getName() {
        return LocaleI18n.get("item.comparator.name");
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.COMPARATOR;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.COMPARATOR);
    }

    protected int x(IBlockData iblockdata) {
        return 2;
    }

    protected IBlockData y(IBlockData iblockdata) {
        Boolean obool = (Boolean) iblockdata.get(BlockRedstoneComparator.POWERED);
        BlockRedstoneComparator.EnumComparatorMode blockredstonecomparator_enumcomparatormode = (BlockRedstoneComparator.EnumComparatorMode) iblockdata.get(BlockRedstoneComparator.MODE);
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockRedstoneComparator.FACING);

        return Blocks.POWERED_COMPARATOR.getBlockData().set(BlockRedstoneComparator.FACING, enumdirection).set(BlockRedstoneComparator.POWERED, obool).set(BlockRedstoneComparator.MODE, blockredstonecomparator_enumcomparatormode);
    }

    protected IBlockData z(IBlockData iblockdata) {
        Boolean obool = (Boolean) iblockdata.get(BlockRedstoneComparator.POWERED);
        BlockRedstoneComparator.EnumComparatorMode blockredstonecomparator_enumcomparatormode = (BlockRedstoneComparator.EnumComparatorMode) iblockdata.get(BlockRedstoneComparator.MODE);
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockRedstoneComparator.FACING);

        return Blocks.UNPOWERED_COMPARATOR.getBlockData().set(BlockRedstoneComparator.FACING, enumdirection).set(BlockRedstoneComparator.POWERED, obool).set(BlockRedstoneComparator.MODE, blockredstonecomparator_enumcomparatormode);
    }

    protected boolean A(IBlockData iblockdata) {
        return this.d || ((Boolean) iblockdata.get(BlockRedstoneComparator.POWERED)).booleanValue();
    }

    protected int a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).a() : 0;
    }

    private int j(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return iblockdata.get(BlockRedstoneComparator.MODE) == BlockRedstoneComparator.EnumComparatorMode.SUBTRACT ? Math.max(this.f(world, blockposition, iblockdata) - this.c((IBlockAccess) world, blockposition, iblockdata), 0) : this.f(world, blockposition, iblockdata);
    }

    protected boolean e(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.f(world, blockposition, iblockdata);

        if (i >= 15) {
            return true;
        } else if (i == 0) {
            return false;
        } else {
            int j = this.c((IBlockAccess) world, blockposition, iblockdata);

            return j == 0 ? true : i >= j;
        }
    }

    protected int f(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = super.f(world, blockposition, iblockdata);
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockRedstoneComparator.FACING);
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.n()) {
            i = iblockdata1.a(world, blockposition1);
        } else if (i < 15 && iblockdata1.l()) {
            blockposition1 = blockposition1.shift(enumdirection);
            iblockdata1 = world.getType(blockposition1);
            if (iblockdata1.n()) {
                i = iblockdata1.a(world, blockposition1);
            } else if (iblockdata1.getMaterial() == Material.AIR) {
                EntityItemFrame entityitemframe = this.a(world, enumdirection, blockposition1);

                if (entityitemframe != null) {
                    i = entityitemframe.t();
                }
            }
        }

        return i;
    }

    @Nullable
    private EntityItemFrame a(World world, final EnumDirection enumdirection, BlockPosition blockposition) {
        List list = world.a(EntityItemFrame.class, new AxisAlignedBB((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), (double) (blockposition.getX() + 1), (double) (blockposition.getY() + 1), (double) (blockposition.getZ() + 1)), new Predicate() {
            public boolean a(@Nullable Entity entity) {
                return entity != null && entity.getDirection() == enumdirection;
            }

            public boolean apply(@Nullable Object object) {
                return this.a((Entity) object);
            }
        });

        return list.size() == 1 ? (EntityItemFrame) list.get(0) : null;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!entityhuman.abilities.mayBuild) {
            return false;
        } else {
            iblockdata = iblockdata.a((IBlockState) BlockRedstoneComparator.MODE);
            float f3 = iblockdata.get(BlockRedstoneComparator.MODE) == BlockRedstoneComparator.EnumComparatorMode.SUBTRACT ? 0.55F : 0.5F;

            world.a(entityhuman, blockposition, SoundEffects.aq, SoundCategory.BLOCKS, 0.3F, f3);
            world.setTypeAndData(blockposition, iblockdata, 2);
            this.k(world, blockposition, iblockdata);
            return true;
        }
    }

    protected void g(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (!world.a(blockposition, (Block) this)) {
            int i = this.j(world, blockposition, iblockdata);
            TileEntity tileentity = world.getTileEntity(blockposition);
            int j = tileentity instanceof TileEntityComparator ? ((TileEntityComparator) tileentity).a() : 0;

            if (i != j || this.A(iblockdata) != this.e(world, blockposition, iblockdata)) {
                if (this.i(world, blockposition, iblockdata)) {
                    world.a(blockposition, this, 2, -1);
                } else {
                    world.a(blockposition, this, 2, 0);
                }
            }

        }
    }

    private void k(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.j(world, blockposition, iblockdata);
        TileEntity tileentity = world.getTileEntity(blockposition);
        int j = 0;

        if (tileentity instanceof TileEntityComparator) {
            TileEntityComparator tileentitycomparator = (TileEntityComparator) tileentity;

            j = tileentitycomparator.a();
            tileentitycomparator.a(i);
        }

        if (j != i || iblockdata.get(BlockRedstoneComparator.MODE) == BlockRedstoneComparator.EnumComparatorMode.COMPARE) {
            boolean flag = this.e(world, blockposition, iblockdata);
            boolean flag1 = this.A(iblockdata);

            if (flag1 && !flag) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockRedstoneComparator.POWERED, Boolean.valueOf(false)), 2);
            } else if (!flag1 && flag) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockRedstoneComparator.POWERED, Boolean.valueOf(true)), 2);
            }

            this.h(world, blockposition, iblockdata);
        }

    }

    public void b(World world, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if (this.d) {
            world.setTypeAndData(blockposition, this.z(iblockdata).set(BlockRedstoneComparator.POWERED, Boolean.valueOf(true)), 4);
        }

        this.k(world, blockposition, iblockdata);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.onPlace(world, blockposition, iblockdata);
        world.setTileEntity(blockposition, this.a(world, 0));
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        world.s(blockposition);
        this.h(world, blockposition, iblockdata);
    }

    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        super.a(iblockdata, world, blockposition, i, j);
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity == null ? false : tileentity.c(i, j);
    }

    public TileEntity a(World world, int i) {
        return new TileEntityComparator();
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockRedstoneComparator.FACING, EnumDirection.fromType2(i)).set(BlockRedstoneComparator.POWERED, Boolean.valueOf((i & 8) > 0)).set(BlockRedstoneComparator.MODE, (i & 4) > 0 ? BlockRedstoneComparator.EnumComparatorMode.SUBTRACT : BlockRedstoneComparator.EnumComparatorMode.COMPARE);
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockRedstoneComparator.FACING)).get2DRotationValue();

        if (((Boolean) iblockdata.get(BlockRedstoneComparator.POWERED)).booleanValue()) {
            i |= 8;
        }

        if (iblockdata.get(BlockRedstoneComparator.MODE) == BlockRedstoneComparator.EnumComparatorMode.SUBTRACT) {
            i |= 4;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockRedstoneComparator.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockRedstoneComparator.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockRedstoneComparator.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockRedstoneComparator.FACING, BlockRedstoneComparator.MODE, BlockRedstoneComparator.POWERED});
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockRedstoneComparator.FACING, entityliving.getDirection().opposite()).set(BlockRedstoneComparator.POWERED, Boolean.valueOf(false)).set(BlockRedstoneComparator.MODE, BlockRedstoneComparator.EnumComparatorMode.COMPARE);
    }

    public static enum EnumComparatorMode implements INamable {

        COMPARE("compare"), SUBTRACT("subtract");

        private final String c;

        private EnumComparatorMode(String s) {
            this.c = s;
        }

        public String toString() {
            return this.c;
        }

        public String getName() {
            return this.c;
        }
    }
}
