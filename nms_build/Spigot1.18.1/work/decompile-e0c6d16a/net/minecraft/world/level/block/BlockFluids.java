package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypeFlowing;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockFluids extends Block implements IFluidSource {

    public static final BlockStateInteger LEVEL = BlockProperties.LEVEL;
    protected final FluidTypeFlowing fluid;
    private final List<Fluid> stateCache;
    public static final VoxelShape STABLE_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final ImmutableList<EnumDirection> POSSIBLE_FLOW_DIRECTIONS = ImmutableList.of(EnumDirection.DOWN, EnumDirection.SOUTH, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.WEST);

    protected BlockFluids(FluidTypeFlowing fluidtypeflowing, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.fluid = fluidtypeflowing;
        this.stateCache = Lists.newArrayList();
        this.stateCache.add(fluidtypeflowing.getSource(false));

        for (int i = 1; i < 8; ++i) {
            this.stateCache.add(fluidtypeflowing.getFlowing(8 - i, false));
        }

        this.stateCache.add(fluidtypeflowing.getFlowing(8, true));
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockFluids.LEVEL, 0));
    }

    @Override
    public VoxelShape getCollisionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return voxelshapecollision.isAbove(BlockFluids.STABLE_SHAPE, blockposition, true) && (Integer) iblockdata.getValue(BlockFluids.LEVEL) == 0 && voxelshapecollision.canStandOnFluid(iblockaccess.getFluidState(blockposition.above()), this.fluid) ? BlockFluids.STABLE_SHAPE : VoxelShapes.empty();
    }

    @Override
    public boolean isRandomlyTicking(IBlockData iblockdata) {
        return iblockdata.getFluidState().isRandomlyTicking();
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        iblockdata.getFluidState().randomTick(worldserver, blockposition, random);
    }

    @Override
    public boolean propagatesSkylightDown(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return !this.fluid.is(TagsFluid.LAVA);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        int i = (Integer) iblockdata.getValue(BlockFluids.LEVEL);

        return (Fluid) this.stateCache.get(Math.min(i, 8));
    }

    @Override
    public boolean skipRendering(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
        return iblockdata1.getFluidState().getType().isSame(this.fluid);
    }

    @Override
    public EnumRenderType getRenderShape(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    public List<ItemStack> getDrops(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.empty();
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (this.shouldSpreadLiquid(world, blockposition, iblockdata)) {
            world.scheduleTick(blockposition, iblockdata.getFluidState().getType(), this.fluid.getTickDelay(world));
        }

    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (iblockdata.getFluidState().isSource() || iblockdata1.getFluidState().isSource()) {
            generatoraccess.scheduleTick(blockposition, iblockdata.getFluidState().getType(), this.fluid.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (this.shouldSpreadLiquid(world, blockposition, iblockdata)) {
            world.scheduleTick(blockposition, iblockdata.getFluidState().getType(), this.fluid.getTickDelay(world));
        }

    }

    private boolean shouldSpreadLiquid(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.fluid.is(TagsFluid.LAVA)) {
            boolean flag = world.getBlockState(blockposition.below()).is(Blocks.SOUL_SOIL);
            UnmodifiableIterator unmodifiableiterator = BlockFluids.POSSIBLE_FLOW_DIRECTIONS.iterator();

            while (unmodifiableiterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) unmodifiableiterator.next();
                BlockPosition blockposition1 = blockposition.relative(enumdirection.getOpposite());

                if (world.getFluidState(blockposition1).is((Tag) TagsFluid.WATER)) {
                    Block block = world.getFluidState(blockposition).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;

                    world.setBlockAndUpdate(blockposition, block.defaultBlockState());
                    this.fizz(world, blockposition);
                    return false;
                }

                if (flag && world.getBlockState(blockposition1).is(Blocks.BLUE_ICE)) {
                    world.setBlockAndUpdate(blockposition, Blocks.BASALT.defaultBlockState());
                    this.fizz(world, blockposition);
                    return false;
                }
            }
        }

        return true;
    }

    private void fizz(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.levelEvent(1501, blockposition, 0);
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockFluids.LEVEL);
    }

    @Override
    public ItemStack pickupBlock(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Integer) iblockdata.getValue(BlockFluids.LEVEL) == 0) {
            generatoraccess.setBlock(blockposition, Blocks.AIR.defaultBlockState(), 11);
            return new ItemStack(this.fluid.getBucket());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public Optional<SoundEffect> getPickupSound() {
        return this.fluid.getPickupSound();
    }
}
