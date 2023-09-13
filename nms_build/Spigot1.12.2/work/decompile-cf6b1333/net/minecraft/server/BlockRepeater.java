package net.minecraft.server;

import java.util.Random;

public class BlockRepeater extends BlockDiodeAbstract {

    public static final BlockStateBoolean LOCKED = BlockStateBoolean.of("locked");
    public static final BlockStateInteger DELAY = BlockStateInteger.of("delay", 1, 4);

    protected BlockRepeater(boolean flag) {
        super(flag);
        this.w(this.blockStateList.getBlockData().set(BlockRepeater.FACING, EnumDirection.NORTH).set(BlockRepeater.DELAY, Integer.valueOf(1)).set(BlockRepeater.LOCKED, Boolean.valueOf(false)));
    }

    public String getName() {
        return LocaleI18n.get("item.diode.name");
    }

    public IBlockData updateState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.set(BlockRepeater.LOCKED, Boolean.valueOf(this.b(iblockaccess, blockposition, iblockdata)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockRepeater.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockRepeater.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockRepeater.FACING)));
    }

    public boolean interact(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (!entityhuman.abilities.mayBuild) {
            return false;
        } else {
            world.setTypeAndData(blockposition, iblockdata.a((IBlockState) BlockRepeater.DELAY), 3);
            return true;
        }
    }

    protected int x(IBlockData iblockdata) {
        return ((Integer) iblockdata.get(BlockRepeater.DELAY)).intValue() * 2;
    }

    protected IBlockData y(IBlockData iblockdata) {
        Integer integer = (Integer) iblockdata.get(BlockRepeater.DELAY);
        Boolean obool = (Boolean) iblockdata.get(BlockRepeater.LOCKED);
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockRepeater.FACING);

        return Blocks.POWERED_REPEATER.getBlockData().set(BlockRepeater.FACING, enumdirection).set(BlockRepeater.DELAY, integer).set(BlockRepeater.LOCKED, obool);
    }

    protected IBlockData z(IBlockData iblockdata) {
        Integer integer = (Integer) iblockdata.get(BlockRepeater.DELAY);
        Boolean obool = (Boolean) iblockdata.get(BlockRepeater.LOCKED);
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockRepeater.FACING);

        return Blocks.UNPOWERED_REPEATER.getBlockData().set(BlockRepeater.FACING, enumdirection).set(BlockRepeater.DELAY, integer).set(BlockRepeater.LOCKED, obool);
    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.REPEATER;
    }

    public ItemStack a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(Items.REPEATER);
    }

    public boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return this.c(iblockaccess, blockposition, iblockdata) > 0;
    }

    protected boolean B(IBlockData iblockdata) {
        return isDiode(iblockdata);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata) {
        super.remove(world, blockposition, iblockdata);
        this.h(world, blockposition, iblockdata);
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockRepeater.FACING, EnumDirection.fromType2(i)).set(BlockRepeater.LOCKED, Boolean.valueOf(false)).set(BlockRepeater.DELAY, Integer.valueOf(1 + (i >> 2)));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockRepeater.FACING)).get2DRotationValue();

        i |= ((Integer) iblockdata.get(BlockRepeater.DELAY)).intValue() - 1 << 2;
        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockRepeater.FACING, BlockRepeater.DELAY, BlockRepeater.LOCKED});
    }
}
