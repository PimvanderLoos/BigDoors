package net.minecraft.server;

import java.util.Random;

public class BlockDaylightDetector extends BlockTileEntity {

    public static final BlockStateInteger POWER = BlockStateInteger.of("power", 0, 15);
    protected static final AxisAlignedBB b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D);
    private final boolean c;

    public BlockDaylightDetector(boolean flag) {
        super(Material.WOOD);
        this.c = flag;
        this.w(this.blockStateList.getBlockData().set(BlockDaylightDetector.POWER, Integer.valueOf(0)));
        this.a(CreativeModeTab.d);
        this.c(0.2F);
        this.a(SoundEffectType.a);
        this.c("daylightDetector");
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockDaylightDetector.b;
    }

    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return ((Integer) iblockdata.get(BlockDaylightDetector.POWER)).intValue();
    }

    public void c(World world, BlockPosition blockposition) {
        if (world.worldProvider.m()) {
            IBlockData iblockdata = world.getType(blockposition);
            int i = world.getBrightness(EnumSkyBlock.SKY, blockposition) - world.ah();
            float f = world.d(1.0F);

            if (this.c) {
                i = 15 - i;
            }

            if (i > 0 && !this.c) {
                float f1 = f < 3.1415927F ? 0.0F : 6.2831855F;

                f += (f1 - f) * 0.2F;
                i = Math.round((float) i * MathHelper.cos(f));
            }

            i = MathHelper.clamp(i, 0, 15);
            if (((Integer) iblockdata.get(BlockDaylightDetector.POWER)).intValue() != i) {
                world.setTypeAndData(blockposition, iblockdata.set(BlockDaylightDetector.POWER, Integer.valueOf(i)), 3);
            }

        }
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (entityhuman.dk()) {
            if (world.isClientSide) {
                return true;
            } else {
                if (this.c) {
                    world.setTypeAndData(blockposition, Blocks.DAYLIGHT_DETECTOR.getBlockData().set(BlockDaylightDetector.POWER, iblockdata.get(BlockDaylightDetector.POWER)), 4);
                    Blocks.DAYLIGHT_DETECTOR.c(world, blockposition);
                } else {
                    world.setTypeAndData(blockposition, Blocks.DAYLIGHT_DETECTOR_INVERTED.getBlockData().set(BlockDaylightDetector.POWER, iblockdata.get(BlockDaylightDetector.POWER)), 4);
                    Blocks.DAYLIGHT_DETECTOR_INVERTED.c(world, blockposition);
                }

                return true;
            }
        } else {
            return super.interact(world, blockposition, iblockdata, entityhuman, enumhand, enumdirection, f, f1, f2);
        }
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Item.getItemOf(Blocks.DAYLIGHT_DETECTOR);
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Blocks.DAYLIGHT_DETECTOR);
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType a(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    public TileEntity a(World world, int i) {
        return new TileEntityLightDetector();
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockDaylightDetector.POWER, Integer.valueOf(i));
    }

    public int toLegacyData(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockDaylightDetector.POWER)).intValue();
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockDaylightDetector.POWER});
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (!this.c) {
            super.a(creativemodetab, nonnulllist);
        }

    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }
}
