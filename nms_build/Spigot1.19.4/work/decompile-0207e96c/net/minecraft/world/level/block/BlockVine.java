package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class BlockVine extends Block {

    public static final BlockStateBoolean UP = BlockSprawling.UP;
    public static final BlockStateBoolean NORTH = BlockSprawling.NORTH;
    public static final BlockStateBoolean EAST = BlockSprawling.EAST;
    public static final BlockStateBoolean SOUTH = BlockSprawling.SOUTH;
    public static final BlockStateBoolean WEST = BlockSprawling.WEST;
    public static final Map<EnumDirection, BlockStateBoolean> PROPERTY_BY_DIRECTION = (Map) BlockSprawling.PROPERTY_BY_DIRECTION.entrySet().stream().filter((entry) -> {
        return entry.getKey() != EnumDirection.DOWN;
    }).collect(SystemUtils.toMap());
    protected static final float AABB_OFFSET = 1.0F;
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private final Map<IBlockData, VoxelShape> shapesCache;

    public BlockVine(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockVine.UP, false)).setValue(BlockVine.NORTH, false)).setValue(BlockVine.EAST, false)).setValue(BlockVine.SOUTH, false)).setValue(BlockVine.WEST, false));
        this.shapesCache = ImmutableMap.copyOf((Map) this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), BlockVine::calculateShape)));
    }

    private static VoxelShape calculateShape(IBlockData iblockdata) {
        VoxelShape voxelshape = VoxelShapes.empty();

        if ((Boolean) iblockdata.getValue(BlockVine.UP)) {
            voxelshape = BlockVine.UP_AABB;
        }

        if ((Boolean) iblockdata.getValue(BlockVine.NORTH)) {
            voxelshape = VoxelShapes.or(voxelshape, BlockVine.NORTH_AABB);
        }

        if ((Boolean) iblockdata.getValue(BlockVine.SOUTH)) {
            voxelshape = VoxelShapes.or(voxelshape, BlockVine.SOUTH_AABB);
        }

        if ((Boolean) iblockdata.getValue(BlockVine.EAST)) {
            voxelshape = VoxelShapes.or(voxelshape, BlockVine.EAST_AABB);
        }

        if ((Boolean) iblockdata.getValue(BlockVine.WEST)) {
            voxelshape = VoxelShapes.or(voxelshape, BlockVine.WEST_AABB);
        }

        return voxelshape.isEmpty() ? VoxelShapes.block() : voxelshape;
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.shapesCache.get(iblockdata);
    }

    @Override
    public boolean propagatesSkylightDown(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return this.hasFaces(this.getUpdatedState(iblockdata, iworldreader, blockposition));
    }

    private boolean hasFaces(IBlockData iblockdata) {
        return this.countFaces(iblockdata) > 0;
    }

    private int countFaces(IBlockData iblockdata) {
        int i = 0;
        Iterator iterator = BlockVine.PROPERTY_BY_DIRECTION.values().iterator();

        while (iterator.hasNext()) {
            BlockStateBoolean blockstateboolean = (BlockStateBoolean) iterator.next();

            if ((Boolean) iblockdata.getValue(blockstateboolean)) {
                ++i;
            }
        }

        return i;
    }

    private boolean canSupportAtFace(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (enumdirection == EnumDirection.DOWN) {
            return false;
        } else {
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            if (isAcceptableNeighbour(iblockaccess, blockposition1, enumdirection)) {
                return true;
            } else if (enumdirection.getAxis() == EnumDirection.EnumAxis.Y) {
                return false;
            } else {
                BlockStateBoolean blockstateboolean = (BlockStateBoolean) BlockVine.PROPERTY_BY_DIRECTION.get(enumdirection);
                IBlockData iblockdata = iblockaccess.getBlockState(blockposition.above());

                return iblockdata.is((Block) this) && (Boolean) iblockdata.getValue(blockstateboolean);
            }
        }
    }

    public static boolean isAcceptableNeighbour(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return MultifaceBlock.canAttachTo(iblockaccess, enumdirection, blockposition, iblockaccess.getBlockState(blockposition));
    }

    private IBlockData getUpdatedState(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.above();

        if ((Boolean) iblockdata.getValue(BlockVine.UP)) {
            iblockdata = (IBlockData) iblockdata.setValue(BlockVine.UP, isAcceptableNeighbour(iblockaccess, blockposition1, EnumDirection.DOWN));
        }

        IBlockData iblockdata1 = null;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockStateBoolean blockstateboolean = getPropertyForFace(enumdirection);

            if ((Boolean) iblockdata.getValue(blockstateboolean)) {
                boolean flag = this.canSupportAtFace(iblockaccess, blockposition, enumdirection);

                if (!flag) {
                    if (iblockdata1 == null) {
                        iblockdata1 = iblockaccess.getBlockState(blockposition1);
                    }

                    flag = iblockdata1.is((Block) this) && (Boolean) iblockdata1.getValue(blockstateboolean);
                }

                iblockdata = (IBlockData) iblockdata.setValue(blockstateboolean, flag);
            }
        }

        return iblockdata;
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.DOWN) {
            return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        } else {
            IBlockData iblockdata2 = this.getUpdatedState(iblockdata, generatoraccess, blockposition);

            return !this.hasFaces(iblockdata2) ? Blocks.AIR.defaultBlockState() : iblockdata2;
        }
    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if (worldserver.getGameRules().getBoolean(GameRules.RULE_DO_VINES_SPREAD)) {
            if (randomsource.nextInt(4) == 0) {
                EnumDirection enumdirection = EnumDirection.getRandom(randomsource);
                BlockPosition blockposition1 = blockposition.above();
                BlockPosition blockposition2;
                IBlockData iblockdata1;
                EnumDirection enumdirection1;

                if (enumdirection.getAxis().isHorizontal() && !(Boolean) iblockdata.getValue(getPropertyForFace(enumdirection))) {
                    if (this.canSpread(worldserver, blockposition)) {
                        blockposition2 = blockposition.relative(enumdirection);
                        iblockdata1 = worldserver.getBlockState(blockposition2);
                        if (iblockdata1.isAir()) {
                            enumdirection1 = enumdirection.getClockWise();
                            EnumDirection enumdirection2 = enumdirection.getCounterClockWise();
                            boolean flag = (Boolean) iblockdata.getValue(getPropertyForFace(enumdirection1));
                            boolean flag1 = (Boolean) iblockdata.getValue(getPropertyForFace(enumdirection2));
                            BlockPosition blockposition3 = blockposition2.relative(enumdirection1);
                            BlockPosition blockposition4 = blockposition2.relative(enumdirection2);

                            if (flag && isAcceptableNeighbour(worldserver, blockposition3, enumdirection1)) {
                                worldserver.setBlock(blockposition2, (IBlockData) this.defaultBlockState().setValue(getPropertyForFace(enumdirection1), true), 2);
                            } else if (flag1 && isAcceptableNeighbour(worldserver, blockposition4, enumdirection2)) {
                                worldserver.setBlock(blockposition2, (IBlockData) this.defaultBlockState().setValue(getPropertyForFace(enumdirection2), true), 2);
                            } else {
                                EnumDirection enumdirection3 = enumdirection.getOpposite();

                                if (flag && worldserver.isEmptyBlock(blockposition3) && isAcceptableNeighbour(worldserver, blockposition.relative(enumdirection1), enumdirection3)) {
                                    worldserver.setBlock(blockposition3, (IBlockData) this.defaultBlockState().setValue(getPropertyForFace(enumdirection3), true), 2);
                                } else if (flag1 && worldserver.isEmptyBlock(blockposition4) && isAcceptableNeighbour(worldserver, blockposition.relative(enumdirection2), enumdirection3)) {
                                    worldserver.setBlock(blockposition4, (IBlockData) this.defaultBlockState().setValue(getPropertyForFace(enumdirection3), true), 2);
                                } else if ((double) randomsource.nextFloat() < 0.05D && isAcceptableNeighbour(worldserver, blockposition2.above(), EnumDirection.UP)) {
                                    worldserver.setBlock(blockposition2, (IBlockData) this.defaultBlockState().setValue(BlockVine.UP, true), 2);
                                }
                            }
                        } else if (isAcceptableNeighbour(worldserver, blockposition2, enumdirection)) {
                            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(getPropertyForFace(enumdirection), true), 2);
                        }

                    }
                } else {
                    if (enumdirection == EnumDirection.UP && blockposition.getY() < worldserver.getMaxBuildHeight() - 1) {
                        if (this.canSupportAtFace(worldserver, blockposition, enumdirection)) {
                            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockVine.UP, true), 2);
                            return;
                        }

                        if (worldserver.isEmptyBlock(blockposition1)) {
                            if (!this.canSpread(worldserver, blockposition)) {
                                return;
                            }

                            IBlockData iblockdata2 = iblockdata;
                            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                            while (iterator.hasNext()) {
                                enumdirection1 = (EnumDirection) iterator.next();
                                if (randomsource.nextBoolean() || !isAcceptableNeighbour(worldserver, blockposition1.relative(enumdirection1), enumdirection1)) {
                                    iblockdata2 = (IBlockData) iblockdata2.setValue(getPropertyForFace(enumdirection1), false);
                                }
                            }

                            if (this.hasHorizontalConnection(iblockdata2)) {
                                worldserver.setBlock(blockposition1, iblockdata2, 2);
                            }

                            return;
                        }
                    }

                    if (blockposition.getY() > worldserver.getMinBuildHeight()) {
                        blockposition2 = blockposition.below();
                        iblockdata1 = worldserver.getBlockState(blockposition2);
                        if (iblockdata1.isAir() || iblockdata1.is((Block) this)) {
                            IBlockData iblockdata3 = iblockdata1.isAir() ? this.defaultBlockState() : iblockdata1;
                            IBlockData iblockdata4 = this.copyRandomFaces(iblockdata, iblockdata3, randomsource);

                            if (iblockdata3 != iblockdata4 && this.hasHorizontalConnection(iblockdata4)) {
                                worldserver.setBlock(blockposition2, iblockdata4, 2);
                            }
                        }
                    }

                }
            }
        }
    }

    private IBlockData copyRandomFaces(IBlockData iblockdata, IBlockData iblockdata1, RandomSource randomsource) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (randomsource.nextBoolean()) {
                BlockStateBoolean blockstateboolean = getPropertyForFace(enumdirection);

                if ((Boolean) iblockdata.getValue(blockstateboolean)) {
                    iblockdata1 = (IBlockData) iblockdata1.setValue(blockstateboolean, true);
                }
            }
        }

        return iblockdata1;
    }

    private boolean hasHorizontalConnection(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(BlockVine.NORTH) || (Boolean) iblockdata.getValue(BlockVine.EAST) || (Boolean) iblockdata.getValue(BlockVine.SOUTH) || (Boolean) iblockdata.getValue(BlockVine.WEST);
    }

    private boolean canSpread(IBlockAccess iblockaccess, BlockPosition blockposition) {
        boolean flag = true;
        Iterable<BlockPosition> iterable = BlockPosition.betweenClosed(blockposition.getX() - 4, blockposition.getY() - 1, blockposition.getZ() - 4, blockposition.getX() + 4, blockposition.getY() + 1, blockposition.getZ() + 4);
        int i = 5;
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition1 = (BlockPosition) iterator.next();

            if (iblockaccess.getBlockState(blockposition1).is((Block) this)) {
                --i;
                if (i <= 0) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        IBlockData iblockdata1 = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos());

        return iblockdata1.is((Block) this) ? this.countFaces(iblockdata1) < BlockVine.PROPERTY_BY_DIRECTION.size() : super.canBeReplaced(iblockdata, blockactioncontext);
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getLevel().getBlockState(blockactioncontext.getClickedPos());
        boolean flag = iblockdata.is((Block) this);
        IBlockData iblockdata1 = flag ? iblockdata : this.defaultBlockState();
        EnumDirection[] aenumdirection = blockactioncontext.getNearestLookingDirections();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection != EnumDirection.DOWN) {
                BlockStateBoolean blockstateboolean = getPropertyForFace(enumdirection);
                boolean flag1 = flag && (Boolean) iblockdata.getValue(blockstateboolean);

                if (!flag1 && this.canSupportAtFace(blockactioncontext.getLevel(), blockactioncontext.getClickedPos(), enumdirection)) {
                    return (IBlockData) iblockdata1.setValue(blockstateboolean, true);
                }
            }
        }

        return flag ? iblockdata1 : null;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockVine.UP, BlockVine.NORTH, BlockVine.EAST, BlockVine.SOUTH, BlockVine.WEST);
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockVine.NORTH, (Boolean) iblockdata.getValue(BlockVine.SOUTH))).setValue(BlockVine.EAST, (Boolean) iblockdata.getValue(BlockVine.WEST))).setValue(BlockVine.SOUTH, (Boolean) iblockdata.getValue(BlockVine.NORTH))).setValue(BlockVine.WEST, (Boolean) iblockdata.getValue(BlockVine.EAST));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockVine.NORTH, (Boolean) iblockdata.getValue(BlockVine.EAST))).setValue(BlockVine.EAST, (Boolean) iblockdata.getValue(BlockVine.SOUTH))).setValue(BlockVine.SOUTH, (Boolean) iblockdata.getValue(BlockVine.WEST))).setValue(BlockVine.WEST, (Boolean) iblockdata.getValue(BlockVine.NORTH));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.setValue(BlockVine.NORTH, (Boolean) iblockdata.getValue(BlockVine.WEST))).setValue(BlockVine.EAST, (Boolean) iblockdata.getValue(BlockVine.NORTH))).setValue(BlockVine.SOUTH, (Boolean) iblockdata.getValue(BlockVine.EAST))).setValue(BlockVine.WEST, (Boolean) iblockdata.getValue(BlockVine.SOUTH));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockVine.NORTH, (Boolean) iblockdata.getValue(BlockVine.SOUTH))).setValue(BlockVine.SOUTH, (Boolean) iblockdata.getValue(BlockVine.NORTH));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.setValue(BlockVine.EAST, (Boolean) iblockdata.getValue(BlockVine.WEST))).setValue(BlockVine.WEST, (Boolean) iblockdata.getValue(BlockVine.EAST));
            default:
                return super.mirror(iblockdata, enumblockmirror);
        }
    }

    public static BlockStateBoolean getPropertyForFace(EnumDirection enumdirection) {
        return (BlockStateBoolean) BlockVine.PROPERTY_BY_DIRECTION.get(enumdirection);
    }
}
