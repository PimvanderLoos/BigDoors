package net.minecraft.server;

import com.google.common.base.Predicates;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockEnderPortalFrame extends Block {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    public static final BlockStateBoolean EYE = BlockStateBoolean.of("eye");
    protected static final AxisAlignedBB c = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.8125D, 1.0D);
    protected static final AxisAlignedBB d = new AxisAlignedBB(0.3125D, 0.8125D, 0.3125D, 0.6875D, 1.0D, 0.6875D);
    private static ShapeDetector e;

    public BlockEnderPortalFrame() {
        super(Material.STONE, MaterialMapColor.D);
        this.w(this.blockStateList.getBlockData().set(BlockEnderPortalFrame.FACING, EnumDirection.NORTH).set(BlockEnderPortalFrame.EYE, Boolean.valueOf(false)));
    }

    public boolean b(IBlockData iblockdata) {
        return false;
    }

    public AxisAlignedBB b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockEnderPortalFrame.c;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, AxisAlignedBB axisalignedbb, List<AxisAlignedBB> list, @Nullable Entity entity, boolean flag) {
        a(blockposition, axisalignedbb, list, BlockEnderPortalFrame.c);
        if (((Boolean) world.getType(blockposition).get(BlockEnderPortalFrame.EYE)).booleanValue()) {
            a(blockposition, axisalignedbb, list, BlockEnderPortalFrame.d);
        }

    }

    public Item getDropType(IBlockData iblockdata, Random random, int i) {
        return Items.a;
    }

    public IBlockData getPlacedState(World world, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2, int i, EntityLiving entityliving) {
        return this.getBlockData().set(BlockEnderPortalFrame.FACING, entityliving.getDirection().opposite()).set(BlockEnderPortalFrame.EYE, Boolean.valueOf(false));
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int c(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return ((Boolean) iblockdata.get(BlockEnderPortalFrame.EYE)).booleanValue() ? 15 : 0;
    }

    public IBlockData fromLegacyData(int i) {
        return this.getBlockData().set(BlockEnderPortalFrame.EYE, Boolean.valueOf((i & 4) != 0)).set(BlockEnderPortalFrame.FACING, EnumDirection.fromType2(i & 3));
    }

    public int toLegacyData(IBlockData iblockdata) {
        byte b0 = 0;
        int i = b0 | ((EnumDirection) iblockdata.get(BlockEnderPortalFrame.FACING)).get2DRotationValue();

        if (((Boolean) iblockdata.get(BlockEnderPortalFrame.EYE)).booleanValue()) {
            i |= 4;
        }

        return i;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata.set(BlockEnderPortalFrame.FACING, enumblockrotation.a((EnumDirection) iblockdata.get(BlockEnderPortalFrame.FACING)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockEnderPortalFrame.FACING)));
    }

    protected BlockStateList getStateList() {
        return new BlockStateList(this, new IBlockState[] { BlockEnderPortalFrame.FACING, BlockEnderPortalFrame.EYE});
    }

    public boolean c(IBlockData iblockdata) {
        return false;
    }

    public static ShapeDetector e() {
        if (BlockEnderPortalFrame.e == null) {
            BlockEnderPortalFrame.e = ShapeDetectorBuilder.a().a(new String[] { "?vvv?", ">???<", ">???<", ">???<", "?^^^?"}).a('?', ShapeDetectorBlock.a(BlockStatePredicate.a)).a('^', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.EYE, Predicates.equalTo(Boolean.valueOf(true))).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.SOUTH)))).a('>', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.EYE, Predicates.equalTo(Boolean.valueOf(true))).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.WEST)))).a('v', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.EYE, Predicates.equalTo(Boolean.valueOf(true))).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.NORTH)))).a('<', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.END_PORTAL_FRAME).a(BlockEnderPortalFrame.EYE, Predicates.equalTo(Boolean.valueOf(true))).a(BlockEnderPortalFrame.FACING, Predicates.equalTo(EnumDirection.EAST)))).b();
        }

        return BlockEnderPortalFrame.e;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.SOLID : EnumBlockFaceShape.UNDEFINED;
    }
}
