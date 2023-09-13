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
import net.minecraft.core.BaseBlockPosition;
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
    private static final VoxelShape SHAPE_DOT = Block.a(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
    private static final Map<EnumDirection, VoxelShape> SHAPES_FLOOR = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, Block.a(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), EnumDirection.SOUTH, Block.a(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), EnumDirection.EAST, Block.a(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), EnumDirection.WEST, Block.a(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
    private static final Map<EnumDirection, VoxelShape> SHAPES_UP = Maps.newEnumMap(ImmutableMap.of(EnumDirection.NORTH, VoxelShapes.a((VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(EnumDirection.NORTH), Block.a(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), EnumDirection.SOUTH, VoxelShapes.a((VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(EnumDirection.SOUTH), Block.a(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), EnumDirection.EAST, VoxelShapes.a((VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(EnumDirection.EAST), Block.a(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), EnumDirection.WEST, VoxelShapes.a((VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(EnumDirection.WEST), Block.a(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
    private static final Map<IBlockData, VoxelShape> SHAPES_CACHE = Maps.newHashMap();
    private static final Vec3D[] COLORS = (Vec3D[]) SystemUtils.a((Object) (new Vec3D[16]), (avec3d) -> {
        for (int i = 0; i <= 15; ++i) {
            float f = (float) i / 15.0F;
            float f1 = f * 0.6F + (f > 0.0F ? 0.4F : 0.3F);
            float f2 = MathHelper.a(f * f * 0.7F - 0.5F, 0.0F, 1.0F);
            float f3 = MathHelper.a(f * f * 0.6F - 0.7F, 0.0F, 1.0F);

            avec3d[i] = new Vec3D((double) f1, (double) f2, (double) f3);
        }

    });
    private static final float PARTICLE_DENSITY = 0.2F;
    private final IBlockData crossState;
    private boolean shouldSignal = true;

    public BlockRedstoneWire(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.NONE)).set(BlockRedstoneWire.POWER, 0));
        this.crossState = (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE)).set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE);
        UnmodifiableIterator unmodifiableiterator = this.getStates().a().iterator();

        while (unmodifiableiterator.hasNext()) {
            IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

            if ((Integer) iblockdata.get(BlockRedstoneWire.POWER) == 0) {
                BlockRedstoneWire.SHAPES_CACHE.put(iblockdata, this.n(iblockdata));
            }
        }

    }

    private VoxelShape n(IBlockData iblockdata) {
        VoxelShape voxelshape = BlockRedstoneWire.SHAPE_DOT;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPropertyRedstoneSide blockpropertyredstoneside = (BlockPropertyRedstoneSide) iblockdata.get((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection));

            if (blockpropertyredstoneside == BlockPropertyRedstoneSide.SIDE) {
                voxelshape = VoxelShapes.a(voxelshape, (VoxelShape) BlockRedstoneWire.SHAPES_FLOOR.get(enumdirection));
            } else if (blockpropertyredstoneside == BlockPropertyRedstoneSide.UP) {
                voxelshape = VoxelShapes.a(voxelshape, (VoxelShape) BlockRedstoneWire.SHAPES_UP.get(enumdirection));
            }
        }

        return voxelshape;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (VoxelShape) BlockRedstoneWire.SHAPES_CACHE.get(iblockdata.set(BlockRedstoneWire.POWER, 0));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return this.a((IBlockAccess) blockactioncontext.getWorld(), this.crossState, blockactioncontext.getClickPosition());
    }

    private IBlockData a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition) {
        boolean flag = p(iblockdata);

        iblockdata = this.b(iblockaccess, (IBlockData) this.getBlockData().set(BlockRedstoneWire.POWER, (Integer) iblockdata.get(BlockRedstoneWire.POWER)), blockposition);
        if (flag && p(iblockdata)) {
            return iblockdata;
        } else {
            boolean flag1 = ((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.NORTH)).a();
            boolean flag2 = ((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.SOUTH)).a();
            boolean flag3 = ((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.EAST)).a();
            boolean flag4 = ((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.WEST)).a();
            boolean flag5 = !flag1 && !flag2;
            boolean flag6 = !flag3 && !flag4;

            if (!flag4 && flag5) {
                iblockdata = (IBlockData) iblockdata.set(BlockRedstoneWire.WEST, BlockPropertyRedstoneSide.SIDE);
            }

            if (!flag3 && flag5) {
                iblockdata = (IBlockData) iblockdata.set(BlockRedstoneWire.EAST, BlockPropertyRedstoneSide.SIDE);
            }

            if (!flag1 && flag6) {
                iblockdata = (IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, BlockPropertyRedstoneSide.SIDE);
            }

            if (!flag2 && flag6) {
                iblockdata = (IBlockData) iblockdata.set(BlockRedstoneWire.SOUTH, BlockPropertyRedstoneSide.SIDE);
            }

            return iblockdata;
        }
    }

    private IBlockData b(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition) {
        boolean flag = !iblockaccess.getType(blockposition.up()).isOccluding(iblockaccess, blockposition);
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();

            if (!((BlockPropertyRedstoneSide) iblockdata.get((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection))).a()) {
                BlockPropertyRedstoneSide blockpropertyredstoneside = this.a(iblockaccess, blockposition, enumdirection, flag);

                iblockdata = (IBlockData) iblockdata.set((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection), blockpropertyredstoneside);
            }
        }

        return iblockdata;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.DOWN) {
            return iblockdata;
        } else if (enumdirection == EnumDirection.UP) {
            return this.a((IBlockAccess) generatoraccess, iblockdata, blockposition);
        } else {
            BlockPropertyRedstoneSide blockpropertyredstoneside = this.a((IBlockAccess) generatoraccess, blockposition, enumdirection);

            return blockpropertyredstoneside.a() == ((BlockPropertyRedstoneSide) iblockdata.get((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection))).a() && !o(iblockdata) ? (IBlockData) iblockdata.set((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection), blockpropertyredstoneside) : this.a((IBlockAccess) generatoraccess, (IBlockData) ((IBlockData) this.crossState.set(BlockRedstoneWire.POWER, (Integer) iblockdata.get(BlockRedstoneWire.POWER))).set((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection), blockpropertyredstoneside), blockposition);
        }
    }

    private static boolean o(IBlockData iblockdata) {
        return ((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.NORTH)).a() && ((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.SOUTH)).a() && ((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.EAST)).a() && ((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.WEST)).a();
    }

    private static boolean p(IBlockData iblockdata) {
        return !((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.NORTH)).a() && !((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.SOUTH)).a() && !((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.EAST)).a() && !((BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.WEST)).a();
    }

    @Override
    public void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i, int j) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPropertyRedstoneSide blockpropertyredstoneside = (BlockPropertyRedstoneSide) iblockdata.get((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection));

            if (blockpropertyredstoneside != BlockPropertyRedstoneSide.NONE && !generatoraccess.getType(blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection)).a((Block) this)) {
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
                IBlockData iblockdata1 = generatoraccess.getType(blockposition_mutableblockposition);

                if (!iblockdata1.a(Blocks.OBSERVER)) {
                    BlockPosition blockposition1 = blockposition_mutableblockposition.shift(enumdirection.opposite());
                    IBlockData iblockdata2 = iblockdata1.updateState(enumdirection.opposite(), generatoraccess.getType(blockposition1), generatoraccess, blockposition_mutableblockposition, blockposition1);

                    a(iblockdata1, iblockdata2, generatoraccess, blockposition_mutableblockposition, i, j);
                }

                blockposition_mutableblockposition.a((BaseBlockPosition) blockposition, enumdirection).c(EnumDirection.UP);
                IBlockData iblockdata3 = generatoraccess.getType(blockposition_mutableblockposition);

                if (!iblockdata3.a(Blocks.OBSERVER)) {
                    BlockPosition blockposition2 = blockposition_mutableblockposition.shift(enumdirection.opposite());
                    IBlockData iblockdata4 = iblockdata3.updateState(enumdirection.opposite(), generatoraccess.getType(blockposition2), generatoraccess, blockposition_mutableblockposition, blockposition2);

                    a(iblockdata3, iblockdata4, generatoraccess, blockposition_mutableblockposition, i, j);
                }
            }
        }

    }

    private BlockPropertyRedstoneSide a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.a(iblockaccess, blockposition, enumdirection, !iblockaccess.getType(blockposition.up()).isOccluding(iblockaccess, blockposition));
    }

    private BlockPropertyRedstoneSide a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = iblockaccess.getType(blockposition1);

        if (flag) {
            boolean flag1 = this.b(iblockaccess, blockposition1, iblockdata);

            if (flag1 && h(iblockaccess.getType(blockposition1.up()))) {
                if (iblockdata.d(iblockaccess, blockposition1, enumdirection.opposite())) {
                    return BlockPropertyRedstoneSide.UP;
                }

                return BlockPropertyRedstoneSide.SIDE;
            }
        }

        return !a(iblockdata, enumdirection) && (iblockdata.isOccluding(iblockaccess, blockposition1) || !h(iblockaccess.getType(blockposition1.down()))) ? BlockPropertyRedstoneSide.NONE : BlockPropertyRedstoneSide.SIDE;
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata1 = iworldreader.getType(blockposition1);

        return this.b((IBlockAccess) iworldreader, blockposition1, iblockdata1);
    }

    private boolean b(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return iblockdata.d(iblockaccess, blockposition, EnumDirection.UP) || iblockdata.a(Blocks.HOPPER);
    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.a(world, blockposition);

        if ((Integer) iblockdata.get(BlockRedstoneWire.POWER) != i) {
            if (world.getType(blockposition) == iblockdata) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockRedstoneWire.POWER, i), 2);
            }

            Set<BlockPosition> set = Sets.newHashSet();

            set.add(blockposition);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                set.add(blockposition.shift(enumdirection));
            }

            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();

                world.applyPhysics(blockposition1, this);
            }
        }

    }

    private int a(World world, BlockPosition blockposition) {
        this.shouldSignal = false;
        int i = world.s(blockposition);

        this.shouldSignal = true;
        int j = 0;

        if (i < 15) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                BlockPosition blockposition1 = blockposition.shift(enumdirection);
                IBlockData iblockdata = world.getType(blockposition1);

                j = Math.max(j, this.q(iblockdata));
                BlockPosition blockposition2 = blockposition.up();

                if (iblockdata.isOccluding(world, blockposition1) && !world.getType(blockposition2).isOccluding(world, blockposition2)) {
                    j = Math.max(j, this.q(world.getType(blockposition1.up())));
                } else if (!iblockdata.isOccluding(world, blockposition1)) {
                    j = Math.max(j, this.q(world.getType(blockposition1.down())));
                }
            }
        }

        return Math.max(i, j - 1);
    }

    private int q(IBlockData iblockdata) {
        return iblockdata.a((Block) this) ? (Integer) iblockdata.get(BlockRedstoneWire.POWER) : 0;
    }

    private void b(World world, BlockPosition blockposition) {
        if (world.getType(blockposition).a((Block) this)) {
            world.applyPhysics(blockposition, this);
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

        }
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata1.a(iblockdata.getBlock()) && !world.isClientSide) {
            this.a(world, blockposition, iblockdata);
            Iterator iterator = EnumDirection.EnumDirectionLimit.VERTICAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                world.applyPhysics(blockposition.shift(enumdirection), this);
            }

            this.c(world, blockposition);
        }
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!flag && !iblockdata.a(iblockdata1.getBlock())) {
            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
            if (!world.isClientSide) {
                EnumDirection[] aenumdirection = EnumDirection.values();
                int i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection = aenumdirection[j];

                    world.applyPhysics(blockposition.shift(enumdirection), this);
                }

                this.a(world, blockposition, iblockdata);
                this.c(world, blockposition);
            }
        }
    }

    private void c(World world, BlockPosition blockposition) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        EnumDirection enumdirection;

        while (iterator.hasNext()) {
            enumdirection = (EnumDirection) iterator.next();
            this.b(world, blockposition.shift(enumdirection));
        }

        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            if (world.getType(blockposition1).isOccluding(world, blockposition1)) {
                this.b(world, blockposition1.up());
            } else {
                this.b(world, blockposition1.down());
            }
        }

    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        if (!world.isClientSide) {
            if (iblockdata.canPlace(world, blockposition)) {
                this.a(world, blockposition, iblockdata);
            } else {
                c(iblockdata, world, blockposition);
                world.a(blockposition, false);
            }

        }
    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return !this.shouldSignal ? 0 : iblockdata.b(iblockaccess, blockposition, enumdirection);
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        if (this.shouldSignal && enumdirection != EnumDirection.DOWN) {
            int i = (Integer) iblockdata.get(BlockRedstoneWire.POWER);

            return i == 0 ? 0 : (enumdirection != EnumDirection.UP && !((BlockPropertyRedstoneSide) this.a(iblockaccess, iblockdata, blockposition).get((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection.opposite()))).a() ? 0 : i);
        } else {
            return 0;
        }
    }

    protected static boolean h(IBlockData iblockdata) {
        return a(iblockdata, (EnumDirection) null);
    }

    protected static boolean a(IBlockData iblockdata, @Nullable EnumDirection enumdirection) {
        if (iblockdata.a(Blocks.REDSTONE_WIRE)) {
            return true;
        } else if (iblockdata.a(Blocks.REPEATER)) {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockRepeater.FACING);

            return enumdirection1 == enumdirection || enumdirection1.opposite() == enumdirection;
        } else {
            return iblockdata.a(Blocks.OBSERVER) ? enumdirection == iblockdata.get(BlockObserver.FACING) : iblockdata.isPowerSource() && enumdirection != null;
        }
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return this.shouldSignal;
    }

    public static int b(int i) {
        Vec3D vec3d = BlockRedstoneWire.COLORS[i];

        return MathHelper.f((float) vec3d.getX(), (float) vec3d.getY(), (float) vec3d.getZ());
    }

    private void a(World world, Random random, BlockPosition blockposition, Vec3D vec3d, EnumDirection enumdirection, EnumDirection enumdirection1, float f, float f1) {
        float f2 = f1 - f;

        if (random.nextFloat() < 0.2F * f2) {
            float f3 = 0.4375F;
            float f4 = f + f2 * random.nextFloat();
            double d0 = 0.5D + (double) (0.4375F * (float) enumdirection.getAdjacentX()) + (double) (f4 * (float) enumdirection1.getAdjacentX());
            double d1 = 0.5D + (double) (0.4375F * (float) enumdirection.getAdjacentY()) + (double) (f4 * (float) enumdirection1.getAdjacentY());
            double d2 = 0.5D + (double) (0.4375F * (float) enumdirection.getAdjacentZ()) + (double) (f4 * (float) enumdirection1.getAdjacentZ());

            world.addParticle(new ParticleParamRedstone(new Vector3fa(vec3d), 1.0F), (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        int i = (Integer) iblockdata.get(BlockRedstoneWire.POWER);

        if (i != 0) {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                BlockPropertyRedstoneSide blockpropertyredstoneside = (BlockPropertyRedstoneSide) iblockdata.get((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection));

                switch (blockpropertyredstoneside) {
                    case UP:
                        this.a(world, random, blockposition, BlockRedstoneWire.COLORS[i], enumdirection, EnumDirection.UP, -0.5F, 0.5F);
                    case SIDE:
                        this.a(world, random, blockposition, BlockRedstoneWire.COLORS[i], EnumDirection.DOWN, enumdirection, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        this.a(world, random, blockposition, BlockRedstoneWire.COLORS[i], EnumDirection.DOWN, enumdirection, 0.0F, 0.3F);
                }
            }

        }
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case CLOCKWISE_180:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.EAST, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.SOUTH, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.NORTH))).set(BlockRedstoneWire.WEST, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.EAST));
            case COUNTERCLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.EAST))).set(BlockRedstoneWire.EAST, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.SOUTH, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.WEST, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.NORTH));
            case CLOCKWISE_90:
                return (IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.EAST, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.NORTH))).set(BlockRedstoneWire.SOUTH, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.EAST))).set(BlockRedstoneWire.WEST, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.SOUTH));
            default:
                return iblockdata;
        }
    }

    @Override
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        switch (enumblockmirror) {
            case LEFT_RIGHT:
                return (IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.NORTH, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.SOUTH))).set(BlockRedstoneWire.SOUTH, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.NORTH));
            case FRONT_BACK:
                return (IBlockData) ((IBlockData) iblockdata.set(BlockRedstoneWire.EAST, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.WEST))).set(BlockRedstoneWire.WEST, (BlockPropertyRedstoneSide) iblockdata.get(BlockRedstoneWire.EAST));
            default:
                return super.a(iblockdata, enumblockmirror);
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockRedstoneWire.NORTH, BlockRedstoneWire.EAST, BlockRedstoneWire.SOUTH, BlockRedstoneWire.WEST, BlockRedstoneWire.POWER);
    }

    @Override
    public EnumInteractionResult interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (!entityhuman.getAbilities().mayBuild) {
            return EnumInteractionResult.PASS;
        } else {
            if (o(iblockdata) || p(iblockdata)) {
                IBlockData iblockdata1 = o(iblockdata) ? this.getBlockData() : this.crossState;

                iblockdata1 = (IBlockData) iblockdata1.set(BlockRedstoneWire.POWER, (Integer) iblockdata.get(BlockRedstoneWire.POWER));
                iblockdata1 = this.a((IBlockAccess) world, iblockdata1, blockposition);
                if (iblockdata1 != iblockdata) {
                    world.setTypeAndData(blockposition, iblockdata1, 3);
                    this.a(world, blockposition, iblockdata, iblockdata1);
                    return EnumInteractionResult.SUCCESS;
                }
            }

            return EnumInteractionResult.PASS;
        }
    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);

            if (((BlockPropertyRedstoneSide) iblockdata.get((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection))).a() != ((BlockPropertyRedstoneSide) iblockdata1.get((IBlockState) BlockRedstoneWire.PROPERTY_BY_DIRECTION.get(enumdirection))).a() && world.getType(blockposition1).isOccluding(world, blockposition1)) {
                world.a(blockposition1, iblockdata1.getBlock(), enumdirection.opposite());
            }
        }

    }
}
