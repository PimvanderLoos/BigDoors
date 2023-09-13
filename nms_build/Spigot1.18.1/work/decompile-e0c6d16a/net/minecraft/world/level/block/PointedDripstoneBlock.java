package net.minecraft.world.level.block;

import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.WorldServer;
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
    private static final VoxelShape TIP_MERGE_SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape TIP_SHAPE_UP = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 11.0D, 11.0D);
    private static final VoxelShape TIP_SHAPE_DOWN = Block.box(5.0D, 5.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    private static final VoxelShape FRUSTUM_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private static final VoxelShape MIDDLE_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    private static final VoxelShape BASE_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    private static final float MAX_HORIZONTAL_OFFSET = 0.125F;

    public PointedDripstoneBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(PointedDripstoneBlock.TIP_DIRECTION, EnumDirection.UP)).setValue(PointedDripstoneBlock.THICKNESS, DripstoneThickness.TIP)).setValue(PointedDripstoneBlock.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(PointedDripstoneBlock.TIP_DIRECTION, PointedDripstoneBlock.THICKNESS, PointedDripstoneBlock.WATERLOGGED);
    }

    @Override
    public boolean canSurvive(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return isValidPointedDripstonePlacement(iworldreader, blockposition, (EnumDirection) iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION));
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(PointedDripstoneBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        if (enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN) {
            return iblockdata;
        } else {
            EnumDirection enumdirection1 = (EnumDirection) iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION);

            if (enumdirection1 == EnumDirection.DOWN && generatoraccess.getBlockTicks().hasScheduledTick(blockposition, this)) {
                return iblockdata;
            } else if (enumdirection == enumdirection1.getOpposite() && !this.canSurvive(iblockdata, generatoraccess, blockposition)) {
                if (enumdirection1 == EnumDirection.DOWN) {
                    this.scheduleStalactiteFallTicks(iblockdata, generatoraccess, blockposition);
                } else {
                    generatoraccess.scheduleTick(blockposition, (Block) this, 1);
                }

                return iblockdata;
            } else {
                boolean flag = iblockdata.getValue(PointedDripstoneBlock.THICKNESS) == DripstoneThickness.TIP_MERGE;
                DripstoneThickness dripstonethickness = calculateDripstoneThickness(generatoraccess, blockposition, enumdirection1, flag);

                return (IBlockData) iblockdata.setValue(PointedDripstoneBlock.THICKNESS, dripstonethickness);
            }
        }
    }

    @Override
    public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        BlockPosition blockposition = movingobjectpositionblock.getBlockPos();

        if (!world.isClientSide && iprojectile.mayInteract(world, blockposition) && iprojectile instanceof EntityThrownTrident && iprojectile.getDeltaMovement().length() > 0.6D) {
            world.destroyBlock(blockposition, true);
        }

    }

    @Override
    public void fallOn(World world, IBlockData iblockdata, BlockPosition blockposition, Entity entity, float f) {
        if (iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION) == EnumDirection.UP && iblockdata.getValue(PointedDripstoneBlock.THICKNESS) == DripstoneThickness.TIP) {
            entity.causeFallDamage(f + 2.0F, 2.0F, DamageSource.STALAGMITE);
        } else {
            super.fallOn(world, iblockdata, blockposition, entity, f);
        }

    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (canDrip(iblockdata)) {
            float f = random.nextFloat();

            if (f <= 0.12F) {
                getFluidAboveStalactite(world, blockposition, iblockdata).filter((fluidtype) -> {
                    return f < 0.02F || canFillCauldron(fluidtype);
                }).ifPresent((fluidtype) -> {
                    spawnDripParticle(world, blockposition, iblockdata, fluidtype);
                });
            }
        }
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (isStalagmite(iblockdata) && !this.canSurvive(iblockdata, worldserver, blockposition)) {
            worldserver.destroyBlock(blockposition, true);
        } else {
            spawnFallingStalactite(iblockdata, worldserver, blockposition);
        }

    }

    @Override
    public void randomTick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        maybeFillCauldron(iblockdata, worldserver, blockposition, random.nextFloat());
        if (random.nextFloat() < 0.011377778F && isStalactiteStartPos(iblockdata, worldserver, blockposition)) {
            growStalactiteOrStalagmiteIfPossible(iblockdata, worldserver, blockposition, random);
        }

    }

    @VisibleForTesting
    public static void maybeFillCauldron(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, float f) {
        if (f <= 0.17578125F || f <= 0.05859375F) {
            if (isStalactiteStartPos(iblockdata, worldserver, blockposition)) {
                FluidType fluidtype = getCauldronFillFluidType(worldserver, blockposition);
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
                    BlockPosition blockposition1 = findTip(iblockdata, worldserver, blockposition, 11, false);

                    if (blockposition1 != null) {
                        BlockPosition blockposition2 = findFillableCauldronBelowStalactiteTip(worldserver, blockposition1, fluidtype);

                        if (blockposition2 != null) {
                            worldserver.levelEvent(1504, blockposition1, 0);
                            int i = blockposition1.getY() - blockposition2.getY();
                            int j = 50 + i;
                            IBlockData iblockdata1 = worldserver.getBlockState(blockposition2);

                            worldserver.scheduleTick(blockposition2, iblockdata1.getBlock(), j);
                        }
                    }
                }
            }
        }
    }

    @Override
    public EnumPistonReaction getPistonPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Nullable
    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getLevel();
        BlockPosition blockposition = blockactioncontext.getClickedPos();
        EnumDirection enumdirection = blockactioncontext.getNearestLookingVerticalDirection().getOpposite();
        EnumDirection enumdirection1 = calculateTipDirection(world, blockposition, enumdirection);

        if (enumdirection1 == null) {
            return null;
        } else {
            boolean flag = !blockactioncontext.isSecondaryUseActive();
            DripstoneThickness dripstonethickness = calculateDripstoneThickness(world, blockposition, enumdirection1, flag);

            return dripstonethickness == null ? null : (IBlockData) ((IBlockData) ((IBlockData) this.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, enumdirection1)).setValue(PointedDripstoneBlock.THICKNESS, dripstonethickness)).setValue(PointedDripstoneBlock.WATERLOGGED, world.getFluidState(blockposition).getType() == FluidTypes.WATER);
        }
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(PointedDripstoneBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public VoxelShape getOcclusionShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getShape(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        DripstoneThickness dripstonethickness = (DripstoneThickness) iblockdata.getValue(PointedDripstoneBlock.THICKNESS);
        VoxelShape voxelshape;

        if (dripstonethickness == DripstoneThickness.TIP_MERGE) {
            voxelshape = PointedDripstoneBlock.TIP_MERGE_SHAPE;
        } else if (dripstonethickness == DripstoneThickness.TIP) {
            if (iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION) == EnumDirection.DOWN) {
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

        Vec3D vec3d = iblockdata.getOffset(iblockaccess, blockposition);

        return voxelshape.move(vec3d.x, 0.0D, vec3d.z);
    }

    @Override
    public boolean isCollisionShapeFullBlock(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Override
    public BlockBase.EnumRandomOffset getOffsetType() {
        return BlockBase.EnumRandomOffset.XZ;
    }

    @Override
    public float getMaxHorizontalOffset() {
        return 0.125F;
    }

    @Override
    public void onBrokenAfterFall(World world, BlockPosition blockposition, EntityFallingBlock entityfallingblock) {
        if (!entityfallingblock.isSilent()) {
            world.levelEvent(1045, blockposition, 0);
        }

    }

    @Override
    public DamageSource getFallDamageSource() {
        return DamageSource.FALLING_STALACTITE;
    }

    @Override
    public Predicate<Entity> getHurtsEntitySelector() {
        return IEntitySelector.NO_CREATIVE_OR_SPECTATOR.and(IEntitySelector.LIVING_ENTITY_STILL_ALIVE);
    }

    private void scheduleStalactiteFallTicks(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = findTip(iblockdata, generatoraccess, blockposition, Integer.MAX_VALUE, true);

        if (blockposition1 != null) {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition1.mutable();

            blockposition_mutableblockposition.move(EnumDirection.DOWN);
            IBlockData iblockdata1 = generatoraccess.getBlockState(blockposition_mutableblockposition);

            if (iblockdata1.getCollisionShape(generatoraccess, blockposition_mutableblockposition, VoxelShapeCollision.empty()).max(EnumDirection.EnumAxis.Y) >= 1.0D || iblockdata1.is(Blocks.POWDER_SNOW)) {
                generatoraccess.destroyBlock(blockposition1, true);
                blockposition_mutableblockposition.move(EnumDirection.UP);
            }

            blockposition_mutableblockposition.move(EnumDirection.UP);

            while (isStalactite(generatoraccess.getBlockState(blockposition_mutableblockposition))) {
                generatoraccess.scheduleTick(blockposition_mutableblockposition, (Block) this, 2);
                blockposition_mutableblockposition.move(EnumDirection.UP);
            }

        }
    }

    private static int getStalactiteSizeFromTip(WorldServer worldserver, BlockPosition blockposition, int i) {
        int j = 1;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable().move(EnumDirection.UP);

        while (j < i && isStalactite(worldserver.getBlockState(blockposition_mutableblockposition))) {
            ++j;
            blockposition_mutableblockposition.move(EnumDirection.UP);
        }

        return j;
    }

    private static void spawnFallingStalactite(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition) {
        Vec3D vec3d = Vec3D.atBottomCenterOf(blockposition);
        EntityFallingBlock entityfallingblock = new EntityFallingBlock(worldserver, vec3d.x, vec3d.y, vec3d.z, iblockdata);

        if (isTip(iblockdata, true)) {
            int i = getStalactiteSizeFromTip(worldserver, blockposition, 6);
            float f = 1.0F * (float) i;

            entityfallingblock.setHurtsEntities(f, 40);
        }

        worldserver.addFreshEntity(entityfallingblock);
    }

    @VisibleForTesting
    public static void growStalactiteOrStalagmiteIfPossible(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        IBlockData iblockdata1 = worldserver.getBlockState(blockposition.above(1));
        IBlockData iblockdata2 = worldserver.getBlockState(blockposition.above(2));

        if (canGrow(iblockdata1, iblockdata2)) {
            BlockPosition blockposition1 = findTip(iblockdata, worldserver, blockposition, 7, false);

            if (blockposition1 != null) {
                IBlockData iblockdata3 = worldserver.getBlockState(blockposition1);

                if (canDrip(iblockdata3) && canTipGrow(iblockdata3, worldserver, blockposition1)) {
                    if (random.nextBoolean()) {
                        grow(worldserver, blockposition1, EnumDirection.DOWN);
                    } else {
                        growStalagmiteBelow(worldserver, blockposition1);
                    }

                }
            }
        }
    }

    private static void growStalagmiteBelow(WorldServer worldserver, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int i = 0; i < 10; ++i) {
            blockposition_mutableblockposition.move(EnumDirection.DOWN);
            IBlockData iblockdata = worldserver.getBlockState(blockposition_mutableblockposition);

            if (!iblockdata.getFluidState().isEmpty()) {
                return;
            }

            if (isUnmergedTipWithDirection(iblockdata, EnumDirection.UP) && canTipGrow(iblockdata, worldserver, blockposition_mutableblockposition)) {
                grow(worldserver, blockposition_mutableblockposition, EnumDirection.UP);
                return;
            }

            if (isValidPointedDripstonePlacement(worldserver, blockposition_mutableblockposition, EnumDirection.UP) && !worldserver.isWaterAt(blockposition_mutableblockposition.below())) {
                grow(worldserver, blockposition_mutableblockposition.below(), EnumDirection.UP);
                return;
            }
        }

    }

    private static void grow(WorldServer worldserver, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.relative(enumdirection);
        IBlockData iblockdata = worldserver.getBlockState(blockposition1);

        if (isUnmergedTipWithDirection(iblockdata, enumdirection.getOpposite())) {
            createMergedTips(iblockdata, worldserver, blockposition1);
        } else if (iblockdata.isAir() || iblockdata.is(Blocks.WATER)) {
            createDripstone(worldserver, blockposition1, enumdirection, DripstoneThickness.TIP);
        }

    }

    private static void createDripstone(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection enumdirection, DripstoneThickness dripstonethickness) {
        IBlockData iblockdata = (IBlockData) ((IBlockData) ((IBlockData) Blocks.POINTED_DRIPSTONE.defaultBlockState().setValue(PointedDripstoneBlock.TIP_DIRECTION, enumdirection)).setValue(PointedDripstoneBlock.THICKNESS, dripstonethickness)).setValue(PointedDripstoneBlock.WATERLOGGED, generatoraccess.getFluidState(blockposition).getType() == FluidTypes.WATER);

        generatoraccess.setBlock(blockposition, iblockdata, 3);
    }

    private static void createMergedTips(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPosition blockposition1;
        BlockPosition blockposition2;

        if (iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION) == EnumDirection.UP) {
            blockposition1 = blockposition;
            blockposition2 = blockposition.above();
        } else {
            blockposition2 = blockposition;
            blockposition1 = blockposition.below();
        }

        createDripstone(generatoraccess, blockposition2, EnumDirection.DOWN, DripstoneThickness.TIP_MERGE);
        createDripstone(generatoraccess, blockposition1, EnumDirection.UP, DripstoneThickness.TIP_MERGE);
    }

    public static void spawnDripParticle(World world, BlockPosition blockposition, IBlockData iblockdata) {
        getFluidAboveStalactite(world, blockposition, iblockdata).ifPresent((fluidtype) -> {
            spawnDripParticle(world, blockposition, iblockdata, fluidtype);
        });
    }

    private static void spawnDripParticle(World world, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        Vec3D vec3d = iblockdata.getOffset(world, blockposition);
        double d0 = 0.0625D;
        double d1 = (double) blockposition.getX() + 0.5D + vec3d.x;
        double d2 = (double) ((float) (blockposition.getY() + 1) - 0.6875F) - 0.0625D;
        double d3 = (double) blockposition.getZ() + 0.5D + vec3d.z;
        FluidType fluidtype1 = getDripFluid(world, fluidtype);
        ParticleType particletype = fluidtype1.is(TagsFluid.LAVA) ? Particles.DRIPPING_DRIPSTONE_LAVA : Particles.DRIPPING_DRIPSTONE_WATER;

        world.addParticle(particletype, d1, d2, d3, 0.0D, 0.0D, 0.0D);
    }

    @Nullable
    private static BlockPosition findTip(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i, boolean flag) {
        if (isTip(iblockdata, flag)) {
            return blockposition;
        } else {
            EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION);
            Predicate<IBlockData> predicate = (iblockdata1) -> {
                return iblockdata1.is(Blocks.POINTED_DRIPSTONE) && iblockdata1.getValue(PointedDripstoneBlock.TIP_DIRECTION) == enumdirection;
            };

            return (BlockPosition) findBlockVertical(generatoraccess, blockposition, enumdirection.getAxisDirection(), predicate, (iblockdata1) -> {
                return isTip(iblockdata1, flag);
            }, i).orElse((Object) null);
        }
    }

    @Nullable
    private static EnumDirection calculateTipDirection(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection) {
        EnumDirection enumdirection1;

        if (isValidPointedDripstonePlacement(iworldreader, blockposition, enumdirection)) {
            enumdirection1 = enumdirection;
        } else {
            if (!isValidPointedDripstonePlacement(iworldreader, blockposition, enumdirection.getOpposite())) {
                return null;
            }

            enumdirection1 = enumdirection.getOpposite();
        }

        return enumdirection1;
    }

    private static DripstoneThickness calculateDripstoneThickness(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection, boolean flag) {
        EnumDirection enumdirection1 = enumdirection.getOpposite();
        IBlockData iblockdata = iworldreader.getBlockState(blockposition.relative(enumdirection));

        if (isPointedDripstoneWithDirection(iblockdata, enumdirection1)) {
            return !flag && iblockdata.getValue(PointedDripstoneBlock.THICKNESS) != DripstoneThickness.TIP_MERGE ? DripstoneThickness.TIP : DripstoneThickness.TIP_MERGE;
        } else if (!isPointedDripstoneWithDirection(iblockdata, enumdirection)) {
            return DripstoneThickness.TIP;
        } else {
            DripstoneThickness dripstonethickness = (DripstoneThickness) iblockdata.getValue(PointedDripstoneBlock.THICKNESS);

            if (dripstonethickness != DripstoneThickness.TIP && dripstonethickness != DripstoneThickness.TIP_MERGE) {
                IBlockData iblockdata1 = iworldreader.getBlockState(blockposition.relative(enumdirection1));

                return !isPointedDripstoneWithDirection(iblockdata1, enumdirection) ? DripstoneThickness.BASE : DripstoneThickness.MIDDLE;
            } else {
                return DripstoneThickness.FRUSTUM;
            }
        }
    }

    public static boolean canDrip(IBlockData iblockdata) {
        return isStalactite(iblockdata) && iblockdata.getValue(PointedDripstoneBlock.THICKNESS) == DripstoneThickness.TIP && !(Boolean) iblockdata.getValue(PointedDripstoneBlock.WATERLOGGED);
    }

    private static boolean canTipGrow(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION);
        BlockPosition blockposition1 = blockposition.relative(enumdirection);
        IBlockData iblockdata1 = worldserver.getBlockState(blockposition1);

        return !iblockdata1.getFluidState().isEmpty() ? false : (iblockdata1.isAir() ? true : isUnmergedTipWithDirection(iblockdata1, enumdirection.getOpposite()));
    }

    private static Optional<BlockPosition> findRootBlock(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION);
        Predicate<IBlockData> predicate = (iblockdata1) -> {
            return iblockdata1.is(Blocks.POINTED_DRIPSTONE) && iblockdata1.getValue(PointedDripstoneBlock.TIP_DIRECTION) == enumdirection;
        };

        return findBlockVertical(world, blockposition, enumdirection.getOpposite().getAxisDirection(), predicate, (iblockdata1) -> {
            return !iblockdata1.is(Blocks.POINTED_DRIPSTONE);
        }, i);
    }

    private static boolean isValidPointedDripstonePlacement(IWorldReader iworldreader, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.relative(enumdirection.getOpposite());
        IBlockData iblockdata = iworldreader.getBlockState(blockposition1);

        return iblockdata.isFaceSturdy(iworldreader, blockposition1, enumdirection) || isPointedDripstoneWithDirection(iblockdata, enumdirection);
    }

    private static boolean isTip(IBlockData iblockdata, boolean flag) {
        if (!iblockdata.is(Blocks.POINTED_DRIPSTONE)) {
            return false;
        } else {
            DripstoneThickness dripstonethickness = (DripstoneThickness) iblockdata.getValue(PointedDripstoneBlock.THICKNESS);

            return dripstonethickness == DripstoneThickness.TIP || flag && dripstonethickness == DripstoneThickness.TIP_MERGE;
        }
    }

    private static boolean isUnmergedTipWithDirection(IBlockData iblockdata, EnumDirection enumdirection) {
        return isTip(iblockdata, false) && iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION) == enumdirection;
    }

    private static boolean isStalactite(IBlockData iblockdata) {
        return isPointedDripstoneWithDirection(iblockdata, EnumDirection.DOWN);
    }

    private static boolean isStalagmite(IBlockData iblockdata) {
        return isPointedDripstoneWithDirection(iblockdata, EnumDirection.UP);
    }

    private static boolean isStalactiteStartPos(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return isStalactite(iblockdata) && !iworldreader.getBlockState(blockposition.above()).is(Blocks.POINTED_DRIPSTONE);
    }

    @Override
    public boolean isPathfindable(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    private static boolean isPointedDripstoneWithDirection(IBlockData iblockdata, EnumDirection enumdirection) {
        return iblockdata.is(Blocks.POINTED_DRIPSTONE) && iblockdata.getValue(PointedDripstoneBlock.TIP_DIRECTION) == enumdirection;
    }

    @Nullable
    private static BlockPosition findFillableCauldronBelowStalactiteTip(World world, BlockPosition blockposition, FluidType fluidtype) {
        Predicate<IBlockData> predicate = (iblockdata) -> {
            return iblockdata.getBlock() instanceof AbstractCauldronBlock && ((AbstractCauldronBlock) iblockdata.getBlock()).canReceiveStalactiteDrip(fluidtype);
        };

        return (BlockPosition) findBlockVertical(world, blockposition, EnumDirection.DOWN.getAxisDirection(), BlockBase.BlockData::isAir, predicate, 11).orElse((Object) null);
    }

    @Nullable
    public static BlockPosition findStalactiteTipAboveCauldron(World world, BlockPosition blockposition) {
        return (BlockPosition) findBlockVertical(world, blockposition, EnumDirection.UP.getAxisDirection(), BlockBase.BlockData::isAir, PointedDripstoneBlock::canDrip, 11).orElse((Object) null);
    }

    public static FluidType getCauldronFillFluidType(World world, BlockPosition blockposition) {
        return (FluidType) getFluidAboveStalactite(world, blockposition, world.getBlockState(blockposition)).filter(PointedDripstoneBlock::canFillCauldron).orElse(FluidTypes.EMPTY);
    }

    private static Optional<FluidType> getFluidAboveStalactite(World world, BlockPosition blockposition, IBlockData iblockdata) {
        return !isStalactite(iblockdata) ? Optional.empty() : findRootBlock(world, blockposition, iblockdata, 11).map((blockposition1) -> {
            return world.getFluidState(blockposition1.above()).getType();
        });
    }

    private static boolean canFillCauldron(FluidType fluidtype) {
        return fluidtype == FluidTypes.LAVA || fluidtype == FluidTypes.WATER;
    }

    private static boolean canGrow(IBlockData iblockdata, IBlockData iblockdata1) {
        return iblockdata.is(Blocks.DRIPSTONE_BLOCK) && iblockdata1.is(Blocks.WATER) && iblockdata1.getFluidState().isSource();
    }

    private static FluidType getDripFluid(World world, FluidType fluidtype) {
        return (FluidType) (fluidtype.isSame(FluidTypes.EMPTY) ? (world.dimensionType().ultraWarm() ? FluidTypes.LAVA : FluidTypes.WATER) : fluidtype);
    }

    private static Optional<BlockPosition> findBlockVertical(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection, Predicate<IBlockData> predicate, Predicate<IBlockData> predicate1, int i) {
        EnumDirection enumdirection = EnumDirection.get(enumdirection_enumaxisdirection, EnumDirection.EnumAxis.Y);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int j = 1; j < i; ++j) {
            blockposition_mutableblockposition.move(enumdirection);
            IBlockData iblockdata = generatoraccess.getBlockState(blockposition_mutableblockposition);

            if (predicate1.test(iblockdata)) {
                return Optional.of(blockposition_mutableblockposition.immutable());
            }

            if (generatoraccess.isOutsideBuildHeight(blockposition_mutableblockposition.getY()) || !predicate.test(iblockdata)) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}
