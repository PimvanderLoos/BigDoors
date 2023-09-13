package net.minecraft.server;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockBrewingStand extends BlockTileEntity {

    public static final BlockStateBoolean[] HAS_BOTTLE = new BlockStateBoolean[] { BlockStateBoolean.of("has_bottle_0"), BlockStateBoolean.of("has_bottle_1"), BlockStateBoolean.of("has_bottle_2")};
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D);
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.4375D, 0.0D, 0.4375D, 0.5625D, 0.875D, 0.5625D);

    public BlockBrewingStand() {
        super(Material.ORE);
        this.w(this.blockStateList.getBlockData().set(BlockBrewingStand.HAS_BOTTLE[0], Boolean.valueOf(false)).set(BlockBrewingStand.HAS_BOTTLE[1], Boolean.valueOf(false)).set(BlockBrewingStand.HAS_BOTTLE[2], Boolean.valueOf(false)));
    }

    public String getName() {
        return LocaleI18n.get("item.brewingStand.name");
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public TileEntity a(World world, int i) {
        return new TileEntityBrewingStand();
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        a(blockposition, axisalignedbb, list, BlockBrewingStand.c);
        a(blockposition, axisalignedbb, list, BlockBrewingStand.b);
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockBrewingStand.b;
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBrewingStand) {
                entityhuman.openContainer((TileEntityBrewingStand) tileentity);
                entityhuman.b(StatisticList.M);
            }

            return true;
        }
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityBrewingStand) {
                ((TileEntityBrewingStand) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityBrewingStand) {
            InventoryUtils.dropInventory(world, blockposition, (TileEntityBrewingStand) tileentity);
        }

        super.remove(world, blockposition, iblockdata);
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.BREWING_STAND;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.BREWING_STAND);
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.a(world.getTileEntity(blockposition));
    }

    public IBlockData fromLegacyData(int i) {
        IBlockData iblockdata = this.getBlockData();

        for (int j = 0; j < 3; ++j) {
            iblockdata = iblockdata.set(BlockBrewingStand.HAS_BOTTLE[j], Boolean.valueOf((i & 1 << j) > 0));
        }

        return iblockdata;
    }

    public int toLegacyData(IBlockData iblockdata) {
        int i = 0;

        for (int j = 0; j < 3; ++j) {
            if (((Boolean) iblockdata.get(BlockBrewingStand.HAS_BOTTLE[j])).booleanValue()) {
                i |= 1 << j;
            }
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockBrewingStand.HAS_BOTTLE[0], BlockBrewingStand.HAS_BOTTLE[1], BlockBrewingStand.HAS_BOTTLE[2]});
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }
}
