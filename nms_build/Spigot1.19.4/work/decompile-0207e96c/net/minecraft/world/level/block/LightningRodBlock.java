package net.minecraft.world.level.block;

import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightning;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityThrownTrident;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidType;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class LightningRodBlock extends RodBlock implements IBlockWaterlogged {

    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    public static final BlockStateBoolean POWERED = BlockProperties.POWERED;
    private static final int ACTIVATION_TICKS = 8;
    public static final int RANGE = 128;
    private static final int SPARK_CYCLE = 200;

    public LightningRodBlock(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(LightningRodBlock.FACING, EnumDirection.UP)).setValue(LightningRodBlock.WATERLOGGED, false)).setValue(LightningRodBlock.POWERED, false));
    }

    @Override
    public IBlockData getStateForPlacement(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getLevel().getFluidState(blockactioncontext.getClickedPos());
        boolean flag = fluid.getType() == FluidTypes.WATER;

        return (IBlockData) ((IBlockData) this.defaultBlockState().setValue(LightningRodBlock.FACING, blockactioncontext.getClickedFace())).setValue(LightningRodBlock.WATERLOGGED, flag);
    }

    @Override
    public IBlockData updateShape(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.getValue(LightningRodBlock.WATERLOGGED)) {
            generatoraccess.scheduleTick(blockposition, (FluidType) FluidTypes.WATER, FluidTypes.WATER.getTickDelay(generatoraccess));
        }

        return super.updateShape(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public Fluid getFluidState(IBlockData iblockdata) {
        return (Boolean) iblockdata.getValue(LightningRodBlock.WATERLOGGED) ? FluidTypes.WATER.getSource(false) : super.getFluidState(iblockdata);
    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(LightningRodBlock.POWERED) ? 15 : 0;
    }

    @Override
    public int getDirectSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.getValue(LightningRodBlock.POWERED) && iblockdata.getValue(LightningRodBlock.FACING) == enumdirection ? 15 : 0;
    }

    public void onLightningStrike(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.setBlock(blockposition, (IBlockData) iblockdata.setValue(LightningRodBlock.POWERED, true), 3);
        this.updateNeighbours(iblockdata, world, blockposition);
        world.scheduleTick(blockposition, (Block) this, 8);
        world.levelEvent(3002, blockposition, ((EnumDirection) iblockdata.getValue(LightningRodBlock.FACING)).getAxis().ordinal());
    }

    private void updateNeighbours(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.updateNeighborsAt(blockposition.relative(((EnumDirection) iblockdata.getValue(LightningRodBlock.FACING)).getOpposite()), this);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(LightningRodBlock.POWERED, false), 3);
        this.updateNeighbours(iblockdata, worldserver, blockposition);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        if (world.isThundering() && (long) world.random.nextInt(200) <= world.getGameTime() % 200L && blockposition.getY() == world.getHeight(HeightMap.Type.WORLD_SURFACE, blockposition.getX(), blockposition.getZ()) - 1) {
            ParticleUtils.spawnParticlesAlongAxis(((EnumDirection) iblockdata.getValue(LightningRodBlock.FACING)).getAxis(), world, blockposition, 0.125D, Particles.ELECTRIC_SPARK, UniformInt.of(1, 2));
        }
    }

    @Override
    public void onRemove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.getValue(LightningRodBlock.POWERED)) {
                this.updateNeighbours(iblockdata, world, blockposition);
            }

            super.onRemove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.is(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.getValue(LightningRodBlock.POWERED) && !world.getBlockTicks().hasScheduledTick(blockposition, this)) {
                world.setBlock(blockposition, (IBlockData) iblockdata.setValue(LightningRodBlock.POWERED, false), 18);
            }

        }
    }

    @Override
    public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        if (world.isThundering() && iprojectile instanceof EntityThrownTrident && ((EntityThrownTrident) iprojectile).isChanneling()) {
            BlockPosition blockposition = movingobjectpositionblock.getBlockPos();

            if (world.canSeeSky(blockposition)) {
                EntityLightning entitylightning = (EntityLightning) EntityTypes.LIGHTNING_BOLT.create(world);

                if (entitylightning != null) {
                    entitylightning.moveTo(Vec3D.atBottomCenterOf(blockposition.above()));
                    Entity entity = iprojectile.getOwner();

                    entitylightning.setCause(entity instanceof EntityPlayer ? (EntityPlayer) entity : null);
                    world.addFreshEntity(entitylightning);
                }

                world.playSound((EntityHuman) null, blockposition, SoundEffects.TRIDENT_THUNDER, SoundCategory.WEATHER, 5.0F, 1.0F);
            }
        }

    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(LightningRodBlock.FACING, LightningRodBlock.POWERED, LightningRodBlock.WATERLOGGED);
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }
}
