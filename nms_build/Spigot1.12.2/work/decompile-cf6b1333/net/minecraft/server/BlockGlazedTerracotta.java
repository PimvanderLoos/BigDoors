package net.minecraft.server;

public class BlockGlazedTerracotta extends BlockFacingHorizontal {

    public BlockGlazedTerracotta(EnumColor enumcolor) {
        super(Material.STONE, MaterialMapColor.a(enumcolor));
        this.c(1.4F);
        this.a(SoundEffectType.d);
        String s = enumcolor.d();

        if (s.length() > 1) {
            String s1 = s.substring(0, 1).toUpperCase() + s.substring(1, s.length());

            this.c("glazedTerracotta" + s1);
        }

        this.a(CreativeModeTab.c);
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockGlazedTerracotta.FACING});
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockGlazedTerracotta.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockGlazedTerracotta.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockGlazedTerracotta.FACING)));
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockGlazedTerracotta.FACING, entityliving.getDirection().opposite());
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockGlazedTerracotta.FACING)).get2DRotationValue();

        return i;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockGlazedTerracotta.FACING, EnumDirection.fromType2(i));
    }

    public EnumPistonReaction h(IBlockData iblockdata) {
        return EnumPistonReaction.PUSH_ONLY;
    }
}
