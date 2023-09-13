package net.minecraft.world.level.block;

import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.IProjectile;
import net.minecraft.world.level.GeneratorAccess;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.BlockStateList;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.block.state.properties.BlockProperties;
import net.minecraft.world.level.block.state.properties.BlockStateInteger;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class BlockTarget extends Block {

    private static final BlockStateInteger OUTPUT_POWER = BlockProperties.POWER;
    private static final int ACTIVATION_TICKS_ARROWS = 20;
    private static final int ACTIVATION_TICKS_OTHER = 8;

    public BlockTarget(BlockBase.Info blockbase_info) {
        super(blockbase_info);
        this.registerDefaultState((IBlockData) ((IBlockData) this.stateDefinition.any()).setValue(BlockTarget.OUTPUT_POWER, 0));
    }

    @Override
    public void onProjectileHit(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, IProjectile iprojectile) {
        int i = updateRedstoneOutput(world, iblockdata, movingobjectpositionblock, iprojectile);
        Entity entity = iprojectile.getOwner();

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;

            entityplayer.awardStat(StatisticList.TARGET_HIT);
            CriterionTriggers.TARGET_BLOCK_HIT.trigger(entityplayer, iprojectile, movingobjectpositionblock.getLocation(), i);
        }

    }

    private static int updateRedstoneOutput(GeneratorAccess generatoraccess, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, Entity entity) {
        int i = getRedstoneStrength(movingobjectpositionblock, movingobjectpositionblock.getLocation());
        int j = entity instanceof EntityArrow ? 20 : 8;

        if (!generatoraccess.getBlockTicks().hasScheduledTick(movingobjectpositionblock.getBlockPos(), iblockdata.getBlock())) {
            setOutputPower(generatoraccess, iblockdata, i, movingobjectpositionblock.getBlockPos(), j);
        }

        return i;
    }

    private static int getRedstoneStrength(MovingObjectPositionBlock movingobjectpositionblock, Vec3D vec3d) {
        EnumDirection enumdirection = movingobjectpositionblock.getDirection();
        double d0 = Math.abs(MathHelper.frac(vec3d.x) - 0.5D);
        double d1 = Math.abs(MathHelper.frac(vec3d.y) - 0.5D);
        double d2 = Math.abs(MathHelper.frac(vec3d.z) - 0.5D);
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();
        double d3;

        if (enumdirection_enumaxis == EnumDirection.EnumAxis.Y) {
            d3 = Math.max(d0, d2);
        } else if (enumdirection_enumaxis == EnumDirection.EnumAxis.Z) {
            d3 = Math.max(d0, d1);
        } else {
            d3 = Math.max(d1, d2);
        }

        return Math.max(1, MathHelper.ceil(15.0D * MathHelper.clamp((0.5D - d3) / 0.5D, 0.0D, 1.0D)));
    }

    private static void setOutputPower(GeneratorAccess generatoraccess, IBlockData iblockdata, int i, BlockPosition blockposition, int j) {
        generatoraccess.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockTarget.OUTPUT_POWER, i), 3);
        generatoraccess.scheduleTick(blockposition, iblockdata.getBlock(), j);
    }

    @Override
    public void tick(IBlockData iblockdata, WorldServer worldserver, BlockPosition blockposition, RandomSource randomsource) {
        if ((Integer) iblockdata.getValue(BlockTarget.OUTPUT_POWER) != 0) {
            worldserver.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockTarget.OUTPUT_POWER, 0), 3);
        }

    }

    @Override
    public int getSignal(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return (Integer) iblockdata.getValue(BlockTarget.OUTPUT_POWER);
    }

    @Override
    public boolean isSignalSource(IBlockData iblockdata) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.add(BlockTarget.OUTPUT_POWER);
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (!world.isClientSide() && !iblockdata.is(iblockdata1.getBlock())) {
            if ((Integer) iblockdata.getValue(BlockTarget.OUTPUT_POWER) > 0 && !world.getBlockTicks().hasScheduledTick(blockposition, this)) {
                world.setBlock(blockposition, (IBlockData) iblockdata.setValue(BlockTarget.OUTPUT_POWER, 0), 18);
            }

        }
    }
}
