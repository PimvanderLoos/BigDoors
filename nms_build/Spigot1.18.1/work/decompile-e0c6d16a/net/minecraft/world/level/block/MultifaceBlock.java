package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class MultifaceBlock extends Block {

    private static final float AABB_OFFSET = 1.0F;
    private static final VoxelShape UP_AABB = Block.box(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private static final Map<EnumDirection, BlockStateBoolean> PROPERTY_BY_DIRECTION = BlockSprawling.PROPERTY_BY_DIRECTION;
    private static final Map<EnumDirection, VoxelShape> SHAPE_BY_DIRECTION = (Map) SystemUtils.make(Maps.newEnumMap(EnumDirection.class), (enummap) -> {
        enummap.put(EnumDirection.NORTH, MultifaceBlock.NORTH_AABB);
        enummap.put(EnumDirection.EAST, MultifaceBlock.EAST_AABB);
        enummap.put(EnumDirection.SOUTH, MultifaceBlock.SOUTH_AABB);
        enummap.put(EnumDirection.WEST, MultifaceBlock.WEST_AABB);
        enummap.put(EnumDirection.UP, MultifaceBlock.UP_AABB);
        enummap.put(EnumDirection.DOWN, MultifaceBlock.DOWN_AABB);
    });
    protected static final EnumDirection[] DIRECTIONS = EnumDirection.values();
    private final ImmutableMap<IBlockData, VoxelShape> shapesCache;
    private final boolean canRotate;
    private final boolean canMirrorX;
    private final boolean canMirrorZ;

    public MultifaceBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState(getDefaultMultifaceState(this.stateDefinition));
        this.shapesCache = this.getShapeForEachState(MultifaceBlock::calculateMultifaceShape);
        this.canRotate = EnumDirection.EnumDirectionLimit.HORIZONTAL.stream().allMatch(this::isFaceSupported);
        this.canMirrorX = EnumDirection.EnumDirectionLimit.HORIZONTAL.stream().filter(EnumDirection.EnumAxis.X).filter(this::isFaceSupported).count() % 2L == 0L;
        this.canMirrorZ = EnumDirection.EnumDirectionLimit.HORIZONTAL.stream().filter(EnumDirection.EnumAxis.Z).filter(this::isFaceSupported).count() % 2L == 0L;
    }

    protected boolean isFaceSupported(EnumDirection enumdirection) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        EnumDirection[] aenumdirection = MultifaceBlock.DIRECTIONS;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.isFaceSupported(enumdirection)) {
                blockstatelist_a.add(getFaceProperty(enumdirection));
            }
        }

    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return !hasAnyFace(iblockdata) ? Blocks.AIR.defaultBlockState() : (hasFace(iblockdata, enumdirection) && !canAttachTo(generatoraccess, enumdirection, blockposition1, iblockdata1) ? removeFace(iblockdata, getFaceProperty(enumdirection)) : iblockdata);
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.shapesCache.get(iblockdata);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        boolean flag = false;
        EnumDirection[] aenumdirection = MultifaceBlock.DIRECTIONS;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (hasFace(iblockdata, enumdirection)) {
                BlockPosition blockposition1 = blockposition.relative(enumdirection);

                if (!canAttachTo(iworldreader, enumdirection, blockposition1, iworldreader.getBlockState(blockposition1))) {
                    return false;
                }

                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return hasAnyVacantFace(iblockdata);
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        IBlockData iblockdata = world.getBlockState(blockposition);

        return (IBlockData) Arrays.stream(blockactioncontext.getNearestLookingDirections()).map((enumdirection) -> {
            return this.getStateForPlacement(iblockdata, world, blockposition, enumdirection);
        }).filter(Objects::nonNull).findFirst().orElse((Object) null);
    }

    @Nullable
    public IBlockData getStateForPlacement(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (!this.isFaceSupported(enumdirection)) {
            return null;
        } else {
            IBlockData iblockdata1;

            if (iblockdata.is((Block) this)) {
                if (hasFace(iblockdata, enumdirection)) {
                    return null;
                }

                iblockdata1 = iblockdata;
            } else if (this.isWaterloggable() && iblockdata.getFluidState().isSourceOfType(FluidTypes.WATER)) {
                iblockdata1 = (IBlockData) this.defaultBlockState().setValue(BlockProperties.WATERLOGGED, true);
            } else {
                iblockdata1 = this.defaultBlockState();
            }

            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            return canAttachTo(iblockaccess, enumdirection, blockposition1, iblockaccess.getBlockState(blockposition1)) ? (IBlockData) iblockdata1.setValue(getFaceProperty(enumdirection), true) : null;
        }
    }

    @Override
    public IBlockData rotate(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        if (!this.canRotate) {
            return iblockdata;
        } else {
            Objects.requireNonNull(enumblockrotation);
            return this.mapDirections(iblockdata, enumblockrotation::rotate);
        }
    }

    @Override
    public IBlockData mirror(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        if (enumblockmirror == EnumBlockMirror.FRONT_BACK && !this.canMirrorX) {
            return iblockdata;
        } else if (enumblockmirror == EnumBlockMirror.LEFT_RIGHT && !this.canMirrorZ) {
            return iblockdata;
        } else {
            Objects.requireNonNull(enumblockmirror);
            return this.mapDirections(iblockdata, enumblockmirror::mirror);
        }
    }

    private IBlockData mapDirections(IBlockData iblockdata, Function<EnumDirection, EnumDirection> function) {
        IBlockData iblockdata1 = iblockdata;
        EnumDirection[] aenumdirection = MultifaceBlock.DIRECTIONS;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.isFaceSupported(enumdirection)) {
                iblockdata1 = (IBlockData) iblockdata1.setValue(getFaceProperty((EnumDirection) function.apply(enumdirection)), (Boolean) iblockdata.getValue(getFaceProperty(enumdirection)));
            }
        }

        return iblockdata1;
    }

    public boolean spreadFromRandomFaceTowardRandomDirection(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        List<EnumDirection> list = Lists.newArrayList(MultifaceBlock.DIRECTIONS);

        Collections.shuffle(list);
        return list.stream().filter((enumdirection) -> {
            return hasFace(iblockdata, enumdirection);
        }).anyMatch((enumdirection) -> {
            return this.spreadFromFaceTowardRandomDirection(iblockdata, worldserver, blockposition, enumdirection, random, false);
        });
    }

    public boolean spreadFromFaceTowardRandomDirection(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, Random random, boolean flag) {
        List<EnumDirection> list = Arrays.asList(MultifaceBlock.DIRECTIONS);

        Collections.shuffle(list, random);
        return list.stream().anyMatch((enumdirection1) -> {
            return this.spreadFromFaceTowardDirection(iblockdata, generatoraccess, blockposition, enumdirection, enumdirection1, flag);
        });
    }

    public boolean spreadFromFaceTowardDirection(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, boolean flag) {
        Optional<Pair<BlockPosition, EnumDirection>> optional = this.getSpreadFromFaceTowardDirection(iblockdata, generatoraccess, blockposition, enumdirection, enumdirection1);

        if (optional.isPresent()) {
            Pair<BlockPosition, EnumDirection> pair = (Pair) optional.get();

            return this.spreadToFace(generatoraccess, (BlockPosition) pair.getFirst(), (EnumDirection) pair.getSecond(), flag);
        } else {
            return false;
        }
    }

    protected boolean canSpread(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return Stream.of(MultifaceBlock.DIRECTIONS).anyMatch((enumdirection1) -> {
            return this.getSpreadFromFaceTowardDirection(iblockdata, iblockaccess, blockposition, enumdirection, enumdirection1).isPresent();
        });
    }

    private Optional<Pair<BlockPosition, EnumDirection>> getSpreadFromFaceTowardDirection(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1) {
        if (enumdirection1.getAxis() != enumdirection.getAxis() && hasFace(iblockdata, enumdirection) && !hasFace(iblockdata, enumdirection1)) {
            if (this.canSpreadToFace(iblockaccess, blockposition, enumdirection1)) {
                return Optional.of(Pair.of(blockposition, enumdirection1));
            } else {
                BlockPosition blockposition1 = blockposition.relative(enumdirection1);

                if (this.canSpreadToFace(iblockaccess, blockposition1, enumdirection)) {
                    return Optional.of(Pair.of(blockposition1, enumdirection));
                } else {
                    BlockPosition blockposition2 = blockposition1.relative(enumdirection);
                    EnumDirection enumdirection2 = enumdirection1.getOpposite();

                    return this.canSpreadToFace(iblockaccess, blockposition2, enumdirection2) ? Optional.of(Pair.of(blockposition2, enumdirection2)) : Optional.empty();
                }
            }
        } else {
            return Optional.empty();
        }
    }

    private boolean canSpreadToFace(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getBlockState(blockposition);

        if (!this.canSpreadInto(iblockdata)) {
            return false;
        } else {
            IBlockData iblockdata1 = this.getStateForPlacement(iblockdata, iblockaccess, blockposition, enumdirection);

            return iblockdata1 != null;
        }
    }

    private boolean spreadToFace(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        IBlockData iblockdata = generatoraccess.getBlockState(blockposition);
        IBlockData iblockdata1 = this.getStateForPlacement(iblockdata, generatoraccess, blockposition, enumdirection);

        if (iblockdata1 != null) {
            if (flag) {
                generatoraccess.getChunk(blockposition).markPosForPostprocessing(blockposition);
            }

            return generatoraccess.setBlock(blockposition, iblockdata1, 2);
        } else {
            return false;
        }
    }

    private boolean canSpreadInto(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.is((Block) this) || iblockdata.is(Blocks.WATER) && iblockdata.getFluidState().isSource();
    }

    private static boolean hasFace(IBlockData iblockdata, EnumDirection enumdirection) {
        BlockStateBoolean blockstateboolean = getFaceProperty(enumdirection);

        return iblockdata.hasProperty(blockstateboolean) && (Boolean) iblockdata.getValue(blockstateboolean);
    }

    private static boolean canAttachTo(IBlockAccess iblockaccess, EnumDirection enumdirection, BlockPosition blockposition, IBlockData iblockdata) {
        return Block.isFaceFull(iblockdata.getCollisionShape(iblockaccess, blockposition), enumdirection.getOpposite());
    }

    private boolean isWaterloggable() {
        return this.stateDefinition.getProperties().contains(BlockProperties.WATERLOGGED);
    }

    private static IBlockData removeFace(IBlockData iblockdata, BlockStateBoolean blockstateboolean) {
        IBlockData iblockdata1 = (IBlockData) iblockdata.setValue(blockstateboolean, false);

        return hasAnyFace(iblockdata1) ? iblockdata1 : Blocks.AIR.defaultBlockState();
    }

    public static BlockStateBoolean getFaceProperty(EnumDirection enumdirection) {
        return (BlockStateBoolean) MultifaceBlock.PROPERTY_BY_DIRECTION.get(enumdirection);
    }

    private static IBlockData getDefaultMultifaceState(BlockStateList<Block, IBlockData> blockstatelist) {
        IBlockData iblockdata = (IBlockData) blockstatelist.any();
        Iterator iterator = MultifaceBlock.PROPERTY_BY_DIRECTION.values().iterator();

        while (iterator.hasNext()) {
            BlockStateBoolean blockstateboolean = (BlockStateBoolean) iterator.next();

            if (iblockdata.hasProperty(blockstateboolean)) {
                iblockdata = (IBlockData) iblockdata.setValue(blockstateboolean, false);
            }
        }

        return iblockdata;
    }

    private static VoxelShape calculateMultifaceShape(IBlockData iblockdata) {
        VoxelShape voxelshape = VoxelShapes.empty();
        EnumDirection[] aenumdirection = MultifaceBlock.DIRECTIONS;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (hasFace(iblockdata, enumdirection)) {
                voxelshape = VoxelShapes.or(voxelshape, (VoxelShape) MultifaceBlock.SHAPE_BY_DIRECTION.get(enumdirection));
            }
        }

        return voxelshape.isEmpty() ? VoxelShapes.block() : voxelshape;
    }

    protected static boolean hasAnyFace(IBlockData iblockdata) {
        return Arrays.stream(MultifaceBlock.DIRECTIONS).anyMatch((enumdirection) -> {
            return hasFace(iblockdata, enumdirection);
        });
    }

    private static boolean hasAnyVacantFace(IBlockData iblockdata) {
        return Arrays.stream(MultifaceBlock.DIRECTIONS).anyMatch((enumdirection) -> {
            return !hasFace(iblockdata, enumdirection);
        });
    }
}
