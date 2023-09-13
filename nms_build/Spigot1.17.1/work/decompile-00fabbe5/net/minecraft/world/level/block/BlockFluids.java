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
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
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
    public static final VoxelShape STABLE_SHAPE = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    public static final ImmutableList<EnumDirection> POSSIBLE_FLOW_DIRECTIONS = ImmutableList.of(EnumDirection.DOWN, EnumDirection.SOUTH, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.WEST);

    protected BlockFluids(FluidTypeFlowing fluidtypeflowing, BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.fluid = fluidtypeflowing;
        this.stateCache = Lists.newArrayList();
        this.stateCache.add(fluidtypeflowing.a(false));

        for (int i = 1; i < 8; ++i) {
            this.stateCache.add(fluidtypeflowing.a(8 - i, false));
        }

        this.stateCache.add(fluidtypeflowing.a(8, true));
        this.k((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockFluids.LEVEL, 0));
    }

    @Override
    public VoxelShape c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return voxelshapecollision.a(BlockFluids.STABLE_SHAPE, blockposition, true) && (Integer) iblockdata.get(BlockFluids.LEVEL) == 0 && voxelshapecollision.a(iblockaccess.getFluid(blockposition.up()), this.fluid) ? BlockFluids.STABLE_SHAPE : VoxelShapes.a();
    }

    @Override
    public boolean isTicking(IBlockData iblockdata) {
        return iblockdata.getFluid().f();
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        iblockdata.getFluid().b(worldserver, blockposition, random);
    }

    @Override
    public boolean c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return !this.fluid.a((Tag) TagsFluid.LAVA);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        int i = (Integer) iblockdata.get(BlockFluids.LEVEL);

        return (Fluid) this.stateCache.get(Math.min(i, 8));
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
        return iblockdata1.getFluid().getType().a((FluidType) this.fluid);
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    @Override
    public List<ItemStack> a(IBlockData iblockdata, LootTableInfo.Builder loottableinfo_builder) {
        return Collections.emptyList();
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.a();
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (this.a(world, blockposition, iblockdata)) {
            world.getFluidTickList().a(blockposition, iblockdata.getFluid().getType(), this.fluid.a((IWorldReader) world));
        }

    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (iblockdata.getFluid().isSource() || iblockdata1.getFluid().isSource()) {
            generatoraccess.getFluidTickList().a(blockposition, iblockdata.getFluid().getType(), this.fluid.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (this.a(world, blockposition, iblockdata)) {
            world.getFluidTickList().a(blockposition, iblockdata.getFluid().getType(), this.fluid.a((IWorldReader) world));
        }

    }

    private boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.fluid.a((Tag) TagsFluid.LAVA)) {
            boolean flag = world.getType(blockposition.down()).a(Blocks.SOUL_SOIL);
            UnmodifiableIterator unmodifiableiterator = BlockFluids.POSSIBLE_FLOW_DIRECTIONS.iterator();

            while (unmodifiableiterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) unmodifiableiterator.next();
                BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());

                if (world.getFluid(blockposition1).a((Tag) TagsFluid.WATER)) {
                    Block block = world.getFluid(blockposition).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;

                    world.setTypeUpdate(blockposition, block.getBlockData());
                    this.fizz(world, blockposition);
                    return false;
                }

                if (flag && world.getType(blockposition1).a(Blocks.BLUE_ICE)) {
                    world.setTypeUpdate(blockposition, Blocks.BASALT.getBlockData());
                    this.fizz(world, blockposition);
                    return false;
                }
            }
        }

        return true;
    }

    private void fizz(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.triggerEffect(1501, blockposition, 0);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockFluids.LEVEL);
    }

    @Override
    public ItemStack removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Integer) iblockdata.get(BlockFluids.LEVEL) == 0) {
            generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
            return new ItemStack(this.fluid.a());
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Override
    public Optional<SoundEffect> V_() {
        return this.fluid.k();
    }
}
