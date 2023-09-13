package net.minecraft.world.level.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.server.level.WorldServer;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.BlockActionContext;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.IWorldReader;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.SculkSensorBlockEntity;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateBoolean;
import net.minecraft.world.level.block.state.properties.BlockStateEnum;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.level.block.state.properties.SculkSensorPhase;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidTypes;
import net.minecraft.world.level.pathfinder.PathMode;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;

public class SculkSensorBlock extends BlockTileEntity implements IBlockWaterlogged {

    public static final int ACTIVE_TICKS = 40;
    public static final int COOLDOWN_TICKS = 1;
    public static final Object2IntMap<GameEvent> VIBRATION_STRENGTH_FOR_EVENT = Object2IntMaps.unmodifiable((Object2IntMap) SystemUtils.a((Object) (new Object2IntOpenHashMap()), (object2intopenhashmap) -> {
        object2intopenhashmap.put(GameEvent.STEP, 1);
        object2intopenhashmap.put(GameEvent.FLAP, 2);
        object2intopenhashmap.put(GameEvent.SWIM, 3);
        object2intopenhashmap.put(GameEvent.ELYTRA_FREE_FALL, 4);
        object2intopenhashmap.put(GameEvent.HIT_GROUND, 5);
        object2intopenhashmap.put(GameEvent.SPLASH, 6);
        object2intopenhashmap.put(GameEvent.WOLF_SHAKING, 6);
        object2intopenhashmap.put(GameEvent.MINECART_MOVING, 6);
        object2intopenhashmap.put(GameEvent.RING_BELL, 6);
        object2intopenhashmap.put(GameEvent.BLOCK_CHANGE, 6);
        object2intopenhashmap.put(GameEvent.PROJECTILE_SHOOT, 7);
        object2intopenhashmap.put(GameEvent.DRINKING_FINISH, 7);
        object2intopenhashmap.put(GameEvent.PRIME_FUSE, 7);
        object2intopenhashmap.put(GameEvent.PROJECTILE_LAND, 8);
        object2intopenhashmap.put(GameEvent.EAT, 8);
        object2intopenhashmap.put(GameEvent.MOB_INTERACT, 8);
        object2intopenhashmap.put(GameEvent.ENTITY_DAMAGED, 8);
        object2intopenhashmap.put(GameEvent.EQUIP, 9);
        object2intopenhashmap.put(GameEvent.SHEAR, 9);
        object2intopenhashmap.put(GameEvent.RAVAGER_ROAR, 9);
        object2intopenhashmap.put(GameEvent.BLOCK_CLOSE, 10);
        object2intopenhashmap.put(GameEvent.BLOCK_UNSWITCH, 10);
        object2intopenhashmap.put(GameEvent.BLOCK_UNPRESS, 10);
        object2intopenhashmap.put(GameEvent.BLOCK_DETACH, 10);
        object2intopenhashmap.put(GameEvent.DISPENSE_FAIL, 10);
        object2intopenhashmap.put(GameEvent.BLOCK_OPEN, 11);
        object2intopenhashmap.put(GameEvent.BLOCK_SWITCH, 11);
        object2intopenhashmap.put(GameEvent.BLOCK_PRESS, 11);
        object2intopenhashmap.put(GameEvent.BLOCK_ATTACH, 11);
        object2intopenhashmap.put(GameEvent.ENTITY_PLACE, 12);
        object2intopenhashmap.put(GameEvent.BLOCK_PLACE, 12);
        object2intopenhashmap.put(GameEvent.FLUID_PLACE, 12);
        object2intopenhashmap.put(GameEvent.ENTITY_KILLED, 13);
        object2intopenhashmap.put(GameEvent.BLOCK_DESTROY, 13);
        object2intopenhashmap.put(GameEvent.FLUID_PICKUP, 13);
        object2intopenhashmap.put(GameEvent.FISHING_ROD_REEL_IN, 14);
        object2intopenhashmap.put(GameEvent.CONTAINER_CLOSE, 14);
        object2intopenhashmap.put(GameEvent.PISTON_CONTRACT, 14);
        object2intopenhashmap.put(GameEvent.SHULKER_CLOSE, 14);
        object2intopenhashmap.put(GameEvent.PISTON_EXTEND, 15);
        object2intopenhashmap.put(GameEvent.CONTAINER_OPEN, 15);
        object2intopenhashmap.put(GameEvent.FISHING_ROD_CAST, 15);
        object2intopenhashmap.put(GameEvent.EXPLODE, 15);
        object2intopenhashmap.put(GameEvent.LIGHTNING_STRIKE, 15);
        object2intopenhashmap.put(GameEvent.SHULKER_OPEN, 15);
    }));
    public static final BlockStateEnum<SculkSensorPhase> PHASE = BlockProperties.SCULK_SENSOR_PHASE;
    public static final BlockStateInteger POWER = BlockProperties.POWER;
    public static final BlockStateBoolean WATERLOGGED = BlockProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = Block.a(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
    private final int listenerRange;

    public SculkSensorBlock(BlockBase.Info blockbase_info, int i) {
        super(blockbase_info);
        this.k((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.stateDefinition.getBlockData()).set(SculkSensorBlock.PHASE, SculkSensorPhase.INACTIVE)).set(SculkSensorBlock.POWER, 0)).set(SculkSensorBlock.WATERLOGGED, false));
        this.listenerRange = i;
    }

    public int e() {
        return this.listenerRange;
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockposition);

        return (IBlockData) this.getBlockData().set(SculkSensorBlock.WATERLOGGED, fluid.getType() == FluidTypes.WATER);
    }

    @Override
    public Fluid c_(IBlockData iblockdata) {
        return (Boolean) iblockdata.get(SculkSensorBlock.WATERLOGGED) ? FluidTypes.WATER.a(false) : super.c_(iblockdata);
    }

    @Override
    public void tickAlways(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, Random random) {
        if (h(iblockdata) != SculkSensorPhase.ACTIVE) {
            if (h(iblockdata) == SculkSensorPhase.COOLDOWN) {
                worldserver.setTypeAndData(blockposition, (IBlockData) iblockdata.set(SculkSensorBlock.PHASE, SculkSensorPhase.INACTIVE), 3);
            }

        } else {
            a((World) worldserver, blockposition, iblockdata);
        }
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!world.isClientSide() && !iblockdata.a(iblockdata1.getBlock())) {
            if ((Integer) iblockdata.get(SculkSensorBlock.POWER) > 0 && !world.getBlockTickList().a(blockposition, this)) {
                world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(SculkSensorBlock.POWER, 0), 18);
            }

            world.getBlockTickList().a(new BlockPosition(blockposition), iblockdata.getBlock(), 1);
        }
    }

    @Override
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!iblockdata.a(iblockdata1.getBlock())) {
            if (h(iblockdata) == SculkSensorPhase.ACTIVE) {
                a(world, blockposition);
            }

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if ((Boolean) iblockdata.get(SculkSensorBlock.WATERLOGGED)) {
            generatoraccess.getFluidTickList().a(blockposition, FluidTypes.WATER, FluidTypes.WATER.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private static void a(World world, BlockPosition blockposition) {
        world.applyPhysics(blockposition, Blocks.SCULK_SENSOR);
        world.applyPhysics(blockposition.shift(EnumDirection.UP.opposite()), Blocks.SCULK_SENSOR);
    }

    @Nullable
    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new SculkSensorBlockEntity(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> GameEventListener a(World world, T t0) {
        return t0 instanceof SculkSensorBlockEntity ? ((SculkSensorBlockEntity) t0).d() : null;
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return !world.isClientSide ? a(tileentitytypes, TileEntityTypes.SCULK_SENSOR, (world1, blockposition, iblockdata1, sculksensorblockentity) -> {
            sculksensorblockentity.d().a(world1);
        }) : null;
    }

    @Override
    public EnumRenderType b_(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return SculkSensorBlock.SHAPE;
    }

    @Override
    public boolean isPowerSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Integer) iblockdata.get(SculkSensorBlock.POWER);
    }

    public static SculkSensorPhase h(IBlockData iblockdata) {
        return (SculkSensorPhase) iblockdata.get(SculkSensorBlock.PHASE);
    }

    public static boolean n(IBlockData iblockdata) {
        return h(iblockdata) == SculkSensorPhase.INACTIVE;
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.setTypeAndData(blockposition, (IBlockData) ((IBlockData) iblockdata.set(SculkSensorBlock.PHASE, SculkSensorPhase.COOLDOWN)).set(SculkSensorBlock.POWER, 0), 3);
        world.getBlockTickList().a(new BlockPosition(blockposition), iblockdata.getBlock(), 1);
        if (!(Boolean) iblockdata.get(SculkSensorBlock.WATERLOGGED)) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.SCULK_CLICKING_STOP, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
        }

        a(world, blockposition);
    }

    public static void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        world.setTypeAndData(blockposition, (IBlockData) ((IBlockData) iblockdata.set(SculkSensorBlock.PHASE, SculkSensorPhase.ACTIVE)).set(SculkSensorBlock.POWER, i), 3);
        world.getBlockTickList().a(new BlockPosition(blockposition), iblockdata.getBlock(), 40);
        a(world, blockposition);
        if (!(Boolean) iblockdata.get(SculkSensorBlock.WATERLOGGED)) {
            world.playSound((EntityHuman) null, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, SoundEffects.SCULK_CLICKING, SoundCategory.BLOCKS, 1.0F, world.random.nextFloat() * 0.2F + 0.8F);
        }

    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (h(iblockdata) == SculkSensorPhase.ACTIVE) {
            EnumDirection enumdirection = EnumDirection.a(random);

            if (enumdirection != EnumDirection.UP && enumdirection != EnumDirection.DOWN) {
                double d0 = (double) blockposition.getX() + 0.5D + (enumdirection.getAdjacentX() == 0 ? 0.5D - random.nextDouble() : (double) enumdirection.getAdjacentX() * 0.6D);
                double d1 = (double) blockposition.getY() + 0.25D;
                double d2 = (double) blockposition.getZ() + 0.5D + (enumdirection.getAdjacentZ() == 0 ? 0.5D - random.nextDouble() : (double) enumdirection.getAdjacentZ() * 0.6D);
                double d3 = (double) random.nextFloat() * 0.04D;

                world.addParticle(DustColorTransitionOptions.SCULK_TO_REDSTONE, d0, d1, d2, 0.0D, d3, 0.0D);
            }
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(SculkSensorBlock.PHASE, SculkSensorBlock.POWER, SculkSensorBlock.WATERLOGGED);
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof SculkSensorBlockEntity) {
            SculkSensorBlockEntity sculksensorblockentity = (SculkSensorBlockEntity) tileentity;

            return h(iblockdata) == SculkSensorPhase.ACTIVE ? sculksensorblockentity.getLastVibrationFrequency() : 0;
        } else {
            return 0;
        }
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public boolean g_(IBlockData iblockdata) {
        return true;
    }
}
