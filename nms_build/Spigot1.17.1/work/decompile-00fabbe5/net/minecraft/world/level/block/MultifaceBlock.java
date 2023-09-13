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
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class MultifaceBlock extends Block {

    private static final float AABB_OFFSET = 1.0F;
    private static final VoxelShape UP_AABB = Block.a(0.0D, 15.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape DOWN_AABB = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);
    private static final VoxelShape WEST_AABB = Block.a(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    private static final VoxelShape EAST_AABB = Block.a(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape NORTH_AABB = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    private static final VoxelShape SOUTH_AABB = Block.a(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);
    private static final Map<EnumDirection, BlockStateBoolean> PROPERTY_BY_DIRECTION = BlockSprawling.PROPERTY_BY_DIRECTION;
    private static final Map<EnumDirection, VoxelShape> SHAPE_BY_DIRECTION = (Map) SystemUtils.a((Object) Maps.newEnumMap(EnumDirection.class), (enummap) -> {
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
        this.k(a(this.stateDefinition));
        this.shapesCache = this.a(MultifaceBlock::o);
        this.canRotate = EnumDirection.EnumDirectionLimit.HORIZONTAL.a().allMatch(this::a);
        this.canMirrorX = EnumDirection.EnumDirectionLimit.HORIZONTAL.a().filter(EnumDirection.EnumAxis.X).filter(this::a).count() % 2L == 0L;
        this.canMirrorZ = EnumDirection.EnumDirectionLimit.HORIZONTAL.a().filter(EnumDirection.EnumAxis.Z).filter(this::a).count() % 2L == 0L;
    }

    protected boolean a(EnumDirection enumdirection) {
        return true;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        EnumDirection[] aenumdirection = MultifaceBlock.DIRECTIONS;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.a(enumdirection)) {
                blockstatelist_a.a(b(enumdirection));
            }
        }

    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return !h(iblockdata) ? Blocks.AIR.getBlockData() : (a(iblockdata, enumdirection) && !a((IBlockAccess) generatoraccess, enumdirection, blockposition1, iblockdata1) ? a(iblockdata, b(enumdirection)) : iblockdata);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) this.shapesCache.get(iblockdata);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        boolean flag = false;
        EnumDirection[] aenumdirection = MultifaceBlock.DIRECTIONS;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (a(iblockdata, enumdirection)) {
                BlockPosition blockposition1 = blockposition.shift(enumdirection);

                if (!a((IBlockAccess) iworldreader, enumdirection, blockposition1, iworldreader.getType(blockposition1))) {
                    return false;
                }

                flag = true;
            }
        }

        return flag;
    }

    @Override
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return p(iblockdata);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        return (IBlockData) Arrays.stream(blockactioncontext.f()).map((enumdirection) -> {
            return this.c(iblockdata, (IBlockAccess) world, blockposition, enumdirection);
        }).filter(Objects::nonNull).findFirst().orElse((Object) null);
    }

    @Nullable
    public IBlockData c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (!this.a(enumdirection)) {
            return null;
        } else {
            IBlockData iblockdata1;

            if (iblockdata.a((Block) this)) {
                if (a(iblockdata, enumdirection)) {
                    return null;
                }

                iblockdata1 = iblockdata;
            } else if (this.q() && iblockdata.getFluid().a((FluidType) FluidTypes.WATER)) {
                iblockdata1 = (IBlockData) this.getBlockData().set(BlockProperties.WATERLOGGED, true);
            } else {
                iblockdata1 = this.getBlockData();
            }

            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            return a(iblockaccess, enumdirection, blockposition1, iblockaccess.getType(blockposition1)) ? (IBlockData) iblockdata1.set(b(enumdirection), true) : null;
        }
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        if (!this.canRotate) {
            return iblockdata;
        } else {
            Objects.requireNonNull(enumblockrotation);
            return this.a(iblockdata, enumblockrotation::a);
        }
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        if (enumblockmirror == EnumBlockMirror.FRONT_BACK && !this.canMirrorX) {
            return iblockdata;
        } else if (enumblockmirror == EnumBlockMirror.LEFT_RIGHT && !this.canMirrorZ) {
            return iblockdata;
        } else {
            Objects.requireNonNull(enumblockmirror);
            return this.a(iblockdata, enumblockmirror::b);
        }
    }

    private IBlockData a(IBlockData iblockdata, Function<EnumDirection, EnumDirection> function) {
        IBlockData iblockdata1 = iblockdata;
        EnumDirection[] aenumdirection = MultifaceBlock.DIRECTIONS;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.a(enumdirection)) {
                iblockdata1 = (IBlockData) iblockdata1.set(b((EnumDirection) function.apply(enumdirection)), (Boolean) iblockdata.get(b(enumdirection)));
            }
        }

        return iblockdata1;
    }

    public boolean c(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        List<EnumDirection> list = Lists.newArrayList(MultifaceBlock.DIRECTIONS);

        Collections.shuffle(list);
        return list.stream().filter((enumdirection) -> {
            return a(iblockdata, enumdirection);
        }).anyMatch((enumdirection) -> {
            return this.a(iblockdata, worldserver, blockposition, enumdirection, random, false);
        });
    }

    public boolean a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, Random random, boolean flag) {
        List<EnumDirection> list = Arrays.asList(MultifaceBlock.DIRECTIONS);

        Collections.shuffle(list, random);
        return list.stream().anyMatch((enumdirection1) -> {
            return this.a(iblockdata, generatoraccess, blockposition, enumdirection, enumdirection1, flag);
        });
    }

    public boolean a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1, boolean flag) {
        Optional<Pair<BlockPosition, EnumDirection>> optional = this.a(iblockdata, (IBlockAccess) generatoraccess, blockposition, enumdirection, enumdirection1);

        if (optional.isPresent()) {
            Pair<BlockPosition, EnumDirection> pair = (Pair) optional.get();

            return this.a(generatoraccess, (BlockPosition) pair.getFirst(), (EnumDirection) pair.getSecond(), flag);
        } else {
            return false;
        }
    }

    protected boolean d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return Stream.of(MultifaceBlock.DIRECTIONS).anyMatch((enumdirection1) -> {
            return this.a(iblockdata, iblockaccess, blockposition, enumdirection, enumdirection1).isPresent();
        });
    }

    private Optional<Pair<BlockPosition, EnumDirection>> a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, EnumDirection enumdirection1) {
        if (enumdirection1.n() != enumdirection.n() && a(iblockdata, enumdirection) && !a(iblockdata, enumdirection1)) {
            if (this.a(iblockaccess, blockposition, enumdirection1)) {
                return Optional.of(Pair.of(blockposition, enumdirection1));
            } else {
                BlockPosition blockposition1 = blockposition.shift(enumdirection1);

                if (this.a(iblockaccess, blockposition1, enumdirection)) {
                    return Optional.of(Pair.of(blockposition1, enumdirection));
                } else {
                    BlockPosition blockposition2 = blockposition1.shift(enumdirection);
                    EnumDirection enumdirection2 = enumdirection1.opposite();

                    return this.a(iblockaccess, blockposition2, enumdirection2) ? Optional.of(Pair.of(blockposition2, enumdirection2)) : Optional.empty();
                }
            }
        } else {
            return Optional.empty();
        }
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        if (!this.n(iblockdata)) {
            return false;
        } else {
            IBlockData iblockdata1 = this.c(iblockdata, iblockaccess, blockposition, enumdirection);

            return iblockdata1 != null;
        }
    }

    private boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        IBlockData iblockdata = generatoraccess.getType(blockposition);
        IBlockData iblockdata1 = this.c(iblockdata, (IBlockAccess) generatoraccess, blockposition, enumdirection);

        if (iblockdata1 != null) {
            if (flag) {
                generatoraccess.A(blockposition).e(blockposition);
            }

            return generatoraccess.setTypeAndData(blockposition, iblockdata1, 2);
        } else {
            return false;
        }
    }

    private boolean n(IBlockData iblockdata) {
        return iblockdata.isAir() || iblockdata.a((Block) this) || iblockdata.a(Blocks.WATER) && iblockdata.getFluid().isSource();
    }

    private static boolean a(IBlockData iblockdata, EnumDirection enumdirection) {
        BlockStateBoolean blockstateboolean = b(enumdirection);

        return iblockdata.b(blockstateboolean) && (Boolean) iblockdata.get(blockstateboolean);
    }

    private static boolean a(IBlockAccess iblockaccess, EnumDirection enumdirection, BlockPosition blockposition, IBlockData iblockdata) {
        return Block.a(iblockdata.getCollisionShape(iblockaccess, blockposition), enumdirection.opposite());
    }

    private boolean q() {
        return this.stateDefinition.d().contains(BlockProperties.WATERLOGGED);
    }

    private static IBlockData a(IBlockData iblockdata, BlockStateBoolean blockstateboolean) {
        IBlockData iblockdata1 = (IBlockData) iblockdata.set(blockstateboolean, false);

        return h(iblockdata1) ? iblockdata1 : Blocks.AIR.getBlockData();
    }

    public static BlockStateBoolean b(EnumDirection enumdirection) {
        return (BlockStateBoolean) MultifaceBlock.PROPERTY_BY_DIRECTION.get(enumdirection);
    }

    private static IBlockData a(BlockStateList<Block, IBlockData> blockstatelist) {
        IBlockData iblockdata = (IBlockData) blockstatelist.getBlockData();
        Iterator iterator = MultifaceBlock.PROPERTY_BY_DIRECTION.values().iterator();

        while (iterator.hasNext()) {
            BlockStateBoolean blockstateboolean = (BlockStateBoolean) iterator.next();

            if (iblockdata.b(blockstateboolean)) {
                iblockdata = (IBlockData) iblockdata.set(blockstateboolean, false);
            }
        }

        return iblockdata;
    }

    private static VoxelShape o(IBlockData iblockdata) {
        VoxelShape voxelshape = VoxelShapes.a();
        EnumDirection[] aenumdirection = MultifaceBlock.DIRECTIONS;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (a(iblockdata, enumdirection)) {
                voxelshape = VoxelShapes.a(voxelshape, (VoxelShape) MultifaceBlock.SHAPE_BY_DIRECTION.get(enumdirection));
            }
        }

        return voxelshape.isEmpty() ? VoxelShapes.b() : voxelshape;
    }

    protected static boolean h(IBlockData iblockdata) {
        return Arrays.stream(MultifaceBlock.DIRECTIONS).anyMatch((enumdirection) -> {
            return a(iblockdata, enumdirection);
        });
    }

    private static boolean p(IBlockData iblockdata) {
        return Arrays.stream(MultifaceBlock.DIRECTIONS).anyMatch((enumdirection) -> {
            return !a(iblockdata, enumdirection);
        });
    }
}
