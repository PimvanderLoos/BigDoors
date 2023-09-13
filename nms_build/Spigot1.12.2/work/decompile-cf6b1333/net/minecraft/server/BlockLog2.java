package net.minecraft.server;

import com.google.common.base.Predicate;
import javax.annotation.Nullable;

public class BlockLog2 extends BlockLogAbstract {

    public static final BlockStateEnum<BlockWood.EnumLogVariant> VARIANT = BlockStateEnum.a("variant", BlockWood.EnumLogVariant.class, new Predicate() {
        public boolean a(@Nullable BlockWood.EnumLogVariant blockwood_enumlogvariant) {
            return blockwood_enumlogvariant.a() >= 4;
        }

        public boolean apply(@Nullable Object object) {
            return this.a((BlockWood.EnumLogVariant) object);
        }
    });

    public BlockLog2() {
        this.w(this.blockStateList.getBlockData().set(BlockLog2.VARIANT, BlockWood.EnumLogVariant.ACACIA).set(BlockLog2.AXIS, BlockLogAbstract.EnumLogRotation.Y));
    }

    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockWood.EnumLogVariant blockwood_enumlogvariant = (BlockWood.EnumLogVariant) iblockdata.get(BlockLog2.VARIANT);

        switch ((BlockLogAbstract.EnumLogRotation) iblockdata.get(BlockLog2.AXIS)) {
        case X:
        case Z:
        case NONE:
        default:
            switch (blockwood_enumlogvariant) {
            case ACACIA:
            default:
                return MaterialMapColor.n;

            case DARK_OAK:
                return BlockWood.EnumLogVariant.DARK_OAK.c();
            }

        case Y:
            return blockwood_enumlogvariant.c();
        }
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        nonnulllist.add(new ItemStack(this, 1, BlockWood.EnumLogVariant.ACACIA.a() - 4));
        nonnulllist.add(new ItemStack(this, 1, BlockWood.EnumLogVariant.DARK_OAK.a() - 4));
    }

    public IBlockData fromLegacyData(int i) {
        IBlockData iblockdata = this.getBlockData().set(BlockLog2.VARIANT, BlockWood.EnumLogVariant.a((i & 3) + 4));

        switch (i & 12) {
        case 0:
            iblockdata = iblockdata.set(BlockLog2.AXIS, BlockLogAbstract.EnumLogRotation.Y);
            break;

        case 4:
            iblockdata = iblockdata.set(BlockLog2.AXIS, BlockLogAbstract.EnumLogRotation.X);
            break;

        case 8:
            iblockdata = iblockdata.set(BlockLog2.AXIS, BlockLogAbstract.EnumLogRotation.Z);
            break;

        default:
            iblockdata = iblockdata.set(BlockLog2.AXIS, BlockLogAbstract.EnumLogRotation.NONE);
        }

        return iblockdata;
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((BlockWood.EnumLogVariant) iblockdata.get(BlockLog2.VARIANT)).a() - 4;

        switch ((BlockLogAbstract.EnumLogRotation) iblockdata.get(BlockLog2.AXIS)) {
        case X:
            i |= 4;
            break;

        case Z:
            i |= 8;
            break;

        case NONE:
            i |= 12;
        }

        return i;
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockLog2.VARIANT, BlockLog2.AXIS});
    }

    protected ItemStack u(IBlockData iblockdata) {
        return new ItemStack(Item.getItemOf(this), 1, ((BlockWood.EnumLogVariant) iblockdata.get(BlockLog2.VARIANT)).a() - 4);
    }

    public int getDropData(IBlockData iblockdata) {
        return ((BlockWood.EnumLogVariant) iblockdata.get(BlockLog2.VARIANT)).a() - 4;
    }
}
