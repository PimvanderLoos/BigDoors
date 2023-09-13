package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.math.Vector3fa;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleParamRedstone;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockPropertyRedstoneSide;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.IBlockState;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockRedstoneWire extends Block {

    public static final BlockStateEnum<BlockPropertyRedstoneSide> NORTH = BlockProperties.NORTH_REDSTONE;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> EAST = BlockProperties.EAST_REDSTONE;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> SOUTH = BlockProperties.SOUTH_REDSTONE;
    public static final BlockStateEnum<BlockPropertyRedstoneSide> WEST = BlockProperties.WEST_REDSTONE;
    public static final BlockStateInteger POWER = BlockProperties.POWER;
    public static final Map<EnumDirection, BlockStateEnum<BlockPropertyRedstoneSide>> PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, BlockRedstoneWire.NORTH, EnumDirection.EAST, BlockRedstoneWire.EAST, EnumDirection.SOUTH, BlockRedstoneWire.SOUTH, EnumDirection.WEST, BlockRedstoneWire.WEST));
    protected static final int H = 1;
    protected static final int W = 3;
    protected static final int E = 13;
    protected static final int N = 3;
    protected static final int S = 13;
    private static final VoxelShape SHAPE_DOT = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
    private static final Map<EnumDirection, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), EnumDirection.SOUTH, Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), EnumDirection.EAST, Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), EnumDirection.WEST, Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
    private static final Map<EnumDirection, VoxelShape> SHAPES_UP = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, VoxelShapes.or((VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(EnumDirection.NORTH), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), EnumDirection.SOUTH, VoxelShapes.or((VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(EnumDirection.SOUTH), Block.box(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), EnumDirection.EAST, VoxelShapes.or((VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(EnumDirection.EAST), Block.box(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), EnumDirection.WEST, VoxelShapes.or((VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(EnumDirection.WEST), Block.box(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
    private static final Map<IBlockData, VoxelShape> SHAPES_CACHE = Maps.newHashMap();
    private static final Vec3D[] COLORS = (Vec3D[]) SystemUtils.make(new Vec3D[16], (avec3d) -> {
        for (int i = 0; i <= 15; ++i) {
            float f = (float) i / 15.0F;
            float f1 = f * 0.6F + (f > 0.0F ? 0.4F : 0.3F);
            float f2 = MathHelper.clamp(f * f * 0.7F - 0.5F, 0.0F, 1.0F);
            float f3 = MathHelper.clamp(f * f * 0.6F - 0.7F, 0.0F, 1.0F);

            avec3d[i] = new Vec3D((double) f1, (double) f2, (double) f3);
        }

    });
    private static final float PARTICLE_DENSITY = 0.2F;
    private final IBlockData crossState;
    private boolean shouldSignal = true;

    public BlockRedstoneWire(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.NONE)).setValue(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.NONE)).setValue(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.NONE)).setValue(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.NONE)).setValue(BlockRedstoneWire.POWER, 0));
        this.crossState = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE)).setValue(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE);
        UnmodifiableIterator unmodifiableiterator = this.getStateDefinition().getPossibleStates().iterator();

        while (unmodifiableiterator.hasNext()) {
            IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

            if ((Integer) iblockdata.getValue(BlockRedstoneWire.POWER) == 0) {
                BlockRedstoneWire.SHAPES_CACHE.put(iblockdata, this.calculateShape(iblockdata));
            }
        }

    }

    private VoxelShape calculateShape(IBlockData iblockdata) {
        VoxelShape voxelshape = BlockRedstoneWire.SHAPE_DOT;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPropertyRedstoneSide blockpropertyredstoneside = (BlockPropertyRedstoneSide) iblockdata.getValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection));

            if (blockpropertyredstoneside == BlockPropertyRedstoneSide.SIDE) {
                voxelshape = VoxelShapes.or(voxelshape, (VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(enumdirection));
            } else if (blockpropertyredstoneside == BlockPropertyRedstoneSide.UP) {
                voxelshape = VoxelShapes.or(voxelshape, (VoxelShape) BlockRedstoneWire.SHAPES_UP.get(enumdirection));
            }
        }

        return voxelshape;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) BlockRedstoneWire.SHAPES_CACHE.get(iblockdata.setValue(BlockRedstoneWire.POWER, 0));
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        return this.getConnectionState(blockactioncontext.getLevel(), this.crossState, blockactioncontext.getClickedPos());
    }

    private IBlockData getConnectionState(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition) {
        boolean flag = isDot(iblockdata);

        iblockdata = this.getMissingConnections(iblockaccess, (IBlockData) this.defaultBlockState().setValue(BlockRedstoneWire.POWER, (Integer) iblockdata.getValue(BlockRedstoneWire.POWER)), blockposition);
        if (flag && isDot(iblockdata)) {
            return iblockdata;
        } else {
            boolean flag1 = ((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.NORTH)).isConnected();
            boolean flag2 = ((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.SOUTH)).isConnected();
            boolean flag3 = ((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.EAST)).isConnected();
            boolean flag4 = ((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.WEST)).isConnected();
            boolean flag5 = !flag1 && !flag2;
            boolean flag6 = !flag3 && !flag4;

            if (!flag4 && flag5) {
                iblockdata = (IBlockData) iblockdata.setValue(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE);
            }

            if (!flag3 && flag5) {
                iblockdata = (IBlockData) iblockdata.setValue(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE);
            }

            if (!flag1 && flag6) {
                iblockdata = (IBlockData) iblockdata.setValue(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE);
            }

            if (!flag2 && flag6) {
                iblockdata = (IBlockData) iblockdata.setValue(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE);
            }

            return iblockdata;
        }
    }

    private IBlockData getMissingConnections(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition) {
        boolean flag = !iblockaccess.getBlockState(blockposition.above()).isRedstoneConductor(iblockaccess, blockposition);
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (!((BlockPropertyRedstoneSide) iblockdata.getValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection))).isConnected()) {
                BlockPropertyRedstoneSide blockpropertyredstoneside = this.getConnectingSide(iblockaccess, blockposition, enumdirection, flag);

                iblockdata = (IBlockData) iblockdata.setValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection), blockpropertyredstoneside);
            }
        }

        return iblockdata;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.DOWN) {
            return iblockdata;
        } else if (enumdirection == EnumDirection.UP) {
            return this.getConnectionState(generatoraccess, iblockdata, blockposition);
        } else {
            BlockPropertyRedstoneSide blockpropertyredstoneside = this.getConnectingSide(generatoraccess, blockposition, enumdirection);

            return blockpropertyredstoneside.isConnected() == ((BlockPropertyRedstoneSide) iblockdata.getValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection))).isConnected() && !isCross(iblockdata) ? (IBlockData) iblockdata.setValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection), blockpropertyredstoneside) : this.getConnectionState(generatoraccess, (IBlockData) ((IBlockData) this.crossState.setValue(BlockRedstoneWire.POWER, (Integer) iblockdata.getValue(BlockRedstoneWire.POWER))).setValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection), blockpropertyredstoneside), blockposition);
        }
    }

    private static boolean isCross(IBlockData iblockdata) {
        return ((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.NORTH)).isConnected() && ((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.SOUTH)).isConnected() && ((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.EAST)).isConnected() && ((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.WEST)).isConnected();
    }

    private static boolean isDot(IBlockData iblockdata) {
        return !((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.NORTH)).isConnected() && !((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.SOUTH)).isConnected() && !((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.EAST)).isConnected() && !((BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.WEST)).isConnected();
    }

    @Override
    public void updateIndirectNeighbourShapes(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPropertyRedstoneSide blockpropertyredstoneside = (BlockPropertyRedstoneSide) iblockdata.getValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection));

            if (blockpropertyredstoneside != BlockPropertyRedstoneSide.NONE && !generatoraccess.getBlockState(blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection)).is((Block) this)) {
                blockposition_mutableblockposition.move(EnumDirection.DOWN);
                IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition_mutableblockposition);

                if (!iblockdata1.is(Blocks.OBSERVER)) {
                    BlockPosition blockposition1 = blockposition_mutableblockposition.relative(enumdirection.getOpposite());
                    IBlockData iblockdata2 = iblockdata1.updateShape(enumdirection.getOpposite(), generatoraccess.getBlockState(blockposition1), generatoraccess, blockposition_mutableblockposition, blockposition1);

                    updateOrDestroy(iblockdata1, iblockdata2, generatoraccess, blockposition_mutableblockposition, i, j);
                }

                blockposition_mutableblockposition.setWithOffset(blockposition, enumdirection).move(EnumDirection.UP);
                IBlockData iblockdata3 = generatoraccess.getBlockState(blockposition_mutableblockposition);

                if (!iblockdata3.is(Blocks.OBSERVER)) {
                    BlockPosition blockposition2 = blockposition_mutableblockposition.relative(enumdirection.getOpposite());
                    IBlockData iblockdata4 = iblockdata3.updateShape(enumdirection.getOpposite(), generatoraccess.getBlockState(blockposition2), generatoraccess, blockposition_mutableblockposition, blockposition2);

                    updateOrDestroy(iblockdata3, iblockdata4, generatoraccess, blockposition_mutableblockposition, i, j);
                }
            }
        }

    }

    private BlockPropertyRedstoneSide getConnectingSide(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getConnectingSide(iblockaccess, blockposition, enumdirection, !iblockaccess.getBlockState(blockposition.above()).isRedstoneConductor(iblockaccess, blockposition));
    }

    private BlockPropertyRedstoneSide getConnectingSide(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        BlockPosition blockposition1 = blockposition.relative(enumdirection);
        IBlockData iblockdata = iblockaccess.getBlockState(blockposition1);

        if (flag) {
            boolean flag1 = this.canSurviveOn(iblockaccess, blockposition1, iblockdata);

            if (flag1 && shouldConnectTo(iblockaccess.getBlockState(blockposition1.above()))) {
                if (iblockdata.isFaceSturdy(iblockaccess, blockposition1, enumdirection.getOpposite())) {
                    return BlockPropertyRedstoneSide.UP;
                }

                return BlockPropertyRedstoneSide.SIDE;
            }
        }

        return !shouldConnectTo(iblockdata, enumdirection) && (iblockdata.isRedstoneConductor(iblockaccess, blockposition1) || !shouldConnectTo(iblockaccess.getBlockState(blockposition1.below()))) ? BlockPropertyRedstoneSide.NONE : BlockPropertyRedstoneSide.SIDE;
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.below();
        IBlockData iblockdata1 = iworldreader.getBlockState(blockposition1);

        return this.canSurviveOn(iworldreader, blockposition1, iblockdata1);
    }

    private boolean canSurviveOn(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return iblockdata.isFaceSturdy(iblockaccess, blockposition, EnumDirection.UP) || iblockdata.is(Blocks.HOPPER);
    }

    private void updatePowerStrength(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.calculateTargetStrength(world, blockposition);

        if ((Integer) iblockdata.getValue(BlockRedstoneWire.POWER) != i) {
            if (world.getBlockState(blockposition) == iblockdata) {
                world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockRedstoneWire.POWER, i), 2);
            }

            Set<BlockPosition> set = Sets.newHashSet();

            set.add(blockposition);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                set.add(blockposition.relative(enumdirection));
            }

            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                world.updateNeighborsAt(blockposition1, this);
            }
        }

    }

    private int calculateTargetStrength(World world, BlockPosition blockposition) {
        this.shouldSignal = false;
        int i = world.getBestNeighborSignal(blockposition);

        this.shouldSignal = true;
        int j = 0;

        if (i < 15) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                BlockPosition blockposition1 = blockposition.relative(enumdirection);
                IBlockData iblockdata = world.getBlockState(blockposition1);

                j = Math.max(j, this.getWireSignal(iblockdata));
                BlockPosition blockposition2 = blockposition.above();

                if (iblockdata.isRedstoneConductor(world, blockposition1) && !world.getBlockState(blockposition2).isRedstoneConductor(world, blockposition2)) {
                    j = Math.max(j, this.getWireSignal(world.getBlockState(blockposition1.above())));
                } else if (!iblockdata.isRedstoneConductor(world, blockposition1)) {
                    j = Math.max(j, this.getWireSignal(world.getBlockState(blockposition1.below())));
                }
            }
        }

        return Math.max(i, j - 1);
    }

    private int getWireSignal(IBlockData iblockdata) {
        return iblockdata.is((Block) this) ? (Integer) iblockdata.getValue(BlockRedstoneWire.POWER) : 0;
    }

    private void checkCornerChangeAt(World world, BlockPosition blockposition) {
        if (world.getBlockState(blockposition).is((Block) this)) {
            world.updateNeighborsAt(blockposition, this);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.updateNeighborsAt(blockposition.relative(enumdirection), this);
            }

        }
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.is(iblockdata.getBlock()) && !world.isClientSide) {
            this.updatePowerStrength(world, blockposition, iblockdata);
            Iterator iterator = EnumDirection.EnumDirectionLimit.VERTICAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                world.updateNeighborsAt(blockposition.relative(enumdirection), this);
            }

            this.updateNeighborsOfNeighboringWires(world, blockposition);
        }
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.is(iblockdata1.getBlock())) {
            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
            if (!world.isClientSide) {
                EnumDirection[] aenumdirection = EnumDirection.values();
                int i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection = aenumdirection[j];

                    world.updateNeighborsAt(blockposition.relative(enumdirection), this);
                }

                this.updatePowerStrength(world, blockposition, iblockdata);
                this.updateNeighborsOfNeighboringWires(world, blockposition);
            }
        }
    }

    private void updateNeighborsOfNeighboringWires(World world, BlockPosition blockposition) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        EnumDirection enumdirection;

        while (iterator.hasNext()) {
            enumdirection = (EnumDirection) iterator.next();
            this.checkCornerChangeAt(world, blockposition.relative(enumdirection));
        }

        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            if (world.getBlockState(blockposition1).isRedstoneConductor(world, blockposition1)) {
                this.checkCornerChangeAt(world, blockposition1.above());
            } else {
                this.checkCornerChangeAt(world, blockposition1.below());
            }
        }

    }

    @Override
    public void neighborChanged(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            if (iblockdata.canSurvive(world, blockposition)) {
                this.updatePowerStrength(world, blockposition, iblockdata);
            } else {
                dropResources(iblockdata, world, blockposition);
                world.removeBlock(blockposition, false);
            }

        }
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !this.shouldSignal ? 0 : iblockdata.getSignal(iblockaccess, blockposition, enumdirection);
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (this.shouldSignal && enumdirection != EnumDirection.DOWN) {
            int i = (Integer) iblockdata.getValue(BlockRedstoneWire.POWER);

            return i == 0 ? 0 : (enumdirection != EnumDirection.UP && !((BlockPropertyRedstoneSide) this.getConnectionState(iblockaccess, iblockdata, blockposition).getValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection.getOpposite()))).isConnected() ? 0 : i);
        } else {
            return 0;
        }
    }

    protected static boolean shouldConnectTo(IBlockData iblockdata) {
        return shouldConnectTo(iblockdata, (EnumDirection) null);
    }

    protected static boolean shouldConnectTo(IBlockData iblockdata, @Nullable EnumDirection enumdirection) {
        if (iblockdata.is(Blocks.REDSTONE_WIRE)) {
            return true;
        } else if (iblockdata.is(Blocks.REPEATER)) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.getValue(BlockRepeater.FACING);

            return enumdirection1 == enumdirection || enumdirection1.getOpposite() == enumdirection;
        } else {
            return iblockdata.is(Blocks.OBSERVER) ? enumdirection == iblockdata.getValue(BlockObserver.FACING) : iblockdata.isSignalSource() && enumdirection != null;
        }
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return this.shouldSignal;
    }

    public static int getColorForPower(int i) {
        Vec3D vec3d = BlockRedstoneWire.COLORS[i];

        return MathHelper.color((float) vec3d.x(), (float) vec3d.y(), (float) vec3d.z());
    }

    private void spawnParticlesAlongLine(World world, Random random, BlockPosition blockposition, Vec3D vec3d, EnumDirection enumdirection, EnumDirection enumdirection1, float f, float f1) {
        float f2 = f1 - f;

        if (random.nextFloat() < 0.2F * f2) {
            float f3 = 0.4375F;
            float f4 = f + f2 * random.nextFloat();
            double d0 = 0.5D + (double) (0.4375F * (float) enumdirection.getStepX()) + (double) (f4 * (float) enumdirection1.getStepX());
            double d1 = 0.5D + (double) (0.4375F * (float) enumdirection.getStepY()) + (double) (f4 * (float) enumdirection1.getStepY());
            double d2 = 0.5D + (double) (0.4375F * (float) enumdirection.getStepZ()) + (double) (f4 * (float) enumdirection1.getStepZ());

            world.addParticle(new ParticleParamRedstone(new Vector3fa(vec3d), 1.0F), (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        int i = (Integer) iblockdata.getValue(BlockRedstoneWire.POWER);

        if (i != 0) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                BlockPropertyRedstoneSide blockpropertyredstoneside = (BlockPropertyRedstoneSide) iblockdata.getValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection));

                switch (blockpropertyredstoneside) {
                    case UP:
                        this.spawnParticlesAlongLine(world, random, blockposition, BlockRedstoneWire.COLORS[i], enumdirection, EnumDirection.UP, -0.5F, 0.5F);
                    case SIDE:
                        this.spawnParticlesAlongLine(world, random, blockposition, BlockRedstoneWire.COLORS[i], EnumDirection.DOWN, enumdirection, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        this.spawnParticlesAlongLine(world, random, blockposition, BlockRedstoneWire.COLORS[i], EnumDirection.DOWN, enumdirection, 0.0F, 0.3F);
                }
            }

        }
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockRedstoneWire.NORTH, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.SOUTH))).setValue(BlockRedstoneWire.EAST, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.WEST))).setValue(BlockRedstoneWire.SOUTH, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.NORTH))).setValue(BlockRedstoneWire.WEST, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.EAST));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockRedstoneWire.NORTH, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.EAST))).setValue(BlockRedstoneWire.EAST, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.SOUTH))).setValue(BlockRedstoneWire.SOUTH, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.WEST))).setValue(BlockRedstoneWire.WEST, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.NORTH));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockRedstoneWire.NORTH, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.WEST))).setValue(BlockRedstoneWire.EAST, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.NORTH))).setValue(BlockRedstoneWire.SOUTH, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.EAST))).setValue(BlockRedstoneWire.WEST, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.SOUTH));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockRedstoneWire.NORTH, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.SOUTH))).setValue(BlockRedstoneWire.SOUTH, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.NORTH));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockRedstoneWire.EAST, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.WEST))).setValue(BlockRedstoneWire.WEST, (BlockPropertyRedstoneSide) iblockdata.getValue(BlockRedstoneWire.EAST));
            default:
                return super.mirror(iblockdata, enumblockmirror);
        }
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockRedstoneWire.NORTH, BlockRedstoneWire.EAST, BlockRedstoneWire.SOUTH, BlockRedstoneWire.WEST, BlockRedstoneWire.POWER);
    }

    @Override
    public EnumInteractionResult use(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (!entityhuman.getAbilities().mayBuild) {
            return EnumInteractionResult.PASS;
        } else {
            if (isCross(iblockdata) || isDot(iblockdata)) {
                IBlockData iblockdata1 = isCross(iblockdata) ? this.defaultBlockState() : this.crossState;

                iblockdata1 = (IBlockData) iblockdata1.setValue(BlockRedstoneWire.POWER, (Integer) iblockdata.getValue(BlockRedstoneWire.POWER));
                iblockdata1 = this.getConnectionState(world, iblockdata1, blockposition);
                if (iblockdata1 != iblockdata) {
                    world.setBlock(blockposition, iblockdata1, 3);
                    this.updatesOnShapeChange(world, blockposition, iblockdata, iblockdata1);
                    return EnumInteractionResult.SUCCESS;
                }
            }

            return EnumInteractionResult.PASS;
        }
    }

    private void updatesOnShapeChange(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            if (((BlockPropertyRedstoneSide) iblockdata.getValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection))).isConnected() != ((BlockPropertyRedstoneSide) iblockdata1.getValue((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection))).isConnected() && world.getBlockState(blockposition1).isRedstoneConductor(world, blockposition1)) {
                world.updateNeighborsAtExceptFromFacing(blockposition1, iblockdata1.getBlock(), enumdirection.getOpposite());
            }
        }

    }
}
