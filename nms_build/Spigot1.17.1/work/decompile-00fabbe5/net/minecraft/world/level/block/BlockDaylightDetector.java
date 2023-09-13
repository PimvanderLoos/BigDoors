package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityLightDetector;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockDaylightDetector extends BlockTileEntity {

    public static final BlockStateInteger POWER = BlockProperties.POWER;
    public static final BlockStateBoolean INVERTED = BlockProperties.INVERTED;
    protected static final VoxelShape SHAPE = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

    public BlockDaylightDetector(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockDaylightDetector.POWER, 0)).set(BlockDaylightDetector.INVERTED, false));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockDaylightDetector.SHAPE;
    }

    @Override
    public boolean g_(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Integer) iblockdata.get(BlockDaylightDetector.POWER);
    }

    private static void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        int i = world.getBrightness(EnumSkyBlock.SKY, blockposition) - world.n_();
        float f = world.a(1.0F);
        boolean flag = (Boolean) iblockdata.get(BlockDaylightDetector.INVERTED);

        if (flag) {
            i = 15 - i;
        } else if (i > 0) {
            float f1 = f < 3.1415927F ? 0.0F : 6.2831855F;

            f += (f1 - f) * 0.2F;
            i = Math.round((float) i * MathHelper.cos(f));
        }

        i = MathHelper.clamp(i, 0, 15);
        if ((Integer) iblockdata.get(BlockDaylightDetector.POWER) != i) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockDaylightDetector.POWER, i), 3);
        }

    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (entityhuman.fv()) {
            if (world.isClientSide) {
                return EnumInteractionResult.SUCCESS;
            } else {
                IBlockData iblockdata1 = (IBlockData) iblockdata.a((IBlockState) BlockDaylightDetector.INVERTED);

                world.setTypeAndData(blockposition, iblockdata1, 4);
                d(iblockdata1, world, blockposition);
                return EnumInteractionResult.CONSUME;
            }
        } else {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityLightDetector(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return !world.isClientSide && world.getDimensionManager().hasSkyLight() ? a(tileentitytypes, TileEntityTypes.DAYLIGHT_DETECTOR, BlockDaylightDetector::a) : null;
    }

    private static void a(World world, BlockPosition blockposition, IBlockData iblockdata, TileEntityLightDetector tileentitylightdetector) {
        if (world.getTime() % 20L == 0L) {
            d(iblockdata, world, blockposition);
        }

    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockDaylightDetector.POWER, BlockDaylightDetector.INVERTED);
    }
}
