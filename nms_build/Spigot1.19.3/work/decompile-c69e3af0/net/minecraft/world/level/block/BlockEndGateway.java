package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.particles.Particles;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityEndGateway;
import net.minecraft.world.level.block.entity.TileEntityTypes;
import net.minecraft.world.level.block.state.BlockBase;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.material.FluidType;

public class BlockEndGateway extends BlockTileEntity {

    protected BlockEndGateway(BlockBase.Info blockbase_info) {
        super(blockbase_info);
    }

    @Override
    public TileEntity newBlockEntity(BlockPosition blockposition, IBlockData iblockdata) {
        return new TileEntityEndGateway(blockposition, iblockdata);
    }

    @Nullable
    @Override
    public <T extends TileEntity> BlockEntityTicker<T> getTicker(World world, IBlockData iblockdata, TileEntityTypes<T> tileentitytypes) {
        return createTickerHelper(tileentitytypes, TileEntityTypes.END_GATEWAY, world.isClientSide ? TileEntityEndGateway::beamAnimationTick : TileEntityEndGateway::teleportTick);
    }

    @Override
    public void animateTick(IBlockData iblockdata, World world, BlockPosition blockposition, RandomSource randomsource) {
        TileEntity tileentity = world.getBlockEntity(blockposition);

        if (tileentity instanceof TileEntityEndGateway) {
            int i = ((TileEntityEndGateway) tileentity).getParticleAmount();

            for (int j = 0; j < i; ++j) {
                double d0 = (double) blockposition.getX() + randomsource.nextDouble();
                double d1 = (double) blockposition.getY() + randomsource.nextDouble();
                double d2 = (double) blockposition.getZ() + randomsource.nextDouble();
                double d3 = (randomsource.nextDouble() - 0.5D) * 0.5D;
                double d4 = (randomsource.nextDouble() - 0.5D) * 0.5D;
                double d5 = (randomsource.nextDouble() - 0.5D) * 0.5D;
                int k = randomsource.nextInt(2) * 2 - 1;

                if (randomsource.nextBoolean()) {
                    d2 = (double) blockposition.getZ() + 0.5D + 0.25D * (double) k;
                    d5 = (double) (randomsource.nextFloat() * 2.0F * (float) k);
                } else {
                    d0 = (double) blockposition.getX() + 0.5D + 0.25D * (double) k;
                    d3 = (double) (randomsource.nextFloat() * 2.0F * (float) k);
                }

                world.addParticle(Particles.PORTAL, d0, d1, d2, d3, d4, d5);
            }

        }
    }

    @Override
    public ItemStack getCloneItemStack(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canBeReplaced(IBlockData iblockdata, FluidType fluidtype) {
        return false;
    }
}
