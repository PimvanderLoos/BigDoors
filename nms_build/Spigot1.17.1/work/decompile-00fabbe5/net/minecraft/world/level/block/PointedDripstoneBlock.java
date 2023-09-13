package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsFluid;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.item.EntityFallingBlock;
import net.minecraft.world.entity.projectile.EntityThrownTrident;
import net.minecraft.world.entity.projectile.IProjectile;
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
import net.minecraft.world.level.block.state.properties.BlockStateDirection;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.DripstoneThickness;
import net.minecraft.world.level.material.EnumPistonReaction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import net.minecraft.world.phys.shapes.VoxelShapes;

public class PointedDripstoneBlock extends Block implements Fallable, IBlockWaterlogged {

    public static final BlockStateDirection TIP_DIRECTION = BlockProperties.VERTICAL_DIRECTION;
    public static final BlockStateEnum<DripstoneThickness> THICKNESS = BlockProperties.DRIPSTONE_THICKNESS;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    private static final int MAX_SEARCH_LENGTH_WHEN_CHECKING_DRIP_TYPE = 11;
    private static final int MAX_SEARCH_LENGTH_WHEN_LOOKING_FOR_TIP_OF_FALLING_STALACTITE = Integer.MAX_VALUE;
    private static final int DELAY_BEFORE_FALLING = 2;
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK = 0.02F;
    private static final float DRIP_PROBABILITY_PER_ANIMATE_TICK_IF_UNDER_LIQUID_SOURCE = 0.12F;
    private static final int MAX_SEARCH_LENGTH_BETWEEN_STALACTITE_TIP_AND_CAULDRON = 11;
    private static final float WATER_CAULDRON_FILL_PROBABILITY_PER_RANDOM_TICK = 0.17578125F;
    private static final float LAVA_CAULDRON_FILL_PROBABILITY_PER_RANDOM_TICK = 0.05859375F;
    private static final double MIN_TRIDENT_VELOCITY_TO_BREAK_DRIPSTONE = 0.6D;
    private static final float STALACTITE_DAMAGE_PER_FALL_DISTANCE_AND_SIZE = 1.0F;
    private static final int STALACTITE_MAX_DAMAGE = 40;
    private static final int MAX_STALACTITE_HEIGHT_FOR_DAMAGE_CALCULATION = 6;
    private static final float STALAGMITE_FALL_DISTANCE_OFFSET = 2.0F;
    private static final int STALAGMITE_FALL_DAMAGE_MODIFIER = 2;
    private static final float AVERAGE_DAYS_PER_GROWTH = 5.0F;
    private static final float GROWTH_PROBABILITY_PER_RANDOM_TICK = 0.011377778F;
    private static final int MAX_GROWTH_LENGTH = 7;
    private static final int MAX_STALAGMITE_SEARCH_RANGE_WHEN_GROWING = 10;
    private static final float STALACTITE_DRIP_START_PIXEL = 0.6875F;
    private static final VoxelShape TIP_MERGE_SHAPE = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape TIP_SHAPE_UP = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 11.0D, 11.0D);
    private static final VoxelShape TIP_SHAPE_DOWN = Block.a(5.0D, 5.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape FRUSTUM_SHAPE = Block.a(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private static final VoxelShape MIDDLE_SHAPE = Block.a(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    private static final VoxelShape BASE_SHAPE = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final float MAX_HORIZONTAL_OFFSET = 0.125F;

    public PointedDripstoneBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(PointedDripstoneBlock.TIP_DIRECTION, EnumDirection.UP)).set(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP)).set(PointedDripstoneBlock.WATERLOGGED, false));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(PointedDripstoneBlock.TIP_DIRECTION, PointedDripstoneBlock.THICKNESS, PointedDripstoneBlock.WATERLOGGED);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return c(iworldreader, blockposition, (EnumDirection) iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(PointedDripstoneBlock.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        if (enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN) {
            return iblockdata;
        } else {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION);

            if (enumdirection1 == EnumDirection.DOWN && generatoraccess.getBlockTickList().a(blockposition, this)) {
                return iblockdata;
            } else if (enumdirection == enumdirection1.opposite() && !this.canPlace(iblockdata, generatoraccess, blockposition)) {
                if (enumdirection1 == EnumDirection.DOWN) {
                    this.a(iblockdata, generatoraccess, blockposition);
                } else {
                    generatoraccess.getBlockTickList().a(blockposition, this, 1);
                }

                return iblockdata;
            } else {
                boolean flag = iblockdata.get(PointedDripstoneBlock.THICKNESS) == DripstoneThickness.TIP_MERGE;
                DripstoneThickness dripstonethickness = a(generatoraccess, blockposition, enumdirection1, flag);

                return (IBlockData) iblockdata.set(PointedDripstoneBlock.THICKNESS, dripstonethickness);
            }
        }
    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();

        if (!world.isClientSide && iprojectile.a(world, blockposition) && iprojectile instanceof EntityThrownTrident && iprojectile.getMot().f() > 0.6D) {
            world.b(blockposition, true);
        }

    }

    @Override
    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        if (iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION) == EnumDirection.UP && iblockdata.get(PointedDripstoneBlock.THICKNESS) == DripstoneThickness.TIP) {
            entity.a(f + 2.0F, 2.0F, DamageSource.STALAGMITE);
        } else {
            super.fallOn(world, iblockdata, blockposition, entity, f);
        }

    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (h(iblockdata)) {
            float f = random.nextFloat();

            if (f <= 0.12F) {
                b(world, blockposition, iblockdata).filter((fluidtype) -> {
                    return f < 0.02F || a(fluidtype);
                }).ifPresent((fluidtype) -> {
                    a(world, blockposition, iblockdata, fluidtype);
                });
            }
        }
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (o(iblockdata) && !this.canPlace(iblockdata, worldserver, blockposition)) {
            worldserver.b(blockposition, true);
        } else {
            a(iblockdata, worldserver, blockposition);
        }

    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        a(iblockdata, worldserver, blockposition, random.nextFloat());
        if (random.nextFloat() < 0.011377778F && b(iblockdata, (IWorldReader) worldserver, blockposition)) {
            c(iblockdata, worldserver, blockposition, random);
        }

    }

    @VisibleForTesting
    public static void a(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, float f) {
        if (f <= 0.17578125F || f <= 0.05859375F) {
            if (b(iblockdata, (IWorldReader) worldserver, blockposition)) {
                FluidType fluidtype = b((World) worldserver, blockposition);
                float f1;

                if (fluidtype == FluidTypes.WATER) {
                    f1 = 0.17578125F;
                } else {
                    if (fluidtype != FluidTypes.LAVA) {
                        return;
                    }

                    f1 = 0.05859375F;
                }

                if (f < f1) {
                    BlockPosition blockposition1 = a(iblockdata, worldserver, blockposition, 11, false);

                    if (blockposition1 != null) {
                        BlockPosition blockposition2 = a((World) worldserver, blockposition1, fluidtype);

                        if (blockposition2 != null) {
                            worldserver.triggerEffect(1504, blockposition1, 0);
                            int i = blockposition1.getY() - blockposition2.getY();
                            int j = 50 + i;
                            IBlockData iblockdata1 = worldserver.getType(blockposition2);

                            worldserver.getBlockTickList().a(blockposition2, iblockdata1.getBlock(), j);
                        }
                    }
                }
            }
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        EnumDirection enumdirection = blockactioncontext.e().opposite();
        EnumDirection enumdirection1 = b((IWorldReader) world, blockposition, enumdirection);

        if (enumdirection1 == null) {
            return null;
        } else {
            boolean flag = !blockactioncontext.isSneaking();
            DripstoneThickness dripstonethickness = a(world, blockposition, enumdirection1, flag);

            return dripstonethickness == null ? null : (IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(PointedDripstoneBlock.TIP_DIRECTION, enumdirection1)).set(PointedDripstoneBlock.THICKNESS, dripstonethickness)).set(PointedDripstoneBlock.WATERLOGGED, world.getFluid(blockposition).getType() == FluidTypes.WATER);
        }
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(PointedDripstoneBlock.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public VoxelShape b_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        DripstoneThickness dripstonethickness = (DripstoneThickness) iblockdata.get(PointedDripstoneBlock.THICKNESS);
        VoxelShape voxelshape;

        if (dripstonethickness == DripstoneThickness.TIP_MERGE) {
            voxelshape = PointedDripstoneBlock.TIP_MERGE_SHAPE;
        } else if (dripstonethickness == DripstoneThickness.TIP) {
            if (iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION) == EnumDirection.DOWN) {
                voxelshape = PointedDripstoneBlock.TIP_SHAPE_DOWN;
            } else {
                voxelshape = PointedDripstoneBlock.TIP_SHAPE_UP;
            }
        } else if (dripstonethickness == DripstoneThickness.FRUSTUM) {
            voxelshape = PointedDripstoneBlock.FRUSTUM_SHAPE;
        } else if (dripstonethickness == DripstoneThickness.MIDDLE) {
            voxelshape = PointedDripstoneBlock.MIDDLE_SHAPE;
        } else {
            voxelshape = PointedDripstoneBlock.BASE_SHAPE;
        }

        Vec3D vec3d = iblockdata.n(iblockaccess, blockposition);

        return voxelshape.a(vec3d.x, 0.0D, vec3d.z);
    }

    @Override
    public boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Override
    public BlockBase.EnumRandomOffset S_() {
        return BlockBase.EnumRandomOffset.XZ;
    }

    @Override
    public float U_() {
        return 0.125F;
    }

    @Override
    public void a(World world, BlockPosition blockposition, EntityFallingBlock entityfallingblock) {
        if (!entityfallingblock.isSilent()) {
            world.triggerEffect(1045, blockposition, 0);
        }

    }

    @Override
    public DamageSource b() {
        return DamageSource.FALLING_STALACTITE;
    }

    @Override
    public Predicate<Entity> T_() {
        return IEntitySelector.NO_CREATIVE_OR_SPECTATOR.and(IEntitySelector.LIVING_ENTITY_STILL_ALIVE);
    }

    private void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = a(iblockdata, generatoraccess, blockposition, Integer.MAX_VALUE, true);

        if (blockposition1 != null) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition1.i();

            while (n(generatoraccess.getType(blockposition_mutableblockposition))) {
                generatoraccess.getBlockTickList().a(blockposition_mutableblockposition, this, 2);
                blockposition_mutableblockposition.c(EnumDirection.UP);
            }

        }
    }

    private static int b(WorldServer worldserver, BlockPosition blockposition, int i) {
        int j = 1;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i().c(EnumDirection.UP);

        while (j < i && n(worldserver.getType(blockposition_mutableblockposition))) {
            ++j;
            blockposition_mutableblockposition.c(EnumDirection.UP);
        }

        return j;
    }

    private static void a(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition) {
        Vec3D vec3d = Vec3D.c((BaseBlockPosition) blockposition);
        EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldserver, vec3d.x, vec3d.y, vec3d.z, iblockdata);

        if (a(iblockdata, true)) {
            int i = b(worldserver, blockposition, 6);
            float f = 1.0F * (float) i;

            entityfallingblock.b(f, 40);
        }

        worldserver.addEntity(entityfallingblock);
    }

    @VisibleForTesting
    public static void c(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        IBlockData iblockdata1 = worldserver.getType(blockposition.up(1));
        IBlockData iblockdata2 = worldserver.getType(blockposition.up(2));

        if (a(iblockdata1, iblockdata2)) {
            BlockPosition blockposition1 = a(iblockdata, worldserver, blockposition, 7, false);

            if (blockposition1 != null) {
                IBlockData iblockdata3 = worldserver.getType(blockposition1);

                if (h(iblockdata3) && b(iblockdata3, worldserver, blockposition1)) {
                    if (random.nextBoolean()) {
                        a(worldserver, blockposition1, EnumDirection.DOWN);
                    } else {
                        a(worldserver, blockposition1);
                    }

                }
            }
        }
    }

    private static void a(WorldServer worldserver, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        for (int i = 0; i < 10; ++i) {
            blockposition_mutableblockposition.c(EnumDirection.DOWN);
            IBlockData iblockdata = worldserver.getType(blockposition_mutableblockposition);

            if (!iblockdata.getFluid().isEmpty()) {
                return;
            }

            if (a(iblockdata, EnumDirection.UP) && b(iblockdata, worldserver, (BlockPosition) blockposition_mutableblockposition)) {
                a(worldserver, (BlockPosition) blockposition_mutableblockposition, EnumDirection.UP);
                return;
            }

            if (c((IWorldReader) worldserver, (BlockPosition) blockposition_mutableblockposition, EnumDirection.UP) && !worldserver.B(blockposition_mutableblockposition.down())) {
                a(worldserver, blockposition_mutableblockposition.down(), EnumDirection.UP);
                return;
            }
        }

    }

    private static void a(WorldServer worldserver, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata = worldserver.getType(blockposition1);

        if (a(iblockdata, enumdirection.opposite())) {
            c(iblockdata, (GeneratorAccess) worldserver, blockposition1);
        } else if (iblockdata.isAir() || iblockdata.a(Blocks.WATER)) {
            a((GeneratorAccess) worldserver, blockposition1, enumdirection, DripstoneThickness.TIP);
        }

    }

    private static void a(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, DripstoneThickness dripstonethickness) {
        IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) Blocks.POINTED_DRIPSTONE.getBlockData().set(PointedDripstoneBlock.TIP_DIRECTION, enumdirection)).set(PointedDripstoneBlock.THICKNESS, dripstonethickness)).set(PointedDripstoneBlock.WATERLOGGED, generatoraccess.getFluid(blockposition).getType() == FluidTypes.WATER);

        generatoraccess.setTypeAndData(blockposition, iblockdata, 3);
    }

    private static void c(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPosition blockposition1;
        BlockPosition blockposition2;

        if (iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION) == EnumDirection.UP) {
            blockposition1 = blockposition;
            blockposition2 = blockposition.up();
        } else {
            blockposition2 = blockposition;
            blockposition1 = blockposition.down();
        }

        a(generatoraccess, blockposition2, EnumDirection.DOWN, DripstoneThickness.TIP_MERGE);
        a(generatoraccess, blockposition1, EnumDirection.UP, DripstoneThickness.TIP_MERGE);
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        b(world, blockposition, iblockdata).ifPresent((fluidtype) -> {
            a(world, blockposition, iblockdata, fluidtype);
        });
    }

    private static void a(World world, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        Vec3D vec3d = iblockdata.n(world, blockposition);
        double d0 = 0.0625D;
        double d1 = (double) blockposition.getX() + 0.5D + vec3d.x;
        double d2 = (double) ((float) (blockposition.getY() + 1) - 0.6875F) - 0.0625D;
        double d3 = (double) blockposition.getZ() + 0.5D + vec3d.z;
        FluidType fluidtype1 = a(world, fluidtype);
        ParticleType particletype = fluidtype1.a((Tag) TagsFluid.LAVA) ? Particles.DRIPPING_DRIPSTONE_LAVA : Particles.DRIPPING_DRIPSTONE_WATER;

        world.addParticle(particletype, d1, d2, d3, 0.0D, 0.0D, 0.0D);
    }

    @Nullable
    private static BlockPosition a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i, boolean flag) {
        if (a(iblockdata, flag)) {
            return blockposition;
        } else {
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION);
            Predicate<IBlockData> predicate = (iblockdata1) -> {
                return iblockdata1.a(Blocks.POINTED_DRIPSTONE) && iblockdata1.get(PointedDripstoneBlock.TIP_DIRECTION) == enumdirection;
            };

            return (BlockPosition) a(generatoraccess, blockposition, enumdirection.e(), predicate, (iblockdata1) -> {
                return a(iblockdata1, flag);
            }, i).orElse((Object) null);
        }
    }

    @Nullable
    private static EnumDirection b(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection) {
        EnumDirection enumdirection1;

        if (c(iworldreader, blockposition, enumdirection)) {
            enumdirection1 = enumdirection;
        } else {
            if (!c(iworldreader, blockposition, enumdirection.opposite())) {
                return null;
            }

            enumdirection1 = enumdirection.opposite();
        }

        return enumdirection1;
    }

    private static DripstoneThickness a(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        EnumDirection enumdirection1 = enumdirection.opposite();
        IBlockData iblockdata = iworldreader.getType(blockposition.shift(enumdirection));

        if (b(iblockdata, enumdirection1)) {
            return !flag && iblockdata.get(PointedDripstoneBlock.THICKNESS) != DripstoneThickness.TIP_MERGE ? DripstoneThickness.TIP : DripstoneThickness.TIP_MERGE;
        } else if (!b(iblockdata, enumdirection)) {
            return DripstoneThickness.TIP;
        } else {
            DripstoneThickness dripstonethickness = (DripstoneThickness) iblockdata.get(PointedDripstoneBlock.THICKNESS);

            if (dripstonethickness != DripstoneThickness.TIP && dripstonethickness != DripstoneThickness.TIP_MERGE) {
                IBlockData iblockdata1 = iworldreader.getType(blockposition.shift(enumdirection1));

                return !b(iblockdata1, enumdirection) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
            } else {
                return DripstoneThickness.FRUSTUM;
            }
        }
    }

    public static boolean h(IBlockData iblockdata) {
        return n(iblockdata) && iblockdata.get(PointedDripstoneBlock.THICKNESS) == DripstoneThickness.TIP && !(Boolean) iblockdata.get(PointedDripstoneBlock.WATERLOGGED);
    }

    private static boolean b(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION);
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        IBlockData iblockdata1 = worldserver.getType(blockposition1);

        return !iblockdata1.getFluid().isEmpty() ? false : (iblockdata1.isAir() ? true : a(iblockdata1, enumdirection.opposite()));
    }

    private static Optional<BlockPosition> a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION);
        Predicate<IBlockData> predicate = (iblockdata1) -> {
            return iblockdata1.a(Blocks.POINTED_DRIPSTONE) && iblockdata1.get(PointedDripstoneBlock.TIP_DIRECTION) == enumdirection;
        };

        return a(world, blockposition, enumdirection.opposite().e(), predicate, (iblockdata1) -> {
            return !iblockdata1.a(Blocks.POINTED_DRIPSTONE);
        }, i);
    }

    private static boolean c(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.shift(enumdirection.opposite());
        IBlockData iblockdata = iworldreader.getType(blockposition1);

        return iblockdata.d(iworldreader, blockposition1, enumdirection) || b(iblockdata, enumdirection);
    }

    private static boolean a(IBlockData iblockdata, boolean flag) {
        if (!iblockdata.a(Blocks.POINTED_DRIPSTONE)) {
            return false;
        } else {
            DripstoneThickness dripstonethickness = (DripstoneThickness) iblockdata.get(PointedDripstoneBlock.THICKNESS);

            return dripstonethickness == DripstoneThickness.TIP || flag && dripstonethickness == DripstoneThickness.TIP_MERGE;
        }
    }

    private static boolean a(IBlockData iblockdata, EnumDirection enumdirection) {
        return a(iblockdata, false) && iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION) == enumdirection;
    }

    private static boolean n(IBlockData iblockdata) {
        return b(iblockdata, EnumDirection.DOWN);
    }

    private static boolean o(IBlockData iblockdata) {
        return b(iblockdata, EnumDirection.UP);
    }

    private static boolean b(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return n(iblockdata) && !iworldreader.getType(blockposition.up()).a(Blocks.POINTED_DRIPSTONE);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    private static boolean b(IBlockData iblockdata, EnumDirection enumdirection) {
        return iblockdata.a(Blocks.POINTED_DRIPSTONE) && iblockdata.get(PointedDripstoneBlock.TIP_DIRECTION) == enumdirection;
    }

    @Nullable
    private static BlockPosition a(World world, BlockPosition blockposition, FluidType fluidtype) {
        Predicate<IBlockData> predicate = (iblockdata) -> {
            return iblockdata.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock) iblockdata.getBlock()).a(fluidtype);
        };

        return (BlockPosition) a(world, blockposition, EnumDirection.DOWN.e(), BlockBase.BlockData::isAir, predicate, 11).orElse((Object) null);
    }

    @Nullable
    public static BlockPosition a(World world, BlockPosition blockposition) {
        return (BlockPosition) a(world, blockposition, EnumDirection.UP.e(), BlockBase.BlockData::isAir, PointedDripstoneBlock::h, 11).orElse((Object) null);
    }

    public static FluidType b(World world, BlockPosition blockposition) {
        return (FluidType) b(world, blockposition, world.getType(blockposition)).filter(PointedDripstoneBlock::a).orElse(FluidTypes.EMPTY);
    }

    private static Optional<FluidType> b(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return !n(iblockdata) ? Optional.empty() : a(world, blockposition, iblockdata, 11).map((blockposition1) -> {
            return world.getFluid(blockposition1.up()).getType();
        });
    }

    private static boolean a(FluidType fluidtype) {
        return fluidtype == FluidTypes.LAVA || fluidtype == FluidTypes.WATER;
    }

    private static boolean a(IBlockData iblockdata, IBlockData iblockdata1) {
        return iblockdata.a(Blocks.DRIPSTONE_BLOCK) && iblockdata1.a(Blocks.WATER) && iblockdata1.getFluid().isSource();
    }

    private static FluidType a(World world, FluidType fluidtype) {
        return (FluidType) (fluidtype.a(FluidTypes.EMPTY) ? (world.getDimensionManager().isNether() ? FluidTypes.LAVA : FluidTypes.WATER) : fluidtype);
    }

    private static Optional<BlockPosition> a(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, Predicate<IBlockData> predicate, Predicate<IBlockData> predicate1, int i) {
        EnumDirection enumdirection = EnumDirection.a(enumdirection_enumaxisdirection, EnumDirection.EnumAxis.Y);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.i();

        for (int j = 1; j < i; ++j) {
            blockposition_mutableblockposition.c(enumdirection);
            IBlockData iblockdata = generatoraccess.getType(blockposition_mutableblockposition);

            if (predicate1.test(iblockdata)) {
                return Optional.of(blockposition_mutableblockposition.immutableCopy());
            }

            if (generatoraccess.d(blockposition_mutableblockposition.getY()) || !predicate.test(iblockdata)) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
