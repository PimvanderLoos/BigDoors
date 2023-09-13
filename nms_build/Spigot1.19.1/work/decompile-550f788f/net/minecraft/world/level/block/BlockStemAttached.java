package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IMaterial;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class BlockStemAttached extends BlockPlant {

    public static final BlockStateDirection FACING = BlockFacingHorizontal.FACING;
    protected static final float AABB_OFFSET = 2.0F;
    private static final Map<EnumDirection, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.of(EnumDirection.SOUTH, Block.box(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 16.0D), EnumDirection.WEST, Block.box(0.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D), EnumDirection.NORTH, Block.box(6.0D, 0.0D, 0.0D, 10.0D, 10.0D, 10.0D), EnumDirection.EAST, Block.box(6.0D, 0.0D, 6.0D, 16.0D, 10.0D, 10.0D)));
    private final BlockStemmed fruit;
    private final Supplier<Item> seedSupplier;

    protected BlockStemAttached(BlockStemmed blockstemmed, Supplier<Item> supplier, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockStemAttached.FACING, EnumDirection.NORTH));
        this.fruit = blockstemmed;
        this.seedSupplier = supplier;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) BlockStemAttached.AABBS.get(iblockdata.getValue(BlockStemAttached.FACING));
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return !iblockdata1.is((Block) this.fruit) && enumdirection == iblockdata.getValue(BlockStemAttached.FACING) ? (IBlockData) this.fruit.getStem().defaultBlockState().setValue(BlockStem.AGE, 7) : super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    protected boolean mayPlaceOn(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.is(Blocks.FARMLAND);
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack((IMaterial) this.seedSupplier.get());
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.setValue(BlockStemAttached.FACING, enumblockrotation.rotate((EnumDirection) iblockdata.getValue(BlockStemAttached.FACING)));
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.rotate(enumblockmirror.getRotation((EnumDirection) iblockdata.getValue(BlockStemAttached.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockStemAttached.FACING);
    }
}
