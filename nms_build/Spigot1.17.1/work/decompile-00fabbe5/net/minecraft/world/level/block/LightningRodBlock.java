package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.util.ParticleUtils;
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
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.levelgen.HeightMap;
import net.minecraft.world.level.material.Fluid;
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
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(LightningRodBlock.FACING, EnumDirection.UP)).set(LightningRodBlock.WATERLOGGED, false)).set(LightningRodBlock.POWERED, false));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());
        boolean flag = fluid.getType() == FluidTypes.WATER;

        return (IBlockData) ((IBlockData) this.getBlockData().set(LightningRodBlock.FACING, blockactioncontext.getClickedFace())).set(LightningRodBlock.WATERLOGGED, flag);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(LightningRodBlock.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(LightningRodBlock.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(LightningRodBlock.POWERED) ? 15 : 0;
    }

    @Override
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Boolean) iblockdata.get(LightningRodBlock.POWERED) && iblockdata.get(LightningRodBlock.FACING) == enumdirection ? 15 : 0;
    }

    public void d(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(LightningRodBlock.POWERED, true), 3);
        this.e(iblockdata, world, blockposition);
        world.getBlockTickList().a(blockposition, this, 8);
        world.triggerEffect(3002, blockposition, ((EnumDirection) iblockdata.get(LightningRodBlock.FACING)).n().ordinal());
    }

    private void e(IBlockData iblockdata, World world, BlockPosition blockposition) {
        world.applyPhysics(blockposition.shift(((EnumDirection) iblockdata.get(LightningRodBlock.FACING)).opposite()), this);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(LightningRodBlock.POWERED, false), 3);
        this.e(iblockdata, worldserver, blockposition);
    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.Y() && (long) world.random.nextInt(200) <= world.getTime() % 200L && blockposition.getY() == world.a(HeightMap.Type.WORLD_SURFACE, blockposition.getX(), blockposition.getZ()) - 1) {
            ParticleUtils.a(((EnumDirection) iblockdata.get(LightningRodBlock.FACING)).n(), world, blockposition, 0.125D, Particles.ELECTRIC_SPARK, UniformInt.a(1, 2));
        }
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.get(LightningRodBlock.POWERED)) {
                this.e(iblockdata, world, blockposition);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            if ((Boolean) iblockdata.get(LightningRodBlock.POWERED) && !world.getBlockTickList().a(blockposition, this)) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(LightningRodBlock.POWERED, false), 18);
            }

        }
    }

    @Override
    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        if (world.Y() && iprojectile instanceof EntityThrownTrident && ((EntityThrownTrident) iprojectile).A()) {
            BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();

            if (world.g(blockposition)) {
                EntityLightning entitylightning = (EntityLightning) EntityTypes.LIGHTNING_BOLT.a(world);

                entitylightning.d(Vec3D.c((BaseBlockPosition) blockposition.up()));
                Entity entity = iprojectile.getShooter();

                entitylightning.b(entity instanceof EntityPlayer ? (EntityPlayer) entity : null);
                world.addEntity(entitylightning);
                world.playSound((EntityHuman) null, blockposition, SoundEffects.TRIDENT_THUNDER, SoundCategory.WEATHER, 5.0F, 1.0F);
            }
        }

    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(LightningRodBlock.FACING, LightningRodBlock.POWERED, LightningRodBlock.WATERLOGGED);
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }
}
