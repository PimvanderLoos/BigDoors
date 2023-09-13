package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityBlastFurnace;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockBlastFurnace extends BlockFurnace {

    protected BlockBlastFurnace(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityBlastFurnace(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return createFurnaceTicker(world, tileentitytypes, TileEntityTypes.BLAST_FURNACE);
    }

    @Override
    protected void openContainer(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityBlastFurnace) {
            entityhuman.openMenu((ITileInventory) tileentity);
            entityhuman.awardStat(StatisticList.INTERACT_WITH_BLAST_FURNACE);
        }

    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        if ((Boolean) iblockdata.getValue(BlockBlastFurnace.LIT)) {
            double d0 = (double) blockposition.getX() + 0.5D;
            double d1 = (double) blockposition.getY();
            double d2 = (double) blockposition.getZ() + 0.5D;

            if (randomsource.nextDouble() < 0.1D) {
                world.playLocalSound(d0, d1, d2, SoundEffects.BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            EnumDirection enumdirection = (EnumDirection) iblockdata.getValue(BlockBlastFurnace.FACING);
            EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.getAxis();
            double d3 = 0.52D;
            double d4 = randomsource.nextDouble() * 0.6D - 0.3D;
            double d5 = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? (double) enumdirection.getStepX() * 0.52D : d4;
            double d6 = randomsource.nextDouble() * 9.0D / 16.0D;
            double d7 = enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? (double) enumdirection.getStepZ() * 0.52D : d4;

            world.addParticle(Particles.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }
}
