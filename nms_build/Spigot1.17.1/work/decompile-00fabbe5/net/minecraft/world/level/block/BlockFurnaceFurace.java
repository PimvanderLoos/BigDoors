package net.minecraft.world.level.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.particles.Particles;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffects;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityFurnaceFurnace;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;

public class BlockFurnaceFurace extends BlockFurnace {

    protected BlockFurnaceFurace(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity createTile(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityFurnaceFurnace(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> a(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return a(world, tileentitytypes, TileEntityTypes.FURNACE);
    }

    @Override
    protected void a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityFurnaceFurnace) {
            entityhuman.openContainer((ITileInventory) tileentity);
            entityhuman.a(StatisticList.INTERACT_WITH_FURNACE);
        }

    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Boolean) iblockdata.get(BlockFurnaceFurace.LIT)) {
            double d0 = (double) blockposition.getX() + 0.5D;
            double d1 = (double) blockposition.getY();
            double d2 = (double) blockposition.getZ() + 0.5D;

            if (random.nextDouble() < 0.1D) {
                world.a(d0, d1, d2, SoundEffects.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockFurnaceFurace.FACING);
            EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.n();
            double d3 = 0.52D;
            double d4 = random.nextDouble() * 0.6D - 0.3D;
            double d5 = enumdirection_enumaxis == EnumDirection.EnumAxis.X ? (double) enumdirection.getAdjacentX() * 0.52D : d4;
            double d6 = random.nextDouble() * 6.0D / 16.0D;
            double d7 = enumdirection_enumaxis == EnumDirection.EnumAxis.Z ? (double) enumdirection.getAdjacentZ() * 0.52D : d4;

            world.addParticle(Particles.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
            world.addParticle(Particles.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
        }
    }
}
