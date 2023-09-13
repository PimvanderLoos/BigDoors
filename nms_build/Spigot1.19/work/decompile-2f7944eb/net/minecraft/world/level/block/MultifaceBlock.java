package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
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

public abstract class MultifaceBlock extends Block {

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

    public static Set<EnumDirection> availableFaces(IBlockData iblockdata) {
        if (!(iblockdata.getBlock() instanceof MultifaceBlock)) {
            return Set.of();
        } else {
            Set<EnumDirection> set = EnumSet.noneOf(EnumDirection.class);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (hasFace(iblockdata, enumdirection)) {
                    set.add(enumdirection);
                }
            }

            return set;
        }
    }

    public static Set<EnumDirection> unpack(byte b0) {
        Set<EnumDirection> set = EnumSet.noneOf(EnumDirection.class);
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if ((b0 & (byte) (1 << enumdirection.ordinal())) > 0) {
                set.add(enumdirection);
            }
        }

        return set;
    }

    public static byte pack(Collection<EnumDirection> collection) {
        byte b0 = 0;

        EnumDirection enumdirection;

        for (Iterator iterator = collection.iterator(); iterator.hasNext(); b0 = (byte) (b0 | 1 << enumdirection.ordinal())) {
            enumdirection = (EnumDirection) iterator.next();
        }

        return b0;
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

    public boolean isValidStateForPlacement(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        if (this.isFaceSupported(enumdirection) && (!iblockdata.is((Block) this) || !hasFace(iblockdata, enumdirection))) {
            BlockPosition blockposition1 = blockposition.relative(enumdirection);

            return canAttachTo(iblockaccess, enumdirection, blockposition1, iblockaccess.getBlockState(blockposition1));
        } else {
            return false;
        }
    }

    @Nullable
    public IBlockData getStateForPlacement(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (!this.isValidStateForPlacement(iblockaccess, iblockdata, blockposition, enumdirection)) {
            return null;
        } else {
            IBlockData iblockdata1;

            if (iblockdata.is((Block) this)) {
                iblockdata1 = iblockdata;
            } else if (this.isWaterloggable() && iblockdata.getFluidState().isSourceOfType(FluidTypes.WATER)) {
                iblockdata1 = (IBlockData) this.defaultBlockState().setValue(BlockProperties.WATERLOGGED, true);
            } else {
                iblockdata1 = this.defaultBlockState();
            }

            return (IBlockData) iblockdata1.setValue(getFaceProperty(enumdirection), true);
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

    public static boolean hasFace(IBlockData iblockdata, EnumDirection enumdirection) {
        BlockStateBoolean blockstateboolean = getFaceProperty(enumdirection);

        return iblockdata.hasProperty(blockstateboolean) && (Boolean) iblockdata.getValue(blockstateboolean);
    }

    public static boolean canAttachTo(IBlockAccess iblockaccess, EnumDirection enumdirection, BlockPosition blockposition, IBlockData iblockdata) {
        return Block.isFaceFull(iblockdata.getBlockSupportShape(iblockaccess, blockposition), enumdirection.getOpposite()) || Block.isFaceFull(iblockdata.getCollisionShape(iblockaccess, blockposition), enumdirection.getOpposite());
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

    public abstract MultifaceSpreader getSpreader();
}
